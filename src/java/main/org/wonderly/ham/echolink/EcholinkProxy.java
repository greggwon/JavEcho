package org.wonderly.ham.echolink;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class EcholinkProxy implements RtpRtcpHandler {
	private ServerSocket srv;
	private boolean done;
	private Logger log = Logger.getLogger( getClass().getName() );
	private DatagramSocket dsc, dsd;
	private Hashtable<String,Vector<EcholinkNode>> toNodes;
	private DatagramSocket dataOut = new DatagramSocket();
	public static final int ECHOLINK_CTRL = 5198;
	public static final int ECHOLINK_DATA = 5199;

	public static void main( String args[] ) throws IOException {
		new EcholinkProxy( args );
	}

	public EcholinkProxy( String args[] ) throws IOException {
		srv = new ServerSocket( 5200 );
		dsc = new DatagramSocket( ECHOLINK_CTRL );
		dsd = new DatagramSocket( ECHOLINK_DATA );
		toNodes = new Hashtable<String,Vector<EcholinkNode>>();
		servers.addElement( "nasouth.echolink.org" );
		servers.addElement( "naeast.echolink.org"  );
		servers.addElement( "backup.echolink.org"  );
		servers.addElement( "servers.echolink.org" );
		
		new Thread( new Runnable() {
			public void run() {
				handleControl( dsc );
			}
		}, "inbound UDP handler").start();

		new Thread( new Runnable() {
			public void run() {
				handleData( dsd );
			}
		}, "outbound UDP handle").start();

		while(!done) {
			try {
				Socket s = srv.accept();
				handleUser( s );
			} catch( Exception ex ) {
				reportException(ex);
			}
		}
	}

	private void handleUser( Socket s ) throws IOException {
		InetAddress addr = s.getInetAddress();
		String ipaddr = addr.getHostName();

		log.fine( "Recevied station list request from: "+s );
		proxyStationListTo( s );
	}
	
	String toStr( byte[]arr, int len ) {
		String str = "";
		for( int i = 0; i < len; ++i ) {
			if(arr[i] >= 32 && arr[i] < 127 )
				str += (char)arr[i];
			else
				str += "("+(arr[i]&0xff)+")";
		}
		return str;
	}

	Vector<String> servers = new Vector<String>();
	private void proxyStationListTo( Socket s ) throws IOException {
		for( int i = 0; i < servers.size(); ++i ) {
			try {
				tryProxyStationList( servers.elementAt(i), s );
				break;
			} catch( IOException ex ) {
				reportException(ex);
			}
		}
	}

	private void tryProxyStationList( final String name, 
			final Socket s ) throws IOException {

		final Socket out = new Socket(name, 5200);
		final IOException[]exs = new IOException[1];

		log.info("talk to server @"+name+", for: "+s );
		final Thread th = new Thread("StationList Out") {
			public void run() {
				try {
					OutputStream os = out.getOutputStream();
					InputStream is = s.getInputStream();
					log.info("Proxy Local Stream out");			
					proxyOutIO( 
						s.getInetAddress().getHostAddress(),
							is, os, "outbound" );
					log.info("Local Stream ends");
				} catch( IOException ex ) {
					log.log( Level.SEVERE, "Local Error: "+ex, ex );
					exs[0] = ex;
				}
			}
		};
		th.start();

		try {
			OutputStream os = s.getOutputStream();
			InputStream is = out.getInputStream();
			log.info("Proxy remote Stream back");			
			proxyIO( is, os, "inbound" );
			log.info("Remote Stream ends");
		} finally {
			log.info("Wait for other side to shutdown");
//			try {
//				th.join();
//			} catch( Exception ex ) {
//				reportException(ex);
//			}
			try {
				out.close();
			} catch( Exception ex ) {
				reportException(ex);
			}
			try {
				s.close();
			} catch( Exception ex ) {
				reportException(ex);
			}
		}

		if( exs[0] != null )
			throw exs[0];
	}

	private void proxyOutIO( String ipaddr, InputStream is, OutputStream os, String type )
			throws IOException {

		try {
			byte[]arr = new byte[ 10240 ];
			int n;
			log.info("["+type+"] starting stream");
			int cnt = 0;
			while( ( n = is.read(arr) ) > 0 ) {
				log.info( "["+type+"] "+n+" bytes "+toStr(arr,n) );
				if( cnt == 0 && arr[0] == 'l' || arr[0] == 'L' ) {
					int c = 0;
					while( arr[c+1] > ' ' && c+1 < n )
						++c;
					String call = new String(arr, 1, c );
					log.info("Call is: \""+call+"\"");
					String lns[] = new String(arr,0,n).split("\r");
					int port = 5198;
					try {
						port = Integer.parseInt( lns[lns.length-1] );
					} catch( Exception ex ) {
						log.log(Level.FINER, ex.toString(), ex );
					}
					log.info("Port is("+lns.length+"): \""+port+"\"");

					// Take the call and address info and
					// remember where this local station is
					// at on the network.
					EcholinkNode eu = new EcholinkNode( call, ipaddr );
					Vector<EcholinkNode> v = toNodes.get( call );
					if( v == null ) {
						log.fine("Create initial list for: "+call );
						v = new Vector<EcholinkNode>();
						toNodes.put( call, v );
					}

					Vector<EcholinkNode> iv = toNodes.get( ipaddr );
					if( iv == null ) {
						log.fine("Create initial list for: "+ipaddr );
						iv = new Vector<EcholinkNode>();
						toNodes.put( ipaddr, iv );
					}

					int idx = v.indexOf( eu );
					if( idx >= 0 ) {
						EcholinkNode nu = v.elementAt(idx);
						nu.timeon = System.currentTimeMillis();
					} else {
						v.addElement( eu );
					}
				}
				os.write( arr, 0, n );
				os.flush();
			}
			log.info("["+type+"] "+n+" bytes read");
		} catch( SocketException ex ) {
			if( ex.getMessage().equals("socket closed") == false ||
				ex.getMessage().equals("Socket closed") == false )
				reportException(ex);
		} catch( EOFException ex ) {
		} catch( IOException ex ) {
			throw ex;
		} finally {
			log.info("["+type+"] stream shutdown");
		}
	}

	private void proxyIO( InputStream is, OutputStream os, String type )
			throws IOException {

		try {
			byte[]arr = new byte[ 10240 ];
			int n;
			log.info("["+type+"] starting stream");
			while( ( n = is.read(arr) ) > 0 ) {
				log.fine( "["+type+"] "+n+" bytes read" );
				os.write( arr, 0, n );
				os.flush();
			}
			log.info("["+type+"] "+n+" bytes read");
		} catch( SocketException ex ) {
			if( ex.getMessage().equals("socket closed") == false ||
				ex.getMessage().equals("Socket closed") == false )
				reportException(ex);
		} catch( EOFException ex ) {
		} catch( IOException ex ) {
			throw ex;
		} finally {
			log.info("["+type+"] stream shutdown");
		}
	}

	private void handleControl( DatagramSocket s ) {
		byte[]arr = new byte[10000];
		DatagramPacket p = new DatagramPacket(
			arr, arr.length);
		while(!done) {
			try {
				s.receive(p);
				log.fine("Received control from: "+
					p.getAddress() );
				handleControl( 
					p.getAddress().getHostAddress(),
					p.getData(), p.getLength() );
			} catch( Throwable ex ) {
				reportException(ex);
			}
		}
	}

	private void handleData( DatagramSocket s ) {
		byte[]arr = new byte[10000];
		DatagramPacket p = new DatagramPacket(
			arr, arr.length);
		while(!done) {
			try {
				s.receive(p);
				log.fine("Received data from: "+
					p.getAddress() );
				handleData( 
					p.getAddress().getHostAddress(),
					p.getData(), p.getLength() );
			} catch( Throwable ex ) {
				reportException(ex);
			}
		}
	}

	public void handleControl( String addr, 
				byte[]data, int len ) throws IOException {
		Vector<EcholinkNode> v = toNodes.get( addr );
		RTPacket pk = new RTPacket( data, len );
		rtcp_sdes_request r = new rtcp_sdes_request(2);
		boolean isSdes = false;

		r.item[0].r_item = rtcp_sdes_type_t.RTCP_SDES_NAME;
		isSdes = pk.parseSDES(r);

//		if( RTPacket.isRTCPSdespacket( data, len ) ) {
		if( isSdes ) {
			Vector pv = pk.parseSDES();
			try {
				String connTo = null;
				for( int i = 0; i < pv.size(); ++i ) {
					rtcp_sdes_request_item item = (rtcp_sdes_request_item)pv.elementAt(i);
					if( item.r_item == rtcp_sdes_type_t.RTCP_SDES_NAME ) {
						log.finest("rtext.length: "+item.r_text.length+", [0] = "+
							(item.r_text[0]&0xff)+", [1] = "+(item.r_text[1] & 0xff) );
						connTo = new String( item.r_text ).split(" ")[0].trim();
//						if( name.length() == 0 )
//							name = new String( item.r_text,2,item.r_text[1] ).split(" ")[1].trim();
						log.info("getting name from RTCP_SDES_NAME: \""+connTo+
							"\" in \""+new String(item.r_text)+"\"");
					} else {
						log.info("unprocessed sdes packet: "+
								RTPacket.sdesType( item.r_item & 0xff ) );
					}
				}

				Vector<EcholinkNode> ve = null;
				if( connTo != null ) {
					log.info("Checking for entry for \""+connTo+
						"\" [len="+connTo.length()+"]" );
					ve = toNodes.get( connTo );
					if( ve == null ) {
						ve = new Vector<EcholinkNode>();
						toNodes.put( connTo, ve );
					}
				}

				if( ve != null ) {
					log.info( "sdes update from "+connTo+" @ "+addr );
//					e.getStation().setSDES( v );
//					e.getStation().setPorts( dataPort, controlPort );
//					if( ssa.isConnectedTo(e.getStation().getIPAddr()) ) {
//						log.info("already connected to: "+connTo );
//					} else {
//						log.info("not connected to: \""+name+"\" ("+ e.getStation()+")" );
//						connectTo( e.getStation(), true );
//						audio.connected();
//					}
				} else {
					log.info( "sdes update from unknown "+connTo+" @ "+addr );
//					refreshList(false);
//					if( pr.isUserMode() ) {
//						showMessageDialog( "Connection from: \""+name+"\"\n\n"+
//							"Entry not found in list, can't connect!",
//							"Can't Find Station",
//							JOptionPane.ERROR_MESSAGE );
//					} else {
//						log.info( "Connection from: \""+name+"\" failed" );
//						log.info( name+" not found in list, can't connect!" );
//					}
				}
			} catch( Exception ex ) {
				log.log(Level.WARNING,ex.toString(), ex);
			}
			if( pk.isRTCPByepacket() ) {
				log.info( "Bye packet received" );
			}
			if( pk.isRTCPSdespacket() ) {
				log.info( "SS/SR packet received" );
			}

			
			for( int i = 0; i < v.size(); ++i ) {
				EcholinkNode n = v.elementAt(i);
				DatagramPacket p = new DatagramPacket( 
					data, len,
					new InetSocketAddress( n.ipaddr,
						ECHOLINK_CTRL ) );
				dataOut.send( p );
			}
		}
	}

	public void handleData( String addr, 
				byte[]data, int len ) throws IOException {
		Vector<EcholinkNode> v = toNodes.get( addr );
		if( v == null ) {
			log.info("No known node yet for data("+len+"): "+addr );
			return;
		}
		for( int i = 0; i < v.size(); ++i ) {
			EcholinkNode n = v.elementAt(i);
			DatagramPacket p = new DatagramPacket( 
				data, len, 
				new InetSocketAddress( n.ipaddr,
					ECHOLINK_DATA ) );
			dataOut.send( p );
		}
	}

	public void reportException( Throwable ex ) {
		log.log( Level.SEVERE, ex.toString(), ex );
	}

	private static class EcholinkNode {
		public String name;
		public String ipaddr;
		public Vector<String> conns;
		public long timeon;

		public boolean equals( Object obj ) {
			if( obj instanceof EcholinkNode == false )
				return false;
			return ((EcholinkNode)obj).name.equals(name);
		}
		public int hashCode() {
			super.hashCode();
			return name.hashCode();
		}

		public EcholinkNode( String name, String addr ) {
			this.name = name;
			this.ipaddr = addr;
			conns = new Vector<String>();
			timeon = System.currentTimeMillis();
		}

		public EcholinkNode( String addr ) {
			this.name = "Node @"+addr;
			this.ipaddr = addr;
			conns = new Vector<String>();
		}

		public void addConnection( String addr ) {
			if( conns.contains( addr ) == false ) {
				conns.addElement( addr );
			}
		}
	}
}