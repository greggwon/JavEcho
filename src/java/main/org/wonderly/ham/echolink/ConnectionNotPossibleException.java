package org.wonderly.ham.echolink;

import java.io.*;

class ConnectionNotPossibleException extends IOException {
	public ConnectionNotPossibleException( String msg ) {
		super(msg);
	}
	public ConnectionNotPossibleException() {
		super();
	}
}