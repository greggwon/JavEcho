package org.wonderly.ham.echolink;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 *  This class provides the events created as the
 *  Javecho system process events for echolink operations
 */
public class LinkEvent<T> extends java.util.EventObject {
	int typ;
	int value;
	boolean send;
	T data;
	public static final int MODE_IDLE            = 0;
	public static final int MODE_RECEIVE         = 1;
	public static final int MODE_TRANSMIT        = 2;
	public static final int MODE_SYSOPRECEIVE    = 3;
	public static final int MODE_SYSOPTRANSMIT   = 4;
	public static final int MODE_SYSOPIDLE       = 5;

	public static final int MICDATA_EVENT        = 20;
	public static final int CONN_EVENT           = 21;
	public static final int DISC_EVENT           = 22;
	public static final int INFO_EVENT           = 23;
	public static final int SDES_EVENT           = 24;
	public static final int MISSED_DATA          = 25;
	public static final int OUT_OF_SEQUENCE_DATA = 26;
	public static final int VOX_OPEN_EVENT       = 27;
	public static final int VOX_CLOSE_EVENT      = 28;
	public static final int NETDATA_EVENT        = 29;
	public static final int STATION_CONN_EVENT   = 30;
	public static final int STATION_DISC_EVENT   = 31;

	private String typeVal() {
		switch(typ) {
			case MICDATA_EVENT: return "mic data";
			case NETDATA_EVENT: return "net data";
			case CONN_EVENT: return "conn";
			case DISC_EVENT: return "disc";
			case STATION_CONN_EVENT: return "station conn";
			case STATION_DISC_EVENT: return "station disc";
			case INFO_EVENT: return "info";
			case SDES_EVENT: return "sdes";
			case MISSED_DATA: return "missed packets";
			case OUT_OF_SEQUENCE_DATA: return "out of sequence packets";
			case VOX_OPEN_EVENT: return "vox open";
			case VOX_CLOSE_EVENT: return "vox close";
		}
		return "unknown";
	}

	public String toString() {
		String src;
		Object s = getSource();

		src = s.toString();
		
		return "LinkEvent: src="+src+", send="+send+", type="+typeVal()+", value="+value;
	}

	public LinkEvent( Object src, boolean send, int type, int val ) {
		super(src);
		typ = type;
		value = val;
		this.send = send;
	}

	public LinkEvent( Object src, boolean send, int type, T data ) {
		super(src);
		typ = type;
		this.data = data;
		this.send = send;
	}

	public LinkEvent( Object src, boolean send, int type, int val, T data ) {
		super(src);
		typ = type;
		value = val;
		this.send = send;
		this.data = data;
	}
	
	public T getData() {
		return data;
	}

	public boolean isSend() {
		return send;
	}

	public int getType() {
		return typ;
	}

	public int getValue() {
		return value;
	}
}