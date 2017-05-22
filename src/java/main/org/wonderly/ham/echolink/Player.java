package org.wonderly.ham.echolink;

import javax.sound.sampled.*;
import java.util.logging.*;
import java.util.*;
import org.wonderly.swing.*;
import java.io.*;
import org.wonderly.awt.*;
import java.util.Timer;
import javax.swing.*;

class Player implements Runnable {
	Queue bufs;
	String addr;
	boolean done;
	SourceDataLine line;
	ConnectionManager mgr;
	int maxAvail;
	Parameters prm;
	boolean vox;
	Logger log = Logger.getLogger("org.wonderly.ham.echolink.playback");
	
	private void progress( String str ) {
		log.fine(str);
	}

	synchronized boolean startPlayback() throws LineUnavailableException {
		if( line != null ) {
			progress("line already opened, set gain");
			try {
				mgr.je.setGainControl( line.getControl(FloatControl.Type.MASTER_GAIN ) );
			} catch( Exception ex ) {
				log.log(Level.INFO, ex.toString(),ex);
			}
			return true;
		}
		progress("Starting playback setup" );
		final DataLine.Info info = new DataLine.Info(
			SourceDataLine.class, mgr.playFormat);
		if (!AudioSystem.isLineSupported(info)) {
			throw new IllegalArgumentException(info+" type not available" );
		}
		progress("playback: getLine");
		line = new LineWrapper((SourceDataLine) AudioSystem.getLine(info));
		if( prm.useSelectedAudio() ) {
			try {
		        Mixer.Info infos[] = AudioSystem.getMixerInfo();
		        Mixer.Info inf = infos[prm.getAudioDevice()];
		        Mixer mix = AudioSystem.getMixer( inf );
        		Line.Info sinfos[] = mix.getSourceLineInfo( info );
		        if( sinfos != null && sinfos.length > 0 ) {
		        	try {
			        	SourceDataLine dt = (SourceDataLine)
			        		AudioSystem.getLine( sinfos[0] );
			        	progress("overriding default SourceDataLine "+
			        		line);
			        	progress("with: "+dt );
			        	line = new LineWrapper( dt );
		        	} catch( Exception ex ) {
						log.log(Level.SEVERE, ex.toString(),ex);
		        	}
		        } else {
		        	progress("No overriding SourceDataLine found" );
		        }
			} catch( Exception ex ) {
				log.log(Level.SEVERE, ex.toString(),ex);
			}
		}
		progress("playback: open");
		line.open( mgr.playFormat, maxAvail=8000*128);
		
		progress("playback: start");
		line.start();
		try {
			mgr.je.setGainControl( line.getControl(FloatControl.Type.MASTER_GAIN ) );
		} catch( Exception ex ) {
			log.log(Level.INFO, ex.toString(),ex);
		}
		progress("playback setup complete" );
		
		return true;
	}
	public void stop() {
//			new Throwable("Player stopping").printStackTrace();
		done = true;
		started = true;
		synchronized(bufs) {
			bufs.notifyAll();
		}
		bufs.removeAllElements();
		timer.cancel();
	}

	public Player( Queue buffers, String addr, ConnectionManager mgr,
			Parameters prm, boolean isVox ) {
		bufs = buffers;
		vox = isVox;
		this.addr = addr;
		this.prm = prm;
		this.mgr = mgr;
		timer = new Timer();
	}

	boolean started = false;
	Timer timer;
	class AvgEntry {
		long time;
		long val;
		int max;
		AvgEntry next;
		public AvgEntry( long time, long val, int max ) {
			this.time = time;
			this.val = val;
			this.max = max;
		}
	}

	class AverageRenderer extends TimerTask {
		AvgEntry head;
		AvgEntry tail;
		boolean done;
		
		public synchronized void enqueue( AvgEntry ent ) {
			if( tail == null ) {
				head = tail = ent;
			} else {
				tail.next = ent;
				tail = ent;
			}
		}

		public AverageRenderer( int millis ) {
			try {
				timer.schedule( this, millis, millis );
			} catch( IllegalStateException ex ) {
				log.log(Level.SEVERE, ex.toString(), ex );
				timer = new Timer();
				timer.schedule( this, millis, millis );
			} catch( Exception ex ) {
				log.log(Level.SEVERE, ex.toString(), ex );
			}
		}

		public void run() {
			boolean end = false;
			while(!end && head != null ) {
				synchronized( this ) {
					if( head.time > System.currentTimeMillis() ) {
						end = true;
						continue;
					}
					final AvgEntry ent = head;
					head = head.next;
					if( head == null )
						tail = null;
					try {
						SwingUtilities.invokeAndWait( new Runnable() {
							public void run() {
								mgr.je.setAverage( (int)ent.val, ent.max );
							}
						});
					} catch( Exception ex ) {
					}
				}
			}
		}
		
