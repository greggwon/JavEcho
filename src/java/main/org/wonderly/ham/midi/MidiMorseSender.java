package org.wonderly.ham.midi;

import javax.sound.midi.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import org.wonderly.ham.*;

/**
 *  
 */
public class MidiMorseSender implements MorseControl {
	int perc = 60;
	int trans = 0;
	int speed = 300;
	int instrument = 15;
	/** Dash resolution */
	public static final int RES = 64;
	public static final int DOT_RES = RES/3;
	public static final int DASH_RES = RES/1;
	public static final int SPACE_RES = RES/1;
	public static int velocity = 120;
	Hashtable<Character,String> chars;
	Logger log = Logger.getLogger( getClass().getName() );
	
	public void setPitchFreq( int freqHz ) throws UnsupportedOperationException {
		throw new UnsupportedOperationException( "no pitch control available");
	}
	public void setVolumeLevelDb( int db ) throws IOException {
	}

	public MidiMorseSender() {
		log.fine("Initializing morse set");
		buildCharTable();
	}
	
	protected void buildCharTable() {
		chars = new Hashtable<Character,String>();
		chars.put(new Character('A'),"�-");
		chars.put(new Character('B'),"-���");
		chars.put(new Character('C'),"-�-�");
		chars.put(new Character('D'),"-��");
		chars.put(new Character('E'),"�");
		chars.put(new Character('F'),"��-�");
		chars.put(new Character('G'),"--�");
		chars.put(new Character('H'),"����");
		chars.put(new Character('I'),"��");
		chars.put(new Character('J'),"�---");
		chars.put(new Character('K'),"-�-");
		chars.put(new Character('L'),"�-��");
		chars.put(new Character('M'),"--");
		chars.put(new Character('N'),"-�");
		chars.put(new Character('O'),"---");
		chars.put(new Character('P'),"�--�");
		chars.put(new Character('Q'),"--�-");
		chars.put(new Character('R'),"�-�");
		chars.put(new Character('S'),"���");
		chars.put(new Character('T'),"-");
		chars.put(new Character('U'),"��-");
		chars.put(new Character('V'),"���-");
		chars.put(new Character('W'),"�--");
		chars.put(new Character('X'),"-��-");
		chars.put(new Character('Y'),"-�--");
		chars.put(new Character('Z'),"--��");
		chars.put(new Character('1'),"�----");
		chars.put(new Character('2'),"��---");
		chars.put(new Character('3'),"���--");
		chars.put(new Character('4'),"����-");
		chars.put(new Character('5'),"�����");
		chars.put(new Character('6'),"-����");
		chars.put(new Character('7'),"--���");
		chars.put(new Character('8'),"---��");
		chars.put(new Character('9'),"----�");
		chars.put(new Character('0'),"-----");
		chars.put(new Character('.'),"�-�-�-");
		chars.put(new Character(','),"--��--");
		chars.put(new Character('A'),".-");
		chars.put(new Character('B'),"-...");
		chars.put(new Character('C'),"-.-.");
		chars.put(new Character('D'),"-..");
		chars.put(new Character('E'),".");
		chars.put(new Character('F'),"..-.");
		chars.put(new Character('G'),"--.");
		chars.put(new Character('H'),"....");
		chars.put(new Character('I'),"..");
		chars.put(new Character('J'),".---");
		chars.put(new Character('K'),"-.-");
		chars.put(new Character('L'),".-..");
		chars.put(new Character('M'),"--");
		chars.put(new Character('N'),"-.");
		chars.put(new Character('O'),"---");
		chars.put(new Character('P'),".--.");
		chars.put(new Character('Q'),"--.-");
		chars.put(new Character('R'),".-.");
		chars.put(new Character('S'),"...");
		chars.put(new Character('T'),"-");
		chars.put(new Character('U'),"..-");
		chars.put(new Character('V'),"...-");
		chars.put(new Character('W'),".--");
		chars.put(new Character('X'),"-..-");
		chars.put(new Character('Y'),"-.--");
		chars.put(new Character('Z'),"--..");
		chars.put(new Character('1'),".----");
		chars.put(new Character('2'),"..---");
		chars.put(new Character('3'),"...--");
		chars.put(new Character('4'),"....-");
		chars.put(new Character('5'),".....");
		chars.put(new Character('6'),"-....");
		chars.put(new Character('7'),"--...");
		chars.put(new Character('8'),"---..");
		chars.put(new Character('9'),"----.");
		chars.put(new Character('0'),"-----");
		chars.put(new Character('.'),".-.-.-");
		chars.put(new Character(','),"--..--");
		chars.put(new Character(' ')," ");
		chars.put(new Character(' ')," ");
		log.fine(chars.size()+" characters defined in: "+chars);
	}

	public static void main( String args[] ) throws Exception {
		MidiMorseSender mc = new MidiMorseSender();
		mc.log.info("Testing");
		int ins[] = {
//			25,
//			65,
			110
//			111
		};
		int trans[] = {
//			0,
//			-4, 
			-6
//			-6
		};
		for( int i = 0; i < ins.length; ++i ) {
			mc.instrument = ins[i];
			mc.trans = trans[i];
//		for( mc.instrument = 0; mc.instrument < 256; ++mc.instrument ) {
			mc.log.info("instrument: "+mc.instrument);
			mc.sendMorseChars( "W5GGW de KD5KGR");
		}
	}
	
