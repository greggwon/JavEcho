package org.wonderly.ham.echolink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Line;

import org.wonderly.ham.echolink.LinkEvent.LinkMode;
import org.wonderly.ham.echolink.audio.AudioEventHandler;

/**
 *  This class manages the connection to/from other stations.
 *  the RTP/RTCP traffic controls the state of operations of
 *  the station.  The Javecho class manages all the UI activies
 *  based on calls from this class back into that object.  The
 *  GUI components that control transmit/receive and 
 *  connect/disconnect result in calls into this class.
 *  @see Javecho
 */
public class ConnectionManager implements AudioEventHandler {        
	Javecho je;
	private Logger log = Logger.getLogger( "org.wonderly.ham.echolink.conn" );
//	private TargetDataLine micline;
	private Parameters prm;
	private Vector<Connection> conns = new Vector<Connection>();
//	private  static final int VOICE_PORT = 5198;
//	private  static final int STATUS_PORT = 5199;
	public static final int VOICE_TYPE = 1;
	public static final int CHAT_TYPE = 2;
	public static final int INFO_TYPE = 3;
	volatile boolean vox;
	volatile int seq = 0;
	EventPackets ep;
	Timer timer = new Timer();
	private Vector<AudioEventListener> audLis = new Vector<AudioEventListener>();
	
	public String toString() {
		return "ConnectionManager";
	}
	
	public Connection getConnectionHost( String addr ) {
//		progress("look for connection to: \""+addr+"\"");
		for( int i = 0; i < conns.size(); ++i ) {
			Connection c = (Connection)conns.elementAt(i); 
//			progress("c["+i+"]: "+c+", addr: \""+c.addr+"\"" );
			if( c.getAddress().equals(addr) )
				return c;
		}
		return null;
	}
	
	public void addAudioEventListener( AudioEventListener lis ) {
		audLis.addElement(lis);
	}
	
	private void startAudioListeners() {
		log.info("Starting "+audLis.size()+" audio listeners: "+getClass().getName());
		for( int i = 0; i < audLis.size(); ++i ) {
			AudioEventListener lis = audLis.elementAt(i);
			log.finer("startAudioListener: "+lis );
			lis.setSoundTotal( prm.getNetBuffering() );
			lis.setSoundCurrent( 5 );
			lis.setNetTotal( prm.getNetBuffering() );
			lis.setNetCurrent( 0 );
			lis.setSendTotal( prm.getPCBuffering() );
			lis.setSendCurrent( 0 );
		}
	}

	public void setCurrentRecvBuffering( int val ) {
		log.fine("setCurrentRecvBuffering("+val+")");
		for( int i = 0; i < audLis.size(); ++i ) {
			AudioEventListener lis = audLis.elementAt(i);
			lis.setNetCurrent( val );
		}
	}

	public void setCurrentSendBuffering( int val ) {
		log.fine("setCurrentSendBuffering("+val+")");
		for( int i = 0; i < audLis.size(); ++i ) {
			AudioEventListener lis = audLis.elementAt(i);
			lis.setSendCurrent( val );
		}
	}

	public void setCurrentAudioBuffering( int val ) {
		log.fine("setCurrentAudioBuffering("+val+")");
		for( int i = 0; i < audLis.size(); ++i ) {
			AudioEventListener lis = audLis.elementAt(i);
			lis.setSoundCurrent( val );
		}
	}

	public boolean isConnectedTo( String addr ) {
		Connection c = getConnectionHost( addr );
//		new Throwable( "isconn("+(c != null ? c.heardCnt : -1)+
//			"): "+(c!= null ? c.connPend: false) ).printStackTrace();
		boolean isconn = c != null && c.isConnPending() == false;
		log.fine("isConnectedTo "+addr+"? "+isconn);
		return isconn;
	}
	
	public void heardFrom( String addr ) {
		Connection c = getConnectionHost( addr );
		log.fine("update HeardFrom "+addr );
		if( c != null ) {
//			progress("heard from: "+addr );
			c.setHeard(true);
		} else {
			progress("Can't set heard for: "+addr );
		}
	}
	
