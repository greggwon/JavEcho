package org.wonderly.swing.net;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.wonderly.swing.*;
import org.wonderly.awt.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.text.html.*;
import javax.swing.text.*;
import java.io.*;
//import org.jdesktop.jdic.browser.*;

/*
import java.net.*;
import javax.swing.*;

public class BrowserTest {
    public static void main(String[] args) throws Exception {
        
        WebBrowser browser = new WebBrowser();
        browser.setURL(new URL("http://java.net"));
        
        JFrame frame = new JFrame("Browser Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(browser);
        frame.pack();
        frame.setSize(500,500);
        frame.setVisible(true);
    }
}
*/

/**
 *  This is a simple browser.  Should investigate JDIC at some point.
 */
public class HTMLBrowser extends JPanel {
	JEditorPane brws;
//	WebBrowser wbrws;
	JButton bback;
	JButton bgo;
	JTextField brtxt;
	ExceptionHandler eh;
	URL currentURL;
	Stack<URL> urls = new Stack<URL>();
	private static Logger log = Logger.getLogger( HTMLBrowser.class.getName() );
	Vector<PageListener>lis = new Vector<PageListener>();

	public void addPageListener( PageListener pl ) {
		lis.addElement(pl);
	}
	
	public void removePageListener( PageListener pl ) {
		lis.removeElement( pl );
	}

	public static void main( String args[] ) throws Exception {
		JFrame f = new JFrame( "Testing" );
//		WebBrowser w = new WebBrowser();
//		f.getContentPane().add(w);
		HTMLBrowser br = new HTMLBrowser(new ExceptionHandler() {
			public void reportException( Throwable ex ) {
				ex.printStackTrace();
			}
		});
		f.setContentPane(br);
		br.setPage(new URL("http://www.wonderly.org") );
		f.pack();
//		f.setSize(500,500);
		f.setLocationRelativeTo(null);
		f.addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent ev ) {
				System.exit(1);
			}
		});
		f.setVisible(true);
//		w.setURL(new URL("http://www.artima.com"));
	}

	public void setPage( URL url ) throws IOException {
//		wbrws.setURL( currentURL = url);
		brws.setPage( currentURL = url );
		brtxt.setText( url.toString() );
	}

	public void backup() throws IOException {
		URL u = popURL();
		setPage( u );
	}

	/**
	 *  Push the indicated URL onto the history stack
	 */
	public void pushURL( URL url ) {
		urls.push(url);
	}

	/**
	 *  Get the previous URL
	 */
	public URL popURL() {
		if( urls.isEmpty() )
			return null;
		return (URL)urls.pop();
	}

	public HTMLBrowser(JFrame par, final ExceptionHandler eh) {
		this((Window)par, eh );
	}

	public HTMLBrowser( JDialog par, final ExceptionHandler eh) {
		this((Window)par, eh );
	}

	public HTMLBrowser(final ExceptionHandler eh) {
		this((Window)null,eh);
	}
	
	Window win;
	public HTMLBrowser(Window win, final ExceptionHandler eh) {
		this.eh = eh;
		this.win = win;
		JPanel web = this;
		JPanel brp = new JPanel();
		Packer brpk = new Packer( brp );
		
		brws = new JEditorPane("text/html", "<html><b>Home Page Here</b></html>");
//		wbrws = new WebBrowser();
//		wbrws.setSize( 500, 500 );
		Packer wpk = new Packer(web);
		brpk.pack( new JScrollPane( brws ) ).gridx(0).gridy(0).fillboth();
//		brpk.pack( wbrws ).gridx(0).gridy(0).fillboth();
		final JLabel prog = new JLabel();
		brpk.pack( prog ).gridx(0).gridy(1).fillx();
		int wy = -1;
		final JButton bgo = new JButton("Go");
		bback = new JButton("Back");
		brtxt = new JTextField();
		brtxt.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				URL url = null;
				try {
					url = new URL( brtxt.getText() );
				} catch( MalformedURLException ex ) {
					if( ex.toString().startsWith("java.net.MalformedURLException: no protocol:") ) {
						try {
							url = new URL( "http://"+brtxt.getText() );
							brtxt.setText( "http://"+brtxt.getText() );
						} catch( Exception exx ) {
							eh.reportException(ex);
							url = null;
						}
					} else {
						eh.reportException(ex);
						url = null;
					}
				} catch( Exception ex ) {
					eh.reportException(ex);
					url = null;
				}
				if( url != null ) {
					pushURL( currentURL );
					openURL( url, brws, brtxt, bback );
				}
			}
		});
		bback.setEnabled(false);
		brws.setEditable(false);
		HyperlinkListener listener = new HyperlinkListener() {
			public void hyperlinkUpdate( HyperlinkEvent ev ) {
				HyperlinkEvent.EventType ty = ev.getEventType();
				if( ty == HyperlinkEvent.EventType.ENTERED ) {
					prog.setText( ev.getURL() == null ? 
						"No URL provided" : ev.getURL().toString() );
				} else if( ty == HyperlinkEvent.EventType.EXITED ) {
					prog.setText("");
				} else if( ty == HyperlinkEvent.EventType.ACTIVATED ) {
//					progress("pushing: "+currentURL);
					if (ev.getURL() != null ) {
						bback.setEnabled(true);
						bback.repaint();
						pushURL( currentURL );
						openURL( ev.getURL(), brws, brtxt, bback );
					}
				}
			}
		};
		brws.addHyperlinkListener( listener);
		bgo.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				try {
					bback.setEnabled(true);
					bback.repaint();
					URL url = new URL( brtxt.getText() );
					pushURL( currentURL );
					openURL( url, brws, brtxt, bback );
				} catch( Exception ex ) {
					eh.reportException(ex);
				}
			}
		});
		bback.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				try {
					URL u = popURL();
					bback.setEnabled(urls.isEmpty()==false);
					bback.repaint();
					if( u != null ) {
//						progress("going back to: "+u+", have: "+urls );
						openURL( u, brws, brtxt, bback );
					}
				} catch( Exception ex ) {
					eh.reportException(ex);
				}
			}
		});
		wpk.pack( bback ).gridx(0).gridy(wy);
		wpk.pack( bgo ).gridx(1).gridy(++wy);
		wpk.pack( brtxt ).gridx(2).gridy(wy).fillx();
		wpk.pack( brp ).gridx(0).gridy(++wy).fillboth().gridw(3);
