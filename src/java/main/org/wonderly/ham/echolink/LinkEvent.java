package org.wonderly.ham.echolink;

import java.util.Arrays;

/**
 *  This class provides the events created as the
 *  Javecho system process events for echolink operations
 */
public class LinkEvent<T> extends java.util.EventObject {
	private static final long serialVersionUID = 1L;
	LinkMode typ;
	int value;
	boolean send;
	T data;
	public static enum LinkMode {MODE_IDLE,MODE_RECEIVE,MODE_TRANSMIT,MODE_SYSOPRECEIVE,MODE_SYSOPTRANSMIT,MODE_SYSOPIDLE,MICDATA_EVENT,
		CONN_EVENT,DISC_EVENT,INFO_EVENT,SDES_EVENT,MISSED_DATA ,OUT_OF_SEQUENCE_DATA,VOX_OPEN_EVENT ,VOX_CLOSE_EVENT,NETDATA_EVENT,STATION_CONN_EVENT,STATION_DISC_EVENT, NONE }

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
		if( s instanceof byte[] ) {
			StringBuilder b = new StringBuilder();
			for( byte c : (byte[])s) {
				if( c <' ' || c > '~')
					c = '.';
				b.append((char)c);
			}
			src =b.toString();
		}
		
		return "LinkEvent: src="+src+", send="+send+", type="+typeVal()+", value="+value;
	}

	public LinkEvent( Object src, boolean send, LinkMode type, int val ) {
		super(src);
		typ = type;
		value = val;
		this.send = send;
	}

	public LinkEvent( Object src, boolean send, LinkMode type, T data ) {
		super(src);
		typ = type;
		this.data = data;
		this.send = send;
	}

	public LinkEvent( Object src, boolean send, LinkMode type, int val, T data ) {
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

	public LinkMode getType() {
		return typ;
	}

	public int getValue() {
		return value;
	}
}