	/**
	 *  Send new SDES packet with updated web page
	 */
	public void sendSdesWithWebPage( String url ) {
		log.fine("SendSdesWithWebPage(\""+url+"\")");
		for( int i = 0; i < conns.size(); ++i ) {
			Connection c = (Connection)conns.elementAt(i);
			c.buildSdes(url);
			c.sendSdesPacket();
		}
	}
	
	boolean havePTT() {
		log.fine("havePTT? "+je.getTxMode() );
		return je.getTxMode();
	}

	public void setTrans( String addr, boolean how ) {
		log.fine("setTrans: "+addr+"? "+how );
		Connection c = getConnectionHost( addr );
		if( c != null )
			c.setTrans(how);
		sendInfoToAll( je.getCurrentInfo() );
	}

	public void sendInfoToAll( String info ) {
		log.fine("Send info to all: \""+info+"\"");
		for( int i = 0; i < conns.size(); ++i ) {
			Connection c = (Connection)conns.elementAt(i);
			try {
				log.info("Sending info: "+info+" to station: "+c);
				c.sendInfo(("oNDATACONF"+info).getBytes() );
			} catch( Exception ex ) {
				log.log(Level.SEVERE, ex.toString(), ex);
			}
		}
	}

	public static void main( String args[] ) throws Exception {
		Parameters pr = new Parameters( null );
		ConnectionManager ssa = new ConnectionManager( null, null, pr );
		ssa.startMic(false);
	}

	public ConnectionManager( Javecho je, EventPackets ep,
			Parameters params ) {
		this.je = je;
		this.ep = ep;
		this.prm = params;
//		log.setLevel( Level.FINEST );
	}
	
	public void setEventPacketsInstance( EventPackets ep ) {
		this.ep = ep;
	}

//	void setup() {
//		mic = new MicIO();
//	}
	
	public boolean disconnectFrom( TimedConnectionHost c
				) throws IOException {
		log.fine("disconnectFrom (timedConnectionHost): "+
			c.getAddr() );
		return disconnectFrom(c.getAddr());
	}
	
	public boolean disconnectFrom( String addr ) throws IOException {
		log.fine("disconnect from: "+addr );
		for( int i = 0; i < conns.size(); ++i ) {
			Connection c = (Connection)conns.elementAt(i);
			if( c.getAddress().equals(addr) ) {
				progress("disconnecting from: "+addr );
				conns.removeElement(c);
				progress("shutting down connection");
				c.disconnect();
				c.setConnPending( true );
				c.setDiscPending( true );
				conf = false;
				progress("disconnecting in UI elements");
				je.disconnectFrom(addr);
				progress("checking disconnects");
				je.checkConnections();
				LinkEvent<Number> le = new LinkEvent<Number>(
					addr, true, LinkMode.DISC_EVENT, seq );
				je.sendEvent( le );
				if( je.getConnectionStats() != null ) {
					je.getConnectionStats().removeConnectedStation(
						StationData.stationForAddress( addr ) );
				}
				if( prm.isUserMode() == false )
					mic.setNetConnected( conns.size() > 0 );
				return true;
			}
			je.checkConnections();
		}
		return false;
	}
	
	public String getConferenceName() {
		log.fine("getConferenceName() "+
			((conns.size() > 0 ) ?
			((Connection)conns.elementAt(0)).getName() :
			"<none>") );  
		if( conns.size() > 0 )
			return ((Connection)conns.elementAt(0)).getName();
		throw new IllegalStateException("No connections active");
	}

	public boolean disconnectAll( ) throws IOException {
		log.fine("Disconnect from all: cnt="+conns.size());
		while( conns.size() > 0 ) {
			Connection c = (Connection)conns.elementAt(0);
			try {
				disconnectFrom( c.getAddress() );
			} catch( Exception ex ) {
				log.log(Level.SEVERE, ex.toString(), ex);
				// If it did not get removed, remove it
				conns.removeElement(c);
			}   
		}
		if( prm.isUserMode() == false && mic != null )
			mic.setNetConnected( conns.size() > 0 );
		conf = false;
		return true;
	}
	
