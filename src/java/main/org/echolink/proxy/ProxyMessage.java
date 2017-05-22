// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProxyMessage.java

package org.echolink.proxy;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// Referenced classes of package org.echolink.proxy:
//            ELProxy

public class ProxyMessage
{

    public ProxyMessage()
    {
        m_nType = 0;
        m_aAddress = null;
        m_nSize = 0;
        m_bData = null;
    }

    public ProxyMessage(int nType, InetAddress aAddress, int nSize, byte bData[])
    {
        m_nType = nType;
        m_aAddress = aAddress;
        m_nSize = nSize;
        m_bData = bData;
    }

    public int getType()
    {
        return m_nType;
    }

    public int getSize()
    {
        return m_nSize;
    }

    public InetAddress getAddress()
    {
        return m_aAddress;
    }

    public byte[] getData()
    {
        return m_bData;
    }

    public static ProxyMessage readFromSocket(Socket s)
        throws IOException
    {
        ProxyMessage cMsg = null;
        InputStream stm = s.getInputStream();
        byte bufHeader[] = new byte[9];
        int nLenRemaining = 9;
        int nRead;
        for(int nOffset = 0; nLenRemaining > 0; nOffset += nRead)
        {
            nRead = stm.read(bufHeader, nOffset, nLenRemaining);
            if(nRead < 0)
                throw new IOException("stm.read() returned EOF");
            nLenRemaining -= nRead;
        }

        ByteBuffer bb = ByteBuffer.wrap(bufHeader);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        int nType = bb.get();
        if(nType < 1 || nType > 7)
            throw new IOException("Unrecognized message type in client request");
        byte bAddr[] = new byte[4];
        bAddr[0] = bb.get();
        bAddr[1] = bb.get();
        bAddr[2] = bb.get();
        bAddr[3] = bb.get();
        int nSize = bb.getInt();
        if(nSize > 5000)
            throw new IOException("Message data too large in client request (" + nSize + " bytes)");
        InetAddress aAddress = InetAddress.getByAddress(bAddr);
        byte bData[] = (byte[])null;
        if(nSize > 0)
        {
            bData = new byte[nSize];
            nLenRemaining = nSize;
            for(int nOffset = 0; nLenRemaining > 0; nOffset += nRead)
            {
                nRead = stm.read(bData, nOffset, nLenRemaining);
                if(nRead < 0)
                    throw new IOException("stm.read() returned EOF");
                nLenRemaining -= nRead;
            }

        }
        ELProxy.traceMsg("Read from client: type=" + nType + ", size=" + nSize);
        cMsg = new ProxyMessage(nType, aAddress, nSize, bData);
        return cMsg;
    }

    public synchronized void writeToSocket(Socket s)
        throws IOException
    {
        OutputStream stm = s.getOutputStream();
        byte bufHeader[] = new byte[9];
        ByteBuffer bb = ByteBuffer.wrap(bufHeader);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put((byte)m_nType);
        byte bAddr[] = (byte[])null;
        if(m_aAddress != null)
        {
            bAddr = m_aAddress.getAddress();
        } else
        {
            bAddr = new byte[4];
            bAddr[0] = bAddr[1] = bAddr[2] = bAddr[3] = 0;
        }
        bb.put(bAddr);
        bb.putInt(m_nSize);
        stm.write(bufHeader);
        if(m_nSize > 0)
            stm.write(m_bData, 0, m_nSize);
        ELProxy.traceMsg("Sent to client: type=" + m_nType + ", size=" + m_nSize);
    }

    public static final int PROXY_MSG_FIRST = 1;
    public static final int PROXY_MSG_TCP_OPEN = 1;
    public static final int PROXY_MSG_TCP_DATA = 2;
    public static final int PROXY_MSG_TCP_CLOSE = 3;
    public static final int PROXY_MSG_TCP_STATUS = 4;
    public static final int PROXY_MSG_UDP_DATA = 5;
    public static final int PROXY_MSG_UDP_CONTROL = 6;
    public static final int PROXY_MSG_SYSTEM = 7;
    public static final int PROXY_MSG_LAST = 7;
    private static final int HEADER_SIZE = 9;
    private static final int MAX_DATA_SIZE = 5000;
    private int m_nType;
    private InetAddress m_aAddress;
    private int m_nSize;
    private byte m_bData[];
}
