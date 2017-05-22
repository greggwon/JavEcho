// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TCPListener.java

package org.echolink.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

// Referenced classes of package org.echolink.proxy:
//            ELProxy, ProxyMessage

public class TCPListener extends Thread
{
	private InputStream m_stm;
	private Socket m_sClient;

	public TCPListener(Socket sClient, Socket sListen)
		throws IOException {
		m_sClient = sClient;
		m_stm = sListen.getInputStream();
	}

	public void run() {
		try {
			while(true) {
				byte bData[] = new byte[4096];
				int nRead = m_stm.read(bData);

				if(nRead < 0)
					throw new IOException("Remote server closed socket");

				ELProxy.traceMsg("TCPListener: received " + nRead + " bytes");
				ProxyMessage cMsg = new ProxyMessage(2, null, nRead, bData);

				try {
					cMsg.writeToSocket(m_sClient);
				} catch(IOException e2) {
					break;
				}
			}
		} catch( IOException e ) {
			ELProxy.traceMsg("TCPListener: " + e.toString());
			ProxyMessage cMsgClose = new ProxyMessage(3, null, 0, null);

			try {
				cMsgClose.writeToSocket(m_sClient);
			} catch(IOException ioexception) {
			}
		}
		ELProxy.traceMsg("TCPListener exiting");
		return;
	}
}