	private boolean conf;
	public boolean inConference() {
		return conf;
	}
	
	public void connectTo( String name, String addr, String info, 
			boolean isConference, int data,
				int ctrl, boolean connectFrom ) throws IOException {
		log.fine( "ConnectTo: "+name+", "+addr+", "+info+", isConf="+isConference+ 
			", dataPort="+data+", ctrlPort="+ctrl+", connFrom? "+connectFrom );
		if( isConnectedTo(addr) ) {
			log.fine("Already connected to: "+addr );
			return;
		}
		conf = conns.size() == 0 ? false : conf;
		if( conf && !prm.isAllowMulti() ) {
			log.fine( "Already in conference for connect to: "+name );
			if( connectFrom ) {
				new ConnectionNotPossibleException(
					"In Conference, can't connect with "+name ).printStackTrace();
				return;
			} else {
				throw new ConnectionNotPossibleException(
					"In Conference, can't connect to "+name );
			}
		}
		if( ( !prm.isAllowConferences() && conns.size() > 0 ) ||
			( prm.isAllowConferences() && 
				conns.size() >= prm.getConferenceCount() ) ) {
			log.fine( "Already at conference limit ("+
				( prm.isAllowConferences() && 
				conns.size() >= prm.getConferenceCount() )+
				", or no conferences allowed: "+
				( !prm.isAllowConferences() && conns.size() > 0 ));
			if( connectFrom ) {
				new ConnectionNotPossibleException(
					"Can not add connection  ("+
						(prm.isAllowConferences() ? 
							prm.getConferenceCount()+"" :
							"1")+
						" allowed) from "+name+" ("+addr+") "
					).printStackTrace();		
			} else {
				throw new ConnectionNotPossibleException(
					"Can not add connection ("+
						(prm.isAllowConferences() ? 
							prm.getConferenceCount()+"" :
							"1")+
						" allowed) to "+name+" ("+addr+") "
					);
			}
		}
			
		conf = isConference;
		log.info( (connectFrom ?
			"Accept connect from: " :
			"Connecting to: "
			)+name);
		Connection c = new Connection(name,addr,je,this,data,ctrl);
		initStream(c);
		if( je.getConnectionStats() != null ) {
			je.getConnectionStats().addConnectedStation(
				StationData.stationForAddress( addr ) );
		}
		if( prm.isUserMode() == false ) {
			if( mic == null )
				throw new NullPointerException("MIC is not initialized yet!");
			mic.setNetConnected( conns.size() > 0 );
		}

		log.info("Sending initial connection with: "+info+" to connection: "+c);
		c.sendInfo(("oNDATACONF"+info).getBytes() );
	}

	public List<Connection> getConnectedList() {
		List<Connection> v = new ArrayList<Connection>();
		log.finer("getConnectedList: "+conns );
		for( int i = 0; i < conns.size(); ++i ) {
			Connection c = conns.elementAt(i);
			v.add( c );
		}
		return v;
	}

	public boolean isConnected() {
		log.fine("isConnected ? "+
			(getConnectCount() > 0));
		return getConnectCount() > 0;
	}

	public int getConnectCount() {
		log.fine("connect count ? "+conns.size());
		return conns.size();
	}

	public void transmit() {
//			je.setTxMode(true);
		log.info("transmit: startMic(false)");
		startMic(vox=false);
	}

	public void voxReceive() {
		log.fine("start voxReceive mode");
		vox = true;
//		receive();
	}
	
	public void voxTransmit() {
		log.fine("transmit: startMic(true)");
		startMic(vox=true);
	}

	public void receive() {
		log.fine("receive: isVox: "+
			(mic == null ? false : mic.isVox()) );
		if( mic != null ) {
			if( mic.isVox() )
				voxReceive();
			else
				mic.close();
		}
	}

