package org.wonderly.ham.echolink;

import javax.sound.midi.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.wonderly.awt.*;

/**
 *  An audio provider that uses JavaSound MIDI for the events
 *  rather than recorded audio.  The note events are hard coded
 *  in here.  At some point, we could provide a simple user interface
 *  to let note sequences be configured.
 */
public class JavaMidiAudioProvider implements AudioProvider {
	int perc = 90;
	int trans = 0;
	int speed = 80;
	int instrument = 112;
	public static final int OCTAVE = 12;
	public void connected() throws IOException {
		try {
			Sequence seq = new Sequence( Sequence.PPQ, RES, 1 );
			Track t = seq.createTrack();
//			System.out.println("seq: "+seq+", t: "+t );
			perc = 60;
			

			setInstrument( t, instrument );
			addNote( t, NOTE_MIDDLE_C, velocity, Q_NOTE );
			addNote( t, NOTE_MIDDLE_C, 0, Q_NOTE );
			addNote( t, NOTE_MIDDLE_C+OCTAVE, velocity, Q_NOTE );
			addNote( t, NOTE_MIDDLE_C+OCTAVE, 0, Q_NOTE );
			
			playSequence( seq );
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
		}
	}
	
	String cmds( int cmd ) {
		switch( cmd ) {
			case ShortMessage.NOTE_ON: return "note_on";
			case ShortMessage.NOTE_OFF: return "note_off";
			case ShortMessage.PITCH_BEND: return "pitch_bend";
			case ShortMessage.PROGRAM_CHANGE: return "program_change";
			case ShortMessage.CONTROL_CHANGE: return "control_change";

		}
		return cmd+"";
	}

	String dumpFmt( byte[]d ) {
		String str = "";
		for( int i = 0; i < d.length; ++i ) {
			if( d[i] >= 32 && d[i] <= 127 ) {
				str += (char)d[i];
			} else {
				str += ",0x"+Long.toHexString(d[i]&0xff);
			}
		}
		return str;
	}

	public void alarm() throws IOException {
		try {
			Sequence seq = new Sequence( Sequence.PPQ, RES, 2 );
			Track t = seq.createTrack();
			Track t2 = seq.createTrack();
//			System.out.println("seq: "+seq+", t: "+t );
			setInstrument( t, instrument );
			perc = 60;
			addNote( t, NOTE_MIDDLE_C+OCTAVE, velocity, S_NOTE );
			addNote( t, NOTE_MIDDLE_C+OCTAVE, velocity, S_NOTE );
			addNote( t, NOTE_MIDDLE_C+OCTAVE, 0, S_NOTE );
			addNote( t, NOTE_E+OCTAVE, velocity, Q_NOTE );
			addNote( t, NOTE_E+OCTAVE, 0, Q_NOTE );
			setInstrument( t2, instrument );
			perc = 60;
			addNote( t2, NOTE_MIDDLE_C+OCTAVE, velocity, E_NOTE );
			addNote( t2, NOTE_MIDDLE_C+OCTAVE, 0, E_NOTE );
			addNote( t2, NOTE_G+OCTAVE, velocity, Q_NOTE );
			addNote( t2, NOTE_G+OCTAVE, 0, Q_NOTE );
			playSequence( seq );
		} catch( Exception ex ) {
			IOException ioe = new IOException( "Error Preparing Sequence" );
			ioe.initCause( ex );
		}
	}

	public void disconnect() throws IOException {
		try {
			Sequence seq = new Sequence( Sequence.PPQ, RES, 1 );
			Track t = seq.createTrack();
//			System.out.println("seq: "+seq+", t: "+t );
			setInstrument( t, instrument );
			perc = 60;
			addNote( t, NOTE_MIDDLE_C+OCTAVE, velocity, Q_NOTE );
			addNote( t, NOTE_MIDDLE_C+OCTAVE, 0, Q_NOTE );
			addNote( t, NOTE_MIDDLE_C, velocity, Q_NOTE );
			addNote( t, NOTE_MIDDLE_C, 0, E_NOTE );
			playSequence( seq );
		} catch( Exception ex ) {
			IOException ioe = new IOException( "Error Preparing Sequence" );
			ioe.initCause( ex );
		}
	}

	public final static int NOTE_MIDDLE_C = 60;
	public static final int NOTE_C_SHARP = 61;
	public static final int NOTE_D = 62;
	public static final int NOTE_D_SHARP = 63;
	public static final int NOTE_E = 64;
	public static final int NOTE_F = 65;
	public static final int NOTE_F_SHARP = 66;
	public static final int NOTE_G = 67;
	public static final int NOTE_G_SHARP = 68;
	public static final int NOTE_A = 69;
	public static final int NOTE_A_SHARP = 70;
	public static final int NOTE_B = 71;
	public static final int NOTE_C = 72;

