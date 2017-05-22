// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UDPListener.java

package org.echolink.proxy;

import java.io.IOException;
import java.net.*;

// Referenced classes of package org.echolink.proxy:
//            ELProxy, ProxyMessage

public class UDPListener extends Thread
{

    public UDPListener(int nType, Socket sClient, DatagramSocket sListen)
    {
        m_sClient = sClient;
        m_sListen = sListen;
        m_nType = nType;
    }

    public void run()
    {
        ELProxy.traceMsg("UDPListener started (type=" + m_nType + ")");
        byte buf[] = new byte[1500];
        try {
            do {
                DatagramPacket dp = new DatagramPacket(buf, 1500);
                m_sListen.receive(dp);
                java.net.InetAddress addrSource = dp.getAddress();
                byte bData[] = dp.getData();
                ProxyMessage cMsg = new ProxyMessage(m_nType, addrSource, dp.getLength(), bData);
                cMsg.writeToSocket(m_sClient);
            } while(true);
        } catch(IOException ex) {
            ELProxy.traceMsg("UDPListener exiting");
        }
    }

    private Socket m_sClient;
    private DatagramSocket m_sListen;
    private int m_nType;
}