	private void initStream( Connection c ) throws IOException {
		log.fine("initStream: connection: "+c);
		c.connect();
		if( conns.contains(c) == false )
			conns.addElement(c);
	}

	void shutdownStream( Connection c ) throws IOException {
		log.fine("shutdownStream: connection: "+c);
//		new Throwable("shutdownStream: "+c).printStackTrace();
		progress( "Shutting down connection: " + c );
		conns.removeElement(c);
		c.disconnect();
		if( conns.size() == 0 )
			receive();
	}
 
	private MicIO mic;
    void shutDown(Throwable message) {
    	log.info("ShutDown mic with message: "+message );
        if( message != null) {
        	message.printStackTrace();
        }
        if( mic != null ) {
        	receive();
        }
    }

	private void startMic(boolean vox) {
		log.info( "startMic: vox=" + vox );
		startAudioListeners();
		if( mic == null ) {
			progress( "Create new MicIO()");
			mic = new MicIO(vox, this, prm);
		}
		progress("Start MIC reading thread");
		new Thread( mic, "Source Sampler" ).start();
	}

	void progress( String str ) {
		if(prm.isAudioTrace())
			je.msg(str);
		log.finer( str );
	}

	String dumpFormat( AudioFormat f, Line.Info inf ) {
		return f.toString()+" == "+inf.toString();
	}
	
	public void sendPacket( byte data[], int type ) throws IOException {
		log.fine("sendPacket: "+data.length+" bytes as "+
			(type == VOICE_TYPE ? "voice" :
				type == CHAT_TYPE ? "chat" :
					"info") );
   		for( int i = 0; i < conns.size(); ++i ) {
   			Connection c = (Connection)conns.elementAt(i);
   			try {
   				switch( type ){
   					case VOICE_TYPE:
   						c.sendVoiceData( data );
   						break;
   					case CHAT_TYPE:
   						c.sendChat( data );
   						break;
   					case INFO_TYPE:
   						c.sendInfo( data );
   						break;
   				}
   			} catch( IOException ex ) {
   				log.log(Level.SEVERE, ex.toString(), ex);
   				shutdownStream( c );
   				--i;  // removeElement will point us one ahead.
   			}
        }
        if( conns.size() > 1 ) {
			LinkEvent<Number> le = new LinkEvent<Number>( data, true, LinkMode.MICDATA_EVENT, seq );
			je.sendEvent( le );
        }
	}
	
	public void sendPacket( byte data[], int type, String except ) throws IOException {
		log.fine("sendPacket: ("+except+") "+data.length+" bytes as "+
			(type == VOICE_TYPE ? "voice" :
				type == CHAT_TYPE ? "chat" :
					"info") );
   		for( int i = 0; i < conns.size(); ++i ) {
   			Connection c = (Connection)conns.elementAt(i);
   			if( c.getAddress().equals(except) )
   				continue;
   			try {
   				switch( type ){
   					case VOICE_TYPE:
   						c.sendVoiceData( data );
   						break;
   					case CHAT_TYPE:
   						c.sendChat( data );
   						break;
   					case INFO_TYPE:
   						c.sendInfo( data );
   						break;
   				}
   			} catch( IOException ex ) {
   				log.log(Level.SEVERE, ex.toString(), ex);
   				shutdownStream( c );
   				--i;  // removeElement will point us one ahead.
   			}
			LinkEvent<Number> le = new LinkEvent<Number>( data, true, LinkMode.MICDATA_EVENT, seq );
			je.sendEvent( le );
        }
	}
	
//	boolean rxActive;
	
	String zeroTermText( byte arr[], int off ) {
		for( int i = off; i < arr.length; ++i ) {
			if( arr[i] == 0 )
				return new String(arr, off, i-off);
		}
		return new String(arr,off, arr.length-off);
	}
	
	void playAudio( byte[]data, int len ) {
	}

	boolean rxd;
	Object rxlock = new Object();
	
