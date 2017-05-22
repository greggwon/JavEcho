package org.wonderly.ham.echolink;

import javax.sound.midi.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.wonderly.awt.*;

/**
 *  This still needs work, creates silence for now
 */
public class WavAudioProvider implements AudioProvider {
	public void connected() throws IOException {
	}
	public void disconnect() throws IOException {
	}
	public void over() throws IOException {
	}
	public void transmit() throws IOException {
	}
	public void receive() throws IOException {
	}
	public void alarm() throws IOException {
	}
	public JComponent getEditor() {
		JPanel p = new JPanel();
		p.add( new JLabel ("No Editor Provided" ) );
		return p;
	}
}