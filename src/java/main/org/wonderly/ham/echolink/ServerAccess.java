package org.wonderly.ham.echolink;

import java.net.*;
import java.io.*;
import java.util.zip.*;
import java.nio.*;
import java.util.*;
import java.text.*;
import java.util.logging.*;

/**
 * This is the class used to talk to the EchoLink director server. It handles
 * login and logout and retrieval of the directory content.
 */
public class ServerAccess {
	private volatile String location;
	private volatile String site = "nasouth.echolink.org";
	private volatile String lastInfo;
	private Javecho je;
	private Parameters prm;
	private Logger log = Logger.getLogger(getClass().getName());

	public ServerAccess(Javecho je, Parameters parms) {
		this.je = je;
		prm = parms;
		lastInfo = location = prm.getQTH();
	}

	public void setup(String loc) {
		site = loc;
	}

	private StationData station;
	private Vector<Entry> listHead;
	private volatile boolean switchServer = true;

	public Vector<Entry> getList(List<String> servers, String use, String[] sites, Socket last) throws IOException {
		if (use != null) {
			site = use;
			sites[0] = site;
			switchServer = false;
			log.info("Defaulting to site: " + use);
		}

		if (servers != null && switchServer == true) {
			site = servers.get((int) (Math.random() * servers.size()));
			log.info("Starting with random site: " + site);
			sites[0] = site;
		}
		try {
			log.info("get call list of logged on stations");
			listHead = getCalls(site, last);
			log.info("call list: " + listHead.size());
			switchServer = false;
			return listHead;
		} catch (IOException ex) {
			switchServer = true;
			throw (IOException) new IOException(ex.toString() + ": " + site).initCause(ex);
		}
	}

	public synchronized Socket openSocket(String srvr_addr) throws IOException {
		/*
		 * Create a TDP/IP socket to use :
		 */
		IOException ioex = null;
		// for( int i = 0; i < 3; ++i ) {
		try {
			log.log(Level.INFO, "Connecting... to " + InetAddress.getByName(srvr_addr) + ":" + 5200,
					new Throwable("Connecting to " + srvr_addr + ":5200"));
			Socket s = new Socket() {
				public void close() throws IOException {
					log.log(Level.FINE, "Socket closed: " + this, new Throwable("socket closed"));
					super.close();
				}
			};
			log.info("Calling connect: " + srvr_addr + ":5200");
			s.connect(new InetSocketAddress(srvr_addr, 5200));
			return s;
		} catch (IOException ex) {
			if (ioex == null)
				ioex = ex;
			log.log(Level.WARNING, ex.toString() + ": " + srvr_addr + ":5200", ex);

		}
		// }
		throw ioex;
	}

	private String curInfo;

	/**
	 * Update our logon status with the indicated message and busy status of
	 * true.
	 * 
	 * @param msg
	 *            the message to user for the station status
	 * @throws IOException
	 *             if server connection error occurs
	 */
	public boolean updateLogon(String msg) throws IOException {
		return updateLogon(msg, true);
	}

	/**
	 * Update our logon status with the indicated message and busy status
	 * 
	 * @param msg
	 *            the message to user for the station status
	 * @param how
	 *            true if the station is busy
	 * @throws IOException
	 *             if server connection error occurs
	 */
	public boolean updateLogon(String msg, boolean how) throws IOException {
		location = msg;
		sendLogoff();
		lastInfo = msg;
		return sendLogon(how);
	}

	public Socket sendLogon(boolean conn, boolean returnSocket) throws IOException {
		return doLogon(conn, returnSocket);
	}

	/**
	 * Update our logon status indicating the current busy status
	 * 
	 * @param conn
	 *            true if station is busy
	 * @throws IOException
	 *             if server connection error occurs
	 */
	public boolean sendLogon(boolean conn) throws IOException {
		return doLogon(conn, true) != null;
	}

