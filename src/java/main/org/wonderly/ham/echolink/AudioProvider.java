package org.wonderly.ham.echolink;

import java.io.*;
import javax.swing.JComponent;

/**
 *  This interface provides the interface point for 
 *  creating audible or visual indications of the
 *  echolink station events.  This is called AudioProvider
 *  but clearly could use visual or tactile events to
 *  provide the services of these methods
 */
public interface AudioProvider {
	public void connected() throws IOException;
	public void disconnect() throws IOException;
	public void over() throws IOException;
	public void transmit() throws IOException;
	public void receive() throws IOException;
	public void alarm() throws IOException;
	public JComponent getEditor();
}