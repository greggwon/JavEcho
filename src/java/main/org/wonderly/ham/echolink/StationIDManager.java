package org.wonderly.ham.echolink;

import java.util.*;
import java.util.logging.*;
import java.io.*;

/**
 *  This class manages the operation of station identification.
 *  It listens to events in the system and based on parameters
 *  configured by the user, it will cause the appropriate action
 *  to identify the station as selected by the parameters.
 */
public class StationIDManager implements LinkEventListener {
	Parameters pr;
	Javecho je;
	Timer tm;
	long lastIdTime;
	long idInterval;
	Logger log = Logger.getLogger( getClass().getName() );

	public StationIDManager( Parameters p, Javecho jes ) {
		log.info("Creating ID Manager");
		pr = p;
		this.je = jes;
		je.addLinkEventListener( this );
		tm = new Timer();
		tm.schedule( new TimerTask() {
			public void run() {
				log.fine("Processing ID timer task, lastid: "+new Date(lastIdTime)+
					", interval: "+idInterval+", now: "+new Date()+", remain: "+
					(idInterval-(System.currentTimeMillis()-lastIdTime))+" millis");
				idInterval = pr.getIdentWhileactiveTime()*60000;
				if( lastIdTime + idInterval < System.currentTimeMillis() ) {
					try {
						log.fine("Time to ID, whileActive? "+pr.isIdentWhileactive()+
							", conn count: "+je.getConnectCount() );
						log.fine("whileInActive? "+pr.isIdentWhileinactive()+
							", conn count: "+je.getConnectCount() );
						if( pr.isIdentWhileactive() && je.getConnectCount() > 0 ) {	
							log.fine("send active ident out");
							sendIdent();
							lastIdTime = System.currentTimeMillis();
							log.fine("next ID: "+new Date(idInterval+lastIdTime));
						} else if( pr.isIdentWhileinactive() && je.getConnectCount() == 0 ) {	
							log.fine("send inactive ident out");
							sendIdent();
							lastIdTime = System.currentTimeMillis();
							log.fine("next ID: "+new Date(idInterval+lastIdTime));
						}
					} catch( Throwable ex ) {
						log.log(Level.SEVERE,ex.toString(),ex);
					}
				}
			}
		}, 10000, 10000 );
	}
	
	private void sendSignal( Signal sig ) {
		log.info("Should Send Signal: "+sig);
	}

	private void sendIdent() throws IOException {
		if( pr.isIdentMorseOn() ) {
			log.finer("Send Morse ident");
			je.sendMorse( pr.getPttControlParms(), pr.getIdentMorseId() );
		} else if( pr.isIdentSpeechOn() ) {
			log.finer("Send voice ident");
			je.transmitSpeech( pr.getPttControlParms(), pr.getIdentSpeechId() );
		} else if( pr.isIdentAudiofileOn() ) {
			log.finer("Send audio sampled ident");
			je.transmitAudio( pr.getPttControlParms(), pr.getIdentAudioFile() );
		}
	}

	public void processEvent( LinkEvent ev ) {
		try {
			doProcessEvent( ev );
		} catch( IOException ex ) {
			log.log(Level.SEVERE, ex.toString(), ex );
		}
	}
	
	private void doProcessEvent( LinkEvent ev ) throws IOException {
		// Listen for connection and disconnection events.
		if( ev.getType() != LinkEvent.NETDATA_EVENT &&
				ev.getType() != LinkEvent.MICDATA_EVENT ) {
			log.info("Handle LinkEvent: "+ev);
		}
		switch( ev.getType() ) {
			case LinkEvent.STATION_CONN_EVENT:
				if( pr.isIdentEachconnect() ) {
					log.fine("Announce each connect");
					sendIdent();
					break;
				}
				log.info("announce muting: "+pr.getOptionsAnnounceMuting() );
				if( pr.getOptionsAnnounceMuting() != 3 ) {
					// Announce first
					log.info("announce muting not all");
					if( pr.getOptionsAnnounceContacts() == 2 && ev.getValue() == 1 ) {
						log.info("Announce first connect");
						sendIdent();
					// Announce all
					} else if( pr.getOptionsAnnounceContacts() == 1 ) {
						log.info("Announce all contacts");
						sendIdent();
					}
					break;
				}
				break;
			case LinkEvent.STATION_DISC_EVENT:
				if( pr.isIdentEachconnect() ) {
					log.info("Announce on disconnect");
					sendIdent();
					break;
				}
//		if( ann != null && pr.isUserMode() == false ) {
//			switch( pr.getOptionsAnnounceDisconnects() ) {
//				case 0: break;
//				case 1:
//					raisePtt();
//					ann.disconnected( pr, sd.getCall() );
//					break;
//				case 2:
//					if( ssa.getConnectCount() == 0 ) {
//						raisePtt();
//						ann.disconnected( pr, sd.getCall() );
//					}
//					break;
//			}
//		}				break;
		}
	}
}