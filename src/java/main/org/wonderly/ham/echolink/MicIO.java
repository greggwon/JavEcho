package org.wonderly.ham.echolink;

import javax.sound.sampled.*;
import javax.sound.sampled.BooleanControl.Type;

import java.util.logging.*;
import java.util.*;
import gsm.encoder.*;
import java.io.*;

import org.wonderly.ham.echolink.LinkEvent.LinkMode;
import org.wonderly.ham.echolink.audio.*;

class MicIO implements Runnable {
	boolean done;
	TargetDataLine spkline;
	boolean cancelled;
	Queue outqueue = new Queue();
	boolean vox;
	ConnectionManager mgr;
	Parameters prm;
	AudioEventHandler aeh;
	Logger log = Logger.getLogger("org.wonderly.ham.echolink.mic");
	Logger proglog = Logger.getLogger("org.wonderly.ham.echolink.progress");
	
	public boolean isVox() {
		return vox;
	}
	private void progress( String str ) {
		proglog.finer(str);
	}
	private void progress2( String str ) {
		proglog.finest(str);
	}

	public MicIO( boolean voxon, ConnectionManager mgr, Parameters prm ) {
		log.log(Level.FINE, "Creating MicIO for: "+mgr,
			new Throwable("MicIO created") );		
		vox = voxon;
		this.mgr = mgr;
		this.prm = prm;
		aeh = mgr;
		format = new AudioFormat( 8000, 16, 1, true, true );
		info = new DataLine.Info( TargetDataLine.class, format );
		log.info("MicIO: format:"+format+", info: "+info );
		if( ! AudioSystem.isLineSupported( info ) ) {
			log.log(Level.SEVERE, "Format Not Supported: "+format,
				new InvalidSoundSystemConfigurationException(
				"Line matching \"" + info + 
				"\" not supported.") );
			format = new AudioFormat( 8000, 16, 1, true, false );
			info = new DataLine.Info( TargetDataLine.class, format );
			log.info("MicIO: try other format: ("+format.isBigEndian()+
				") "+format+", info: "+info );
			if( AudioSystem.isLineSupported( info ) ) {
				log.info( "Little Endian is available");
				log.warning( "Big Endian conversion required: "+format );
			} else {
				throw new InvalidSoundSystemConfigurationException(
					"No required MIC audio data "+
					"formats supported: \n\n"+format);
			}
		}
	}
	
	public void setMicGain( float percent ) {
		Port lineIn = null;
        FloatControl volCtrl = null;
        Mixer mixer = null;
        try
        {
			Mixer.Info infos[] = AudioSystem.getMixerInfo();
			Mixer.Info inf = infos[prm.getAudioDevice()];
			mixer = AudioSystem.getMixer(inf);
			final int maxLines = mixer.getMaxLines(Port.Info.MICROPHONE);
			if (maxLines > 0) {
				lineIn = (Port) mixer.getLine(Port.Info.MICROPHONE);
				lineIn.open();
				volCtrl = (FloatControl) lineIn.getControl(FloatControl.Type.VOLUME);
				volCtrl.setValue(percent);
			}
        } catch( Exception ex ) {
        	log.log(Level.SEVERE, ex.toString(), ex );
        }
	}
	
	public int calculateRMSLevel(byte[] audioData)
	{ 
	    long lSum = 0;
	    for(int i=0; i < audioData.length; i++)
	        lSum = lSum + audioData[i];

	    double dAvg = lSum / audioData.length;
	    double sumMeanSquare = 0d;

	    for(int j=0; j < audioData.length; j++)
	        sumMeanSquare += Math.pow(audioData[j] - dAvg, 2d);

	    double averageMeanSquare = sumMeanSquare / audioData.length;

	    return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
	}

	public void close() {
		done = true;
	}

	synchronized void stop() {
		log.info("stop: MicIO mode is "+(vox?"vox":"non-vox"));
		done = true;
		if( spkline == null || vox ) {
			return;
		}

		aSend.setDone(done);
		spkline.stop();
		spkline.flush();
		spkline.close();
		spkline = null;
		aeh.setCurrentSendBuffering( 0 );
	}

