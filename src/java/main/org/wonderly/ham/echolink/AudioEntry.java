package org.wonderly.ham.echolink;
 
/**
 *  This class is used to hold a buffer of audio data
 *  in the audio queuing mechanisms
 */
class AudioEntry {
	byte[]data;
	AudioEntry next;
	public AudioEntry( byte[]arr ) {
		data = arr;
	}
}