		public void stop() {
			done = true;
			cancel();
		}
	}

	public void run() {			
		// First packet? If so, start player.
		if( line != null ) {
			new IllegalArgumentException( "line should be null" ).printStackTrace();
			try {
				line.close();
			} catch( Exception ex ) {
				log.log(Level.SEVERE, ex.toString(),ex);
			}
		}
		line = null;
		if( line == null ) {
			try {
				progress("open line");
				startPlayback();
			} catch( LineUnavailableException ex ) {
				try {
					mgr.disconnectFrom(addr);
				} catch( IOException exx ) {
					log.log(Level.FINE, ex.toString(),ex);
				}
				return;
			} catch( Exception ex ) {
				log.log(Level.SEVERE, ex.toString(),ex);
			}
		}
		byte[]lastsamp = null;
		int divs = 4;
		// 8000 (bit) samples per second in 8 intervals as milliseconds
		int intv = 1000/divs;
		final AverageRenderer rend = new AverageRenderer( intv );
		// Current time to render average up to.
		long ct = System.currentTimeMillis();
		// While still sampling and have data
		while( (!done || bufs.queueSize() > 0) && line != null ) {
			while( (!done && bufs.isEmpty()) || 
					(!done && //!started && 
						bufs.queueSize() < prm.getNetBuffering()/2) ) {
				try {
					progress("waiting for buffers: (done="+done+",empty="+
						bufs.isEmpty()+") "+bufs.queueSize()+
						" <> "+prm.getNetBuffering() );
					synchronized( bufs ) {
						bufs.wait(1000);
					}
				} catch( Exception ex ) {
					log.log(Level.SEVERE, ex.toString(),ex);
				}
			}
			mgr.setCurrentRecvBuffering( Math.min( bufs.queueSize(),
				prm.getPCBuffering() ) );
			int sz = line.available();
			progress("Avail: "+sz);
			int sidx = ((maxAvail-sz)*
				prm.getNetBuffering())/maxAvail;
			mgr.setCurrentAudioBuffering( Math.min( sidx, 
				prm.getPCBuffering() ) );
			if( done )
				break;
			if( !started )
				ct = System.currentTimeMillis()-intv;
			started = true;
			if( bufs.isEmpty() ) {
				progress("No data, trying again");
				continue;
			}
			byte[]data = (byte[])bufs.pop();
			if( data == null ) {
				new NullPointerException("Bad data in playback: "+
					data ).printStackTrace();
				continue;
			}
			
			int reclen = data.length/divs;
//				progress("data.length: "+data.length+
//					", divs: "+divs+", reclen: "+reclen );

			for( int j = 0; j < divs; ++j ) {
	            long average = 0;
	            int last = 0;
	            int max = 0;
	            int total = 0;
	            for( int i = j*reclen; i < Math.min((j+1)*reclen, 
	            		data.length); i += 2 ) {
	            	short v = (short)(( (data[ i + 1 ] & 0xff) ) |
	            		(( data[ i ] & 0xff) << 8 ));
	            	int val = Math.abs(v&0x7fff);
	            	if( val > max )
	            		max = val;
	            	average += val;
	            	total += 2;
	            }
	            average /= (reclen)/2;
//		        	progress( "Average audio: " + average );

		        rend.enqueue( new AvgEntry( ct += intv, average, max ) );

            	if( line != null ) {
            		progress ("Write "+total+" at ["+j+"]" );
					line.write( data, j*reclen, total );
            	} else {
            		progress("line not open!");
            	}
			}

			progress("packet done");
			synchronized( mgr.rxlock ) {
				mgr.rxd = true;
				mgr.rxlock.notifyAll();
			}
		}
		
		progress("draining line");
		if( line != null )
			line.drain();
		mgr.setCurrentAudioBuffering(0);
		progress("drain complete");
//			int cnt = 0;
//			while( line.isActive() && cnt++ < 20) {
//				try {
//					Thread.sleep(200);
//				} catch( Exception ex ) {
//				}
//			}
//			g = null;

		if( line != null ) {//&& line.isActive() ) {
			progress("draining stop");
			line.stop();
		}
		mgr.je.rxActive = false;
		mgr.je.setAverage(0,0);
		mgr.setTrans( addr, false );
		mgr.je.setMode( prm.isUserMode() ? LinkEvent.MODE_IDLE : LinkEvent.MODE_SYSOPIDLE );

		if(line != null)
			line.close();
		line = null;
	}
}