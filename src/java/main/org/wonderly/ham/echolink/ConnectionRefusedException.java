package org.wonderly.ham.echolink;

import java.io.*;

public class ConnectionRefusedException extends IOException {
	public ConnectionRefusedException( String str ) {
		super(str);
	}
	public ConnectionRefusedException() {
		super();
	}
}