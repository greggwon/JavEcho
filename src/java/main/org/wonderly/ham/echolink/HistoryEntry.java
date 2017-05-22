package org.wonderly.ham.echolink;

import java.io.*;
import java.util.Date;

/**
 *  This class holds a stations data about a connection to
 *  the echolink station.  We do not keep the StationData
 *  object, but instead extract the pertinent information
 *  and keep just the native typed values.
 */
public class HistoryEntry implements Serializable {
	private String call;
	private String status;
	private String id;
	private long time;
	static final long serialVersionUID = -3381289434433496964L;
	
	public HistoryEntry( StationData dt ) {
		call = dt.getCall();
		status = dt.getLocation();
		id = dt.getID();
		time = System.currentTimeMillis();
	}
	
	/**
	 *  Returns a standard formatted description of the contact
	 */
	public String toString() {
		return new Date( time ) +": "+call+" ("+status+") ["+id+"]";
	}
}