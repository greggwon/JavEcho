package org.wonderly.ham.echolink;

public interface LinkEventListener<T> {
	public void processEvent( LinkEvent<T> ev );
}
