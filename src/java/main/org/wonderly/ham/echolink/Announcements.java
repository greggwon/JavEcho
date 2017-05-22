package org.wonderly.ham.speech;

import org.wonderly.ham.echolink.*;
import javax.speech.synthesis.*;

/**
 *  This class uses the 
 * {@link org.wonderly.ham.speech.TextToSpeech} class
 * to provide an implementation of the Announcer interface
 */
public class Announcements implements Announcer {
	protected TextToSpeech sp;

	public Announcements() {
		sp = new TextToSpeech();
	}
	
	public void say( Parameters p, String text ) {
		sp.speak(text);
	}

	/**
	 * A class that tries to do some JSML markup to provide
	 * some better pronounciations of letters such as 'a'.
	 */
	static class SpeakableText implements Speakable {
		String str;
		Synthesizer synth;
		public SpeakableText(Synthesizer sz) {
			this();
			synth = sz;
		}
		public String toString() {
			return "Speakable: \""+str+"\"";
		}
		public SpeakableText() {
			str = "<jsml> "+
				"<div type=\"paragraph\">";
		}
		public void addCall( String call ) {
			String trail = "";
			if( call.charAt(0) == '*' ) {
				call = call.split("\\*")[1];
				trail = " conference";
			} else if( call.toLowerCase().endsWith("-r") ) {
				call = call.split("-")[0];
				trail = " repeater";
			} else if( call.toLowerCase().endsWith("-l") ) {
				call = call.split("-")[0];
				trail = " link";
			}
			for( int i = 0; i < call.length(); ++i ) {
				char ch = call.charAt(i);
//				if( synth != null )
//					System.out.println("ph: "+synth.phoneme(ch+"") );
				if( ch == 'a' || ch == 'A' ) {
					str += "<phoneme>&#304;</phoneme> ";
				} else {
					str += ch+" ";
				}
			}
			str += trail;
		}
		public void addText( String text ) {
			str += text;
		}

		public void end() {
			str += "</div> </jsml> ";
		}
		public String getJSMLText() {
//			System.out.println("JSML: "+str );
			return str;
		}
	}

	public static void main( String args[] ) throws Exception {
		Announcements ann = new Announcements();

		SpeakableText spk = new SpeakableText(ann.sp.synth);
		spk.addCall("n5uua");
		spk.addText(" link has connected");
		spk.end();
		ann.sp.speak(spk);

		spk = new SpeakableText(ann.sp.synth);
		spk.addCall("ab5ef");
		spk.addText(" repeater has disconnected");
		spk.end();
		ann.sp.speak(spk);

		spk = new SpeakableText(ann.sp.synth);
		spk.addCall("w5ggw");
		spk.addText(" echolink system");
		spk.end();
		ann.sp.speak(spk);

		spk = new SpeakableText(ann.sp.synth);
		spk.addText("Welcome to the ");
		spk.addCall("w5ggw");
		spk.addText("echolink node number ");
		spk.addCall("29387");
		spk.end();
		ann.sp.speak(spk);

		spk = new SpeakableText(ann.sp.synth);
		spk.addText("This node is running jav echo, version ");
		spk.addText("1 dot 3, build number 37 ");
		spk.end();
		ann.sp.speak(spk);

		spk = new SpeakableText(ann.sp.synth);
		spk.addText("Please listen before calling see queue.");
		spk.end();
		ann.sp.speak(spk);

		System.exit(1);
	}

	private void announce( String before, String call, String after ) {
		SpeakableText st = new SpeakableText();
		if( before != null )
			st.addText( before );
		if( call != null )
			st.addCall( call );
		if( after != null )
			st.addText( after );
		st.end();
		try {
			sp.speak( st );
		} catch( Exception ex ) {
		}
	}
	public void connected( Parameters p, String call ) {
//		call = callDescr(call);
		if( p.isSignalsEventsConnectedDefault() ) {
			announce( "connecting to ", call, null );
		}
	}

	public void disconnected( Parameters p, String call) {
//		call = callDescr(call);
		if( p.isSignalsEventsDisconnectedDefault() ) {
			announce( "disconnecting from ", call, null );
		}
	}

	/**
	 *  Speak the passed string
	 */
	public void stationInfo( Parameters p, String msg ) {
		if( p.isSignalsEventsStationInfoDefault() ) {
			announce( msg, null, null );
		}
	}

	public void linkUp( Parameters p ) {
		if( p.isSignalsEventsLinkUpDefault() ) {
			announce( null, p.getCallSign(), " link active" );
		}
	}

	public void linkDown( Parameters p ) {
		if( p.isSignalsEventsLinkDownDefault() ) {
			announce( null, p.getCallSign(), " link down" );
		}
	}

	public void courtesyTone( Parameters p ) {
		if( p.isSignalsEventsCourtesyToneDefault() ) {
			if( p.isOptionsPlaycourtesytone() ) {
//			sp.speak( "beep" );
			}
		}
	}

	public void activityReminder( Parameters p ) {
		if( p.isSignalsEventsLinkDownDefault() ) {
			if( p.isOptionsReminderPlay() ) {
				announce( null, p.getCallSign(), null );
			}
		}
	}
}