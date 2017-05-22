package org.wonderly.ham.echolink;

public interface AudioEventListener {
	public void setSoundTotal( int val );
	public void setSoundCurrent( int val );
	public void setNetTotal( int val );
	public void setNetCurrent( int val );
	public void setSendTotal( int val );
	public void setSendCurrent( int val );
}