// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ELProxy.java

package org.echolink.proxy;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;

// Referenced classes of package org.echolink.proxy:
//            StatusPoster, ProxyMessage, UDPListener, TCPListener

public class ELProxy {
    static Logger log = Logger.getLogger( ELProxy.class.getName() );

    public ELProxy(Properties p) {
        m_nConnectionTimeout = 0;
        m_nPort = Integer.parseInt(p.getProperty("Port"));

        try {
            m_aBindAddress = InetAddress.getByName(p.getProperty("BindAddress"));
        } catch(Exception exception) {
		}
        m_sPassword = p.getProperty("Password");
        m_sPassword = m_sPassword.toUpperCase();
        m_sRegName = p.getProperty("RegistrationName");
        m_sRegComment = p.getProperty("RegistrationComment", "");
        m_nConnectionTimeout = Integer.parseInt(p.getProperty("ConnectionTimeout", "0"));
        m_sDeniedPattern = p.getProperty("CallsignsDenied");
        m_sAllowedPattern = p.getProperty("CallsignsAllowed");
        m_fPublic = false;

        if(m_sPassword.equals("PUBLIC"))
            m_fPublic = true;
    }
    
    String dump(byte[]arr ) {
    	String str="";
    	for( int i = 0; i < arr.length; ++i ) {
    		int v = arr[i]&0xff;
    		str += Integer.toHexString(v>>4);
    		str += Integer.toHexString(v&0xf);
    	}
    	return str;
    }