	public void setNetConnected( boolean how ) {
		if( aSend == null ) {
			log.log(Level.WARNING, "No Audio Sender Yet!",
				new IllegalStateException( "no audio sender") );
			return;
		}
		aSend.setNetActive( how );
	}

	private DataLine.Info info = null;
	public synchronized void setup() {
		log.info("setup MicIO");
		if( spkline != null ) {
			if( vox ) {
				log.fine("VOX mode, no init done");
				return;
			}
			IllegalStateException ie =
				new IllegalStateException("spkline is not null");
			log.throwing( "setup", ie.toString(), ie );
			throw ie;
		} else {
			log.info("No line yet, initializing");
		}

		// define the required attributes for our line, 
        // and make sure a compatible line is supported.
		format = new AudioFormat( 8000, 16, 1, true, true );
		info = new DataLine.Info( TargetDataLine.class, format );
		log.info("MicIO: new line format:"+format+", info: "+info );
		if( ! AudioSystem.isLineSupported( info ) ) {
			log.log(Level.SEVERE, "Format Not Supported: "+format,
				new InvalidSoundSystemConfigurationException(
				"Line matching \"" + info + 
				"\" not supported.") );
			format = new AudioFormat( 8000, 16, 1, true, false );
			info = new DataLine.Info( TargetDataLine.class, format );
			log.info("MicIO: try other format:"+format+", info: "+info );
			if( AudioSystem.isLineSupported( info ) ) {
				log.info( "Little Endian is available");
				log.warning( "Big Endian conversion required: "+format );
			} else {
				throw new InvalidSoundSystemConfigurationException(
					"No required MIC audio data formats supported");
			}
		}

		progress( "Mic Format = "+format );
        // get and open the target data line for capture.
		try {
			progress( "record: getLine()" );
			AudioSystem.isLineSupported( info );
			spkline = new LineWrapper( (TargetDataLine)
				AudioSystem.getLine(info) );
			if( prm.useSelectedAudio() ) {
				try {
			        Mixer.Info infos[] = AudioSystem.getMixerInfo();
			        Mixer.Info inf = infos[prm.getAudioDevice()];
			        Mixer mix = AudioSystem.getMixer( inf );
					final int maxLines = mix.getMaxLines(Port.Info.MICROPHONE);
					if (maxLines > 0) {
						Port lineIn = (Port) mix.getLine(Port.Info.MICROPHONE);
						FloatControl volCtrl = (FloatControl) lineIn.getControl(FloatControl.Type.VOLUME);
						mgr.je.setMicGainControl( volCtrl );
					}

			        if( mix.getTargetLineInfo( info ) != null ) {
			        	try {
				        	TargetDataLine dt = (TargetDataLine)
				        		AudioSystem.getLine( 
				        		mix.getTargetLineInfo( info )[0] );
				        	progress("overriding default "+
				        		"TargetDataLine "+spkline);
				        	progress("with: "+dt );
				        	spkline = new LineWrapper( dt );
			        	} catch( Exception ex ) {
							log.log(Level.SEVERE, ex.toString(), ex);
			        	}
			        } else {
			        	progress("No overriding TargetDataLine found" );
			        }
				} catch( Exception ex ) {
					log.log(Level.SEVERE, ex.toString(), ex);
				}
			}
			progress( "record: open("+mgr.dumpFormat(format,info)+")" );
			spkline.open( format, 1280 );
	        Mixer.Info infos[] = AudioSystem.getMixerInfo();
	        for( Mixer.Info mi : infos ) {
	        	Mixer mix = AudioSystem.getMixer(mi );
				final int maxLines = mix.getMaxLines(Port.Info.MICROPHONE);
				if (maxLines > 0) {
					Port lineIn = (Port) mix.getLine(Port.Info.MICROPHONE);
					for( Control c : lineIn.getControls() ) {
						log.info("Found mic control: "+c);
						if( c instanceof FloatControl ) {
							mgr.je.setMicGainControl( (FloatControl)c );
						}
					}
//					FloatControl volCtrl = (FloatControl) lineIn.getControl(FloatControl.Type.MASTER_GAIN);
//					mgr.je.setMicGainControl( volCtrl );
				}
	        }
			
			progress( "record: start()" );			
			spkline.start();
		} catch (LineUnavailableException ex) { 
			InvalidSoundSystemConfigurationException exx =
				new InvalidSoundSystemConfigurationException(
					"Unable to open the line: "+ex);
			exx.initCause(ex);
			throw exx;
		} catch (SecurityException ex) { 
			mgr.shutDown(ex);
			return;
		} catch (Exception ex) { 
			mgr.shutDown(ex);
			return;
		}
	}

