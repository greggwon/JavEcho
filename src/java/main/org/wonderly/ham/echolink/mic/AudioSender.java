/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wonderly.ham.echolink.mic;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.wonderly.ham.echolink.audio.AudioEventHandler;
import org.wonderly.ham.echolink.*;
/**
 *
 * @author gregg
 */

/**
 *  This class provides the echolink audio sink.  This
 *  class reads from 'outqueue', buffers of GSM compressed
 *  audio.  
 */
public class AudioSender implements Runnable {
	private final Logger log = Logger.getLogger("org.wonderly.ham.echolink.audio");
	volatile boolean done;
	final Queue outqueue;
	final Parameters prm;
	final ConnectionManager mgr;
	private final AudioEventHandler aeh;
	private volatile boolean haveNet;
	private final Javecho je;

	public void setNetActive( boolean how ) {
		if( haveNet != how ) {
			haveNet = how;
			log.finer("set setNetActive("+haveNet+")" );	
		}
	}

	public AudioSender( Queue queue, Javecho je,
			ConnectionManager mgr, Parameters parms ) {
		outqueue = queue;
		prm = parms;
		this.mgr = mgr;
		this.je = je;
		aeh = mgr;
	}

	public void setDone(boolean how) {
		done = how;
	}

	public void run() {
		log.fine("MicIO AudioSender running: done="+done);
		try {
			doProcessing();
		} catch( Exception ex ) {
			log.log(Level.SEVERE,ex.toString(),ex);
		}
	}

	private void doProcessing() {
		boolean init = true;
		while( !done ) {
			aeh.setCurrentSendBuffering( Math.min(
				outqueue.queueSize(), prm.getNetBuffering() ) );
			log.finest("AudioSender check queue: "+outqueue.queueSize() );
			while( outqueue.queueSize() == 0 || 
					( init && 
					outqueue.queueSize() < 
						prm.getNetBuffering() ) ) {
				aeh.setCurrentSendBuffering( Math.min(
					outqueue.queueSize(), prm.getNetBuffering() ) );
				log.finest("AudioSender wait "+
					"outqueue: "+outqueue.queueSize());
				synchronized( outqueue ) {
					try {
						outqueue.wait(10000);
					} catch( Exception ex ) {
					}
				}
			}
			init = false;
			byte[]edata = outqueue.pop();

			log.finest( "AudioSender, next buffer: " + (edata != null ? edata.length : 0 ));
			if( outqueue.queueSize() == 0 ) {
				log.finest("AudioSender last buffer, init again");
				init = true;
			}
			if( edata == null ) {
				init = true;
				continue;
			}

			try {
				// ConnectionManager knows who we are
				// connected to, and who is currently
				// transmitting so that it sends the
				// audio stream to the correct stations.
				if( haveNet ) {
					log.finer("Data event for "+
						"voice packet, haveNet: "+haveNet );
					mgr.sendPacket( edata, mgr.VOICE_TYPE );
					LinkEvent le = new LinkEvent( edata,
						true, LinkEvent.LinkMode.MICDATA_EVENT, edata.length );
					je.sendEvent( le );
				} else {
					log.finest("Drop data event for "+
						"voice packet, haveNet: "+haveNet );
				}
			} catch( IOException ex ) {
				log.log(Level.SEVERE, ex.toString(), ex);
			}
			Thread.yield();
		}
		log.fine("Audio Sender completed");
		aeh.setCurrentSendBuffering( 0 );
	}
}
