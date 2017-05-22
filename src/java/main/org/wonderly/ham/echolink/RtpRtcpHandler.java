package org.wonderly.ham.echolink;

import java.io.*;

/**
 *  Interface for getting events from the RTP/RTCP end points.
 */
public interface RtpRtcpHandler {
	public void handleData( String ipaddr, byte[]data, int len ) throws IOException;
	public void handleControl( String ipaddr, byte[]data, int len ) throws IOException;
}