	/**
	 *  Signal that we are now receiving data from the
	 *  passed address.  This method will initiate all
	 *  the state changes to configure the audio paths etc.
	 *  @return true if we accepted the connection, false if not
	 */
	boolean startRxListener(final String addr) {
		log.fine("StartRxListener: "+addr );
		
		// Configure the UI (should be done in a LinkEventListener)
		if( je.getConnectionStats() != null ) {
			je.getConnectionStats().setTransmittingStation( 
				StationData.stationForAddress( addr ) );
		}
		
		// Should also be done in a LinkEventListener
		je.setMode(LinkMode.MODE_RECEIVE);
		
		// If we are receiving then don't accept this other
		// data path.
		synchronized( this ) {
			if( je.rxActive == true )
				return false;
			je.rxActive = true;
		}
		
		/** Get a queue for data */
		buffers = new Queue();
		
		/** Start listeners that need to see datapackets flowing */
		startAudioListeners();

		// Create the timeout thread for receive data timeouts
		new Thread( new Runnable() {
			public void run() {
				TimerTask rcv = null;
				
				// If there is a receive limit, create a timer for that
				if( prm.getReceiveTimeLimit() > 0 ) {
					timer.schedule( rcv = new TimerTask() {
						public void run() {
							progress("Receive Time Limit exceeded");
							try {
								disconnectFrom(addr);
							} catch( Exception ex ) {
								je.reportException(ex);
							}
						}
					}, prm.getReceiveTimeLimit() * 1000 );
				}

				// Create the thread that will play the buffered data
				new Thread( pl = new Player( buffers, addr, 
					ConnectionManager.this, prm, vox ) ).start();
					
				// Now handle the delay between end of data and PTT unkey
				rxd = true;
				progress("ReceiveHangTime: "+prm.getReceiveHangTimeout());
				
				// Each time data is received, rxd will be set to 
				// true.  Thus, we will wake up with it true if data
				// is still being received.  If data is not being
				// received, then after recvHandTime, we will wakeup
				// with rxd set to false and leave the loop here.
				while(rxd) {
					synchronized(rxlock) {
						rxd = false;
						try {
							int time = prm.getReceiveHangTimeout();
							if( time == 0 )
								time = 1200;
							rxlock.wait(time);
						} catch(Exception ex) {
							log.log(Level.SEVERE, ex.toString(), ex);
						}
					}
				}
				
				// No more data...
				progress("rxd true, cancelling receive");
				if( rcv != null )
					rcv.cancel();
				pl.stop();

				// In sysop mode, open PTT
				if( prm.isUserMode() == false ) {
					LinkEvent<Number> le = new LinkEvent<Number>( this, prm.isUserMode(),
							LinkMode.MODE_SYSOPIDLE, -1, null );
					je.sendEvent( le );
				}
			}
		}).start();
		Thread.yield();
		return true;
	}
	
	AudioEntry head;
	AudioEntry tail;
	Player pl;

	int lastseq;
	Queue buffers;
	byte[]lastbuf;
	de.tu_berlin.GSMDecoder g = new de.tu_berlin.GSMDecoder();