    public void run() {
        String sAddr = m_aBindAddress.getHostAddress();
        if(sAddr.equals("0.0.0.0")) {
            log.info("Listening for connections on port " + m_nPort);
        } else {
            log.info("Listening for connections on " + m_aBindAddress.getHostAddress() + ":" + m_nPort);
        }

        ServerSocket sListen = null;
        try {
            sListen = new ServerSocket(m_nPort, 3, m_aBindAddress);
        } catch(IOException ex) {
            reportException(ex,"Cannot bind proxy socket" );
            log.warning("Check to see if another instance of EchoLink Proxy "+
				"(or some other program) is already using port number " + m_nPort + ".");
            return;
        }

        if(m_sRegName != null && m_sRegName.length() != 0) {
            log.info("Posting registration info to EchoLink Web site");
            m_oPoster = new StatusPoster(m_sRegName, m_sRegComment, m_fPublic, sAddr);
            m_oPoster.start();
        }

        if(m_nConnectionTimeout != 0)
            log.info("Connection timeout: " + m_nConnectionTimeout + " minutes");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutting down...");
                if(m_oPoster != null)
                    m_oPoster.postShutdown();
            }
        });

        do {
            Socket sAccept = null;
            try {
                log.info("Ready for new client connection.");
                sAccept = sListen.accept();
            } catch(IOException ex1) {
                reportException(ex1,"Socket accept() failed" );
                return;
            }

            log.info("Client connected: " + sAccept.getInetAddress().getHostName() +
				" (" + sAccept.getInetAddress().getHostAddress() + ")");
            try {
                sAccept.setSoTimeout(30000);
            } catch(SocketException ex) {
            	log.log(Level.FINER,ex.toString(),ex);
	    	}

            Random cRand = new Random(System.currentTimeMillis());
            String sNonce;

            for(sNonce = Integer.toHexString(cRand.nextInt()); sNonce.length() < 8; sNonce = sNonce + "=")
				continue;

            byte bDigest[] = (byte[])null;
            try {
                MessageDigest cDigest = MessageDigest.getInstance("MD5");
                log.info("Building digest with pass="+m_sPassword+", key="+sNonce);
                cDigest.update(m_sPassword.getBytes());
                cDigest.update(sNonce.getBytes());
                bDigest = cDigest.digest();
                log.info("digest="+dump(bDigest));
            } catch(NoSuchAlgorithmException ea) {
                reportException(ea,"Internal error: cannot load message digest algorithm");
                return;
            }

            String sCallsign = "";
            try {
                InputStream stmIn = sAccept.getInputStream();
                OutputStream stmOut = sAccept.getOutputStream();
                stmOut.write(sNonce.getBytes());
                byte b;
                for(; sCallsign.length() < 12; sCallsign = sCallsign + (char)b) {
                    b = (byte)stmIn.read();
                    if(b == 10 || b == -1)
                        break;
                }
                log.info(sCallsign+" requesting connection");

                byte bReceivedDigest[] = new byte[bDigest.length];
                int nOffset = 0;
                int nRead;
                log.fine("reading "+bDigest.length+" bytes for digest");
                for(int nLen = bDigest.length; nOffset < bDigest.length; nLen -= nRead) {
                    nRead = stmIn.read(bReceivedDigest, nOffset, nLen);
                    if(nRead < 0)
                        throw new IOException("Socket closed");
                    nOffset += nRead;
                }

                int nNext = stmIn.available();
                for(int i = 0; i < bDigest.length; i++) {
                    if(bReceivedDigest[i] != bDigest[i]) {
                        byte bReasonCode[] = new byte[1];
                        bReasonCode[0] = 1;
                        ProxyMessage cMsg = new ProxyMessage(7, null, 1, bReasonCode);
                        try {
                            cMsg.writeToSocket(sAccept);
                        } catch(IOException ioe) {
                        	log.log(Level.FINER,ioe.toString(),ioe);
						}
                        throw new IOException("Incorrect password challenge received["+i+"] (call=" + sCallsign + ")");
                    }
				}

                if(!checkAccessControls(sCallsign)) {
                    byte bReasonCode[] = new byte[1];
                    bReasonCode[0] = 2;
                    ProxyMessage cMsg = new ProxyMessage(7, null, 1, bReasonCode);
                    try {
                        cMsg.writeToSocket(sAccept);
                    } catch(IOException ioexception1) {
		   			}
                    throw new IOException("Access denied (call=" + sCallsign + ")");
                }
            } catch(IOException ex3) {
                reportException(ex3,"client disconnected");
                try {
                    sAccept.close();
                } catch(IOException ioexception) {
				}
                sAccept = null;
                continue;
            }

            log.info("Client authenticated (call=" + sCallsign + ").");
            if(m_oPoster != null)
                m_oPoster.setStatus(true, sCallsign, sAccept.getInetAddress().getHostAddress());

            try {
                sAccept.setSoTimeout(0x927c0);
            } catch(SocketException socketexception1) {
	   		}

            DatagramSocket sUDPControl = null;
            DatagramSocket sUDPData = null;
            try {
                if(m_aBindAddress != null) {
                    sUDPControl = new DatagramSocket(5199, m_aBindAddress);
                    sUDPData = new DatagramSocket(5198, m_aBindAddress);
                } else {
                    sUDPControl = new DatagramSocket(5199);
                    sUDPData = new DatagramSocket(5198);
                }
            } catch(SocketException eUDP) {
                reportException(eUDP,"Datagram socket setup failed" );
                log.warning("Check to see if another instance of EchoLink Proxy (or Echolink) is already running.");
                return;
            }

            UDPListener cUDPControlListener = new UDPListener(6, sAccept, sUDPControl);
            cUDPControlListener.start();
            UDPListener cUDPDataListener = new UDPListener(5, sAccept, sUDPData);

            cUDPDataListener.start();
            Socket sTCPClient = null;
            OutputStream stmOut = null;
            DatagramSocket sUDPDataOut = null;
            DatagramSocket sUDPControlOut = null;

            try {
                sUDPDataOut = new DatagramSocket(0, m_aBindAddress);
                sUDPControlOut = new DatagramSocket(0, m_aBindAddress);
            } catch(SocketException ex) {
            	log.log(Level.SEVERE,ex.toString(),ex);
	    	}

	    	Date dClientConnectionStart = new Date();
            do {
                ProxyMessage cMsg = null;
                try {
                    cMsg = ProxyMessage.readFromSocket(sAccept);
                } catch(IOException eClient) {
                    reportException(eClient,"Client disconnected" );
                    break;
                }

                if(m_nConnectionTimeout != 0) {
                    Date dNow = new Date();
                    if(dNow.getTime() - dClientConnectionStart.getTime() > (long)m_nConnectionTimeout * 60L * 1000L) {
                        log.info("Client connected for too long; disconnecting");
                        break;
                    }
                }

                switch(cMsg.getType()) {
                case 1: // '\001'
                    traceMsg("PROXY_MSG_TCP_OPEN");
                    try {
                        sTCPClient = new Socket();
                        try {
                            sTCPClient.setSoTimeout(60000);
                        } catch(SocketException socketexception3) {
						}
                        sTCPClient.bind(new InetSocketAddress(m_aBindAddress, 0));
                        sTCPClient.connect(new InetSocketAddress(cMsg.getAddress(), 5200), 10000);
                        traceMsg("Connect succeeded to " + cMsg.getAddress().getHostAddress());
                        returnTCPError(sAccept, 0);
                        ProxyMessage cStatusMsg = new ProxyMessage(4, null, 0, null);
                        TCPListener cTCPListener = new TCPListener(sAccept, sTCPClient);
                        cTCPListener.start();
                        stmOut = sTCPClient.getOutputStream();
                    } catch(IOException ex2) {
                        reportException(ex2,"Connect failed to addressing server at " +
							cMsg.getAddress().getHostAddress());
                        returnTCPError(sAccept, ex2.hashCode());
                    }
                    break;

                case 2: // '\002'
                    traceMsg("PROXY_MSG_DATA");
                    try {
                        if(stmOut != null)
                            stmOut.write(cMsg.getData());
                    } catch(IOException eOut) {
                        returnTCPError(sAccept, eOut.hashCode());
                        try {
                            sTCPClient.close();
                        } catch(IOException ioexception7) {
						}
                        sTCPClient = null;
                        stmOut = null;
                    }
                    break;

                case 3: // '\003'
                    traceMsg("PROXY_MSG_TCP_CLOSE");
                    if(sTCPClient != null) {
                        try {
                            sTCPClient.close();
                        } catch(IOException ioexception4) {
						}
                        sTCPClient = null;
                    }
                    stmOut = null;
                    break;

                case 6: // '\006'
                    try {
                        sUDPControlOut.send(new DatagramPacket(cMsg.getData(), cMsg.getSize(), cMsg.getAddress(), 5199));
                    } catch(IOException ioe) {
                    	log.log(Level.FINER, ioe.toString(), ioe );
		   			}
                    break;

                case 5: // '\005'
                    try {
                        sUDPDataOut.send(new DatagramPacket(cMsg.getData(), cMsg.getSize(), cMsg.getAddress(), 5198));
                    } catch(IOException ioe ) {
                    	log.log(Level.FINER, ioe.toString(), ioe );
				    }
                    break;
                }
            } while(true);

            try {
                sAccept.close();
            } catch(IOException ioe ) {
               	log.log(Level.FINER, ioe.toString(), ioe );
	    	}
            sAccept = null;
            sUDPControl.close();
            sUDPData.close();
            if(m_oPoster != null) {
                m_oPoster.setStatus(false, null, null);
	    	}
        } while(true);
    }

    private boolean checkAccessControls(String sCallsign) {
        if(m_sDeniedPattern != null && m_sDeniedPattern.length() > 0 && sCallsign.matches(m_sDeniedPattern))
            return false;
        return m_sAllowedPattern == null || m_sAllowedPattern.length() <= 0 || sCallsign.matches(m_sAllowedPattern);
    }

    public static void showUsage(String sMsg) {
        System.out.println("Error: " + sMsg);
        System.out.println("Command-line arguments:\n");
        System.out.println("  [<configfile>]\n");
        System.out.println("configfile      name of configuration file; optional; default=ELProxy.conf");
    }

    public static void showConfigError(String sMsg) {
        log.severe( sMsg);
        log.severe("Please check and edit the configuration file.");
    }

    public static void main(String sArgs[]) {
        log.info("EchoLink Proxy version 1.0.9");
        String sConfigFile = "ELProxy.conf";
        if(sArgs.length > 0)
            sConfigFile = sArgs[0];
        Properties p = new Properties();
        p.setProperty("BindAddress", "0.0.0.0");
        p.setProperty("Port", String.valueOf(8100));
        p.setProperty("Password", "notset");
        try {
            FileInputStream fs = new FileInputStream(sConfigFile);
            p.load(fs);
            fs.close();
        } catch(Exception fnfe) {
            showUsage("Missing or unreadable configuration file: " + sConfigFile);
            try {
                File f = new File("ELProxy.conf");
                if(!f.exists()) {
                    p.store( new FileOutputStream("ELProxy.conf"), "Generated by EchoLink Proxy");
                    log.info("Created new configuration file: ELProxy.conf");
                }
            } catch( Exception ex ) {
            	log.log(Level.FINER,ex.toString(),ex);
	    	}
            return;
        }

        String sPassword = p.getProperty("Password");
        if(sPassword.length() == 0 || sPassword.equals("notset")) {
            showConfigError("Missing password");
            return;
        }

        int nPort = Integer.parseInt(p.getProperty("Port", String.valueOf(8100)));
        String sBindAddress = p.getProperty("BindAddress", "0.0.0.0");
        InetAddress aBindAddress = null;
        try {
            aBindAddress = InetAddress.getByName(sBindAddress);
        } catch(UnknownHostException e) {
            showUsage("Invalid bind address: " + sBindAddress + " [" + e.toString() + "]");
            return;
        }

        String sVer = System.getProperty("java.version");
        String sVerPieces[] = sVer.split("\\.");
        if(Integer.parseInt(sVerPieces[0]) == 1 && Integer.parseInt(sVerPieces[1]) < 4) {
            log.severe("This program requires version 1.4 (or above) of the Java Runtime Environment.");
            log.severe("Please see java.sun.com");
            return;
        } else {
            ELProxy cProxy = new ELProxy(p);
            cProxy.run();
            return;
        }
    }

    public static void traceMsg(String sMsg) {
        if(m_bTrace)
            log.fine(sMsg);
    }

    private static void reportException( Throwable ex, String msg ) {
		log.log(Level.SEVERE, msg, ex );
    }

    public static void returnTCPError(Socket s, int rc) {
        byte bData[] = new byte[4];
        ByteBuffer bb = ByteBuffer.wrap(bData);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(rc);
        ProxyMessage cMsg = new ProxyMessage(4, null, 4, bData);
        try {
            cMsg.writeToSocket(s);
        } catch(Exception ex) {
            reportException(ex,"returnTCPError() to client failed");
        }
    }

    private int m_nPort;
    private InetAddress m_aBindAddress;
    private String m_sPassword;
    private String m_sRegName;
    private String m_sRegComment;
    private String m_sAllowedPattern;
    private String m_sDeniedPattern;
    private boolean m_fPublic;
    private int m_nConnectionTimeout;
    private static boolean m_bTrace = false;
    private StatusPoster m_oPoster;
    private static final int RTP_DATA_PORT = 5198;
    private static final int RTP_CONTROL_PORT = 5199;
    private static final int ADDR_SERVER_PORT = 5200;
    private static final int REASON_CODE_BAD_PW = 1;
    private static final int REASON_CODE_ACCESS_DENIED = 2;
    private static final int DEFAULT_PROXY_PORT = 8100;
    private static final int AUTH_SOCKET_TIMEOUT = 30000;
    private static final int CLIENT_SOCKET_TIMEOUT = 0x927c0;
    private static final int ADDR_SOCKET_TIMEOUT = 60000;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final String PROGRAM_VERSION = "1.0.9";
    private static final String DEFAULT_CONFIG_FILENAME = "ELProxy.conf";
    private static final String PUBLIC_PASSWORD = "PUBLIC";
}
