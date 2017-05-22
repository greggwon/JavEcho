package org.wonderly.ham.echolink;

import java.net.*;

/**
 *  This interface is used to track the interface back
 *  to an object that tracks the presence of a multicast
 *  echolink server that is broadcasting audio into the
 *  system.
 */
public interface MulticastServer {
	/**
	 *  Called to check on the state of this connection.
	 *  When the remote station disconnects, this method
	 *  should return true so that the disconnected state
	 *  can be detected.
	 */
	public boolean disconnected();
	/**
	 *  Called to set the multicast socket instance that
	 *  is in use.  This socket should be closed when
	 *  the remote station disconnects.
	 */
	public void setMulticastSocket( MulticastSocket sock );
}