	private Socket doLogon(boolean conn, boolean returnSocket) throws IOException {
		String srvr_addr = site;
		byte sendBuf[] = new byte[50];
		byte recvBuf[] = new byte[50];
		int i, z, result;
		Socket s;

		/* Now send the 'l' command */
		s = openSocket(srvr_addr);
		if (s == null)
			return null;
		je.progress("Login as: " + prm.getCallSign());

		DataOutputStream ds = new DataOutputStream(s.getOutputStream());
		DataInputStream is = new DataInputStream(s.getInputStream());
		je.progress("sending 'l'");
		ds.writeByte((byte) 'l');
		// ds.flush();
		// log.info("sending '1'");
		// ds.writeByte('1');
		ds.flush();
		// int x = 0;
		// String key = "";
		// int cnt = 0;
		// while( ( x =is.read()) != -1 ) {
		// if( x > 0 )
		// key += ((char)x);
		// if( x == 10 ) {
		// log.info("line: "+cnt+": "+key );
		// key = "";
		// if( ++cnt == 4 )
		// break;
		// }
		// }
		// log.info("key: "+key);

		/* Get the local time */
		GregorianCalendar c = new GregorianCalendar();
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
		String logon = prm.getCallSign() + "\254\254" + prm.getPassword() + "\r" + (conn ? "BUSY" : "ONLINE")
				+ Javecho.myVersion() + "J(" + sf.format(c.getTime()) + ")\r" + location + "\r" + prm.getDataPort()
				+ "\r";
		je.progress("send logon: " + hex(logon));
		ds.writeBytes(logon);
		ds.flush();
		// s.shutdownOutput();

		result = is.read(recvBuf, 0, recvBuf.length);
		if (result < 0 || result >= 2 && new String(recvBuf, 0, result).startsWith("OK") == false) {
			je.progress("login failed");
			for (i = 0; i < result; ++i) {
				je.progress("  login char[" + i + "]: " + (recvBuf[i] & 0xff));
			}
			if (s != null && returnSocket == false)
				s.close();
			return s;
		}

		if (returnSocket == false) {
			s.close();
		}
		je.progress("login successful (" + result + ")");
		for (i = 0; i < result; ++i) {
			je.progress("  login char[" + i + "]: " + (recvBuf[i] & 0xff));
		}
		if (returnSocket && s.isClosed()) {
			throw new IllegalStateException("return socket already closed: " + s);
		}
		return s;
	}

	String hex(String str) {
		String res = "";
		for (int i = 0; i < str.length(); ++i) {
			if (str.charAt(i) >= ' ' && str.charAt(i) <= '~') {
				res += str.charAt(i) + ",";
			} else {
				res += "0x" + Integer.toHexString(str.charAt(i) & 0xff) + ",";
			}
		}
		return res;
	}

	/**
	 * Send the logoff request to the server we logged onto last
	 */
	public boolean sendLogoff() throws IOException {
		String srvr_addr = site;

		byte sendBuf[] = new byte[50], recvBuf[] = new byte[50];
		int i;
		Socket s = null;
		int result;

		log.info("Making sure to log off");
		try {
			s = openSocket(srvr_addr);
			if (s == null)
				return (false);

			log.log(Level.INFO, "Logging off as: " + prm.getCallSign(), new Throwable("logoff at"));

			/* Now send the 'l' command */

			DataOutputStream ds = new DataOutputStream(s.getOutputStream());
			DataInputStream is = new DataInputStream(s.getInputStream());
			ds.writeByte(0x6c);
			GregorianCalendar c = new GregorianCalendar();
			SimpleDateFormat sf = new SimpleDateFormat("hh:MM");
			ds.writeBytes(prm.getCallSign());
			ds.writeBytes("\254\254");
			ds.writeBytes(prm.getPassword());
			ds.writeBytes("\015OFF-V" + Javecho.myVersion() + "J\015");
			ds.writeBytes(location);
			ds.writeBytes("\015");
			ds.writeBytes(prm.getDataPort() + "");
			ds.writeBytes("\r");
			s.shutdownOutput();

			/* Now get the result */
			result = is.read(recvBuf, 0, 50);
			if (result < 0) {
				log.warning("no data received from log off request");
				return (false);
			} else {

			}
		} finally {
			if (s != null)
				s.close();
		}
		return (true);

	}