//		tp.setOpaque(true);
//		mmpk.pack(tp).fillboth().gridx(0).gridy(1);
	}
	public void openURL( URL url ) {
		openURL( url, brws, brtxt, bback );
	}
	/**
	 *  Open the passed URL in the users browser tab.
	 */
	private void openURL( URL u, 
		final JEditorPane brws,
//		final WebBrowser brws,
		JTextField brtxt, JButton bback ) {
		try {
			URLConnection c = u.openConnection();
//			Map m = c.getHeaderFields();
//			Iterator ir = m.entrySet().iterator();
//			while(ir.hasNext()) {
//				progress("::"+ir.next());
//			}
//			DataInputStream is = new DataInputStream( c.getInputStream() );
//			String str;
//			while( (str = is.readLine()) != null ) {
//				progress("."+str);
//			}
//			is.close();
			String cty = c.getContentType();
			if( cty == null ) {
				brws.setContentType( "text/html" );
				brws.setDocument(brws.getEditorKit().createDefaultDocument());
				String h = "<html><h1>Page Not Found or Bad ContentType</h1></html>";
				brws.setText( h );
				return;
			}
//			log.fine("content-type: "+cty );
			if( cty.startsWith("image/") ) {
				brws.setContentType( "text/html" );
				brws.setDocument(brws.getEditorKit().createDefaultDocument());
				String h = "<html><img src=\""+u+"\"></html>";
				brws.setText( h );
			} else {
				if( cty.startsWith("text/html") )
					cty = "text/html";
				brws.setContentType( cty );
				brws.setDocument(brws.getEditorKit().createDefaultDocument());
				try {
//					wbrws.setURL( u );
					brws.setPage( u );
				} catch( FileNotFoundException ex ) {
					IOException ie = new IOException( "Can't access: "+u );
					ie.initCause( ex );
					brws.setText( "<html>"+
						"<head>"+
						"<title>"+u+" not found</title>"+
						"</head>"+
						"<body>"+
						"<h2>"+u+" not found</h2>"+
						"</body>"+
						"</html>");
					brws.setContentType("text/html");
					throw ie;
				}
			}
			currentURL = u;
			brws.getDocument().render( new Runnable() {
				public void run() {
					HTMLDocument.Iterator i = 
						((HTMLDocument)brws.getDocument()).
							getIterator( HTML.Tag.HTML );
					System.out.println("HTMLDocument.Iterator("+((i==null?false:i.isValid()))+"): "+i );
					while( i != null && i.isValid() ) {
						i.next();
						AttributeSet as = i.getAttributes();
						System.out.println("next attributes: "+as );
						Enumeration e = as.getAttributeNames();
						while( e.hasMoreElements() ) {
							System.out.println("Attribute("+as+"): name="+e.nextElement() );
						}
					}
				}
			});
			brtxt.setText( currentURL.toString() );
			bback.setEnabled( urls.size() > 0 );
//			if( pr.isSendingCurrentPage() ) {
//				ssa.sendSdesWithWebPage( currentWebPage() );
//			}
		} catch( Exception ex ) {
			eh.reportException(ex);
			try {
				brws.setPage( currentURL = popURL() );
//				wbrws.setURL( currentURL = popURL() );
				brtxt.setText( currentURL.toString() );
			} catch( Exception exx ) {
				eh.reportException(exx);
			}
		}
	}
}