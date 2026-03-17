package org.wonderly.ham.echolink;

import javax.sound.sampled.*;
import javax.sound.sampled.BooleanControl.Type;

import java.util.logging.*;
import java.util.*;
import gsm.encoder.*;
import java.io.*;

import org.wonderly.ham.echolink.LinkEvent.LinkMode;
import org.wonderly.ham.echolink.audio.*;
import org.wonderly.ham.echolink.mic.*;

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
		log.log(Level.INFO, "Creating MicIO for: "+mgr,
			new Throwable("MicIO created") );		
		vox = voxon;
		this.mgr = mgr;
		this.prm = prm;
		aeh = mgr;
		checkLineSupported();
	}
	
	private final boolean checkLineSupported() throws InvalidSoundSystemConfigurationException {
		format = new AudioFormat( 8000, 16, 1, true, true );
		info = new DataLine.Info( TargetDataLine.class, format );
		log.log(Level.INFO, "MicIO: format:{0}, info: {1}", new Object[]{format, info});
		if( ! AudioSystem.isLineSupported( info ) ) {
			log.log(Level.SEVERE, "Format Not Supported: "+format,
				new InvalidSoundSystemConfigurationException(
				"Line matching \"" + info + 
				"\" not supported.") );
			format = new AudioFormat( 8000, 16, 1, true, false );
			info = new DataLine.Info( TargetDataLine.class, format );
			log.log(Level.INFO, "MicIO: try other format: ({0}) {1}, info: {2}", new Object[]{format.isBigEndian(), format, info});
			if( AudioSystem.isLineSupported( info ) ) {
				log.info( "Little Endian is available");
				log.log(Level.WARNING, "Big Endian conversion required: {0}", format);
			} else {
				throw new InvalidSoundSystemConfigurationException(
					"No required MIC audio data "+
					"formats supported: \n\n"+format);
			}
		}
		return true;
	}
	
	public void setMicGain( float percent ) {
		Port lineIn;
        FloatControl volCtrl;
        Mixer mixer;
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
//		format = new AudioFormat( 8000, 16, 1, true, true );
//		info = new DataLine.Info( TargetDataLine.class, format );
//		log.info("MicIO: new line format:"+format+", info: "+info );
//		if( ! AudioSystem.isLineSupported( info ) ) {
//			log.log(Level.SEVERE, "Format Not Supported: "+format,
//				new InvalidSoundSystemConfigurationException(
//				"Line matching \"" + info + 
//				"\" not supported.") );
//			format = new AudioFormat( 8000, 16, 1, true, false );
//			info = new DataLine.Info( TargetDataLine.class, format );
//			log.info("MicIO: try other format:"+format+", info: "+info );
//			if( AudioSystem.isLineSupported( info ) ) {
//				log.info( "Little Endian is available");
//				log.warning( "Big Endian conversion required: "+format );
//			} else {
//				throw new InvalidSoundSystemConfigurationException(
//					"No required MIC audio data formats supported");
//			}
//		}

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
						try {
							if( lineIn.isControlSupported(FloatControl.Type.VOLUME)) {
								FloatControl volCtrl = (FloatControl) lineIn.getControl(FloatControl.Type.VOLUME);
								mgr.je.setMicGainControl( volCtrl );
							}
						} catch( Exception ex ) {
							log.log(Level.SEVERE, ex.toString(), ex);
						}
					}

			        if( mix.getTargetLineInfo( info ) != null ) {
			        	try {
							var targets = mix.getTargetLineInfo( info );
							if( targets.length > 0 ) {
								TargetDataLine dt = (TargetDataLine)
									AudioSystem.getLine( 
									targets[0] );
								progress("overriding default "+
									"TargetDataLine "+spkline);
								progress("with: "+dt );
								spkline = new LineWrapper( dt );
							}
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
			log.log(Level.INFO, "Using buffersize: {0}", spkline.getBufferSize());
			spkline.open( format, spkline.getBufferSize() );
	        Mixer.Info infos[] = AudioSystem.getMixerInfo();
	        for( Mixer.Info mi : infos ) {
	        	Mixer mix = AudioSystem.getMixer(mi );
				final int maxLines = mix.getMaxLines(Port.Info.MICROPHONE);
				if (maxLines > 0) {
					Port lineIn = (Port) mix.getLine(Port.Info.MICROPHONE);
					for( Control c : lineIn.getControls() ) {
						log.log(Level.INFO, "Found mic control: {0}", c);
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
		} catch (Exception ex) { 
			mgr.shutDown(ex);
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
		log.log(Level.FINE, "setPttTimer, userMode: {0}", prm.isUserMode());
		if( prm.isUserMode() ) {
			pttActive = true;
			aSend.setNetActive( pttActive );
			log.log(Level.FINE, "Starting PTT timer task: {0} secs", prm.getPTTTimeout());
			mgr.timer.schedule( tsk = new TimerTask() {
				@Override
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
			@Override
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
						log.log(Level.FINE, "Vox Open: Starting PTT timer task: {0} secs", prm.getPTTTimeout());
						mgr.timer.schedule( tsk = new TimerTask() {
							@Override
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
						log.log(Level.FINEST, "setPttTimer Link Event: {0}", ev);
				}
//				aSend.setNetActive( pttActive );
			}
		};
		return le;
	}
				
	private AudioSender aSend;
	@Override
	public void run() {
		log.info("MicIO running");
		setup();

		// create the audio sender that forwards audio
		// data from the microphone jack out to the
		// network as RTP/RTCP audio packets.
		aSend = new AudioSender( outqueue, mgr.je, mgr, prm );
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
        final int bufferLengthInFrames = spkline.getBufferSize()/frameSizeInBytes;
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
       	final double audAmp = 0;// prm.getAudioAmplification();
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
				log.info("Read "+numBytesRead+" audio/MIC bytes");

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
						log.info("segLen="+segLen+", MSB="+bigendian);
            			int xoff = i*(segLen);

	            		for( int x = 0; x < segLen-1; x += 2 ) {
							int lsb = x;
							int msb = x+1;	            			
							int vl;
	            			int vh;
	            			if( bigendian ) {
		            			vl = data[msb] & 0xff;
		            			vh = data[lsb] & 0xff;
	            			} else {
		            			vh = data[msb] & 0xff;
		            			vl = data[lsb] & 0xff;
	            			}
	            			short v = (short)(((vh << 8 )&0xff00) | vl);
							log.info("v["+(x/2)+"] is "+v+", from msb("+vh+")="+(vh<<8)+", lsb="+vl);
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