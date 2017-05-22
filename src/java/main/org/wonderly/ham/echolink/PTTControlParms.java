package org.wonderly.ham.echolink;

/**
 *  This class provides the complete set of parameters 
 *  associated with PTT control.
 */
public class PTTControlParms {
	boolean isVox;
	SerialPttType pttType;
	String serialPort;
	boolean keyPttLocal;

	public enum SerialPttType { ASCII, RTS, DTR };
	
	public String toString() {
		return "PTT Ctl: isVox="+isVox+", type="+pttType+", port=\""+serialPort+"\", keylocal="+keyPttLocal;
	}

	public PTTControlParms( boolean isVox, SerialPttType type, String port ) {
		this.isVox = isVox;
		this.pttType = type;
		this.serialPort = port;
		if( !isVox && port == null ) {
			throw new NullPointerException( "serial port must be non-null for nonVox control");
		}
	}

	public PTTControlParms( boolean isVox, SerialPttType type, String port, boolean keyPttLocal ) {
		this( isVox, type, port );
		this.keyPttLocal = keyPttLocal;
	}

	public String getSerialPort() {
		return isVox ? null : serialPort;
	}

	public SerialPttType getSerialPttType() {
		return pttType;
	}

	public boolean isVoxPtt() {
		return isVox;
	}

	public boolean keyPttOnLocalXmit() {
		return keyPttLocal;
	}
}