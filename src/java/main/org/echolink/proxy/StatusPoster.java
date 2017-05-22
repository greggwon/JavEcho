// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StatusPoster.java

package org.echolink.proxy;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Referenced classes of package org.echolink.proxy:
//            ELProxy
public class StatusPoster extends Thread {
    public StatusPoster(String sName, String sComment, boolean fPublic, String sBindAddr) {
        m_sName = sName;
        m_sComment = sComment;
        m_fPublic = fPublic;
        m_fBusy = false;
        m_sBindAddr = sBindAddr;
        m_sClientCall = null;
        m_sClientIP = null;
    }

    public static String toHexString(byte b[]) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for(int i = 0; i < b.length; i++)
        {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0xf]);
        }

        return sb.toString();
    }

    public void run() {
        try {
            MessageDigest cDigest = MessageDigest.getInstance("MD5");
            cDigest.update(m_sName.getBytes());
            cDigest.update(m_sBindAddr.getBytes());
            cDigest.update("#5A!zu".getBytes());
            byte bDigest[] = cDigest.digest();
            m_sDigestString = toHexString(bDigest);
        } catch(NoSuchAlgorithmException ea) {
            return;
        }
        do {
            postStatus("http://www.echolink.org/proxypost.asp",
		m_sName, m_sComment, m_fPublic,
		m_fBusy, true, m_sClientCall, m_sClientIP);
            try {
                Thread.sleep(0x927c0L);
            } catch(InterruptedException e) {
                return;
            }
        } while(true);
    }

    public void setStatus(boolean fBusy, String sClientCall, String sClientIP) {
        m_fBusy = fBusy;
        m_sClientCall = sClientCall;
        m_sClientIP = sClientIP;
        postStatus("http://www.echolink.org/proxypost.asp", m_sName, m_sComment,
		m_fPublic, m_fBusy, true, m_sClientCall, m_sClientIP);
    }

    public void postShutdown() {
        postStatus("http://www.echolink.org/proxypost.asp", m_sName,
		m_sComment, m_fPublic, false, false, null, null);
    }

    private void postStatus(String sURL, String sName, String sComment, boolean fPublic,
		boolean fBusy, boolean fOnline, String sClientCall, String sClientIP) {
        URL u = null;
        try {
            u = new URL(sURL);
        } catch(MalformedURLException ex) {
            System.out.println("Bad status post URL: " + ex.toString());
        }
        try {
            HttpURLConnection conn = (HttpURLConnection)u.openConnection();
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(512);
            PrintWriter out = new PrintWriter(byteStream, true);
            String sPublic = "N";
            if(fPublic)
                sPublic = "Y";
            String sStatus = "Ready";
            if(fBusy)
                sStatus = "Busy";
            if(!fOnline)
                sStatus = "Off";
            String postData = "name=" + URLEncoder.encode(sName, "UTF-8") + "&comment=" +
                URLEncoder.encode(sComment, "UTF-8") + "&public=" + sPublic +
                "&status=" + sStatus + "&a=" + m_sBindAddr + "&d=" + m_sDigestString;
            if(sClientCall != null && sClientIP != null) {
                postData = postData + "&cc=" + URLEncoder.encode(sClientCall, "UTF-8") +
                        "&ca=" + URLEncoder.encode(sClientIP, "UTF-8");
            }
            out.print(postData);
            out.flush();
            String lengthString = String.valueOf(byteStream.size());
            conn.setRequestProperty("Content-Length", lengthString);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            byteStream.writeTo(conn.getOutputStream());

	    // Flush remaining buffer content
            for( BufferedReader in = new BufferedReader(
		    new InputStreamReader(conn.getInputStream()));
			    in.readLine() != null;) {
		continue;
	    }
        } catch(IOException e) {
            ELProxy.traceMsg("Cannot post registration message: " + e.getMessage());
        }
    }

    private static final String POSTER_URL = "http://www.echolink.org/proxypost.asp";
    private static final int SLEEP_TIME = 0x927c0;
    private static final String SS = "#5A!zu";
    private String m_sName;
    private String m_sComment;
    private boolean m_fPublic;
    private boolean m_fBusy;
    private String m_sBindAddr;
    private String m_sDigestString;
    private String m_sClientCall;
    private String m_sClientIP;
    static char hexChar[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'A', 'B', 'C', 'D', 'E', 'F'
    };
}