	private TimerTask tsk = null;
	private long ptttime;
	private LinkEventListener lel;
	private boolean pttActive;
	
	/**
	 *  Setup the PTT timeout timer based on how the station
	 *  is currently configured.  In Sysop-VOX mode, we use
	 *  a timer that is reset as we receive audio events.
	 */
	private void setPttTimer() {
		log.fine("setPttTimer, userMode: "+prm.isUserMode());
		if( prm.isUserMode() ) {
			pttActive = true;
			aSend.setNetActive( pttActive );
			log.fine("Starting PTT timer task: "+
				prm.getPTTTimeout()+" secs" );
			mgr.timer.schedule( tsk = new TimerTask() {
				public void run() {
					log.fine("PTT cancelled");
					progress("PTT timeout!");
					cancelled = true;
					done = true;
					tsk = null;
				}
			}, prm.getPTTTimeout()*1000 );
		} else {
			pttActive = false;
			aSend.setNetActive( pttActive );
			if (lel == null ) {
				lel = registerPttVoxListener();
				mgr.je.addLinkEventListener( lel );
			} else {
				log.info( "setPttActive: already active no listener added");
			}
		}
	}

	private LinkEventListener registerPttVoxListener() {
		final LinkEventListener le = new LinkEventListener() {
			public void processEvent( LinkEvent ev ) {
				if( ev.getType() != LinkMode.MICDATA_EVENT ) {
					if( log.isLoggable( Level.FINEST ) ) {
						log.log(Level.FINEST, "vox Link Event: "+
							ev, new Throwable("LinkEvent: "+ev));
					}
				}
				if( ev.getType() == LinkMode.VOX_OPEN_EVENT ) {
					ptttime = System.currentTimeMillis();
					pttActive = true;
					if( prm.getPTTTimeout() > 0 && tsk == null ) {
						log.fine("Vox Open: Starting PTT timer task: "+
							prm.getPTTTimeout()+" secs" );
						mgr.timer.schedule( tsk = new TimerTask() {
							public void run() {
								progress("PTT timeout!");
								cancelled = true;
								done = true;
								tsk = null;
							}
						}, prm.getPTTTimeout()*1000 );
					}
				} else if( ev.getType() == LinkMode.VOX_CLOSE_EVENT ) {
					long now = System.currentTimeMillis();
					if( tsk != null && now-ptttime > 
							(prm.getMinPttDownTime()*1000) ) {
						log.fine("Vox closed Stopping PTT timer task" );
						pttActive = false;
						tsk.cancel();
						tsk = null;
						lel = null;
						mgr.je.removeLinkEventListener( this );
					}
				} else if( ev.getType() != LinkMode.MICDATA_EVENT ) {
					if( log.isLoggable( Level.FINEST ) )
						log.finest("setPttTimer Link Event: "+ev );
				}
//				aSend.setNetActive( pttActive );
			}
		};
		return le;
	}
				
