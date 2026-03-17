package org.wonderly.ham.echolink;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wonderly.ham.echolink.LinkEvent.LinkMode;

import java.util.logging.*;

import java.text.*;

/**
 *  This class encompasses a Connection to another remote station.
 */
public class Connection extends TimerTask implements MulticastServer,TimedConnectionHost {
	private final InetSocketAddress statusAddr;
	private final InetSocketAddress voiceAddr;
	private final Javecho je;
	private final String addr;
	private final Parameters pr;
	private final RTPacket rtpk;
	private final RTPacket byepk;
	private int sdesLen;
	private final int byeLen;
	private byte[]sdesPacket;
	private final byte[]byePacket;
	private boolean heard;
	private int heardCnt = 0;
	private long accIdle;
	private final String name;
	private final ConnectionManager mgr;
	private final int timeIntv = 8000;
	private boolean connPend;
	private final int data;
	private final int ctrl;
	private boolean trans;
	private boolean discPend;
	private MulticastSocket multi;
	private static final Logger log = Logger.getLogger( Connection.class.getName() );

	@Override
	public void setMulticastSocket( MulticastSocket sock ) {
		multi = sock;
	}

	@Override
	public boolean disconnected() {
		return discPend;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return addr;
	}
	
	public boolean isConnPending() {
		return connPend;
	}
	public boolean isDiscPending() {
		return discPend;
	}
	public void setConnPending( boolean how ) {
		connPend = how;
	}
	public void setDiscPending( boolean how ) {
		discPend = how;
	}
	public boolean isTrans() {
		return trans;
	}
	public void setTrans( boolean how ) {
		trans = how;
	}
	@Override
	public String getAddr() {
		return addr;
	}
	@Override
	public String toString() {
		return name+" ("+addr+")";
	}

	@Override
	public void setHeard( boolean how ) {
		heard = how;
		accIdle = 0;
		++heardCnt;
		connPend = heardCnt < 1;
	}
	public int heardCount() {
		return heardCnt;
	}
	public Connection( String name, String addr, Javecho je,
			ConnectionManager mgr, int data, int ctrl ) throws IOException {
		this.addr = addr;
		this.je = je;
		pr = je.getParms();
		this.name = name;
		this.mgr = mgr;
		this.data = data;
		this.ctrl = ctrl;
//			progress("Using: "+data+","+ctrl+" as ports for "+name+" @ "+addr );
		statusAddr = new InetSocketAddress( addr, ctrl );
		voiceAddr = new InetSocketAddress( addr, data );
		rtpk = new RTPacket( pr.getCallSign(), pr.getUserName() );
		rtpk.setHomepageURL( pr.getHomepageURL() );
		if( pr.isShowCamera() )
			rtpk.setCameraURL( pr.getCameraURL() );
		byepk = new RTPacket( pr.getCallSign(), pr.getUserName() );
		SimpleDateFormat fmt = new SimpleDateFormat("MMMyyyy");
		byeLen = byepk.rtp_make_bye( 0, fmt.format( new Date() ), true );
		String web = je.currentWebPage();
		if( pr.isSendingCurrentPage() == false ||
			( pr.isSendingCurrentPage() == true &&
				!mgr.havePTT() ) ) {
			web = null;
		}
		buildSdes(web);
		byePacket = byepk.getPacketData();
		mgr.timer.schedule( this, timeIntv, timeIntv );
	}
	public void buildSdes(String page) {
		sdesLen = rtpk.make_sdes( addr, true, "J"+Javecho.VERSION, page, "CALLSIGN" );
		sdesPacket = rtpk.getPacketData();
	}
	public void sendChat( byte[]arr ) throws IOException {
		mgr.ep.sendChatData( arr, voiceAddr );
		LinkEvent<Number> le = new LinkEvent<>( arr, true, LinkMode.INFO_EVENT, mgr.seq );
		je.sendEvent( le );
	}
	public void sendInfo( byte[]arr ) throws IOException {
		mgr.ep.sendData( arr, voiceAddr );
		LinkEvent<Number> le = new LinkEvent<>( arr, true, LinkMode.INFO_EVENT, mgr.seq );
		je.sendEvent( le );
	}
	void sendConnData() throws IOException {
//			new Throwable("Send connData").printStackTrace();
		mgr.ep.sendData( sdesPacket, statusAddr );
		LinkEvent<Number> le = new LinkEvent<>( sdesPacket, true, LinkMode.CONN_EVENT, mgr.seq );
		je.sendEvent( le );
	}
	void sendByeData() throws IOException {
		mgr.ep.sendData( byePacket, statusAddr );
		LinkEvent<Number> le = new LinkEvent<>( addr, true, LinkMode.DISC_EVENT, mgr.seq );
		je.sendEvent( le );
	}
	void sendVoiceData( byte[]data ) throws IOException {
		mgr.ep.sendAudio( data, addr, this.data );
	}
	public void connect() throws IOException {
		connPend = true;
		mgr.progress( this+": connecting" );
		sendConnData();
	}
	public void disconnect() throws IOException {
		connPend = true;
		sendByeData();
		cancel();
		mgr.progress( this+": disconnected" );
	}
	@Override
	public void run() {
//			progress(addr+": timer entry (heard="+heard+"): "+heardCnt );
		if( !heard ) {
			try {
				accIdle += timeIntv;
//					progress( accIdle+" > "+(mgr.
//						prm.getInactiveTimeout()*1000)+" && "+
//						pr.getInactiveTimeout()+" > 0" );
				if( accIdle > (pr.getInactiveTimeout()*1000) && 
						pr.getInactiveTimeout() > 0 ) {
					mgr.progress("Shutting down due to inactivity timeout" );
					mgr.shutdownStream( this );
					cancel();
					return;
				}
			} catch(IOException | RuntimeException ex ) {
				log.log(Level.SEVERE, ex.toString(), ex);
			}
		}
		sendSdesPacket();
	}
	public void sendSdesPacket() {
		try {
			heard = false;
			sendConnData();
		} catch( IOException | RuntimeException ex ) {
			log.log(Level.SEVERE, "Cannot send Sdes packet: "+ex.toString(), ex);
			try {
				mgr.shutdownStream( this );
			} catch( IOException | RuntimeException  exx ) {
				log.log(Level.SEVERE, exx.toString(), exx);
			}
			cancel();
		}
	}
}