	public void setInstrument( Track t, int instrument ) throws InvalidMidiDataException {
		Long tm = (Long)times.get(t);
		log.info("Set instrument("+tm+") for: "+t+" to: "+instrument );
		if( tm == null ) 
			tm = new Long(0);
		long time = tm.longValue();
		t.add( new MidiEvent( new MidiMsg(
			ShortMessage.PROGRAM_CHANGE,
			0, instrument, 4 ), time ) );
	}

	/**
	 *  Send the indicated characters out
	 */
	public void sendMorseChars( String str ) throws IOException {
		try {
			log.fine("Send chars: \""+str+"\"" );
			Sequence seq = new Sequence( Sequence.PPQ, RES, 1 );
			Track t = seq.createTrack();
//			System.out.println("seq: "+seq+", t: "+t );
			str = str.toUpperCase();
			setInstrument( t, instrument );
			for( int i = 0; i < str.length(); ++i ) {
				String c = chars.get( str.charAt(i) );
				log.fine("Got: \""+c+"\" for '"+str.charAt(i)+"'");
				if( c == null )
					throw new IOException("unsupported morse character '"+str.charAt(i)+"'");
				for( int j = 0; j < c.length(); ++j ) {
					log.finer("Adding '"+c.charAt(j)+"' to sequence");
					addChar( t, c.charAt(j), 120 );
				}
				addChar(t,' ',120);
			}
			log.info("Playing sequence for: "+str+": inst: "+instrument );
			playCode(seq);
		} catch( InvalidMidiDataException ex ) {
			IOException ioe = new IOException( "Error Preparing Sequence" );
			ioe.initCause( ex );
			throw ioe;
		} catch( MidiUnavailableException ex ) {
			IOException ioe = new IOException( "Error Preparing Sequence" );
			ioe.initCause( ex );
			throw ioe;
		} catch( RuntimeException ex ) {
			IOException ioe = new IOException( "Error Preparing Sequence" );
			ioe.initCause( ex );
			throw ioe;
		} finally {
		}
	}

	int note = 60;
	Hashtable<Track,Long> times = new Hashtable<Track,Long>();
	public void addChar( Track t, 
			char which, int vel ) throws InvalidMidiDataException {
		Long tm = (Long)times.get(t);
		int dur = 
			(which == '-' ? DASH_RES :
				(which == ' ' ?
					SPACE_RES : DOT_RES) );
		if( tm == null )
			tm = new Long(0);
			long time = tm.longValue();
			if( which != ' ' ) {
			t.add( new MidiEvent( new MidiMsg(
				ShortMessage.NOTE_ON,
				0, note+trans, vel ), time ) );
			log.fine("adding '"+which+"' "+(note+trans)+" note (for "+dur+"), vel="+vel+" at "+time );
			int len = ( dur * perc ) / 100;
			t.add( new MidiEvent( new MidiMsg(
				ShortMessage.NOTE_OFF,
				0, note+trans, vel ), time+len ) );
		}
		if( vel > 0 ) {
			time += dur;
			times.put( t, new Long( time ) );
		}
	}

	protected void playCode( Sequence seq ) 
			throws MidiUnavailableException,
				InvalidMidiDataException {			
		final Object notif = new Object();
		final Sequencer sr = MidiSystem.getSequencer();
//		System.out.println( "open");
		sr.open();
//		System.out.println( "seq seq" );
		sr.setSequence(seq);
//		System.out.println("start");
		sr.setTickPosition(0);
		sr.setTempoInBPM( speed );

		sr.addMetaEventListener( new MetaEventListener() {
			public void meta( MetaMessage msg ) {
//				System.out.println("meta msg: "+msg.getType());
				if( msg.getType() == 47 ) {
					synchronized( notif ) {
						notif.notify();
					}
				}
			}
			});
		sr.start();
		synchronized( notif ) {
			try {
				notif.wait();
				Thread.sleep(500);
			} catch( Exception ex ) {
				ex.printStackTrace();
			}
		}
		while( sr.isRunning() ) {
//			System.out.println("wait running");
			Thread.yield();
		}
//		System.out.println("done");
		Track[]t = sr.getSequence().getTracks();
		for( int i = 0; i < t.length; ++i ) {
			times.remove(t[i]);
		}
		sr.stop();
		sr.close();
	}

	/**
	 *  Set the character rate in Words per Minute.
	 *  This establishes the symbol rate for each
	 *  letter.
	 */
	public void setCharRate( int wpm ) {
	}

	/** 
	 *  Set the word rate in Words per minute.
	 *  This establishes the spacing rate
	 */
	public void setWordRate( int wpm ) {
	}

	/**
	 *  This operation returns the next chars
	 *  that are being received.  If receive is
	 *  not supported, this method will throw
	 *  the UnsupportedOperationException
	 *
	 *  @throws UnsupportedOperationException if receive is not supported
	 *            by the implementation.
	 */
	public String receiveMorse() throws UnsupportedOperationException {
		throw new UnsupportedOperationException( "Receive Not Supported by this Implementation" );
	}
	
	/**
	 *  @return whether receive is supported by the
	 *  implementation.
	 */
	public boolean isReceiveSupported() {
		return false;
	}
	
	static class MidiMsg extends ShortMessage {
		public MidiMsg( int status ) throws InvalidMidiDataException {
			setMessage( status );
		}
		public MidiMsg( int status, 
				int b1, int b2 ) throws InvalidMidiDataException {
			setMessage( status, b1, b2 );
		}
		public MidiMsg( int cmd, int chan,
				int b1, int b2 ) throws InvalidMidiDataException {
			setMessage( cmd, chan, b1, b2 );
		}
	}
}
