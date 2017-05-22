package org.wonderly.swing.net;

import java.util.EventObject;
import java.net.*;

public class PageEvent extends EventObject {
	URL opened;
	public PageEvent( Object source, URL u ) {
		super(source);
		opened = u;
	}
	
	public URL getURL() {
		return opened;
	}
}