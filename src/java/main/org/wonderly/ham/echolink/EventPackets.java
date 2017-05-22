package org.wonderly.ham.echolink;

import java.net.*;
import java.io.*;
import java.util.logging.*;

/**
 *  This class provides the base UDP input/output points 
 *  for RTP/RTCP.  
 */
public class EventPackets {
	private Javecho je;
	private DatagramSocket dg5198, dg5199;
	private File dir;
	private Parameters prm;
	private boolean setup;
	private int data, control;
	private RtpRtcpHandler rh;
	private static Logger log = Logger.getLogger(
		EventPackets.class.getName() );

	public EventPackets( Javecho je, RtpRtcpHandler rh,
			Parameters params ) throws IOException {
		this.je = je;
		this.rh = rh;
		prm = params;
		dir = new File( new File(
			System.getProperty("user.home") ), ".javecho" );
		if( dir.exists() == false )
			dir.mkdirs();
		data = params.getDataPort();
		control = params.getControlPort();
	}
	
	public void setup() throws IOException {
		setup = false;
		bind(data, control);
		sendInfoFile();
	}

	/**
	 *  Try and bind to the needed ports.  If the ports
	 *  can not be bound, an exception will be thrown.
	 *  @throws IOException if the ports cannot be bound
	 */
	private void bind( int dataPort, 
			int controlPort ) throws IOException {
		if( dg5198 != null && dg5199 != null )
			return;
		if( prm.isAudioTrace() ) {
			System.out.println(this+": binding dataport: "+
				dataPort+", controlPort: "+controlPort );
		}
		try {
			dg5198 = new DatagramSocket(
					new InetSocketAddress( dataPort ) ) {
				public String toString() {
					return "sock="+getLocalPort()+"";
				}
			};
		} catch( BindException ex ) {
			log.log( Level.SEVERE, ex.toString(), ex );
			je.reportException( ex );
			dg5198 = new DatagramSocket( 
					new InetSocketAddress( 0 ) ) {
				public String toString() {
					return "sock="+getLocalPort()+"";
				}
			};
			dataPort = dg5198.getLocalPort();
			controlPort = dataPort + 1;
			log.severe( "rebinding to port="+dataPort );
		}
		dg5199 = new DatagramSocket( 
				new InetSocketAddress( controlPort ) ) {
			public String toString() {
				return "sock="+getLocalPort()+"";
			}
		};
		new Thread("Port "+dataPort+" Thread") {
			public void run() {
				serv5198(dg5198);
			}
		}.start();
		new Thread("Port "+controlPort+" Thread") {
			public void run() {
				serv5199(dg5199);
			}
		}.start();
		setup = true;
	}

	/**
	 *  Call this method with a free Thread to start
	 *  receiving multicast audio on the indicated
	 *  multicast address and port.  The MulticastServer
	 *  instance's disconnected() method will control
	 *  when this thread stops.  
	 *  @throws UnknownHostException if multiAddr can't be resolved
	 *  @throws IOException if there is a problem setting
	 *    up or using the MulticastSocket.  If close is
	 *    called on the multicast socket there will probably
	 *    be an exception.
	 */
	public void servMulticast( MulticastServer m, 
			String multiAddr, int port ) 
				throws UnknownHostException, IOException {
		InetAddress group = 
			InetAddress.getByName( multiAddr );
		MulticastSocket msock = new MulticastSocket( port );
		msock.joinGroup( group );
		byte data[] = new byte[6000];
		DatagramPacket p = new DatagramPacket(
			data, data.length );
		m.setMulticastSocket( msock );
		while( !m.disconnected() ) {
			try {
				msock.receive(p);
				rh.handleData( 
					p.getAddress().getHostAddress(),
					p.getData(), p.getLength() );
			} catch( Throwable ex ) {
				je.reportException(ex);
			}
		}
		msock.close();
	}

	private void serv5198(DatagramSocket sock) {
		byte data[] = new byte[6000];
		DatagramPacket p = new DatagramPacket(
			data, data.length);
		while(true) {
			try {
				sock.receive(p);
				rh.handleData( 
					p.getAddress().getHostAddress(),
					p.getData(), p.getLength() );
			} catch( Throwable ex ) {
				je.reportException(ex);
			}
		}
	}

