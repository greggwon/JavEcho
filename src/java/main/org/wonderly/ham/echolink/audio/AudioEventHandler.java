package org.wonderly.ham.echolink.audio;

public interface AudioEventHandler {
	/**
	 *  Specify how many incomming network
	 *  packets are currently buffered.
	 */
	public void setCurrentRecvBuffering( int val );
	/**
	 *  Specify how many outbound network 
	 *  packets are currently buffered
	 */
	public void setCurrentSendBuffering( int val );
	/**
	 *  Specify how many playback audio
	 *  packets are currently buffered.
	 */
	public void setCurrentAudioBuffering( int val );
}