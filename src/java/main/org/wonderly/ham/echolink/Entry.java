package org.wonderly.ham.echolink;

import java.io.*;
import java.util.*;

public class Entry implements java.io.Serializable {
	public static final int TYPE_LINK = 1;
	public static final int TYPE_REPEATER = 2;
	public static final int TYPE_STATION = 3;
	public static final int TYPE_CONF = 4;
	public static final int TYPE_MSG = 5;
	int type;
	private transient StationData  station;
	Entry next;
	static final long serialVersionUID = 3324895912918389202L;

	public boolean equals( Object obj ) {
		if( obj instanceof Entry == false )
			return false;
		Entry e = (Entry)obj;
		return type == e.type && station.equals(e.station);
	}
	
	public int hashCode() {
		int cd = station.hashCode();
		return cd ^ ((type+13) * cd);
	}
	
	private void writeObject( ObjectOutputStream os ) throws IOException {
		os.defaultWriteObject();
		os.writeObject( station.getCall() );
	}
	
	private void readObject( ObjectInputStream is ) throws IOException,ClassNotFoundException {
		is.defaultReadObject();
		station = StationData.dataFor( (String)is.readObject() );
	}
	
	public Entry( StationData dt, int typ ) {
		this(typ);
		station = dt;
	}
	
	public void setStation( StationData sd ) {
		station = sd;
	}
	public StationData getStation() {
		return station;
	}

	public boolean isConnected() {
		return station.getIPAddr() != null;
	}

	public Entry( int typ ) {
		type = typ;
	}

	public boolean isBusy() {
		return station.isBusy();
	}

	public int getType() {
		return type;
	}

	public String toString() {
		return station.toString()+" ["+typeName(type)+(isConnected() ? "*":"")+"]";
	}

	
	public static String typeName( int typ ) {
		switch(typ) {
			case TYPE_LINK: return "Link";
			case TYPE_REPEATER: return "Repeater";
			case TYPE_STATION: return "User";
			case TYPE_CONF: return "Conf Srvr";
			case TYPE_MSG: return "Srvr Msg";
		}
		return "unknown";
	}
}
