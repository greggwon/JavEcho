package org.wonderly.ham.audio;

import javax.sound.midi.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import org.wonderly.ham.MorseControl;
import javax.sound.sampled.*;

/**
 *  This is an Audio based morse code generator.  It
 *  uses 800hz audio tones.  The
 */
public class AudioMorseSender implements MorseControl {
	SourceDataLine line;
	static final int samples = 44000;
	static int volume = 3000;
	static final AudioFormat playFormat = new AudioFormat(
		samples, 16, 1, true, true );
	Logger log = Logger.getLogger( getClass().getName() );
	int charRate = 25, wordRate = 25, freq = 800, wdotlen, charslen, samps;
	Hashtable<Character,String> chars;
	
	public void setPitchFreq( int freqHz ) throws UnsupportedOperationException {
		freq = freqHz;
	}

	public void setVolumeLevelDb( int db ) {
		if( db > 0 )
			db = -db;
		if( db < -20 )
			db = -19;
		volume = (32767 * (20+db)) / 20;
		log.fine("setVolumeLevelDb: db="+db+" becomes "+volume );
	}

	/**
	 *  Set the waveform max amplitude, 0-32767
	 *	default is 10000.
	 */
	public static void setMaxAmplitude( int val ) {
		volume = (int)Math.min(Math.max(val,0), 32767);
	}

	public static void main( String args[] ) throws IOException {
		AudioMorseSender as = new AudioMorseSender();
		as.sendMorseChars( "Welcome to Javecho by w5ggw" );
		System.exit(1);
	}

	public AudioMorseSender( SourceDataLine outLine ) {
		line = outLine;
		buildCharTable();
	}

	public AudioMorseSender() throws IOException {
		buildCharTable();
	}

	public void finalize() {
		if( line != null ) {
//			try {
				line.stop();
//			} catch( IOException ex ) {
//			}
//			try {
				line.close();
//			} catch( IOException ex ) {
//			}
			line = null;
		}
	}

	protected void getLine() throws IOException {
		
		if( line != null )
			return;
		log.fine("Format: "+playFormat );
		final DataLine.Info info = new DataLine.Info(
			SourceDataLine.class, playFormat);
		if (!AudioSystem.isLineSupported(info)) {
			throw new IllegalArgumentException(info+" type not available" );
		}
		progress("playback: getLine");
		try {
			line = (SourceDataLine)AudioSystem.getLine(info);
			line.open( playFormat, samples*8 );
			line.start();
		} catch( LineUnavailableException ex ) {
			throw (IOException)(new IOException( ex.toString() ).initCause( ex ));
		}
	}
	
