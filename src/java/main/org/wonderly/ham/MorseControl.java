package org.wonderly.ham;

import java.io.IOException;

/**
 *  This interface provides access to the control and operation of the
 *  morse code emitter that is currently active.
 */
public interface MorseControl {
	/** 
	 *  Set the volume level from -20db to 0db
	 */
	public void setVolumeLevelDb( int db ) throws IOException;
	/**
	 *  Send the indicated characters out
	 */
	public void sendMorseChars( String str ) throws IOException;
	/**
	 *  Set the character rate in Words per Minute.
	 *  This establishes the symbol rate for each
	 *  letter.
	 */
	public void setCharRate( int wpm );
	/** 
	 *  Set the word rate in Words per minute.
	 *  This establishes the spacing rate
	 */
	public void setWordRate( int wpm );
	public void setPitchFreq( int freqHz ) throws UnsupportedOperationException;
	
	/**
	 *  This operation returns the next chars
	 *  that are being received.  If receive is
	 *  not supported, this method will throw
	 *  the UnsupportedOperationException
	 *
	 *  @throws UnsupportedOperationException if receive is not supported
	 *            by the implementation.
	 */
	public String receiveMorse() throws UnsupportedOperationException;
	
	/**
	 *  @return whether receive is supported by the
	 *  implementation.
	 */
	public boolean isReceiveSupported();
}