	public void transmit() throws IOException {
		try {
			Sequence seq = new Sequence( Sequence.PPQ, RES, 1 );
			Track t = seq.createTrack();
//			System.out.println("seq: "+seq+", t: "+t );
			perc = 60;
			

			setInstrument( t, instrument );
			addNote( t, NOTE_MIDDLE_C, velocity, Q_NOTE );
			addNote( t, NOTE_MIDDLE_C, 0, Q_NOTE );
			
			playSequence( seq );
		} catch( Exception ex ) {
			IOException ioe = new IOException( "Error Preparing Sequence" );
			ioe.initCause( ex );
		}
	}

	public void receive() throws IOException {
		try {
			Sequence seq = new Sequence( Sequence.PPQ, RES, 1 );
			Track t = seq.createTrack();
//			System.out.println("seq: "+seq+", t: "+t );
			perc = 60;
			

			setInstrument( t, instrument );
			addNote( t, NOTE_MIDDLE_C+OCTAVE, velocity, Q_NOTE );
			addNote( t, NOTE_MIDDLE_C+OCTAVE, 0, S_NOTE );
			
			playSequence( seq );
		} catch( Exception ex ) {
			IOException ioe = new IOException( "Error Preparing Sequence" );
			ioe.initCause( ex );
		}
	}

	public void over() throws IOException {
		try {
//			for( int i = 0; i < 128; ++i ) {
			Sequence seq = new Sequence( Sequence.PPQ, RES, 1 );
			Track t = seq.createTrack();
//			System.out.println("seq: "+seq+", t: "+t );
			perc = 80;
			

//			System.out.println("prog: "+i);
			setInstrument( t, 112);
			addNote( t, NOTE_G+OCTAVE, velocity, S_NOTE );
			addNote( t, NOTE_G, 0, S_NOTE );
			
			playSequence( seq );
//			}
		} catch( Exception ex ) {
			IOException ioe = new IOException( "Error Preparing Sequence" );
			ioe.initCause( ex );
		}
	}
	
	public JComponent getEditor() {
		JPanel p = new JPanel();
		Packer pk = new Packer();
		
		return p;
	}

	public static final int RES = 64;
	public static final int T_NOTE = RES/32;
	public static final int S_NOTE = RES/16;
	public static final int E_NOTE = RES/8;
	public static final int Q_NOTE = RES/4;
	public static final int H_NOTE = RES/2;
	public static final int W_NOTE = RES/1;
	public static int velocity = 90;

	public static void main( String args[] ) throws Exception {
		new JavaMidiAudioProvider().connected();
		new JavaMidiAudioProvider().disconnect();
		new JavaMidiAudioProvider().alarm();
		new JavaMidiAudioProvider().transmit();
		new JavaMidiAudioProvider().receive();
		new JavaMidiAudioProvider().over();
	}

	public JavaMidiAudioProvider() throws Exception {
	}

	public void playSequence( Sequence seq ) 
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
	
	public void setInstrument( Track t, int instrument ) throws InvalidMidiDataException {
		Long tm = (Long)times.get(t);
		if( tm == null )
			tm = new Long(0);
		long time = tm.longValue();
		t.add( new MidiEvent( new MidiMsg(
			ShortMessage.PROGRAM_CHANGE,
			0, instrument, 4 ), time ) );
	}
	
	Hashtable<Track,Long> times = new Hashtable<Track,Long>();
	public void addNote( Track t, 
			int note, int vel, int dur ) throws InvalidMidiDataException {
		Long tm = (Long)times.get(t);
		if( tm == null )
			tm = new Long(0);
		long time = tm.longValue();
		t.add( new MidiEvent( new MidiMsg(
			ShortMessage.NOTE_ON,
			0, note+trans, vel ), time ) );
		int len = ( dur * perc ) / 100;
		t.add( new MidiEvent( new MidiMsg(
			ShortMessage.NOTE_OFF,
			0, note+trans, vel ), time+len ) );
		if( vel > 0 ) {
			time += dur;
			times.put( t, new Long( time ) );
		}
	}
	public void addChord( Track t, 
			int note, int vel, int dur ) throws InvalidMidiDataException {
		Long tm = (Long)times.get(t);
		if( tm == null )
			tm = new Long(0);
		long time = tm.longValue();
		time -= dur;
		t.add( new MidiEvent( new MidiMsg(
			ShortMessage.NOTE_ON,
			0, note+trans, vel ), time ) );
		int len = ( dur * perc ) / 100;
		t.add( new MidiEvent( new MidiMsg(
			ShortMessage.NOTE_OFF,
			0, note+trans, vel ), time+len ) );
	}

	public void addTime( Track t, int dur ) throws InvalidMidiDataException {
		Long tm = (Long)times.get(t);
		if( tm == null )
			tm = new Long(0);
		long time = tm.longValue();
		time += dur;
		times.put( t, new Long( time ) );
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