	private AudioSender aSend;
	public void run() {
		log.info("MicIO running");
		setup();

		// create the audio sender that forwards audio
		// data from the microphone jack out to the
		// network as RTP/RTCP audio packets.
		aSend = new AudioSender( outqueue, mgr, prm );
		new Thread( aSend, "Audio Sender" ).start();

		cancelled = false;

		// Setup the PTT timeout timer based on the
		// current mode of the station.  This timer will
		// cause a timeout to result in the output stream
		// being stopped.
		setPttTimer();

		done = false;
		try {
			while( !done ) {
				try {
					doOutputStream();
				} catch( Throwable ex ) {
					log.log(Level.SEVERE, ex.toString(), ex);
				}
			}
		} finally {
			log.fine("MicIO, done="+done+", stopping");
			aSend.setDone(true);
			// Stop timer for transmit timeout
			if( tsk != null && !cancelled )
				tsk.cancel();
			log.fine("Removing Vox PTT LinkEventListener");
			mgr.je.removeLinkEventListener( lel );
			stop();
		}
	}

	/**
	 *  This class provides the echolink audio sink.  This
	 *  class reads from 'outqueue', buffers of GSM compressed
	 *  audio.  
	 */
	private static class AudioSender implements Runnable {
		Logger log = Logger.getLogger("org.wonderly.ham.echolink.audio");
		boolean done;
		Queue outqueue;
		Parameters prm;
		ConnectionManager mgr;
		private AudioEventHandler aeh;
		private boolean haveNet;
		
		public void setNetActive( boolean how ) {
			if( haveNet != how ) {
				haveNet = how;
				log.finer("set setNetActive("+haveNet+")" );	
			}
		}

		public AudioSender( Queue queue,
				ConnectionManager mgr, Parameters parms ) {
			outqueue = queue;
			prm = parms;
			this.mgr = mgr;
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
							true, LinkMode.MICDATA_EVENT, edata.length );
						mgr.je.sendEvent( le );
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