	protected void buildCharTable() {
		chars = new Hashtable<Character,String>();
		chars.put(new Character('A'),"·-");
		chars.put(new Character('B'),"-···");
		chars.put(new Character('C'),"-·-·");
		chars.put(new Character('D'),"-··");
		chars.put(new Character('E'),"·");
		chars.put(new Character('F'),"··-·");
		chars.put(new Character('G'),"--·");
		chars.put(new Character('H'),"····");
		chars.put(new Character('I'),"··");
		chars.put(new Character('J'),"·---");
		chars.put(new Character('K'),"-·-");
		chars.put(new Character('L'),"·-··");
		chars.put(new Character('M'),"--");
		chars.put(new Character('N'),"-·");
		chars.put(new Character('O'),"---");
		chars.put(new Character('P'),"·--·");
		chars.put(new Character('Q'),"--·-");
		chars.put(new Character('R'),"·-·");
		chars.put(new Character('S'),"···");
		chars.put(new Character('T'),"-");
		chars.put(new Character('U'),"··-");
		chars.put(new Character('V'),"···-");
		chars.put(new Character('W'),"·--");
		chars.put(new Character('X'),"-··-");
		chars.put(new Character('Y'),"-·--");
		chars.put(new Character('Z'),"--··");
		chars.put(new Character('1'),"·----");
		chars.put(new Character('2'),"··---");
		chars.put(new Character('3'),"···--");
		chars.put(new Character('4'),"····-");
		chars.put(new Character('5'),"·····");
		chars.put(new Character('6'),"-····");
		chars.put(new Character('7'),"--···");
		chars.put(new Character('8'),"---··");
		chars.put(new Character('9'),"----·");
		chars.put(new Character('0'),"-----");
		chars.put(new Character('.'),"·-·-·-");
		chars.put(new Character(','),"--··--");
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

	private void progress( String str ) {
		log.fine(str);
	}

	/**
	 *  Send the indicated characters out
	<p/>
	Taken from I.T.U. Regulations:
	<p/>

	<i>The International Morse Code is to be used for all 
	general Public Service radio communications.</i>

    <ul>
    <li>A Dash is equal to three dots.
    <li>The Space between parts of the same letter is = 1 Dot.
    <li>The Space between two letters is = 3 Dots.
    <li>The Space between two words is = 7 Dots. 
    </ul>
    <p/>
    For the purposes of determining the speed of transmission
    (words per minute):
    <ul>
    <li>A Word is = five letters + one space
    </ul>
    A word time is thus:
    <pre>
        (2 dashes + 2 dots) * 5 chars per word + a space
 
    	=  (((dottime*3)*2) + ((dottime*1)*2) * 5) + (7*dottime)
    </pre>
    The sample speed of the audio determines how many waveforms
    are required to produce a <code>dottime</code> of sound.  The
    above formula should be equal to 60000ms of audio.  So, to
    solve for waveform samples per <code>dottime</code> we would
    write:
    <pre>
    	(((dottime*3)*2) + ((dottime*1)*2) * 5) + (7*dottime) = 60000ms
    </pre>
    solving for dottime will tell us how many milliseconds a <code>dottime</code>
    is. Simplifying by algebraic rules we get:
    <pre>
    	((dottime*6) + (dottime*2)) * 5) + (7 * dottime) = 60000ms	
    =>
    	(dottime*30) + (dottime*10) + (7*dottime) = 60000ms	
   	=>
   		(30 + 10 + 7 ) = 60000ms / dottime	
   	=>
   		1 / (30 + 10 + 7 ) = dottime / 60000ms
   	=>
   		dottime = 60000ms / ( 30 + 10 + 7);	
   	=>
   		dottime = 1276ms
   	</pre>
   	This is the dottime for 1 wpm.  Now, we would take this number and
   	divide it by wpm to get the ms per dot for that wpm.  Thus,
   	1276/12wpm = 106ms per dot for 12 wpm.
	 */
	public void sendMorseChars( String str ) throws IOException {
		log.fine("Get audio out line");
		getLine();
		try {
			log.fine("Sending chars: \""+str+"\"");
			doSendChars( str );
		} finally {
			line.close();
			line = null;
		}
	}
	
	protected void doSendChars( String str ) throws IOException {
		str = str.toUpperCase();
		log.fine("Sending chars: "+str );
		// 10 seconds of audio
		int off = 0;

		// samples/freq is the number of samples needed to
		// express a single 360 degree wave at freq hz.
		// we need the size of 1/2 the wave form to calculate
		// the size of the form array for putting in half
		// of the wave for.
		samps = samples/freq/2;

		// 1276 is the number of milliseconds a dit would
		// be for a 1wpm morse rate.  So, we need to divide
		// that number by the character rate to get the
		// length of a dit 

		dotlen = (int)Math.round(1000/charRate);
		wdotlen = (int)Math.round(1000/wordRate);
		// dotlen is now the number of milliseconds
		// needed for a dit.

		log.fine( "samps: "+samps+", millis for dit: "+dotlen);

		// To calculate the number of samples in a dit
		// time we do:
		// samples is number of samples in a second.
		// so we need to find the number of samples in
		// the number of milliseconds in dotlen.
		dotlen = (samples * dotlen) / 1000 / samps;

		// The space between words is 7 dottime
		int splen = (int)(wdotlen*7*samps);
		splen = splen & ~1;
		byte[]bsp = new byte[splen];
		log.fine("charRate: "+charRate+", wordRate: "+wordRate );
		log.fine("samples: "+samples+", dotlen: "+dotlen+", splen: "+splen );

		// The Space between two letters is = 3 Dots
		// The Space between parts of the same letter is = 1 Dot
		// so we need 2 dot times after the trailing 1 dot time
		// to get 3 dot times.
		log.fine("samples for "+freq+"hz: "+samps );

		// Allocate the array for a half single wave form
		form = new int[ (int)samps+1 ];
		byte[]buf = new byte[samples*10];

		for( int i = 0; i < samps; ++i ) {
			int deg = (int)((180.0 * i)/samps);
			int val = (int)(volume * Math.sin( 
				(deg * 2 * Math.PI)/180 ) );
			form[i] = val;
		}
		for( int i = 0; i < str.length(); ++i ) {
			String set = chars.get(str.charAt(i));
			log.fine("Send char: '"+str.charAt(i)+"'");
			for( int j = 0; j < set.length(); ++j ) {
				log.fine("encoding: '"+set.charAt(j)+"'");
				off = encodeSym( buf, set.charAt(j), off );
			}
			log.finer("writing out "+off+" bytes" );
			line.write( buf, 0, off );
			off = 0;
			line.write( bsp, 0, bsp.length );
			
		}
		line.drain();
	}

	int form[] = {
		0,     100,   1000,  10000, 12000, 
		14000, 12000, 10000, 1000,  100,
	};

	int dotlen;

	private int encodeSym( byte[] buf, char sym, int off ) {
		int symlen = dotlen;
		if( sym == '-' ) {
			symlen = symlen*3;
		}

		log.fine( "symlen for '"+sym+"': "+symlen );
		if( sym != ' ' ) {
			for( int i = 0; i < symlen/2; ++i ) {
				// positive side of wave form
				for( int j = 0; j < form.length; ++j ) {
					insert( buf, off+=2, form[j] );
				}
				// negative side of wave form
				for( int j = 0; j < form.length; ++j ) {
					insert( buf, off+=2, -form[j] );
				}
			}
		} else {
			// The Space between two words is = 7 Dots
			for( int j = 0; j < dotlen*form.length*7; ++j ) {
				insert( buf, off+=2, 0 );
			}
		}

		// The Space between parts of the same letter is = 1 Dot.
		for( int j = 0; j < dotlen*form.length; ++j ) {
			insert( buf, off+=2, 0 );
		}

		return off;
	}

	private void insert( byte[]buf, int off, int val ) {
		if( log.isLoggable(Level.FINEST) )
			log.finest("inserting sample: "+val+" at "+off );
		buf[off+1] = (byte)(val&0xff);
		buf[off] = (byte)(val>>8);
	}

	/**
	 *  Set the character rate in Words per Minute.
	 *  This establishes the symbol rate for each
	 *  letter.
	 */
	public void setCharRate( int wpm ) {
		charRate = wpm;
	}

	/** 
	 *  Set the word rate in Words per minute.
	 *  This establishes the spacing rate
	 */
	public void setWordRate( int wpm ) {
		wordRate = wpm;
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
		throw new UnsupportedOperationException("Receive not supported");
	}
	
	/**
	 *  @return whether receive is supported by the
	 *  implementation.
	 */
	public boolean isReceiveSupported() {
		return false;
	}
}