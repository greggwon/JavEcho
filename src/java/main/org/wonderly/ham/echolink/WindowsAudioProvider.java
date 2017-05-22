package org.wonderly.ham.echolink;

import javax.sound.sampled.*;
import javax.swing.*;
import org.wonderly.awt.*;
import java.util.*;
import java.io.*;
import java.util.logging.*;

/**
 *  This is a windows audio file AudioProvider.  It uses
 *  windows-XP provided audio files under %SystemRoot%\media.
 *  To replace the use of these files, you can set system
 *  properties to point at each file with
 *<pre>
 *  <code>-Dorg.wonderly.ham.echolink.winaudio.connect=</code><i>File path</i>
 *  <code>-Dorg.wonderly.ham.echolink.winaudio.disconnect=</code><i>File path</i>
 *  <code>-Dorg.wonderly.ham.echolink.winaudio.alarm=</code><i>File path</i>
 *  <code>-Dorg.wonderly.ham.echolink.winaudio.transmit=</code><i>File path</i>
 *  <code>-Dorg.wonderly.ham.echolink.winaudio.receive=</code><i>File path</i>
 *</pre>
 *  You can also create the file <code>&lt;userhome&gt;/.javecho.winaudio.properties</code>
 *  and put property definitions in there with the lines shown above,
 *  but, without the <b>-D</b> on the front.
 */
public class WindowsAudioProvider implements AudioProvider {
	protected String winroot = "C:/windows";
	protected Hashtable<String,String>flist = new Hashtable<String,String>();
	protected Logger log = Logger.getLogger( getClass().getName() );

	public WindowsAudioProvider() {
		if( System.getProperty("SystemRoot") != null )
			winroot = System.getProperty( "SystemRoot" );
		init();
	}

	protected final String filePath( String which ) {
		return flist.get(which);
	}

	protected String[]events = { "connect", "disconnect", "alarm",
		"transmit", "receive" };

	protected final void init() {
		flist.put("connect",winroot+"/media/ringin.wav");
		flist.put("disconnect",winroot+"/media/ding.wav");
		flist.put("alarm",winroot+"/media/tada.wav");
		flist.put("transmit",winroot+"/media/notify.wav");
		flist.put("receive",winroot+"/media/ding.wav");
		Properties p = System.getProperties();
		File f = new File( System.getProperty("user.home")+
			File.separator+
			".javecho"+
			File.separator+
			"winaudio.properties" );
		if( f.exists() ) {
			try {
				FileInputStream rd = new FileInputStream( f );
				try {
					p = new Properties();
					p.load( rd );
				} finally {
					rd.close();
				}
			} catch( FileNotFoundException ex ) {
				log.log(Level.FINE, ex.toString(), ex );
			} catch( Exception ex ) {
				log.log(Level.SEVERE, ex.toString(), ex );
			}
		}
		for( int i = 0; i < events.length; ++i ) {
			String pev = System.getProperty(
				"org.wonderly.ham.echolink.winaudio."+events[i] );
			if( pev != null ) {
				flist.put( events[i], pev );
			}
		}
	}
	
	protected final void playFilePath( String type ) {
		playFile( type, filePath(type) );
	}

	
	public void connected() {
		playFilePath("connect");
	}

	public void disconnect() {
		playFilePath("disconnect");
	}

	public void over() {
		playFilePath("over");
	}

	public void alarm() {
		playFilePath("alarm");
	}
	
	public JComponent getEditor() {
		JPanel p = new JPanel();
		Packer pk = new Packer();
		
		return p;
	}

	public static void main( String args[] ) throws Exception {
		WindowsAudioProvider ap = new WindowsAudioProvider();
		if( args.length > 0 ) {
			ap.winroot = args[0];
			ap.init(); // Use new winroot
		}
		ap.connected();
		ap.disconnect();
		ap.alarm();
		ap.transmit();
		ap.receive();
	}
		
	protected final void playFile( String type, String file ) {
		SourceDataLine line;
		try {
			File f = new File(file);
			if( f.exists() == false ) {
				System.out.println("Can't find: "+f );
				return;
			}
			log.info("playing ("+type+"): "+file );
			AudioFileFormat aff = AudioSystem.getAudioFileFormat( f );
			AudioInputStream ai = AudioSystem.getAudioInputStream( f );
			AudioFormat playFormat = aff.getFormat();
	
			DataLine.Info info = new DataLine.Info(
				SourceDataLine.class, playFormat);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(playFormat,32768);	
			line.start();
			byte[]buf = new byte[8000];
			int n;
			while( ( n = ai.read(buf,0,buf.length) ) > 0 ) {
//				System.out.println("writing: "+n);
				line.write( buf, 0, n );
			}
			line.drain();
			line.stop();
			line.close();
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
	}
	public void transmit() {
		playFilePath("transmit");
	}
	public void receive() {
		playFilePath("receive");
	}
}