	private AudioFormat format;
	/**
	 *  This method reads from the microphone line and 
	 *  then queues the data to outqueue for processing
	 *  by AudioSender.
	 */
	private void doOutputStream() {
		progress("doOutputStream()");
		OutputStream out = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        // Should be 2 for 16 bit mono-sound
        final int frameSizeInBytes = format.getFrameSize();
        // Should be 640 for 1280/2
        final int bufferLengthInFrames = 1280/frameSizeInBytes;
        // Should be 1280.
        final int bufferLengthInBytes = 
        	bufferLengthInFrames * frameSizeInBytes;
        final byte[] data = new byte[bufferLengthInBytes];
        final byte[] zdata = new byte[bufferLengthInBytes];
        final int segLen = bufferLengthInBytes/(frameSizeInBytes);
        boolean voxState = false;
        int numBytesRead;
        long totalSize = 0;
      	progress("frame["+bufferLengthInFrames+"]: "+frameSizeInBytes+
        	", bytes: "+bufferLengthInBytes );
        ByteArrayOutputStream outdata = null;
        // While not done...
       	mgr.seq = 0;
       	done = false;
    	final Encoder encoder = new Encoder();
       	final double audAmp = prm.getAudioAmplification();
       	final byte[]zeros = new byte[8];
       	long voxtimer = 0;
       	boolean firstvox = true;
       	boolean bigendian = format.isBigEndian();
        while (done == false) {
        	int off = 0;
        	int cnt = 0;

        	do {
        		progress2("Reading mic data at "+
        			off+": "+(bufferLengthInBytes-cnt));
	            if((numBytesRead = spkline.read(data,
	            		off, bufferLengthInBytes-cnt)) <= 0 ) {
	                break;
	            }

	            progress2("read "+numBytesRead+" mic bytes");
	            cnt += numBytesRead;
	            off += numBytesRead;
        	} while( cnt < bufferLengthInBytes );
        	
        	// We are in receive mode, ignore data from MIC
        	if( mgr.je.isReceiving() ) {
        		progress("In receive mode, drop mic data");
        		continue;
        	}
        	
        	// Update buffering level, but no more than 20.
			aeh.setCurrentRecvBuffering(
				Math.min( 20, prm.getPCBuffering() ) );
  
//				progress("read "+numBytesRead+" total bytes");
        	progress2( "read "+cnt+" of "+bufferLengthInBytes+" total bytes");
            try {
            	outdata = new ByteArrayOutputStream(1+1+2+4+4+(33*4) );
            	outdata.write( 0xc0);
            	outdata.write(3);
            	outdata.write((mgr.seq&0xff00)>>8);
            	outdata.write( mgr.seq & 0xff);
            	++mgr.seq;
            	outdata.write( zeros );
            	for( int i = 0; i < 4; ++i ) {
            		// Eventually want to implement AGC here to put
            		// the audio at the same level for all stations.
            		if( audAmp != 0 ) {
            			progress("Amplifying by: "+audAmp );
            			int xoff = i*segLen;
            			int lsb = xoff;
            			int msb = xoff+1;
	            		for( int x = 0; x < segLen; x += 2 ) {
	            			int vl;
	            			int vh;
	            			if( bigendian ) {
		            			vl = data[msb] & 0xff;
		            			vh = data[lsb] & 0xff;
	            			} else {
		            			vh = data[msb] & 0xff;
		            			vl = data[lsb] & 0xff;
	            			}
	            			short v = (short)((vh << 8 ) | vl);
	            			if( (Math.abs(v) * audAmp) < 32767 )
	            				v = (short)( v * audAmp );
	            			data[ lsb ] = (byte)( v & 0xff );
	            			data[ msb ] = (byte)( ( v & 0xff00 ) >> 8 );
	            			lsb += 2;
	            			msb += 2;
            			}
            		} else if( !bigendian ) {
            			int xoff = i*segLen;
            			int lsb = xoff;
            			int msb = xoff+1;
	            		for( int x = 0; x < segLen; x += 2 ) {
		            		byte vh = data[ msb ];
		            		data[ msb ] = data[ lsb ];
		            		data[ lsb ] = vh;
		            		lsb += 2;
		            		msb += 2;
	            		}		            		
            		}

            		ByteArrayInputStream strm = 
            			new ByteArrayInputStream( data, i*segLen, segLen );
					encoder.encode( strm, outdata );
            	}
            } catch( Exception ex ) {
            	log.log(Level.SEVERE, ex.toString(), ex);
            }
 
			byte[]edata = outdata.toByteArray();

			progress2("queue next data: ["+
				(mgr.seq-1)+"] "+edata.length );

            long average = 0;
            int max = 0;
            for( int i = 0; i < bufferLengthInBytes;
            		i += frameSizeInBytes ) {
            	short v = (short)(((data[i]&0xff) << 8) |
            		(data[i+1]&0xff));
            	int val = Math.abs(v);
            	if( val > max )
            		max = val;
            	average += val;
            }
            average /= (bufferLengthInBytes/frameSizeInBytes);
            if( mgr.je != null ) {
            	mgr.je.setAverage( (int)average, max );
        	}
        	
        	// Vox based PTT control is handled here.
			if( vox ) {
				int voxlim = prm.getVoxLimit();
				boolean unsquelch = average > voxlim;
				if( unsquelch ) {
					if( !voxState ) {
						log.finer("Audio Sender vox unsquelching "+
							average+" > "+voxlim );
						LinkEvent<Number> le = new LinkEvent<Number>( MicIO.this,
							true, LinkMode.VOX_OPEN_EVENT, average );
						mgr.je.sendEvent( le );
						voxState = true;
						firstvox = true;
					}
					// PTT should be in the correct state, queue the data.
					outqueue.enqueue( edata );
				} else {
					if( voxState ) {
						if( firstvox == true ) {
							firstvox = false;
							voxtimer = System.currentTimeMillis();
						}
						if( System.currentTimeMillis() -
								voxtimer > prm.getRxCtrlVoxdelay() ) {
							log.finer("Audio Sender vox squelching "+
								average+" <= "+voxlim );
							LinkEvent<Number> le = new LinkEvent<Number>( MicIO.this,
								true, LinkMode.VOX_CLOSE_EVENT, average );
							mgr.je.sendEvent( le );
							voxState = false;
//							firstvox = true;
						} else {
							// quiet period for vox timeout, keep sending audio
							outqueue.enqueue( edata );
						}
					}
				}
			} else {
				// PTT should be in the correct state, queue the data.
				outqueue.enqueue( edata );
			}
        }
	}
}