	/**
	 * Get the list of Entry objects representing all stations currently logged
	 * into the indicated server
	 */
	public Vector<Entry> getCalls(String srvr_addr, Socket useSock) throws IOException {
		Vector<Entry> v = new Vector<Entry>();

		Socket s;
		int result, j, numRecs;
		byte sendBuf[] = new byte[10], recvBuf[] = new byte[2000];
		String ptr, eptr;
		String sBeginning, xtemp[] = new String[4];
		BufferedReader rx = null;

		InputStream irx;
		OutputStream otx;
		Entry last;

		if (useSock == null) {
			s = openSocket(srvr_addr);
		} else {
			s = useSock;
		}

		irx = s.getInputStream();
		otx = s.getOutputStream();
		try {
			/* Now send the 'S' command for compressed list */
			otx.write('S');
			otx.flush();

			/* Now read the response */
			byte[] bSourceLen = new byte[4];
			int nRead = irx.read(bSourceLen, 0, 4);
			sBeginning = "";
			if (nRead > 3)
				sBeginning = new String(bSourceLen, 0, nRead);
			je.progress("Beginning of stations (" + nRead + ")");
			for (int i = 0; i < sBeginning.length(); ++i) {
				je.progress("[" + i + "]: " + ((int) sBeginning.charAt(i)));
			}
			if (nRead != 4 || sBeginning.startsWith("%%%")) {
				byte[] buf = new byte[1024];
				int n;
				n = irx.read(buf, 0, buf.length);
				if (n > 0) {
					String rest = new String(buf, 0, n);
					throw new IOException("Unexpected EOF on server response: "
							+ (sBeginning.startsWith("%%%") ? sBeginning.substring(3) + rest
									: ((nRead > 0) ? new String(bSourceLen, 0, nRead) : "No response received")));
				} else {
					throw new IOException("Unexpected EOF on server response: " + sBeginning);
				}
			}

			log.finer("first 4 chars: " + sBeginning);

			if (!sBeginning.startsWith("@@@")) {

				// The response appears to be compressed.
				// Read the data through the inflate stream.

				InflaterInputStream cZStream = new InflaterInputStream(irx);
				rx = new BufferedReader(new InputStreamReader(cZStream));
				String at_at_at = rx.readLine();
				// System.out.println("AtAtAt: "+at_at_at);
			} else {
				rx = new BufferedReader(new InputStreamReader(irx));
			}
			numRecs = Integer.parseInt(rx.readLine());
			/* Now get the number of following records */
			je.progress("numRecs: " + numRecs);

			for (int i = 0; i < numRecs; ++i) {
				String call = rx.readLine();
				if (call == null)
					break;
				String data = rx.readLine();
				if (data.startsWith("Incorrect password")) {
					throw new IllegalArgumentException("Incorrect Login or Password");
				}
				int end = data.indexOf(']');
				int port = 5198;
				if (end >= 0 && data.length() - end > 3) {
					log.finest("port after: " + data.substring(end));
					String rest = data.substring(end + 1);
					int col = rest.indexOf(':');
					log.finest("port follows: \"" + rest.substring(col + 1).trim() + "\"");
					if (col >= 0) {
						String pno = rest.substring(col + 1).trim();
						try {
							port = Integer.parseInt(pno);
						} catch (NumberFormatException ex) {
						}
					}
				}
				log.finer(call + ": remote DataPort: " + port);
				String id = rx.readLine();
				String ip = rx.readLine();
				// if( i < 10 ) {
				//
				// System.out.println("call: "+call);
				// System.out.println(" data: "+data);
				// System.out.println(" id: "+id);
				// System.out.println(" ip: "+ip);
				// }
				if (call == null || data == null || id == null || ip == null) {
					System.out.println("call: " + call);
					System.out.println("data: " + data);
					System.out.println("id: " + id);
					System.out.println("ip: " + ip);
					throw new EOFException("Missing Data in rec #" + i);
				}
				station = StationData.dataFor(call, id, ip, data);
				station.setPorts(port, port + 1);

				Entry ent = null;
				if (station.getCall().indexOf("*") >= 0) {
					ent = newEntry(station, Entry.TYPE_CONF);
					// System.out.println("conf "+station);
				} else if (station.getCall().indexOf("-L") >= 0) {
					// System.out.println("link "+station);
					ent = newEntry(station, Entry.TYPE_LINK);
				} else if (station.getCall().indexOf("-R") >= 0) {
					ent = newEntry(station, Entry.TYPE_REPEATER);
					// System.out.println("repeater "+station);
				} else if (station.getCall().length() == 1) {
					station = StationData.dataFor(data.trim(), id, ip, call);
					ent = newEntry(station, Entry.TYPE_MSG);
					// System.out.println("message: "+station);
				} else {
					ent = newEntry(station, Entry.TYPE_STATION);
					// System.out.println("station "+station);
				}
				v.addElement(ent);
			}

			// System.out.println(v.size()+" entries found from server,
			// sorting");
			int cnt = 0;
			for (int ix = 0; ix < v.size(); ++ix) {
				Entry e = (Entry) v.elementAt(ix);
				if (e.type != Entry.TYPE_MSG)
					++cnt;
			}
			if (cnt == 0)
				switchServer = true;

			Collections.sort(v, new Comparator<Entry>() {
				public int compare(Entry e1, Entry e2) {
					return e1.getStation().call.compareTo(e1.getStation().call);
				}
			});
		} finally {
			s.close();
			otx.close();
			irx.close();
			if (rx != null)
				rx.close();
		}
		return v;
	}

	/**
	 * Add to entry list
	 */
	private Entry newEntry(StationData station, int typ) {
		return new Entry(station, typ);
	}

	/**
	 * Print the list of pointers
	 */
	private Vector printPointers(Vector list) {

		for (int i = 0; i < list.size(); ++i)
			System.out.println("[" + i + "]: " + list.elementAt(i).toString());

		return list;
	}

	/*********** Print stations list **********/

	private int printList(Entry list) {

		int counter;
		counter = 0;
		while (list != null) {
			System.out.println(list.getStation().toString());
			list = list.next;
			counter++;
		}

		return (counter);
	}
}