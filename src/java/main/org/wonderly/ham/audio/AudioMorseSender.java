package org.wonderly.ham.audio;

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
	static final int SAMPLES = 44000;
	static int volume = 3000;
	static int MIN_DB = -20;
	static final AudioFormat playFormat = new AudioFormat(
		SAMPLES, 16, 1, true, true );
	static final Logger log = Logger.getLogger(AudioMorseSender.class.getName() );
	int charRate = 25, wordRate = 25, freq = 800, wdotlen, charslen, samps;
	Map<Character,String> chars;
	
	@Override
	public void setPitchFreq( int freqHz ) throws UnsupportedOperationException {
		freq = freqHz;
	}

	@Override
	public void setVolumeLevelDb( int db ) {
		if( db > 0 )
			db = -db;
		if( db < MIN_DB )
			db = MIN_DB;
		volume = (32767 * (Math.abs(MIN_DB)+db)) / Math.abs(MIN_DB);
		log.log(Level.FINE, "setVolumeLevelDb: db={0} becomes {1}", new Object[]{db, volume});
	}

	/**
	 *  Set the waveform max amplitude, 0-32767
	 *	default is 10000.
	 * @param val
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

	protected void getLine() throws IOException {
		
		if( line != null )
			return;
		log.log(Level.FINE, "Format: {0}", playFormat);
		final DataLine.Info info = new DataLine.Info(
			SourceDataLine.class, playFormat);
		if (!AudioSystem.isLineSupported(info)) {
			throw new IllegalArgumentException(info+" type not available" );
		}
		progress("playback: getLine");
		try {
			line = (SourceDataLine)AudioSystem.getLine(info);
			line.open(playFormat, SAMPLES*8 );
			line.start();
		} catch( LineUnavailableException ex ) {
			throw (IOException)(new IOException( ex.toString() ).initCause( ex ));
		}
	}
	
	protected final void buildCharTable() {
		chars = new HashMap<>();
		chars.put('A',".-");
		chars.put('B',"-...");
		chars.put('C',"-.-.");
		chars.put('D',"-..");
		chars.put('E',".");
		chars.put('F',"..-.");
		chars.put('G',"--.");
		chars.put('H',"....");
		chars.put('I',"..");
		chars.put('J',".---");
		chars.put('K',"-.-");
		chars.put('L',".-..");
		chars.put('M',"--");
		chars.put('N',"-.");
		chars.put('O',"---");
		chars.put('P',".--.");
		chars.put('Q',"--.-");
		chars.put('R',".-.");
		chars.put('S',"...");
		chars.put('T',"-");
		chars.put('U',"..-");
		chars.put('V',"...-");
		chars.put('W',".--");
		chars.put('X',"-..-");
		chars.put('Y',"-.--");
		chars.put('Z',"--..");
		chars.put('1',".----");
		chars.put('2',"..---");
		chars.put('3',"...--");
		chars.put('4',"....-");
		chars.put('5',".....");
		chars.put('6',"-....");
		chars.put('7',"--...");
		chars.put('8',"---..");
		chars.put('9',"----.");
		chars.put('0',"-----");
		chars.put('?',"..--..");
		chars.put('!',"-.-.--");
		chars.put('.',".-.-.-");
		chars.put(',',"--..--");
		chars.put(';',"-.-.-.");
		chars.put('+',".-.-.");
		chars.put('-',"-....-");
		chars.put('/',"-..-.");
		chars.put('=',"-...-");
		chars.put('a',".-");
		chars.put('b',"-...");
		chars.put('c',"-.-.");
		chars.put('d',"-..");
		chars.put('e',".");
		chars.put('f',"..-.");
		chars.put('g',"--.");
		chars.put('h',"....");
		chars.put('i',"..");
		chars.put('j',".---");
		chars.put('k',"-.-");
		chars.put('l',".-..");
		chars.put('m',"--");
		chars.put('n',"-.");
		chars.put('o',"---");
		chars.put('p',".--.");
		chars.put('q',"--.-");
		chars.put('r',".-.");
		chars.put('s',"...");
		chars.put('t',"-");
		chars.put('u',"..-");
		chars.put('v',"...-");
		chars.put('W',".--");
		chars.put('X',"-..-");
		chars.put('Y',"-.--");
		chars.put('Z',"--..");
		chars.put(' '," ");

		log.log(Level.FINE, "{0} characters defined in: {1}", new Object[]{chars.size(), chars});
	}

	private void progress( String str ) {
		log.fine(str);
	}

	/**
	 *  Send the indicated characters out
	<p>
	Taken from I.T.U. Regulations:
	</p>

	<i>The International Morse Code is to be used for all 
	general Public Service radio communications.</i>

    <ul>
    <li>A Dash is equal to three dots.
    <li>The Space between parts of the same letter is = 1 Dot.
    <li>The Space between two letters is = 3 Dots.
    <li>The Space between two words is = 7 Dots. 
    </ul>
    <p>
    For the purposes of determining the speed of transmission
    (words per minute):
	</p>
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
	 * @param str
	 * @throws java.io.IOException
	 */
	@Override
	public void sendMorseChars( String str ) throws IOException {
		log.fine("Get audio out line");
		getLine();
		try {
			log.log(Level.FINE, "Sending chars: \"{0}\"", str);
			doSendChars( str );
		} finally {
			line.close();
			line = null;
		}
	}
	
	protected void doSendChars( String str ) throws IOException {
		str = str.toUpperCase();
		log.log(Level.FINE, "Sending chars: {0}", str);
		// 10 seconds of audio
		int off = 0;

		// samples/freq is the number of samples needed to
		// express a single 360 degree wave at freq hz.
		// we need the size of 1/2 the wave form to calculate
		// the size of the form array for putting in half
		// of the wave form.
		samps = SAMPLES/freq/2;

		// 1276 is the number of milliseconds a dit would
		// be for a 1wpm morse rate.  So, we need to divide
		// that number by the character rate to get the
		// length of a dit 

		dotlen = (int)Math.round(1000/charRate);
		wdotlen = (int)Math.round(1000/wordRate);
		// dotlen is now the number of milliseconds
		// needed for a dit.

		log.log(Level.FINE, "samps: {0}, millis for dit: {1}", new Object[]{samps, dotlen});

		// To calculate the number of samples in a dit
		// time we do:
		// samples is number of samples in a second.
		// so we need to find the number of samples in
		// the number of milliseconds in dotlen.
		dotlen = (SAMPLES * dotlen) / 1000 / samps;

		// The space between words is 7 dottime
		int splen = (int)(wdotlen*7*samps);
		splen = splen & ~1;
		byte[]bsp = new byte[splen];
		log.log(Level.FINE, "charRate: {0}, wordRate: {1}", new Object[]{charRate, wordRate});
		log.log(Level.FINE, "samples: {0}, dotlen: {1}, splen: {2}", new Object[]{SAMPLES, dotlen, splen});

		// The Space between two letters is = 3 Dots
		// The Space between parts of the same letter is = 1 Dot
		// so we need 2 dot times after the trailing 1 dot time
		// to get 3 dot times.
		log.log(Level.FINE, "samples for {0}hz: {1}", new Object[]{freq, samps});

		// Allocate the array for a half single wave form
		form = new int[ (int)samps+1 ];
		byte[]buf = new byte[SAMPLES*10];

		// Construct the wave magnitude for each sample.
		for( int i = 0; i < samps; ++i ) {
			int deg = (int)((180.0 * i)/samps);
			int val = (int)(volume * Math.sin( 
				(deg * 2 * Math.PI)/180 ) );
			form[i] = val;
		}
		for( int i = 0; i < str.length(); ++i ) {
			String set = chars.get(str.charAt(i));
			log.log(Level.FINE, "Send char: ''{0}''", str.charAt(i));
			for( int j = 0; j < set.length(); ++j ) {
				log.log(Level.FINE, "encoding: ''{0}''", set.charAt(j));
				off = encodeSym( buf, set.charAt(j), off );
			}
			log.log(Level.FINER, "writing out {0} bytes", off);
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

		log.log(Level.FINE, "symlen for ''{0}'': {1}", new Object[]{sym, symlen});
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
			log.log(Level.FINEST, "inserting sample: {0} at {1}", new Object[]{val, off});
		buf[off+1] = (byte)(val&0xff);
		buf[off] = (byte)(val>>8);
	}

	/**
	 *  Set the character rate in Words per Minute.This establishes the symbol rate for each
  letter.
	 * @param wpm
	 */
	@Override
	public void setCharRate( int wpm ) {
		charRate = wpm;
	}

	/** 
	 *  Set the word rate in Words per minute.This establishes the spacing rate
	 * @param wpm
	 */
	@Override
	public void setWordRate( int wpm ) {
		wordRate = wpm;
	}

	/**
	 *  This operation returns the next chars
	 *  that are being received.If receive is
  not supported, this method will throw
  the UnsupportedOperationException
	 *
	 * @return 
	 *  @throws UnsupportedOperationException if receive is not supported
	 *            by the implementation.
	 */
	@Override
	public String receiveMorse() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Receive not supported");
	}
	
	/**
	 *  @return whether receive is supported by the
	 *  implementation.
	 */
	@Override
	public boolean isReceiveSupported() {
		return false;
	}
}