	public void handleData( String addr, byte[]arr, int sz ) {
		progress("handling data from: "+addr+", size="+sz );
		heardFrom( addr );
//		Javecho.dumpPacket("data?", arr, sz, 400 ) ;
		if( (arr[0]&0xff) != 0xc0 ) {
			if( arr[0] == 'o' && arr[1] == 'N' ) {
				String str = new String(arr,0,6);
				String tx = null;

				if( str.equals("oNDATA") ) {
					if( new String(arr,6,4).equals("CONF") ) {
						je.setInfo( tx = zeroTermText( arr, 10 ).replace("\r","\n") );
					} else if( arr[6] != '\r' ) {
						Javecho.dumpPacket( "chat text", arr, sz, 500 );
						je.addChatText( tx = zeroTermText( arr, 6 ).replace("\r","\n") );
					} else {
						je.setInfo( tx = zeroTermText( arr, 6 ).replace("\r","\n") );
//						je.dumpPacket( "data", arr, sz, 20 );
					}
				} else {
					Javecho.dumpPacket( "oN but not oNDATA data", arr, sz, 20 );
				}

				if( tx != null ) {
					LinkEvent<Number> le = new LinkEvent<Number>( tx, false, LinkMode.INFO_EVENT, -1 );
					je.sendEvent( le );
				}
			}
		} else {
			if( RTPacket.isRTCPByepacket(arr,sz) ) {
				progress( "Bye packet received" );
			} else if( RTPacket.isRTCPSdespacket(arr,sz) ) {
				progress( "SS/SR packet received" );
//		} else {
//			je.dumpPacket( "data", arr, sz, 20 );
			}

			byte gd[] = new byte[33];
			int off = sz - (((sz/33)/4) * 4 * 33);
			int seq = ((arr[2]&0xff)<<8) | (arr[3]&0xff);

			if( seq == 0 || Math.abs( lastseq - seq ) > 100 ) {
				lastseq = seq - 1;
			}

			if( je.isReceiving() == false ) {
				progress("Starting RX event listener");
				g = new de.tu_berlin.GSMDecoder();
				startRxListener(addr);
				progress("Listener started");
				setTrans( addr, true );
				lastseq = seq - 1;
			}

			if( lastseq >= seq ) {
				LinkEvent<Number> le = new LinkEvent<Number>( new Integer(seq), false, LinkMode.OUT_OF_SEQUENCE_DATA, seq );
				je.sendEvent( le );
				progress("out of sequence voice "+lastseq+" >= "+seq );
				return;
			}

			if( lastseq < seq-1 ) {
				LinkEvent<Number> le = new LinkEvent<Number>( new Integer(seq), false, LinkMode.MISSED_DATA, seq );
				je.sendEvent( le );
				progress("missed data "+lastseq+" < "+(seq-1) );
				lastseq = seq - 1;
			}

			// Notify of audio arriving..
			je.sendEvent( new LinkEvent<Number>( addr, false, LinkMode.NETDATA_EVENT, seq ) );

			final int fsz = 320;
			byte[]outBytes = new byte[fsz*4];
			int ooff = 0;
	
	 		byte[]gsmMsg = new byte[1+1+2+4+4+(33*4)];
			int ii = 0;
			gsmMsg[ii++] = (byte)0xc0;
			gsmMsg[ii++] = 3;
			gsmMsg[ii++] = (byte)((seq&0xff00)>>8);
			gsmMsg[ii++] = (byte)(seq&0xff);
			++seq;
			gsmMsg[ii++] = 0; gsmMsg[ii++] = 0; gsmMsg[ii++] = 0; gsmMsg[ii++] = 0;
			gsmMsg[ii++] = 0; gsmMsg[ii++] = 0; gsmMsg[ii++] = 0; gsmMsg[ii++] = 0;

			progress("reencoded data.length: "+arr.length);
       		System.arraycopy( arr, off, gsmMsg, ii, gsmMsg.length-ii );
       		try {
       			sendPacket( gsmMsg, VOICE_TYPE, addr );
       		} catch( IOException ex ) {
       			log.log(Level.SEVERE, ex.toString(), ex);
       		}

	       	for( int i = off; i < sz; i += 33 ) {
				System.arraycopy( arr, i, gd, 0, 33 );
				try {
					if( g == null )
						g = new de.tu_berlin.GSMDecoder();
					int va[] = g.decode(gd);				
					for(int j=0;j<va.length;j++) {
						int index = (j<<1) + ooff;
						outBytes[index] = (byte)(va[j]&0xff);
						outBytes[++index] = (byte)((va[j]&0xff00)>>8);
					}
				} catch( Exception ex ) {
	       			log.log(Level.SEVERE, ex.toString(), ex);
				}
				ooff += fsz;
			}

			synchronized( rxlock ) {
				rxd = true;
				rxlock.notifyAll();
			}

			buffers.enqueue( outBytes );
			lastseq++;
		}
	}

	static final AudioFormat playFormat = new AudioFormat(
		8000, 16, 1, true, false );
}