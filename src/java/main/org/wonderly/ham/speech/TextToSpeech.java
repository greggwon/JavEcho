package org.wonderly.ham.speech;

import java.util.regex.*;
import javax.speech.*;
import javax.speech.synthesis.*;
import java.util.*;
import java.security.*;
import java.util.logging.*;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

public class TextToSpeech {
    Synthesizer synth = null; 
    private boolean useFreeTTS = true;
    Logger log = Logger.getLogger( "org.wonderly.ham.echolink" );

    public TextToSpeech() {
//    	log.setUseParentHandlers(false);
//    	ConsoleHandler h;
//    	log.addHandler( h = new ConsoleHandler() );
//    	h.setFormatter( new org.wonderly.log.StreamFormatter() );

    	createSynth();
    }
    
    protected void createSynth() {
    	if( synth != null )
    		synth.cancelAll();
    	synth = null;
        if( useFreeTTS == false ) {
        	log.info("Creating default JSAPI");
            synth = createDefaultJSAPIVoice();
        } else {
        	log.info("Using FreeTTS");
            synth = createFreeTTSVoice();
        }
//        speak("W5GGW's ECHO LINK");
    }

    public void speak(Speakable sp ) throws JSMLException {
    	final Object lock = new Object();
    	final boolean ended[] = new boolean[1];
//    	createSynth();
    	synth.speak( sp, new SpeakableAdapter() {
    		public void speakableEnded(SpeakableEvent e) {
    			synchronized( lock ) {
    				ended[0] = true;
    				log.info("Notify speech ended");
    				lock.notifyAll();
    			}
    		}
    	} );
    	synchronized( lock ) {
    		try {
    			log.info("Waiting for speakable to end, yet="+ended[0]);
    			if( !ended[0] )
    				lock.wait(5000);
    			log.info("Speakable ended ("+ended[0]+"): "+sp);
    		} catch( Exception ex ) {
    			log.log(Level.INFO, ex.toString(), ex );
    		}
    	}
    }
    public void speak(String text) {
        if (synth != null) {
        	log.info("Speaking: "+text);
        	synth.speakPlainText(text, null);
        }
    }
    
	private Synthesizer createDefaultJSAPIVoice() {
		Synthesizer ns = null;
		
		try  {
		    ns = Central.createSynthesizer(null);
		    if (ns != null) {
		        ns.allocate();
		        ns.resume();
		    }
		} catch( EngineException ex ) {
			log.log( Level.FINE, ex.toString(), ex );
		    ns = null;
		} catch( AudioException ex ) {
			log.log( Level.FINE, ex.toString(), ex );
		    ns = null;
		} catch( NoClassDefFoundError ex ) {
			log.log( Level.SEVERE, ex.toString(), ex );
		    ns = null;
		}
		return ns;
	}

	private Synthesizer createFreeTTSVoice() {
		Synthesizer ns = null;
		try  {
	   		log.fine("Create kevin16 voice");
	        Voice kevin16 = new Voice("kevin16", 
	        	Voice.GENDER_FEMALE,
	            Voice.AGE_TEENAGER, null);
//	        log.fine("Voice pitch: "+kevin16.getPitch() );
	        log.fine("Voice age: "+kevin16.getAge() );
			SynthesizerModeDesc desc =
	           new SynthesizerModeDesc(null, "general",
	           		null, null, null);
	       
	        log.fine( "created mode desc general");
	        FreeTTSEngineCentral central = new FreeTTSEngineCentral();
	       
	        log.fine("Created TTSEngineCentral");
	        EngineList list = central.createEngineList(desc); 
	       
	        log.fine("get engine list: "+list.size()+" elements = "+list );
	       
	        if (list.size() > 0) { 
	            EngineCreate creator = (EngineCreate) list.get(0); 
	            log.info("Creating new engine with: "+creator );
	            ns = (Synthesizer) creator.createEngine(); 
	            log.info("new engine is: "+ns );
	        } 
			if (ns != null) {
				log.fine("allocate" );
				ns.allocate();
				
				log.fine("resume" );
				ns.resume();
	
				log.fine("setVoice" );
				ns.getSynthesizerProperties().setVoice(kevin16);
	
				log.fine("init done" );
			}
	    } catch( EngineException ex) {
	    	log.log( Level.FINE, ex.toString(), ex );
	        ns = null;
	    } catch( AudioException ex ) {
	    	log.log( Level.FINE, ex.toString(), ex );
	        ns = null;
	    } catch( NoClassDefFoundError ex ) {
	    	log.log( Level.INFO, ex.toString(), ex );
	        ns = null;
	    }  catch( Throwable ex ) {
	    	log.log( Level.SEVERE, ex.toString(), ex );
	        ns = null;
		}
	    return ns;
    }
	
	public static void main(final String[] sargs) throws Exception {
		final String[]args = new String[] {"testing","testing","testing",
			"1", "2", "3", "4", "testing"};
		TextToSpeech ts = new TextToSpeech();
		for (int i = 0; i < args.length; i++) {
			ts.speak(args[i]);
		}
//		System.exit(1);
	}
}