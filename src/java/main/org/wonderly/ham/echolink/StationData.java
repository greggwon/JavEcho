package org.wonderly.ham.echolink;

import java.net.InetAddress;
import java.io.*;
import java.util.*;

public class StationData implements Serializable {
	public String  call;
	private transient String  data;
	private transient String  id;
	private transient String  ip;
	private transient String loc;
	private transient String time;
	private transient boolean wentBusy, wentIdle, discoed, connected;
	private transient boolean busy;
	static final long serialVersionUID = -6154353816391198317L;
	private static Map<String,StationData> stations = new HashMap<String,StationData>();
	private static Map<String,StationData> addrs = new HashMap<String,StationData>();
	transient List<rtcp_sdes_request_item> sdes;
	private transient int dataPort = 5198, controlPort = 5199;

	public static String exportHeader( boolean includeIP ) {
		return "Call,ID,"+(includeIP?"IP Address,":"")+"Data,Location,Time,Data Port,Control Port,Busy";
	}
	public String export(boolean includeIP) {
		return call+","+id+(includeIP?","+ip:"")+","+quotedCSV(data)+","+quotedCSV(loc)+","+time+","+dataPort+","+controlPort+","+busy;
	}
	public static String quotedCSV(String str) {
		String ret = "";
		String pref = "";
		String suff = "";
		if( str == null )
			return "null";
		for( int i = 0; i < str.length(); ++i ) {
			char c = str.charAt(i);
			switch (c) {
				case '"':
					ret += "\"\"";
					break;
				case ',':
					pref = suff = "\"";
					ret += c;
					break;
				default:
					ret += c;
					break;
			}
		}
		return pref + ret + suff;
	}
	public void setSDES( List<rtcp_sdes_request_item> v ) {
		sdes = v;
	}

	public List<rtcp_sdes_request_item> getSDES() {
		return sdes;
	}
	
	public void setPorts( int dataPort, int controlPort ) { 
		this.dataPort = dataPort;
		this.controlPort = controlPort;
	}
	
	public int getDataPort() {
		return dataPort;
	}
	
	public int getControlPort() {
		return controlPort;
	}

	public int getSDESItemCount() {
		return sdes.size();
	}

	public rtcp_sdes_request_item getSDESItemAt( int i ) {
		return sdes.get(i);
	}

	/** 
	 *  Get the first of any SDES item of the passed rtcp_sdes_type_t RTCP_SDES_* type
	 *  Some types have more than one occurance and thus you might need to look through
	 *  the whole array.
	 * @param type
	 * @return 
	 */
	public byte[] getSDES( int type ) {
		for( int i = 0; i < sdes.size(); ++i ) {
			rtcp_sdes_request_item item = sdes.get(i);
			if( item.r_item == type ) {
				return item.r_text;
			}
		}
		return null;
	}

	/**
	 *
	 * @return snapshot copy of station list
	 */
	public static List<StationData> stations() {
		return new ArrayList<>(stations.values());
	}

	public static void clearOnLine() {
		Iterator<StationData> e = stations.values().iterator();
		while( e.hasNext() ) {
			StationData d = e.next();
			d.connected = false;
			d.discoed = true;
			d.wentIdle = false;
			d.wentBusy = false;
		}
	}

	@Override
	public boolean equals( Object obj ) { 
		if( obj instanceof StationData == false )
			return false;
		StationData st = (StationData)obj;
		return call.equals( st.call );
	}

	@Override
	public int hashCode() {
		return call.hashCode();
	}

	public boolean wentBusy() {
		return wentBusy;
	}
	
	public boolean wentIdle() {
		return wentIdle;
	}

	public boolean connected() {
		return connected;
	}

	public boolean disconnected() {
		return discoed;
	}

	public static StationData stationForAddress( String address ) {
		return (StationData)addrs.get(address);
	}

	public static StationData stationFor( String call ) {
		return (StationData)stations.get(call);
	}

	public static StationData dataFor( String call ) {
  		StationData dt;
		if( (dt = (StationData)stations.get(call)) == null )
			return new StationData(call);
		return dt;
	}
	private StationData( String call ) {
  		this.call = call;
  		stations.put( call, this );
	}
 
	public static StationData dataFor( String call, String id, String ip, String data ) {
		StationData dt;
		if( (dt = (StationData)stations.get(call)) == null )
			return new StationData( call, id, ip, data );
		if( dt.ip == null && ip != null )
			dt.connected = true;
		dt.ip = ip;
		dt.data = data;
		if( data != null ) {
			dt.discoed = false;
			boolean obusy = dt.busy;
		  	dt.busy = data.contains("[BUSY");
		  	if( obusy && !dt.busy ) {
		  		dt.wentIdle = true;
		  		dt.wentBusy = false;
		  	} else if( !obusy && dt.busy ) {
		  		dt.wentBusy = true;
		  		dt.wentIdle = false;
		  	}
		  	if( data.contains("[") ) {
		  		int off = data.indexOf("[");
		  		while( data.substring(off+1).contains("[") ) {
		  			off += data.substring(off+1).indexOf("[")+1;
		  		}
		  		dt.time = data.substring(off).split(" ")[1].split("]")[0];
		  		dt.loc = data.substring( 0, off ).trim();
		  	} else {
		  		dt.loc = data;
		  	}
		}
		dt.id = id;
		stations.put( call, dt );
		addrs.put( ip, dt );
		return dt;
	}

	private StationData( String call, String id, String ip, String data ) {
		this(call);
		this.id = id;
		this.ip = ip;
		this.data = data;
		if( data != null ) {
			if( ip != null ) {
				discoed = false;
				connected = true;
			}
		  	busy = data.contains("[BUSY");
		  	if( data.contains("[") ) {
		  		int off = data.indexOf("[");
		  		while( data.substring(off+1).contains("[") ) {
		  			off += data.substring(off+1).indexOf("[")+1;
		  		}
		  		time = data.substring(off).split(" ")[1].split("]")[0];
		  		loc = data.substring( 0, off ).trim();
		  	} else {
		  		loc = data;
		  	}
		}
		stations.put( call, this );
		addrs.put( ip, this );
	}
	public String resolvedIPAddress() throws java.net.UnknownHostException {
			if( System.getProperty("org.wonderly.ham.echolink.nodns") == null )
				return InetAddress.getByName(ip).getHostName();
			return ip;
	}
	public String getID() {
		return id;
	}
	public String getIPAddr() {
		return ip;
	}
	public String getCall() {
		return call;
	}
	public String getOnTime() {
		return time;
	}
	public String getLocation() {
		return loc;
	}
	public boolean isBusy() {
		return busy;
	}
	@Override
	public String toString() {
		return call+" "+data+" ["+id+"]";
	}
}