	private void serv5199(DatagramSocket sock) {
		byte data[] = new byte[6000];
		DatagramPacket p = new DatagramPacket(
			data,data.length);
		while(true) {
			try {
				sock.receive(p);
				rh.handleControl( p.getAddress().
					getHostAddress(), p.getData(),
						p.getLength() );
			} catch( Throwable ex ) {
				je.reportException(ex,false);
			}
		}
	}

	public void sendChat( String ndata, 
				String site ) throws IOException {
		if( !setup )
			setup();
		sendNData( ndata.getBytes(), site );		
	}

	public boolean sendInfoFile() throws IOException {
		if( !setup )
			setup();

		File info = new File( dir, "info.txt" );
		FileReader fr = null;
		try {
			fr = new FileReader( info );
		} catch( FileNotFoundException ex ) {
			ex.printStackTrace();
			return false;
		}
		String msg = "";
		try {
			BufferedReader rd = new BufferedReader( fr );
			String line;
			while( ( line = rd.readLine() ) != null )
				msg += line +"\r";
		} finally {
			fr.close();
		}
		if( msg.trim().length() == 0 )
			return false;

		msg = "oNDATA\r"+msg.trim()+" ";
		sendNData( msg.getBytes(), je.getSite() );
		return true;
	}
	
	public void sendNData( byte arr[],
			String site ) throws IOException {
		if( !setup )
			setup();
		arr[arr.length-1] = 0;
		log.finer( "Sending: "+
			arr.length+" bytes to "+site );
		DatagramPacket dp = new DatagramPacket(
			arr, 0, arr.length,
			new InetSocketAddress( site, 5199 ) );

		dg5199.send( dp );
	}
	
	public void sendAudio( byte arr[],
			String site, int port ) throws IOException {
		sendVoicePacket( new DatagramPacket(
			arr, 0, arr.length,
			new InetSocketAddress( site, port ) ) );
	}
	
	public void sendAudio( byte arr[],
			InetSocketAddress addr ) throws IOException {
		sendVoicePacket( new DatagramPacket( 
			arr, 0, arr.length, addr ) );
	}
	
	public void sendData( byte arr[], 
			InetSocketAddress addr ) throws IOException {
		sendDataPacket( new DatagramPacket(
			arr, 0, arr.length, addr ) );
	}
	
	public void sendChatData( byte arr[],
			InetSocketAddress addr ) throws IOException {
		sendDataPacket( new DatagramPacket(
			arr, 0, arr.length, addr ) );
	}
	
	public void sendVoicePacket(
			DatagramPacket pk ) throws IOException {
		if( !setup )
			setup();
		log.finer(dg5198+": sending "+
			pk.getLength()+" to "+pk.getSocketAddress() );
		if( log.isLoggable(Level.FINEST) )
			dumpPacket(pk,Level.FINEST);
		dg5198.send( pk );
	}
	
	public void sendDataPacket( 
			DatagramPacket pk ) throws IOException {
		if( !setup )
			setup();
		log.finer(dg5199+": sending "+pk.getLength()+
			" to "+pk.getSocketAddress() );
		if( log.isLoggable(Level.FINEST) )
			dumpPacket(pk,Level.FINEST);
		dg5199.send( pk );
	}
	
	static void dumpPacket( DatagramPacket pk, Level lev ) {
		log.log( lev, "Packet Dump", new Throwable() );
		byte[]a = pk.getData();
		dumpPacket( a, a.length, lev );
	}
	static void dumpPacket( byte[]a, int len, Level lev ) {
		String h = "0123456789abcdef";
		String str = a.length+": ";
		for( int i = 0; i < len; ++i ) {
			if( a[i] <= ' ' ) {
				int v1 = (a[i]&0xf0)>>4;
				int v2 = a[i]&0xf;
				str += h.charAt(v1)+""+h.charAt(v2)+" ";
			} else {
				str += ((char)a[i])+" ";
			}
		}
		log.log( lev, str );
	}
}