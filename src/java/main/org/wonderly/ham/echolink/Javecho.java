package org.wonderly.ham.echolink;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TimerTask;
import java.util.TooManyListenersException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.wonderly.awt.Packer;
import org.wonderly.ham.MorseControl;
import org.wonderly.ham.audio.AudioMorseSender;
import org.wonderly.io.HashtableReader;
import org.wonderly.io.VectorReader;
import org.wonderly.logging.SimpleFormatter;
import org.wonderly.swing.*;
import org.wonderly.swing.net.HTMLBrowser;
import org.wonderly.swing.net.PageEvent;
import org.wonderly.swing.net.PageListener;

/**
 *  This is the main class for the <a href="http://javecho.dev.java.net">Javecho</a> VOIP
 *  application for use in interlink Amatuer Radio stations, repeaters and links etc.
 *
 * <dl>
 * <dt><b>Logger: </b><dd><code>org.wonderly.ham.echolink<code><br>
 * <dt>Properties:<dd>Used by logger
 * <dt><b>log file name</b><dd><code>org.wonderly.ham.echolink.log.file</code>
 * <dt><dd><code>${user.home}/.javecho/javecho.log.%g</code>
 * <dt><b>log files sizes</b><dd><code>org.wonderly.ham.echolink.log.size<code><br>
 * <dt><b>log files count</b><dd><code>org.wonderly.ham.echolink.log.count<code><br>
 * </dl>
 */
public class Javecho extends JFrame implements ExceptionHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Access to the echolink login server operations */
	private volatile ServerAccess acc;
	public static final String version = Version.version;
	/** The station list of all connected stations */
	private List<Entry> staList;
	/** The station list of all friend locations */
	private List<Entry> frList;
	/** The trees of various views of the station data */
	private JTree stations, favorites, lastSelTree, locations;
	/** Tree models for all stations and friends trees */
	private MyModel trmod, frmod;
	/** All locations model which includes support for country etc trees */
	private MyLocModel locmod;
	/** The list info.txt display area */
	private JTextArea statList;
	/** The users persistent preferences */
	private Preferences prefs;
	/** The connected station label */
	private JLabel conn;
	/** The tabbed pane of station lists */
	private JTabbedPane stTabs;
	/** The connection manager provides access to the control of connections */
	volatile ConnectionManager ssa;
	private JLabel last;
	private JLabel ipaddr;
	private static JLabel stinfo, oinfo;
	private JPanel idPan;
	private JButton msgSend;
	private JEditorPane msgPane;
	private JTextArea infoPane;
	private boolean enableSysopMode = false;
	/** Announces the connection and disconnection etc. */
	private volatile Announcer ann;
	/** The card layout for the audio level and TX status display */
	private CardLayout auCard;
	/** The audio level display for RX and TX audio */
	private volatile AudioLevel average;
	/** Loggers */
	private final static Logger log = Logger.getLogger( Javecho.class.getName() );
	private final static Logger proglog = Logger.getLogger( Javecho.class.getName()+".progress" );
	private final static Logger seriallog = Logger.getLogger( Javecho.class.getName()+".serial" );
	private JPanel aupan;
	/** The TX indicator, a disabled button */
	private JButton txbut;
	/** Programatic parameters */
	static Parameters pr;
	/** The actions supported inside the GUI */
	private Map<String,Action> actions = new HashMap<String,Action>();
	public static String oneAddress; // Set this to an IP address for debugging...
	private String site = "";
	/** The connection statistics accumulation and display */
	private ConnectionStatistics cons;
	/** The summary of stations dialog */
	private StationSummary stsumm;
	/** Listeners for link up/down events */
	private List<LinkEventListener<?>> listeners;
	/** Tabbed pane for HTML, debugging and echolink tabs */
	private JTabbedPane tabs;
	/** Volume gain control for RX audio */
	private JSlider gainSlider;
	/** A model for the volume gain controller */
	private ControlRangeModel crm;
	JPanel gainPan;
	/** Volume mute control */
	private JCheckBox volMute;
	/** Border on gain control */
	private TitledBorder gainBorder;
	private JLabel gainVal;
	/** Drag source for callsign drag and drop */
	private DragSource dgs;
	/** The lines of text returned from the connection event by the echolink servers */
	private List<Entry> sysmsg;
	/** Implementation of audio tones for event notification */
	private AudioProvider audio;
	private java.util.Timer timer = new java.util.Timer();
	private URL currentURL;
	/** List of stations we have contacted */
	private Map<String,String> contacts = new HashMap<String,String>();
	/** Web page stack of urls */
	private Stack<URL> urls = new Stack<URL>();
	private JPanel controls;
	/** The message text output component */
	private JEditorPane msgData;
//	JEditorPane brws;
//	JTextField brtxt;
	/** A morse code sender implementation for link and repeater ID */
	private MorseControl ms = new AudioMorseSender();
//	JButton bback;
	/** An HTML browsing pane */
	private HTMLBrowser web;
	/** Manager for making sure we ID the station periodically and don't do it repeatedly */
	private StationIDManager statid;
	/** Control of the station by received PTT tones */
	private SysopRecvAvailControl sysopPtt;

	/** Various labels of status text and progress indication */
	private JLabel rxstat;
	private JLabel txstat;
	private JLabel netstat;
	private JLabel xtime;
	private JPanel inpan;
	private JLabel rcvSerialCD, rcvSerialDSR, rcvSerialCTS;
//	private volatile int sDtI, sCtlI, rDtI, rMisI, rSeqI, rCtlI;
	private JLabel sDt, sCtl, rDt, rMis, rSeq, rCtl;

	
	/**
	 *  Get the ServerAccess instance that is active
	 */
	ServerAccess serverAccess() {
		return acc;
	}

	/**
	 *  A control point for managing the ability to perform sysop operations.
	 *  This is currently just used to manage debugging of sysop mode without
	 *  enabling it for general use.
	 */
	public boolean isSysopModeAvailable() {
		return enableSysopMode;
	}

	/**
	 *  Get the list of all known stations
	 */
	public List<Entry> getStationList() {
		return staList;
	}

	/**
	 *  Get the statistics associated with the current connection.
	 */
	public ConnectionStatistics getConnectionStats() {
		return cons;
	}

	/**
	 *  @return the current version string
	 */
	public static String myVersion() {
		return "1.03";
	}
	
	@Override
	public String toString() {
		return "Javecho";
	}

	/**
	 *  Add a listener to receive notification of requested audio events.
	 */
	public void addAudioEventListener( AudioEventListener lis ) {
		if( ssa != null )
			ssa.addAudioEventListener( lis );
	}

	/**
	 *  Set the speed that morse IDs and other morse code should be sent
	 */
	public void setMorseWPM( int val ) {
		ms.setWordRate( val );
		// For good separation make the char rate faster
		ms.setCharRate( val+3 );
	}
	
	/**
	 *  Set the pitch to use for morse code.
	 */
	public void setMorseIdFreq( int freqHz ) throws UnsupportedOperationException {
		ms.setPitchFreq( freqHz );
	}

	/**
	 *  Set the volume level for the morse ID
	 */
	public void setMorseIdVolume( int db ) throws IOException {
		ms.setVolumeLevelDb( db );
	}

	/**
	 *  Send the indicated string out in morse code
	 */
	public void sendMorse( PTTControlParms pttCtl, String str ) throws IOException {
		log.info("Send morse code: \""+str+"\""+
			", pitch: "+pr.getIdentMorsePitch()+
			", vol: "+pr.getIdentMorseVolume()+
			", spd: "+pr.getIdentMorseSpeed());
		ms.setPitchFreq( pr.getIdentMorsePitch() );
		ms.setVolumeLevelDb( pr.getIdentMorseVolume() );
		setMorseWPM( pr.getIdentMorseSpeed() );

		try {
			log.info("Morse PTT up");
			raisePtt( pttCtl );
		} catch( IllegalArgumentException ex ) {
			noPttWorking(ex);
		}

		log.info("Send chars");
		ms.sendMorseChars( str );

		log.info("Morse PTT down");
		try {
			dropPtt( pttCtl );
		} catch( IllegalArgumentException ex ) {
			noPttWorking(ex);
		}
	}
		
	void noPttWorking( Exception ex ) {
		log.log( Level.WARNING, ex.toString(), ex );
		showBadIndicator( txstat, true, ex.toString() );
	}

	/**
	 *  Get the system configuration parameters.
	 */
	public Parameters getParms() {
		return pr;
	}
	
	/**
	 *  Get the currently active Announcer.
	 */
	public Announcer getAnnouncer() {
		return ann;
	}
	
	/**
	 *  Transmit the passed files contents as an audio stream
	 */
	public void transmitAudio( PTTControlParms pttCtl, String file) throws IOException {
		FileInputStream fs = new FileInputStream( file );
		try {
			AudioFileFormat fmt = AudioSystem.getAudioFileFormat( new File(file) );
			log.info(file+": "+fmt);
			AudioInputStream as = AudioSystem.getAudioInputStream( fs );
			ByteArrayOutputStream ba = new ByteArrayOutputStream();
			byte[]data = new byte[102400];
			int n;
			while( ( n = as.read(data)) > 0 ) {
				log.info("read "+n+" bytes from "+file );
				ba.write(data,0,n);
			}
			as.close();
			ba.close();
			transmitAudio( pttCtl, ba.toByteArray(), ba.size(), fmt.getFormat() );
		} catch( UnsupportedAudioFileException ex ) {
			reportException(ex);
		} finally {
			fs.close();
		}
	}

	/**
	 *  Transmit the passed byte array of data, up to cnt bytes, interpreted
	 *  using the passed AudioFormat
	 */
	public void transmitAudio( PTTControlParms pttCtl, byte[]data, int cnt, AudioFormat fmt ) {
		boolean raised = false;
		try {
			SourceDataLine dl = AudioSystem.getSourceDataLine( fmt );
			dl.open(fmt, cnt );
			try {
				dl.start();
				log.info("source data ("+fmt+") line: "+dl );
				raisePtt(pttCtl);
				raised = true;
				log.info(cnt+" total audio bytes to write: "+fmt);
				dl.write( data, 0, cnt );
				log.info("wrote "+cnt+" bytes to "+dl );
				dl.drain();
				dl.stop();
			} finally {
				dl.close();
			}
		} catch( LineUnavailableException ex ) {
			reportException(ex);
		} finally {
			if( raised )
				dropPtt( pttCtl );
		}
	}

	/**
	 *  Speak the passed text string using the active Announcer.
	 */
	public void transmitSpeech( PTTControlParms pttCtl, String text) {
		log.info("Raising PTT for TTS: \""+text+"\"" );
		try {
			raisePtt( pttCtl );	
		} catch( IllegalArgumentException ex ) {
			noPttWorking(ex);
		}
		try {
			Thread.sleep( 500 );
			ann.stationInfo( pr, text );
			Thread.sleep( 500 );
		} catch( Exception ex ) {
			log.log(Level.WARNING, ex.toString(), ex);
		} finally {
			dropPtt( pttCtl );
		}
	}

	/**
	 *  Set the control for user changed gain control for the
	 *  output volume.
	 */
	public void setGainControl(final Control gain) {
		runInSwing( new Runnable() {
			public void run() {
				crm.setControl( (FloatControl)gain, gainPan );
				gainPan.setVisible(true);
				gainPan.revalidate();
			}
		});
	}

	/**
	 *  Add a link status/progress listener
	 */
	public void addLinkEventListener( LinkEventListener<?> lis ) {
		listeners.add( lis );
	}

	/**
	 *  Remove a link status/progress listener
	 */
	public void removeLinkEventListener( LinkEventListener<?> lis ) {
		listeners.remove( lis );
	}

	/**
	 *  Send the indicated LinkEvent to all listeners
	 */
	protected void sendEvent( LinkEvent ev ) {
		log.finer("Sending linkevent to "+listeners.size()+" listeners: "+ev );
		for( int i = 0; i < listeners.size(); ++i ) {
			try {
				listeners.get(i).processEvent( ev );
			} catch( Exception ex ) {
				reportException(ex);
			}
		}
	}

	/**
	 *  Save current favorites data to disk
	 */
	public void saveFavorites() {
		try {
			FileOutputStream fs = new FileOutputStream( 
				new File( new File( System.getProperty("user.home"), ".javecho" ),
					"favorites" ) );
			try {
				ObjectOutputStream os = new ObjectOutputStream( fs );
				try {
			  		List friendData = frmod.getContents();
					os.writeObject( new Integer(6) );
					os.writeObject( friendData );
					os.writeObject( qsoHist.getHistory() );
					os.writeObject( qsoHist.getLocation() );
					os.writeObject( qsoHist.getSize() );
//					os.writeObject( almLog.getHistory() );
					os.writeObject( alarmMgr.almLog.getLocation() );
					os.writeObject( alarmMgr.almLog.getSize() );
					os.writeObject( alarmMgr.almEd.getHistory() );
					os.writeObject( alarmMgr.almEd.getLocation() );
					os.writeObject( alarmMgr.almEd.getSize() );
					os.writeObject( contacts );
				} finally {
					os.close();
				}
			} finally {
				fs.close();
			}
		} catch( Exception ex ) {
			reportException(ex);
		}
	}

	/**
	 *  Comparator for callsign based comparison of Entry objects
	 */
	static class EntryComparator implements Comparator<Entry> {
		public int compare( Entry o1, Entry o2 ) {
			Entry e1 = (Entry)o1;
			Entry e2 = (Entry)o2;
			return e1.getStation().getCall().compareTo( e2.getStation().getCall() );
		}
	}

	/**
	 *  Load the favorites data from the file and populate the
	 *  tree
	 */
	public void loadFavorites() {
		try {
			FileInputStream fi = new FileInputStream( 
				new File( new File( System.getProperty("user.home"), ".javecho" ),
					"favorites") );
			try {
				ObjectInputStream is = new ObjectInputStream(fi);
				try {
					int ver = ((Integer)is.readObject()).intValue(); // version
					final Vector<Entry> friendData = new VectorReader<Entry>().read(is);
					Collections.sort( friendData, new EntryComparator() );
					runInSwing( new Runnable() {
						public void run() {
							frmod.setData( friendData );
						}
					});
					if( ver > 1 ) {
						Vector<HistoryEntry> v = 
							new VectorReader<HistoryEntry>().read(is);
						qsoHist.setHistory( v );
						qsoHist.setLocation( (Point)is.readObject() );
						qsoHist.setSize( (Dimension)is.readObject() );
						if( ver <= 4 ) {
							Vector<StationData> sv = 
								new VectorReader<StationData>().
									read(is);
							alarmMgr.almLog.setHistory( sv );
						}
						alarmMgr.almLog.setLocation( (Point)is.readObject() );
						alarmMgr.almLog.setSize( (Dimension)is.readObject() );
					}
					if( ver > 2 ) {
						Vector<String> v =
							new VectorReader<String>().read(is);
						alarmMgr.almEd.setHistory( v );
					}
					if( ver > 3 ) {
						alarmMgr.almEd.setLocation( (Point)is.readObject() );
						alarmMgr.almEd.setSize( (Dimension)is.readObject() );
					}
					if( ver > 5 ) {
						contacts = new HashtableReader<String,String>(is).read();
					}
				} catch( IOException ex ) {
					reportException(ex);
					frmod.setData( new Vector<Entry>() );
				} finally {
					is.close();
				}
			} finally {
				fi.close();
			}
		} catch( FileNotFoundException ex ) {
			log.log(Level.WARNING, ex.toString(), ex);
		} catch( RuntimeException ex ) {
			log.log(Level.WARNING, ex.toString(), ex );
		} catch( Exception ex ) {

			reportException(ex);
		}
	}
	
	/**
	 *  Main entry point
	 */
	public static void main( String args[] ) throws Exception {
//		new Socket("nasouth.echolink.org", 5200).close();
		Logger.getLogger("").getHandlers()[0].setFormatter(new SimpleFormatter());
		new Javecho(args);
	}

	/**
	 *  Add a line of text to the chat window
	 */
	public void addChatText( String str ) {
		Document doc = msgPane.getDocument();
		try {
			while( str.endsWith("\r") || str.endsWith("\n") )
				str = str.substring(0,str.length()-1);
			doc.insertString( doc.getEndPosition().getOffset()-1,
						str+"\n", null );
			msgPane.setCaretPosition( doc.getEndPosition().getOffset()-1);
		} catch( Exception ex ) {
			reportException(ex);
		}
//				System.out.println("str: \""+str.trim()+"\"");
//		msgPane.append(str.replace('\r','\n')+"\n");
	}

	/**
	 *  Set the displayed station info text.  The text
	 *  will have all occurances of '\r' replaced with
	 *  '\n' and then just infoPane.setText() will be
	 *  used.  Thus, there is no explicit formatting done
	 *  on the text other than what is already present in
	 *  the text.
	 */
	public void setInfo( String txt ) {
		infoPane.setText( txt.trim().replace('\r','\n') );
	}

	/**
	 *  set the indicated label background color based on
	 *  how.  If true, red, otherwise the background of
	 *  {@link #inpan}.
	 */
	protected void showActiveIndicator( JLabel ind, boolean how ) {
		ind.setBackground( how ?
			Color.red : inpan.getBackground() );
		if( how )
			ind.setToolTipText( ind.getText()+" signal active" );
		else
			ind.setToolTipText( null );
	}

	/**
	 *  set the indicated label background color based on
	 *  how.  If true, red, otherwise the background of
	 *  {@link #inpan}.
	 */
	protected void showBadIndicator( JLabel ind, boolean how, String msg ) {
		ind.setBackground( how ?
			Color.yellow : inpan.getBackground() );
		ind.setToolTipText( how ? msg : null );
	}

	/**
	 *  Set the state of the TX indicator
	 */
	public void setTxIndicator(boolean txactive ) {
		log.log(Level.FINEST, "setTxIndicator("+txactive+")", new Throwable() );
		showActiveIndicator( txstat, txactive );
	}

	/**
	 * Set the state of the RX indicator
	 */
	public void setRxIndicator( boolean rxaudio ) {
		log.log(Level.FINEST, "setRxIndicator("+rxaudio+")", new Throwable() );
		showActiveIndicator( rxstat, rxaudio );
	}

	/**
	 *  set the state of the network connection status
	 */
	public void setNetIndicator( boolean rxnet ) {
		log.log(Level.FINEST, "setNetIndicator("+rxnet+")", new Throwable() );
		showActiveIndicator( netstat, rxnet );
	}

	/**
	 *  Set the current average and max audio level data
	 *  @param val the average audio level value from 0 to 32767
	 *  @param max the maximum audio level value from 0 to 32767
	 */
	public void setAverage( final int val, final int max ) {
		if( average != null ) {
			runInSwingLater( ()-> {
				average.setValues(val, max );
				average.repaint();
			});
		}
	}

	/** Site we are connected to */
	public String getSite() {
		return site;
	}

	/**
	 *  Check if connected to a single station, and put
	 *  the passed contact data in for the connected to call.
	 *  @param str the string description of contact information
	 */
	protected void setContacts( String str ) {
		if( ssa.getConnectCount() > 1 )
			return;
		List<Connection> v = ssa.getConnectedList();
		if( v.size() < 1 )
			return;
		Connection c = v.get(0);
		Entry et = findEntry( c.getName() );
		if( et != null ) {
			StationData sd = et.getStation();
			String call = sd.getCall();
			contacts.put( call, str );
		}
	}
	
	/**
	 *  Get access to the passed comm port
	 */
	private CommPortIdentifier getCommPort(String port) {
		Enumeration e = CommPortIdentifier.getPortIdentifiers();
		while( e.hasMoreElements() ) {
			CommPortIdentifier p = (CommPortIdentifier)e.nextElement();
			if( p.getName().equals( port ) ) {
				if( p.getPortType() != CommPortIdentifier.PORT_SERIAL ) 
					throw new IllegalArgumentException( port+": must specified serial port");
				return p;
			}
		}
		throw new IllegalArgumentException( "Can't find comm port named "+port );
	}

	private SerialPort pttCtrlPort;
	/**
	 *  Raise the PTT signal to trigger the transmitter.
	 */
	public void raisePtt( PTTControlParms pttCtl ) {
		if( pr.isUserMode() ) {
			log.finer("raisePtt called, not in sysop mode");
			return;
		}
		log.log(Level.FINER,
			"raisePtt: (port="+pttCtrlPort+") usermode? "+pr.isUserMode(),
			new Throwable("raisePtt called") );
//		if( pttCtrlPort != null ) {
//			try {
//				pttCtrlPort.close();
//			} catch( Exception ex ) {
//				reportException(ex);
//			}
//		}

		String port = pttCtl.getSerialPort();
		seriallog.info("raisePtt: "+pttCtl );
		if( pttCtl.isVoxPtt() == false ) {
			try {
				pttCtrlPort = (SerialPort)getOpenedPort(port);
				pttCtrlPort.setSerialPortParams(pr.isTxCtrlSerialspeed() ? 9600: 2400,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			} catch( Exception ex ) {
				IllegalArgumentException iae = new IllegalArgumentException("Port already in use? "+ex );
				throw ((IllegalArgumentException)iae.initCause(ex));
			}

			if( pttCtl.getSerialPttType() == PTTControlParms.SerialPttType.RTS ) {
				seriallog.info("raisePtt: raise RTS");
				pttCtrlPort.setRTS(true);
				setTxIndicator(true);
			} else if( pttCtl.getSerialPttType() == PTTControlParms.SerialPttType.DTR ) {
				seriallog.info("raisePtt: raise DTR");
				pttCtrlPort.setDTR(true);
				setTxIndicator(true);
			} else if( pttCtl.getSerialPttType() == PTTControlParms.SerialPttType.ASCII ) {
				seriallog.info("raisePtt: send 'T' out "+port);
				try {
					OutputStream os = pttCtrlPort.getOutputStream();
					os.write("T\r".getBytes());
					os.flush();
					setTxIndicator(true);
				} catch( Exception ex ) {
					seriallog.log(Level.WARNING,ex.toString(), ex);
				}
			}
			/** Wait for PTT to settle */
			try {
				Thread.sleep( 750 );
			} catch( Exception ex ) {
			}
		}
	}

	/**
	 *  Drop the PTT 
	 */
	public void dropPtt( PTTControlParms pttCtl ) {
		log.fine("dropPtt: userMode? "+pr.isUserMode()+", port: "+pttCtrlPort );
		if( pr.isUserMode() || pttCtrlPort == null )
			return;
		try {
			Thread.sleep( 750 );
		} catch( Exception ex ) {
		}
		try {
			if( pttCtl.isVoxPtt() == false ) {
				if( pttCtl.getSerialPttType() == PTTControlParms.SerialPttType.RTS ) {
					seriallog.fine("dropPtt: drop RTS");
					pttCtrlPort.setRTS(false);
				} else if( pttCtl.getSerialPttType() == PTTControlParms.SerialPttType.DTR ) {
					seriallog.fine("dropPtt: drop DTR");
					pttCtrlPort.setDTR(false);
				} else if( pttCtl.getSerialPttType() == PTTControlParms.SerialPttType.ASCII ) {
					seriallog.fine("dropPtt: send 'R' out pttCtrlPort");
					try {
						OutputStream os = pttCtrlPort.getOutputStream();
						os.write("R\r".getBytes());
						os.flush();
					} catch( Exception ex ) {
						seriallog.log(Level.WARNING,ex.toString(), ex);
					}
				}
			}
		} finally {
			setTxIndicator(false);
//			try {
//				if( pttCtrlPort != null ) {
//					log.info("dropPtt: close "+pttCtrlPort );
//					pttCtrlPort.close();
//				}
//			} finally {
//				pttCtrlPort = null;
//			}
		}
	}

	int lastmode = -1;
	TimerTask txcnt;
	long txtime;
	private DragGestureRecognizer fdgr;
	private DragGestureRecognizer sdgr;
	private DropTarget dt;
	private DragGestureRecognizer flgr;
	
	/**
	 *  get the mode description string associated with the passed integer state.
	 *  @see LinkEvent
	 */
	private String modeStr( int which ) {
		String str = which+"";
		switch( which ) {
			case LinkEvent.MODE_IDLE: str = "IDLE"; break;
			case LinkEvent.MODE_RECEIVE: str = "RECEIVE"; break;
			case LinkEvent.MODE_SYSOPRECEIVE: str = "SYSOPRECEIVE"; break;
			case LinkEvent.MODE_TRANSMIT: str = "TRANSMIT"; break;
			case LinkEvent.MODE_SYSOPTRANSMIT: str = "VOXTRANSMIT"; break;
			case LinkEvent.MODE_SYSOPIDLE: str = "SYSOPIDLE"; break;
		}
		return str;
	}
	/**
	 *  Set the mode of display for the audio levels and the other 
	 *  GUI components that change between transmit and receive and
	 *  idle mode.
	 *  @param which 0=idle, 1=recv, 2=xmit
	 */
	public void setMode( int which ) {
		if( lastmode == which )
			return;
		log.info( "Set MODE: "+modeStr(which) );
		switch( which ) {
			case LinkEvent.MODE_IDLE:
				if( txcnt != null ) {
					try {
						txcnt.cancel();
					} catch( Exception ex ) {
						log.log(Level.WARNING,ex.toString(), ex);
					}
					txcnt = null;
				}
				auCard.show( idPan, "none" );
				aupan.setVisible(false);
				getAction("Trans").setEnabled(!txActive && (ssa != null && ssa.getConnectCount() > 0));
				dropPtt( pr.getPttControlParms() );
				break;
			case LinkEvent.MODE_RECEIVE:
				startRxTxTimer();
				aupan.setVisible(true);
				txbut.setVisible(false);
				auCard.show( idPan, "rxtx" );
				getAction("Trans").setEnabled(false);
				if(pr.isUserMode() )
					dropPtt(pr.getPttControlParms());
				else
					raisePtt(pr.getPttControlParms());
				break;

			// Receiving audio in via VOX trigger
			case LinkEvent.MODE_SYSOPRECEIVE:
				startRxTxTimer();
				aupan.setVisible(true);
				txbut.setVisible(false);
				auCard.show( idPan, "rxtx" );
				getAction("Trans").setEnabled(false);
				dropPtt(pr.getPttControlParms());
				break;

			case LinkEvent.MODE_TRANSMIT:
				startRxTxTimer();
				aupan.setVisible(true);
				txbut.setVisible(true);
				auCard.show( idPan, "rxtx" );
				getAction("Trans").setEnabled(false);
				raisePtt(pr.getPttControlParms());
				break;

			// No more Data
			case LinkEvent.MODE_SYSOPTRANSMIT:
				startRxTxTimer();
				aupan.setVisible(true);
				txbut.setVisible(true);
				auCard.show( idPan, "rxtx" );
				getAction("Trans").setEnabled(true);
				// receiving VOX audio stream
				raisePtt(pr.getPttControlParms());
				break;

			// No more Data
			case LinkEvent.MODE_SYSOPIDLE:
				aupan.setVisible(true);
				txbut.setVisible(false);
				auCard.show( idPan, "rxtx" );
				getAction("Trans").setEnabled(true);
				// receiving VOX audio stream
				dropPtt(pr.getPttControlParms());
				break;
		}
		lastmode = which;
		log.finer("TX control at: "+which);
		idPan.revalidate();
				idPan.repaint();
				txbut.repaint();
	}

	/**
	 *  Start the RX tx timer task to show the amount of time for that
	 *  operation.
	 */
	private void startRxTxTimer() {
		txtime = System.currentTimeMillis();
		txcnt = new TimerTask() {
			public void run() {
				final long dt = System.currentTimeMillis() - txtime;
				final SimpleDateFormat fmt = new SimpleDateFormat( "hh:mm:ss");
				runInSwing( new Runnable() {
					public void run() {
						int v = (int)(dt/ 1000);
						int s = v % 60;
						int m = (v / 60) % 60;
						int h = v / 3600;
						String ss = s+"";
						if( s < 10 )
							ss = "0"+s;
						String ms = m+"";
						if( m < 10 )
							ms = "0"+m;
						String hs = h+"";
						if( h < 10 )
							hs = "0"+h;
						xtime.setText( hs+":"+ms+":"+ss );
						xtime.repaint();
					}
				});
			}
		};
		timer.schedule( txcnt, 1000, 1000 );
	}

	/**
	 *  Check if we are in receive mode
	 */
	boolean isReceiving() {
		return rxActive;
	}

	/**
	 *  Display the indicated string into the msgData component
	 *  This component is on the "Operational Info" tab and is
	 *  generally used for debug information that might help
	 *  the user understand why things are not working as expected
	 */
	void msg(final String str) {
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				try {
					msgData.getDocument().insertString(msgData.getDocument().getLength(),str+"\n",null);
				} catch( BadLocationException ex ) {
					log.log(Level.WARNING,ex.toString(), ex);
				}
			}
		});
	}

	/**
	 *  Register our LinkEventListener that will handle inbound SDES
	 *  packets during receive and idle mode.  This listener is the
	 *  start of a facility to pass arbitrary properties inside of the
	 *  SDES packet to allow the user to pass their information from
	 *  one side of the connection to the other.  The intent is to
	 *  provide properties such as 'homepage', 'camera', etc.
	 *
	 *  This is still under development...
	 */
	private void registerMediaHandler() {
		addLinkEventListener( new LinkEventListener<Object>() {
			public void processEvent( LinkEvent<Object> ev ) {
				if( ev.getType() == LinkEvent.SDES_EVENT && ev.isSend() == false ) {
					int sz = ev.getValue();
					RTPacket pk = new RTPacket( (byte[])ev.getSource(), sz );
					rtcp_sdes_request r = new rtcp_sdes_request(2);
					if( r == null || r.item == null ) {
						progress("no SDES for "+sz+" byte packet");
						return;
					}
					r.item[0].r_item = rtcp_sdes_type_t.RTCP_SDES_NAME;
					r.item[1].r_item = rtcp_sdes_type_t.RTCP_SDES_PRIV;
					boolean isSdes = pk.parseSDES(r);
					if( !isSdes )
						return;
					if( r.item[1].r_text != null && r.item[1].r_text.length > 0 ) {
						Map h = RTPacket.PropertyManager.getProperties( r.item[1].r_text );
						progress( "Found properties: "+h );
					}
				}
			}
		});
	}
	
	/**
	 *  handles an inbound SDES control packet on the RTP
	 *  link.
	 */
	public void doControl( String addr, byte[]arr, int sz ) {
		progress("handling control from: "+addr+", size="+sz );
		if( ssa == null )
			return;
		StationData sd = StationData.stationForAddress( addr );
		ssa.heardFrom( addr );
		RTPacket pk = new RTPacket( arr, sz );
		rtcp_sdes_request r = new rtcp_sdes_request(2);
		boolean isSdes = false;
		int dataPort = 5198;
		int controlPort = 5199;
		String lat = "0000.00N";
		String lon = "00000.00W";

		if( r == null || r.item == null ) {
			progress("no SDES for "+sz+" byte packet");
			//return;
		} else {
			r.item[0].r_item = rtcp_sdes_type_t.RTCP_SDES_NAME;
			isSdes = pk.parseSDES(r);
		}

		if( pr.isAudioTrace() )
			pk.dumpSdes();

		Vector v = pk.parseSDES();

		progress("parseSDES for name and tool returns: "+isSdes );
		if( isSdes ) {
			sendEvent( new LinkEvent( addr, false, LinkEvent.SDES_EVENT, sz ) );
			try {
				String name = null;
				for( int i = 0; i < v.size(); ++i ) {
					rtcp_sdes_request_item item = (rtcp_sdes_request_item)v.elementAt(i);
					if( item.r_item == rtcp_sdes_type_t.RTCP_SDES_NAME ) {
						log.finest("rtext.length: "+item.r_text.length+", [0] = "+
							(item.r_text[0]&0xff)+", [1] = "+(item.r_text[1] & 0xff) );
						name = new String( item.r_text ).split(" ")[0].trim();
//						if( name.length() == 0 )
//							name = new String( item.r_text,2,item.r_text[1] ).split(" ")[1].trim();
						progress("getting name from RTCP_SDES_NAME: \""+name+
							"\" in \""+new String(item.r_text)+"\"");
					} else if( item.r_item == rtcp_sdes_type_t.RTCP_SDES_PRIV ) {
						if( item.r_text[0] == 1 ) {
							if( item.r_text[1] == 'P' ) {
								dataPort = Integer.parseInt( new String( item.r_text, 2, item.r_text.length-2) );
								controlPort = dataPort + 1;
							}
						} else if( item.r_text.length > 3 && item.r_text[0] == 't' && item.r_text[1] == 'x' && item.r_text[2] == 't' ) {
							progress("Got 'txt' private data: "+
								new String( item.r_text, 4, item.r_text.length - 5 ) );
						} else if( item.r_text.length > 5 && item.r_text[0] == 'w' && item.r_text[1] == 'e' && item.r_text[2] == 'b' ) {
							String url = new String( item.r_text, 4, item.r_text.length - 5 );
							if( pr.isFollowingUsersPage() && 
								( !getTxMode() ||
									( getTxMode() && pr.isToggleSendFollow() == false ) ) ) {
								web.pushURL( currentURL );
								web.openURL( new URL( url ) );//, brws, brtxt, bback );
							}
						}
					} else {
						if( pr.isAudioTrace() ) {
							progress("unprocessed sdes packet: "+
								RTPacket.sdesType( item.r_item & 0xff ) );
							dumpPacket( "ctrl?", arr, sz, 400 );
						}
					}
				}

				Entry e = null;
				if( name != null ) {
					progress("Checking for entry for \""+name+"\" [len="+name.length()+"]" );
					e = findEntry( name );
				}

				if( e != null ) {
					progress( "sdes update from "+name+" @ "+addr );
					e.getStation().setSDES( v );
					e.getStation().setPorts( dataPort, controlPort );
					if( ssa.isConnectedTo(e.getStation().getIPAddr()) ) {
						progress("already connected to: "+name );
					} else {
						progress("not connected to: \""+name+"\" ("+ e.getStation()+")" );
						connectTo( e.getStation(), true );
//						audio.connected();
					}
				} else {
					refreshList(false);
					if( pr.isUserMode() ) {
						showMessageDialog( "Connection from: \""+name+"\"\n\n"+
							"Entry not found in list, can't connect!",
							"Can't Find Station",
							JOptionPane.ERROR_MESSAGE );
					} else {
						progress( "Connection from: \""+name+"\" failed" );
						progress( name+" not found in list, can't connect!" );
					}
				}
			} catch( Exception ex ) {
				log.log(Level.WARNING,ex.toString(), ex);
			}
			if( pk.isRTCPByepacket() ) {
				progress( "Bye packet received" );
			}
			if( pk.isRTCPSdespacket() ) {
				progress( "SS/SR packet received" );
			}
		} else if( pk.isRTCPByepacket() ) {
			progress("bye packet from: "+addr );
//			new Throwable("Bye received "+addr+", conn? "+ssa.isConnectedTo(addr) ).printStackTrace();
			if( ssa.isConnectedTo( addr ) ) {
				try {
					ssa.disconnectFrom( addr );
					sendEvent( new LinkEvent<StationData>(
						this, false, LinkEvent.STATION_DISC_EVENT, 
						ssa.getConnectCount(), sd ) );
				} catch( Exception ex ) {
					log.log(Level.WARNING,ex.toString(), ex);
				}
				if(cons != null ) {
					cons.removeConnectedStation( sd );
				}
				conn.setText( addr+" disconnected" );
			} else {
				try {
					ssa.disconnectFrom( addr );
					sendEvent( new LinkEvent<StationData>(
						this, false, LinkEvent.STATION_DISC_EVENT, 
						ssa.getConnectCount(), sd ) );
				} catch( Exception ex ) {
				}
				conn.setText( "Connection Refused to: "+addr );
//				failSound();
			}
			progress( "Bye packet received" );
		} else if( pk.isRTCPSdespacket() ) {
			progress("SS/SR packet from: "+addr );
			progress( "SS/SR packet received" );
		} else {
			progress("unknown data from: "+addr );
			if( pr.isAudioTrace() ) dumpPacket( "ctrl?", arr, sz, 80 );
		} 
	}
	
	/**
	 *  Show the user an exception message
	 */
	void showMessageDialog( Object msg ) {
		showMessageDialog( this, msg, 
				"Exception Occured", JOptionPane.ERROR_MESSAGE );
	}
	
	/**
	 *  Show the user an exception message
	 */
	void showMessageDialog( Object msg, String title, int type ) {
		showMessageDialog( this, msg, title, type );
	}
	
	/**
	 *  Make sure the dialog is opened inside of an swing
	 *  worker thread.
	 */
	static void showMessageDialog( final Window par, final Object msg, final String title, final int type ) {
		if( pr.isUserMode() ) {
			runInSwing( new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog( par, msg, title, type );
				}
			});
		} else {
			progress( title+": "+msg );
		}
	}
	
	/**
	 *  Run the indicated Runnable inside of a swing worker thread.
	 */
	static void runInSwing( Runnable r ) {
		if( SwingUtilities.isEventDispatchThread() ) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait( r );
			} catch( Exception ex ) {
			}
		}
	}
	
	/**
	 *  Run the indicated Runnable inside of a swing worker thread.
	 */
	static void runInSwingLater( Runnable r ) {
		if( SwingUtilities.isEventDispatchThread() ) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeLater( r );
			} catch( Exception ex ) {
			}
		}
	}
//
//	void failSound() {
//	}
//	void successSound() {
//	}
	
	/**
	 *  Dump the passed packet data with the indicated prefix
	 *  @param which the prefix string
	 *  @param a the array of data
	 *  @param len the length of data that is present
	 *  @param max the maximum length to display, 0 is all
	 */
	public static void dumpPacket( String which, byte[]a, int len, int max ) {
		byte[]h = "0123456789abcdef".getBytes();
		log.finest(which+" ("+max+" of "+len+"):\t");
		String line = "";
		for( int i = 0; i < len && max > 0 && i < max; ++i ) {
			int v1 = (a[i]&0xf0)>>4;
			int v2 = a[i]&0xf;
			line += ((char)h[v1])+""+((char)h[v2])+"["+(a[i] > ' ' ? (char)a[i] : '.')+"] ";
			if( ((i+1)%10) == 0 ) {
				log.finest(line);
				line = "";
			}
		}
	}
	
	/**
	 *  Periodic logged-in user refresher
	 */
	class ListRefreshTask extends TimerTask {
		public void run() {
			progress("Refreshing list...");
			if( pr.isStationListUpdateAuto() ) {
				if( pr.isListUpdateWhenConnected() == true ||
						ssa.isConnected() == false ) {
					refreshList(staList == null || staList.size() < 10 );
				} else {
					progress("No connected update");
				}
				timer.schedule( new ListRefreshTask(), 
					pr.getListUpdateInterval()*1000 );
			} else {
				progress("No Auto update");
			}
		}
	}

	
	/**
	 *  Periodic logged-in user refresher
	 */
	class LogonUpdateTask extends TimerTask {
		public void run() {
			progress("Refreshing logon...");
			try {
				updateLogon();
			} catch( Exception ex ) {
				log.log(Level.WARNING,ex.toString(), ex);
			}
			timer.schedule( new LogonUpdateTask(),
				pr.getLogonInterval()*1000 );
		}
	}

	/**
	 *  Main constructor
	 */
	public Javecho(String args[]) throws Exception {
		super( "JavEcho - W5GGW" );

		prefs = Preferences.userNodeForPackage( getClass() );
		prefs = prefs.node("Javecho");
//		Handler[]harr = log.getHandlers();
//		if( harr.length == 0 ) {
//			System.out.println("Reconfiguring logging, no handler for "+log);
//			//log = Logger.getLogger( "org.wonderly.ham" );
//			Handler h = new ConsoleHandler();
//			StreamFormatter sf = new StreamFormatter();
//			h.setFormatter( sf );
//			log.addHandler( h );
//			String f = System.getProperty("user.home") +
//				File.separator +
//				".javecho";
//			new File(f).mkdirs();
//			f += File.separator +
//				"javecho.log.%g";
//			int fsz = 1024*1024*4;
//			int fcnt = 4;
//			String sz = System.getProperty("org.wonderly.ham.echolink.log.size");
//			if( sz != null )
//				fsz = Integer.parseInt(sz);
//			sz = System.getProperty("org.wonderly.ham.echolink.log.count");
//			if( sz != null )
//				fcnt = Integer.parseInt(sz);
//			FileHandler fh = new FileHandler( f, fsz, fcnt );
//			fh.setFormatter( sf );
//			log.addHandler( fh );
//			log.setUseParentHandlers(false);
//			log.info("Installed Default Logging Handler");
//		}
		String anncls = System.getProperty(
			"org.wonderly.ham.echolink.announce");
		if( anncls == null )
			anncls = "org.wonderly.ham.speech.Announcements";

		log.info("Announcement Implementation: "+anncls );
		try {
			Class acl = Class.forName( anncls );
			ann = (Announcer)acl.newInstance();
		} catch( IllegalAccessException ex ) {
			log.warning( anncls+": class must have public noargs constructor");
		} catch( ClassNotFoundException ex ) {
			log.log( Level.SEVERE, anncls, ex );
		} catch( NoClassDefFoundError ex ) {
			log.log( Level.SEVERE, anncls, ex );
		} catch( InstantiationException ex ) {
			log.warning( anncls+": class must have public noargs constructor");
		}
		String laf = UIManager.getSystemLookAndFeelClassName();
		// If you want the Cross Platform L&F instead, comment out the
		// above line and uncomment the following:
//		String laf = UIManager.getCrossPlatformLookAndFeelClassName();
	 	try {
		  	UIManager.setLookAndFeel(laf);
		} catch (UnsupportedLookAndFeelException exc) {
	 	    System.err.println("Warning: UnsupportedLookAndFeel: " + laf);
		} catch (Exception exc) {
		    System.err.println("Error loading " + laf + ": " + exc);
		}
		audio = new JavaMidiAudioProvider();
		qsoHist = new QSOHistory( this );
		alarmMgr.almLog = new AlarmLog(this,this);
		alarmMgr.almEd = new AlarmEditor(this);
		listeners = new ArrayList<LinkEventListener<?>>();
		pr = new Parameters(this);
		pr.loadData();

		JPanel content = new JPanel();
		controls = new JPanel();
		JPanel mmp = new JPanel();
		mmp.setOpaque(true);
		Packer mmpk = new Packer( mmp );
		JTabbedPane tp = new JTabbedPane(JTabbedPane.BOTTOM);
		tabs = tp;
		Packer cpk = new Packer( controls );
		cpk.add( new JScrollPane( msgData = new JEditorPane() ) ).fillboth();
		msgData.setEditable(false);
		tp.setOpaque(true);
		mmpk.pack(tp).fillboth().gridx(0).gridy(1);

		inpan = new JPanel();
		inpan.setBorder(new SmallBevelBorder(0));
//		inpan.setBackground( inpan.getBackground().darker() );
		Packer inppk = new Packer(inpan);
		JPanel fp;
		int ix = -1;
		inppk.pack( fp = new JPanel() ).gridx(++ix).gridy(0).fillx();
		fp.setOpaque(false);
		Font f = new Font( "courier", Font.PLAIN, 11 );

		inppk.pack( new StatPanel(f,Javecho.this) ).gridx(++ix).gridy(0);
		rxstat = new LoweredLabel("RX",f);
		txstat = new LoweredLabel("PTT",f);
		netstat = new LoweredLabel("NET",f);
		
		rcvSerialCD = new LoweredLabel("CD",f);
		rcvSerialDSR = new LoweredLabel("DSR",f);
		rcvSerialCTS = new LoweredLabel("CTS",f);
//
//		rcvSerialCD.setBackground(inpan.getBackground());
//		rcvSerialDSR.setBackground(inpan.getBackground());
//		rcvSerialCTS.setBackground(inpan.getBackground());
//
//		rcvSerialCD.setOpaque(true);
//		rcvSerialDSR.setOpaque(true);
//		rcvSerialCTS.setOpaque(true);
//
//		rcvSerialCD.setBorder(new SmallBevelBorder(1));
//		rcvSerialDSR.setBorder(new SmallBevelBorder(1));
//		rcvSerialCTS.setBorder(new SmallBevelBorder(1));
//
//		rxstat.setBackground(inpan.getBackground());
//		txstat.setBackground(inpan.getBackground());
//		netstat.setBackground(inpan.getBackground());
//
//		rxstat.setOpaque(true);
//		txstat.setOpaque(true);
//		netstat.setOpaque(true);
//
//		rxstat.setBorder(new SmallBevelBorder(1));
//		txstat.setBorder(new SmallBevelBorder(1));
//		netstat.setBorder(new SmallBevelBorder(1));

//		rxstat.setFont(f);
//		txstat.setFont(f);
//		netstat.setFont(f);
//		rcvSerialCD.setFont(f);
//		rcvSerialDSR.setFont(f);
//		rcvSerialCTS.setFont(f);

		JPanel ssp = new JPanel();
		ssp.setBackground( SystemColor.activeCaption.brighter() );
//		ssp.setOpaque(false);
		ssp.setBorder( BorderFactory.createEtchedBorder() );
		Packer sspk = new Packer( ssp );
		inppk.pack( ssp ).gridx(++ix).gridy(0).inset(0,0,0,10);
		sspk.pack( rcvSerialCD ).gridx(++ix).gridy(0).inset(2,2,2,2);
		sspk.pack( rcvSerialDSR ).gridx(++ix).gridy(0).inset(2,2,2,2);
		sspk.pack( rcvSerialCTS ).gridx(++ix).gridy(0).inset(2,2,2,2);

		ssp = new JPanel();
//		ssp.setOpaque(false);
		ssp.setBackground( SystemColor.activeCaption );
		ssp.setBorder( BorderFactory.createEtchedBorder() );
		sspk = new Packer( ssp );
		inppk.pack( ssp ).gridx(++ix).gridy(0).inset(0,0,0,2);		
		sspk.pack( rxstat ).gridx(++ix).gridy(0).inset(2,2,2,2);
		sspk.pack( txstat ).gridx(++ix).gridy(0).inset(2,2,2,2);
		sspk.pack( netstat ).gridx(++ix).gridy(0).inset(2,2,2,2);

		mmpk.pack(inpan).fillx().gridx(0).gridy(2);
		web = new HTMLBrowser( Javecho.this );
		web.addPageListener( new PageListener() {
			public void pageOpened( PageEvent ev ) {
				log.info("New Page opened: "+ev);
			}
		});
		tp.add("Echo Link", content);
		tp.add("Web", web );
		tp.add("Operational Info", controls );
		setContentPane( mmp );
		setTitle( "JavEcho - "+pr.getCallSign() );

		staList = new Vector<Entry>();
		frList = new Vector<Entry>();

		locmod = new MyLocModel( staList, false );
		trmod = new MyModel( staList, "All Stations", false);
		frmod = new MyModel( frList, "Favorites", false );

		locations = new JTree( locmod );
		favorites = new JTree( frmod );
		stations = new JTree( trmod );

		treeSetup( stations, trmod );
		treeSetup( locations, locmod );
		treeSetup( favorites, frmod );

		stations.repaint();
		JToolBar tools = new JToolBar();
		defineActions();
		buildTools( tools );

		Packer pk = new Packer( content );
		JSplitPane sp1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JSplitPane sp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JSplitPane sp3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mmpk.pack( tools ).fillx().gridx(0).gridy(0);
		pk.pack( sp1 ).fillboth().gridx(0).gridy(1);
		sp1.setLeftComponent( sp2 );
		sp1.setRightComponent( sp3 );
		JPanel up = new JPanel();
		Packer upk = new Packer( up );
		sp2.setTopComponent( up );
		up.setPreferredSize( new Dimension( 400, 200 ) );
		upk.pack( stinfo = new JLabel() ).gridx(0).gridy(0).fillx().inset(2,2,2,2);
		upk.pack( oinfo = new JLabel() ).gridx(1).gridy(0).fillx().inset(2,2,2,2);
		stinfo.setFont( new Font( "serif", Font.PLAIN, 14 ) );
//		stinfo.setBorder( BorderFactory.createEtchedBorder() );
		stinfo.setForeground( Color.blue );
		stinfo.setText( staList.size()+" stations on "+site+" ("+
			connPercent(staList)+"% are busy)" );
		stTabs = new JTabbedPane();
		stTabs.add( "Favorites", new JScrollPane( favorites ) );
		stTabs.add( "Connected", new JScrollPane( stations ) );
		stTabs.add( "By Location", new JScrollPane( locations ) );
		upk.pack( stTabs ).gridx(0).gridy(1).fillboth().weighty(3).gridw(2);

		dt = new DropTarget( favorites, new FavoritesTargetListener() );
		
		dgs = DragSource.getDefaultDragSource();
		sdgr = dgs.createDefaultDragGestureRecognizer( stations, 
			255, new EntryDragRecoginizer(stations));
		fdgr = dgs.createDefaultDragGestureRecognizer( favorites, 
			255, new EntryDragRecoginizer(favorites));

		flgr = dgs.createDefaultDragGestureRecognizer( locations, 
			255, new EntryDragRecoginizer(locations));

		JPanel infop = new JPanel();
		Packer ipk = new Packer( infop );
		ipk.pack( conn = new JLabel("",JLabel.CENTER)
			).gridx(0).gridy(0).fillx().inset(4,4,4,4);
		ipk.pack( last = new JLabel("",JLabel.CENTER) 
			).gridx(0).gridy(1).fillx().inset(4,4,4,4);
		ipk.pack( ipaddr = new JLabel("",JLabel.CENTER)
			).gridx(0).gridy(2).fillx().inset(4,4,4,4);
		aupan = new JPanel();
//		aupan.setOpaque(true);
//		aupan.setBackground( Color.green );
		Packer apk = new Packer( aupan );
		apk.pack( txbut = new JButton("TX") ).gridx(0).gridy(0);
		txbut.setMargin( new Insets(1,1,1,1) );
		txbut.setBackground( Color.red );
		txbut.setForeground( Color.white );
		txbut.setEnabled(false);
		txbut.setFont( new Font( "serif", Font.BOLD, 12 ) );
		JPanel avpan = new JPanel();
		avpan.setOpaque(false);
		Packer avpk = new Packer(avpan);
//		avpan.setOpaque(true);
//		avpan.setBackground( Color.red );

		avpk.pack( new InvisPanel() ).gridx(0).gridy(0).fillx().weightx(1);
		avpk.pack( average = new AudioLevel( Javecho.this, 0, 0x7fff ) ).gridx(1).gridy(0).fillx().weightx(6);
		JPanel tpan = new JPanel();
		Packer tpk = new Packer( tpan );
		tpk.pack( xtime = new JLabel("00:00:00") ).fillboth().inset(5,5,5,5);
		avpk.pack( tpan ).gridx(1).gridy(1).inset(3,3,3,3);
		tpan.setBorder( BorderFactory.createEtchedBorder() );
		xtime.setFont( new Font( "serif", Font.PLAIN, 18 ) );
		xtime.setForeground( new Color( 255,80,80) );
		xtime.setOpaque(true);
		xtime.setBorder( BorderFactory.createEmptyBorder(0,3,0,3));
//		xtime.setBackground( getBackground().darker() );
		tpan.setBackground( getBackground().darker() );
		tpan.setOpaque(true);
		xtime.setBackground( Color.white );
		avpk.pack( new InvisPanel() ).gridx(2).gridy(0).fillx().weightx(1);
		apk.pack( avpan ).gridx(1).gridy(0).fillx();
		idPan = new JPanel();
		//aupan.setBorder( BorderFactory.createEtchedBorder() );
		idPan.setLayout( auCard = new CardLayout() );
		idPan.add( "rxtx", aupan );
		idPan.add( "none", new InvisPanel() );
		aupan.setOpaque(false);
		ipk.pack( new InvisPanel() ).gridx(0).gridy(3).filly();
		ipk.pack( idPan ).gridx(0).gridy(4).fillx().weightx(2);
		idPan.setOpaque(false);
		ipk.pack( new InvisPanel() ).gridx(0).gridy(5).filly();
		JPanel inp = new JPanel();
		Packer inpk = new Packer( inp );
		inpk.pack( infop ).gridx(0).gridy(0).fillboth().weightx(4);
		inpk.pack( gainPan = new JPanel() ).gridx(1).gridy(0).fillboth().weightx(1);//.gridx(1).gridy(0).gridh(6).filly();
		Packer gpk = new Packer( gainPan );
		JLabel gainMax = new JLabel(" ");
		gpk.pack( gainMax ).gridx(0).gridy(0);
		JLabel gainMin = new JLabel(" ");
		gpk.pack( gainMin ).gridx(0).gridy(2);
		JLabel gainVal = new JLabel("0", JLabel.CENTER );
		gainSlider = new JSlider( crm = new ControlRangeModel( Javecho.this, gainMin, gainMax, gainVal ) );
		gainSlider.setOrientation( SwingConstants.VERTICAL );
		gpk.pack(gainSlider).gridx(0).gridy(1).filly();
		gainPan.setBorder( gainBorder = BorderFactory.createTitledBorder("Volume") );
		gainPan.setVisible(false);
		gpk.pack( new JSeparator() ).gridx(0).gridy(3).fillx();
		gpk.pack( gainVal ).gridx(0).gridy(4).fillx();
		gpk.pack( new JSeparator() ).gridx(0).gridy(5).fillx();
		volMute = new JCheckBox("Mute");
		volMute.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				crm.setMuted( volMute.isSelected() );
			}
		});
		gpk.pack( volMute ).gridx(0).gridy(6);

		infop.setBackground( getBackground().darker() );
		infop.setOpaque(true);
		infop.setBorder( BorderFactory.createEtchedBorder() );
		sp2.setBottomComponent( inp );
		auCard.show( idPan, "none");
		JPanel ip = new JPanel();
		Packer ifpk = new Packer( ip );
		infoPane = new JTextArea();
		infoPane.setWrapStyleWord(true);
		infoPane.setLineWrap(true);
		infoPane.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent ev ) {
				if( ev.isPopupTrigger() )
					popup(ev);
			}
			public void mouseReleased( MouseEvent ev ) {
				if( ev.isPopupTrigger() )
					popup(ev);
			}
			void popup(MouseEvent ev) {
				JPopupMenu pop = new JPopupMenu();
				final Point ep = new Point(ev.getX(),ev.getY());
				Object o = infoPane.getInputMap().get(
					KeyStroke.getKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_MASK ) );
				if( o == null ) {
					throw new NullPointerException( "No CTRL-C in infoPane: "+infoPane.getActionMap() );
				}
				Action a = infoPane.getActionMap().get(o);
				if( a == null ) {
					throw new NullPointerException( "No CTRL-C action in infoPane: "+infoPane.getActionMap() );
				}
				JMenuItem mi = new JMenuItem("Copy");
				mi.setAction(a);
				mi.setText("Copy Selected Text");
				if( infoPane.getSelectionStart() == infoPane.getSelectionEnd() )
					mi.setEnabled(false);
				pop.add(mi);
				pop.addSeparator();
				mi = new JMenuItem("Add to Favorites");
				final StationData nd = getDragCall( infoPane, ep );
				mi.addActionListener( new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						Entry e = findEntry( nd.getCall() );
						if( e == null )
							return;
					  	List<Entry> friendData = frmod.getContents();
						if( friendData.contains(e) == false ) {
							frmod.addData(e);
						}
					}
				});
				if( nd == null )
					mi.setEnabled(false);
				else
					mi.setText( "Add "+nd.getCall()+" to Favorites" );
				pop.add( mi );
				pop.show( infoPane, ev.getX(), ev.getY() );
			}
		});
		DropTarget idt = new DropTarget( infoPane, new InfoPaneTargetListener() );
		//infoPane.setWrapStyleWord(true);
		infoPane.setFont( new Font("serif", Font.BOLD, 14 ) );
		infoPane.setForeground( Color.blue );
		infoPane.setEditable(false);
		infoPane.setBackground( sp2.getBackground() );
		DragGestureRecognizer infodg = dgs.createDefaultDragGestureRecognizer( 
			infoPane, 255, new TextDragRecoginizer(infoPane) );
		JPanel ipn = new JPanel();
		Packer ipnk = new Packer( ipn );
		ipnk.pack( new JScrollPane( infoPane ) ).fillboth();
		ipn.setBorder( BorderFactory.createTitledBorder("Info") );
		JPanel stpan = new JPanel();
		Packer stpk = new Packer( stpan );
		stpan.setBorder( BorderFactory.createTitledBorder( "Contact Information" ) );
		statList = new JTextArea();
		statList.getDocument().addDocumentListener( new DocumentListener() {
			public void removeUpdate( DocumentEvent ev ) {
				setContacts( statList.getText() );
			}
			public void changedUpdate( DocumentEvent ev ) {
				setContacts( statList.getText() );
			}
			public void insertUpdate( DocumentEvent ev ) {
				setContacts( statList.getText() );
			}
		});
		DragGestureRecognizer stldg = dgs.createDefaultDragGestureRecognizer(
			statList, 255, new StationDataDragRecoginizer(statList) );
		stpk.pack( new JScrollPane(  statList ) ).fillboth();
		JTabbedPane iftp = new JTabbedPane();
		iftp.add( "Info", ipn );
		iftp.add( "Contacts", stpan );
		ifpk.pack( iftp ).gridx(1).gridy(0).fillboth();
//		ifpk.pack( ipn ).gridx(1).gridy(0).fillboth();
		sp3.setTopComponent( ip );
		JPanel tmp = new JPanel();
		Packer mpk = new Packer( tmp );
		msgPane = new JEditorPane();
		msgPane.setEditable(false);
		msgSend = new JButton( "Send" );
		msgPane.setBackground( sp3.getBackground() );
		mpk.pack( new JScrollPane( msgPane ) ).gridx(0).gridy(0).fillboth().gridw(2);
		final JTextField msgText = new JTextField();
		mpk.pack( msgText ).gridx(0).gridy(1).fillx();
		mpk.pack( msgSend ).gridx(1).gridy(1);
		ActionListener msgAct = new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				try {
					String call = pr.getCallSign();
					String text = msgText.getText();
					byte data[] = new byte[7+call.length()+text.length()+3];
					byte[]arr = ("oNDATA"+call+">"+text).getBytes();
					System.arraycopy( arr, 0, data, 0, arr.length );
					data[arr.length] = '\r';
					data[arr.length+1] = '\n';
					data[arr.length+2] = 0;
					ssa.sendPacket( data, ConnectionManager.CHAT_TYPE );
					msgText.setText("");
					msgText.repaint();
					msgSend.requestFocus(false);
					msgText.requestFocus(true);
				} catch( Exception ex ) {
					reportException(ex);
				}
			}
		};
		msgText.addActionListener( msgAct );
		msgSend.addActionListener( msgAct );
		sp3.setBottomComponent( tmp );

		JMenuBar bar = new JMenuBar();
		buildMenuBar( bar );
		setJMenuBar( bar );

		// Get window up and visible
		pack();
		setSize( 700, 600 );
		setLocation(100,100);
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent ev ) {
				new ComponentUpdateThread<Object>( Javecho.this ) {
					public Object construct() {
						checkExit();
						return null;
					}
				}.start();
			}
		});
		setVisible( true );
		
		// Position dividers
		sp3.setDividerLocation(.7);
		sp3.revalidate();
		sp3.repaint();
		sp2.setDividerLocation(.7);
		sp2.revalidate();
		sp2.repaint();

		new ComponentUpdateThread<Void>( favorites ) {
			public void setup() {
				super.setup();
				favorites.repaint();
			}
			public Void construct() {
				loadFavorites();
				return null;
			}
			public void finished() {
				favorites.repaint();
			}
		}.start();
		cons = new ConnectionStatistics( Javecho.this );

		logonAndSetup();
		
		setMode(LinkEvent.MODE_IDLE);
		addKeyListener( new KeyListener() {
			public void keyTyped( KeyEvent ev ) {
//				progress("Got keyevent: "+ev );
				if( ev.getKeyChar() == ' ' && txToggle )
					setTxMode(!getTxMode());
			}
			public void keyPressed( KeyEvent ev ) {
//				progress("Got keyevent: "+ev );
				if( ev.getKeyChar() == ' ' && !txToggle )
					setTxMode(true);
			}
			public void keyReleased( KeyEvent ev ) {
//				progress("Got keyevent: "+ev ); 
				if( ev.getKeyChar() == ' ' && !txToggle )
					setTxMode(false);
			}
		});
		setFocusable(true);
		getAction( "Info" ).setEnabled(false);
		ActionMap myMap = content.getActionMap();
		myMap.put( KeyStroke.getKeyStroke(' '),
			getAction("Trans") );
		content.setActionMap( myMap );
		int upd = pr.getListUpdateInterval();
		if( pr.isStationListUpdateAuto() == false )
			upd = 120*60;
		timer.schedule( new ListRefreshTask(), upd*1000 );
		if( (upd = pr.getLogonInterval() ) == 0 )
			pr.setLogonInterval( upd = 360 );
		else if( (upd = pr.getLogonInterval() ) < 120 )
			pr.setLogonInterval( upd = 120 );
		timer.schedule( new LogonUpdateTask(), upd*1000 );
		if( pr.isOpenHomePage() ) {
				try {
					URL url = new URL( pr.getHomepageURL() );
					try {
						web.setPage( url );
					} catch( IOException ex ) {
						log.log(Level.WARNING, ex.toString(), ex );
					}
				} catch( Exception ex ) {
					reportException(ex);
				}			
		}
		if( pr.getInfoFileName() != null ) {
//			File f = new File( pr.getInfoFileName() );
//			if( f.exists() ) {
//				try {
//					FileReader fr = new FileReader( f );
//					String str = "";
//					try {
//						BufferedReader br = new BufferedReader( fr );
//						String ln = "";
//						while( (ln = br.readLine()) != null ) {
//							str += ln;
//						}
//						setInfo(str);
//						br.close();
//					} finally {
//						fr.close();
//					}
//				} catch( IOException ex ) {
//					ex.printStackTrace();
//				}
//			} else {
//				progress("No "+f+" for station info");
//			}
			setInfo( getCurrentInfo() );
		} else {
			progress("No station info property set");
		}
		new Thread() {
			public void run() {
				if( ann != null )
					ann.say(pr, "Welcome to Jav echo");
			}
		}.start();

		// Sysop verse user mode setup
		if( pr.isUserMode() == false && enableSysopMode ) {
			statid = new StationIDManager( pr, Javecho.this );
			sysopPtt = new SysopRecvAvailControl( pr.getRxCtrlSerialport() );
			average.setVox( pr.isRxCtrlVox() );
//			addLinkEventListener( sysopPtt );
		} else {
			average.setVox( false );
		}
	}
	
	/**
	 *  Get the current AudioProvider
	 */
	AudioProvider getAudioProvider() {
		return audio;
	}

	/**
	 *  A translucent panel for padding in components that have
	 *  a non-standard background pane
	 */
	private static class InvisPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public InvisPanel() {
			setOpaque(false);
		}
	}
	
	/**
	 *  Write out all dirty data, disconnect and exit.
	 */
	void checkExit() {
		if( alarmMgr.almEd.isDirty() || qsoHist.isDirty() )
			saveFavorites();
		if( acc != null ) {
			if( ssa != null ) {
				if( ssa.getConnectCount() > 0 ) {
					if( pr.isUserMode() == false ) {
						raisePtt(pr.getPttControlParms());
					}
				}
			}
			try {
				try {
					acc.sendLogoff();
				} catch( Exception ex ) {
					log.log(Level.WARNING,ex.toString(), ex);
				}
				try {
					if( ssa != null ) {
						if( ssa.getConnectCount() > 0 ) {
							if( pr.isUserMode() == false ) {
								raisePtt(pr.getPttControlParms());
							}
							ssa.disconnectAll();
						}
					}
				} catch( Exception ex ) {
					log.log(Level.WARNING,ex.toString(), ex);
				}
			} finally {
				if( pr.isUserMode() == false ) {
					dropPtt(pr.getPttControlParms());
				}
			}
		}
		System.exit(1);
	}
	

	/**
	 *  Open the passed URL in the users browser tab.
	 */
	public void openURL( URL u, JEditorPane brws, JTextField brtxt, JButton bback ) {
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
			progress("content-type: "+cty );
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
				brws.setPage( u );
			}
			currentURL = u;
			brtxt.setText( currentURL.toString() );
			bback.setEnabled( urls.size() > 0 );
			if( pr.isSendingCurrentPage() ) {
				ssa.sendSdesWithWebPage( currentWebPage() );
			}
		} catch( Exception ex ) {
			reportException(ex);
			try {
				web.backup();
			} catch( Exception exx ) {
				reportException(exx);
			}
		}
	}

	/**
	 *  Get the URL of the current web page
	 */
	public String currentWebPage() {
		if( currentURL == null )
			return null;
		return currentURL.toString();
	}

	/**
	 *  Add the indicated action to the passed toolbar
	 */
	public void toolsAdd( JToolBar tools, Action a ) {
		JButton b = tools.add( a );
		Object str = a.getValue(Action.SHORT_DESCRIPTION);
		b.setToolTipText( str == null ? (String)null : str.toString() );
	}
	
	/**
	 *  Add the passed action to the passed toolbar as a toggled
	 *  action.
	 *  @param tools a toolbar to use
	 *  @param a LabeledToggleAction instance to use
	 */
	public void toolsAddToggle( JToolBar tools, Action a ) {
		final JToggleButton b = new JToggleButton(a);
		tools.add( b );
		Object str = a.getValue(Action.SHORT_DESCRIPTION);
		b.setToolTipText( str == null ? (String)null : str.toString() );
		if( a instanceof LabeledToggleAction ) {
			final LabeledToggleAction la = (LabeledToggleAction)a;
			la.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent ev ) {
					b.setSelected( la.isSelected() );
				}
			});
		}
		b.setText(null);
	}
	
	/**
	 *  Build the standard toolbar into the passed toolbar
	 */
	private void buildTools( JToolBar tools ) {
		
		toolsAdd( tools, getAction("Trans") );
		tools.addSeparator();
		toolsAdd( tools, getAction("Conn") );
		toolsAdd( tools, getAction("Disc") );
		tools.addSeparator();
		toolsAdd( tools, getAction("Refresh") );
		toolsAdd( tools, getAction("Info") );
		toolsAdd( tools, getAction("Find") );
		toolsAdd( tools, getAction("Alarms...") );
		tools.addSeparator();
		toolsAdd( tools, getAction("Setup") );
		toolsAdd( tools, getAction("Prefs") );
		tools.addSeparator();
		toolsAddToggle( tools, getAction("Busy") );
		tools.addSeparator();
		toolsAdd( tools, getAction("Connection Statistics") );
		toolsAdd( tools, getAction("Station Summary") );
		tools.addSeparator();
		toolsAdd( tools, getAction("Print") );
		tools.addSeparator();
		toolsAdd( tools, getAction("Help") );
		tools.setFloatable(false);
	}

	/**
	 *  Build the standard menus into the passed menubar
	 */
	private void buildMenuBar( JMenuBar bar ) {
		JMenu m = new JMenu( "File" );
		m.setMnemonic( 'F' );
		bar.add(m);
		menuAction( m, getAction("Print"),
			KeyStroke.getKeyStroke( KeyEvent.VK_P, ActionEvent.CTRL_MASK ) );
		m.add( getAction("Print Preview") );
		m.add( getAction("Print Setup") );
		m.addSeparator();
		m.add( getAction("Exit") );
		m = new JMenu( "Edit" );
		m.setMnemonic( 'E' );
		menuAction( m, getAction("Undo"),
			KeyStroke.getKeyStroke( KeyEvent.VK_Z, ActionEvent.CTRL_MASK ) );
		m.addSeparator();
		menuAction( m, getAction("Cut"),
			KeyStroke.getKeyStroke( KeyEvent.VK_X, ActionEvent.CTRL_MASK ) );
		menuAction( m, getAction("Copy"),
			KeyStroke.getKeyStroke( KeyEvent.VK_C, ActionEvent.CTRL_MASK ) );
		menuAction( m, getAction("Paste"),
			KeyStroke.getKeyStroke( KeyEvent.VK_V, ActionEvent.CTRL_MASK ) );
		bar.add(m);
		m = new JMenu( "Station" );
		m.setMnemonic( 'S' );
		menuAction( m, getAction("Export"),
			KeyStroke.getKeyStroke( KeyEvent.VK_E, ActionEvent.ALT_MASK ) );
		menuAction( m, getAction("Trans"),
			KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, ActionEvent.ALT_MASK ) );
		m.addSeparator();
		menuAction( m, getAction("Conn"), 
			KeyStroke.getKeyStroke( KeyEvent.VK_C, ActionEvent.ALT_MASK ) );
		menuAction( m, getAction("Connect To..."),
			KeyStroke.getKeyStroke( KeyEvent.VK_O, ActionEvent.CTRL_MASK ) );
		m.add( getAction("Reconnect To ") );
		menuAction( m, getAction("Disc"),
			KeyStroke.getKeyStroke( KeyEvent.VK_D, ActionEvent.ALT_MASK ) );
		m.addSeparator();
		m.add( getAction("Request Version") );
		m.addSeparator();
		m.add( getAction("Info") );
		menuAction( m, getAction("Find"), 
			KeyStroke.getKeyStroke( KeyEvent.VK_D, ActionEvent.CTRL_MASK ) );
		menuAction( m, getAction("Refresh"),
			KeyStroke.getKeyStroke( KeyEvent.VK_F5, 0 ) );
		bar.add(m);
		m = new JMenu( "Tools" );
		m.setMnemonic( 'T' );
		menuAction( m, getAction("Alarms..."),
			KeyStroke.getKeyStroke( KeyEvent.VK_A, ActionEvent.CTRL_MASK ) );
		m.addSeparator();
		menuToggleAction( m, getAction("Busy"),
			KeyStroke.getKeyStroke( KeyEvent.VK_B, ActionEvent.CTRL_MASK ) );
		m.add( getAction("Disable Link") );
		m.addSeparator();
//		m.add( getAction("Setup") );
//		m.add( getAction("Prefs") );
		menuAction( m, getAction("Setup"), 
			KeyStroke.getKeyStroke( KeyEvent.VK_E, ActionEvent.ALT_MASK ) );
		menuAction( m, getAction("Prefs"), 
			KeyStroke.getKeyStroke( KeyEvent.VK_P, ActionEvent.ALT_MASK ) );
		menuAction( m, getAction("Sysop Settings..."), 
			KeyStroke.getKeyStroke( KeyEvent.VK_Y, ActionEvent.ALT_MASK ) );
		getAction("Sysop Settings...").setEnabled( enableSysopMode && !pr.isUserMode() );
		menuAction( m, getAction("Link Setup Wizard..."),
			null, KeyEvent.VK_W );
		m.addSeparator();
		JMenu sm = new JMenu( "Adjust Volume" );
		m.add( sm );
		sm.add( getAction("Playback...") );
		sm.add( getAction("Recording...") );
		menuAction( m, getAction("Tone Generator..."),
			null, KeyEvent.VK_G );
		m.addSeparator();
		menuAction( m, getAction("Start Recording"), 
			KeyStroke.getKeyStroke( KeyEvent.VK_F2, ActionEvent.ALT_MASK ) );
		menuAction( m, getAction("Play Sound File"), 
			null, KeyEvent.VK_Y );
		menuAction( m, getAction("Stop"), 
			KeyStroke.getKeyStroke( KeyEvent.VK_F3, ActionEvent.ALT_MASK ) );
		m.add( getAction("Pause Playback") );
		bar.add(m);
		m = new JMenu( "View" );
		m.setMnemonic( 'V' );
		m.add( new JCheckBoxMenuItem(getAction("Tool Bar")) );
		m.add( new JCheckBoxMenuItem(getAction("Status Bar")) );
		m.addSeparator();
		m.add( new JCheckBoxMenuItem(getAction("Large Font")) );
		m.addSeparator();
		menuAction( m, getAction("Connection Statistics"),
			KeyStroke.getKeyStroke( KeyEvent.VK_F12, ActionEvent.ALT_MASK ) );
		menuAction( m, getAction("Station Summary"), 
			KeyStroke.getKeyStroke( KeyEvent.VK_F11, ActionEvent.ALT_MASK ) );
		menuAction( m, getAction("Alarm Log"), 
			KeyStroke.getKeyStroke( KeyEvent.VK_F10, ActionEvent.ALT_MASK ) );
		m.addSeparator();
		menuAction( m, getAction("Server Message..."), 
			KeyStroke.getKeyStroke( KeyEvent.VK_M, ActionEvent.ALT_MASK ) );
		m.add( getAction("System Log...") );
		m.add( getAction("Callsign Log...") );
		bar.add(m);
		m = new JMenu( "Help" );
		m.setMnemonic( 'H' );
		menuAction( m, getAction("Contents"), KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
		m.add( getAction("Search...") );
		m.add( getAction("Index...") );
		m.addSeparator();
		m.add( getAction("JavEcho Web Site...") );
		m.add( getAction("Help on the Web...") );
		m.add( getAction("Software Updates...") );
		m.addSeparator();
		m.add( getAction("About JavEcho...") );
		bar.add(m);
	}

	/**
	 *  Set up the passed tree and model to have all the appropriate
	 *  event handling, rendering etc
	 */
	private void treeSetup( final JTree tree, final TreeModel mod ) {

		tree.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent ev ) {
				if( ev.isPopupTrigger() == false )
					return;
				stationPopup( ev, tree );
			}
			public void mouseReleased( MouseEvent ev ) {
				if( ev.isPopupTrigger() == false )
					return;
				stationPopup( ev, tree );
			}
		});

		tree.addTreeSelectionListener( new TreeSelectionListener() {
			public void valueChanged( TreeSelectionEvent ev ) {
				checkTreeSelection( tree, ev, mod );
			}
		});
		tree.setCellRenderer( new MyCellRenderer(tree.getCellRenderer()) );
		tree.addMouseListener( new MouseAdapter() {
			public void mouseClicked( MouseEvent ev ) {
				if( tree.getSelectionPath() != null ) {
					Object node = tree.getSelectionPath().getLastPathComponent();
					lastSelTree = tree;
					if( tree != favorites  )
						favorites.clearSelection();
					if( tree != stations  )
						stations.clearSelection();
					if( tree != locations  )
						locations.clearSelection();
					if( mod.isLeaf(node) ) {
						if( ev.getClickCount() == 2 && ev.isMetaDown() == false ) {
							connectToSelected(tree);
							getAction("Disc").setEnabled(true);
							getAction("Conn").setEnabled(isMoreConnections());
						}
					}
				}
			}
		});
	}
	
	/**
	 *  Add the passed station as one we want alarms for
	 */
	public void addAlarm( StationData station ) {
		String st = station.getCall();
		alarmMgr.almEd.addEntry( st );
		saveFavorites();
	}

	/**
	 *  handle a popup for a menu event for the station under
	 *  the mouse event in the indicate tree.
	 */
	private void stationPopup( MouseEvent ev, final JTree tree ) {
		TreePath p = tree.getPathForLocation(ev.getX(),ev.getY());
		if( p == null )
			return;
		tree.setSelectionPath( p );
		final Object o = p.getLastPathComponent();
		JPopupMenu m = new JPopupMenu();
		JMenuItem mi;
		m.add( mi = new JMenuItem( "Add Call Sign" ) );
		mi.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				String call = JOptionPane.showInputDialog( Javecho.this,
					"Enter Call To Add", "Add a Call Sign",
					JOptionPane.QUESTION_MESSAGE );
				if( call == null )
					return;
				call = call.toUpperCase();
				Entry e = trmod.findStation( call );
				if( e == null ) {
					StationData dt = StationData.dataFor( call );
					e = new Entry( dt, Entry.TYPE_STATION );
				}
				frmod.addData( e );
				saveFavorites();
			}
		});
			
		if( o instanceof Entry ) {
			m.add( mi = new JMenuItem( "Add Alarm" ) );
			mi.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent ev ) {
					Entry e = (Entry)o;
					addAlarm( e.getStation() );
				}
			});
			m.addSeparator();
			if( tree != favorites ) {
				m.add( mi = new JMenuItem("Add to Favorites") );
				mi.addActionListener( new ActionListener() {
					public void actionPerformed( ActionEvent ev ) {
						Entry e = (Entry)o;
						List<Entry> friendData = frmod.getContents();
						if( friendData.contains(e) == false ) {
							frmod.addData(e);
							saveFavorites();
						}
					}
				});
			}
			if( tree == favorites ) {
				m.add( mi = new JMenuItem("Remove Entry") );
				mi.addActionListener( new ActionListener() {
					public void actionPerformed( ActionEvent ev ) {
						Entry e = (Entry)o;
						List<Entry> friendData = frmod.getContents();
	//					progress("Check remove for: "+e+
	//						", friendData.contains(e): "+friendData.contains(e) );
						if( tree == favorites ) {
							if( friendData.contains(e) == true ) {
								friendData.remove(e);
								saveFavorites();
								frmod.setData(friendData);
							}
						} else if( tree == stations ) {
						} else if( tree == stations ) {
						}
					}
				});
				m.addSeparator();
			}
			m.add( mi = new JMenuItem("Connect") );
			mi.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent ev ) {
					final Entry e = (Entry)o;
					new ComponentUpdateThread( tree ) {
						public Object construct() {
							connectTo( e.getStation(), false );
							return null;
						}
					}.start();
				}
			});
			m.addSeparator();
			m.add( mi = new JMenuItem("Info") );
			mi.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent ev ) {
					stationInfo( ((Entry)o).getStation() );
				}
			});
			if( ((Entry)o).getStation().getIPAddr() == null )
				mi.setEnabled(false);
		}
		m.show( tree, ev.getX(), ev.getY() );
	}

	/**
	 *  setup the menu item toggle action for the passed action
	 *  and associated keystroke
	 */
	private void menuToggleAction( JMenu m, Action act, KeyStroke k ) {
		final JCheckBoxMenuItem item = new JCheckBoxMenuItem(act);
		if( act instanceof LabeledToggleAction ) {
			final LabeledToggleAction la = (LabeledToggleAction)act;
			la.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent ev ) {
					item.setSelected( la.isSelected() );
				}
			});
		}
		m.add( item );
		item.setAccelerator( k );
	}

	/**
	 *  Set the menu action for the passed action and keystroke
	 */
	private void menuAction( JMenu m, Action act, KeyStroke k ) {
		JMenuItem item = m.add( act );		
		item.setAccelerator( k );
	}

	/**
	 *  Set the menu action for the passed action and keystroke
	 */
	private void menuAction( JMenu m, Action act, KeyStroke ks, int k ) {
		JMenuItem item = m.add( act );
		if( ks != null )
			item.setAccelerator( ks );
		if( k > 0 )
			item.setMnemonic( k );
	}

	/**
	 *  Handle drop events on the favorites pane
	 */
	private class FavoritesTargetListener extends DropTargetAdapter {
		public void drop( DropTargetDropEvent ev ) {
			progress("Dropped: "+ev );
			Transferable tf = ev.getTransferable();
			try {
				DataFlavor entdf = new DataFlavor(  Entry.class, "Entry" );
				DataFlavor stddf = new DataFlavor(  StationData.class, "StationData" );
				if( tf.isDataFlavorSupported( entdf ) ) {
					Object obj = tf.getTransferData( entdf );
					ev.dropComplete( obj instanceof Entry );
					if( obj instanceof Entry ) {
						final Entry ent = (Entry)obj;
						frmod.addData( ent );
						saveFavorites();
					}
				} else if( tf.isDataFlavorSupported( stddf ) ) {
					Object obj = tf.getTransferData( stddf );
					if( obj instanceof StationData ) {
						final StationData st = (StationData)obj;
						Entry ent = findEntry( st.getCall() );
						ev.dropComplete( ent != null );
						if( ent != null ) {
							frmod.addData( ent );
							saveFavorites();
						}
					} else {
						ev.dropComplete( false );
					}
				} else {
					ev.dropComplete( false );
				}
			} catch( Exception ex ) {
				reportException(ex);
			}
		}
	}

	class EntryDragRecoginizer implements DragGestureListener {
		JTree tree;
		public EntryDragRecoginizer(JTree tree ) {
			this.tree = tree;
		}
		public void dragGestureRecognized( DragGestureEvent ev ) {
			progress("Drag started: "+ev );
			Point p = ev.getDragOrigin();
			TreePath pth = tree.getPathForLocation(p.x,p.y);
			if( pth == null )
				return;
			Object obj = pth.getLastPathComponent();
			if( obj == null || obj instanceof Entry == false ) {
				return;
			}
			tree.setSelectionPath( pth );
			final Entry nd = (Entry)obj;
			dgs.startDrag( ev, DragSource.DefaultLinkDrop,
				(Image)null, new Point(0,0),
				// Transfer the node object.
				new Transferable() {
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[] { 
								new DataFlavor(  Entry.class, "Entry" ) };
					}
					public boolean isDataFlavorSupported( DataFlavor flav ) {
						return flav.equals( new DataFlavor( Entry.class, "Entry" ) );
					}
					public Object getTransferData( DataFlavor flav ) {
						return nd;
					}
				},
				new DragSourceListener() {
					public void dragEnter( DragSourceDragEvent evv ) {
					}
					public void dragOver( DragSourceDragEvent evv ) {
					}
					public void dropActionChanged( DragSourceDragEvent evv ) {
					}
					public void dragExit( DragSourceEvent evv ) {
					}
					public void dragDropEnd( DragSourceDropEvent evv ) {
					}
				}
			);
		}
	}

	/**
	 *  Handle drag of text from the station data JTextComponent
	 */
	private class StationDataDragRecoginizer extends TextDragRecoginizer {
		public StationDataDragRecoginizer( JTextComponent text ) {
			super( text );
		}
	}

//	class xxStationDataDragRecoginizer implements DragGestureListener {
//		JList list;
//		public xxStationDataDragRecoginizer( JList list ) {
//			this.list = list;
//		}
//		public void dragGestureRecognized( DragGestureEvent ev ) {
//			Point p = ev.getDragOrigin();
//			int idx = list.locationToIndex( p );
//			if( idx == -1 )
//				return;
//			progress("Drag started: "+ev );
//			list.setSelectedIndex( idx );
//			Object obj = list.getSelectedValue();
//			if( obj == null || obj instanceof StationData == false ) {
//				return;
//			}
//			final StationData nd = (StationData)obj;
//			dgs.startDrag( ev, DragSource.DefaultLinkDrop,
//				(Image)null, new Point(0,0),
//				// Transfer the node object.
//				new Transferable() {
//					public DataFlavor[] getTransferDataFlavors() {
//						return new DataFlavor[] { 
//								new DataFlavor(  StationData.class, "StationData" ) };
//					}
//					public boolean isDataFlavorSupported( DataFlavor flav ) {
//						return flav.equals( new DataFlavor( StationData.class, "StationData" ) );
//					}
//					public Object getTransferData( DataFlavor flav ) {
//						return nd;
//					}
//				},
//				new DragSourceListener() {
//					public void dragEnter( DragSourceDragEvent evv ) {
//					}
//					public void dragOver( DragSourceDragEvent evv ) {
//					}
//					public void dropActionChanged( DragSourceDragEvent evv ) {
//					}
//					public void dragExit( DragSourceEvent evv ) {
//					}
//					public void dragDropEnd( DragSourceDropEvent evv ) {
//					}
//					public void drop( DragSourceDropEvent evv ) {
//					}
//				}
//			);
//		}
//	}

	/**
	 *  Text drag recognizer for text components
	 */
	private class TextDragRecoginizer implements DragGestureListener {
		JTextComponent text;
		public TextDragRecoginizer( JTextComponent text ) {
			this.text = text;
		}
		public void dragGestureRecognized( DragGestureEvent ev ) {
			Point p = ev.getDragOrigin();
			final StationData nd = getDragCall( text, p );
			if( nd == null )
				return;
			int w, h;
			Graphics g = getGraphics();
			Font f = new Font( "serif", Font.PLAIN, 14 );
			FontMetrics fm = g.getFontMetrics( f );
			w = fm.stringWidth( nd.getCall() );
			h = fm.getHeight();
			Image img = createImage( w, h );
			Graphics gr = img.getGraphics();
			gr.setColor( Color.white );
			gr.fillRect( 0, 0, w, h );
			gr.setColor( Color.blue );
			gr.drawString(nd.getCall(),0,fm.getMaxAscent());
			progress("img: "+img+", w: "+w+", h: "+h+", can drag? "+dgs.isDragImageSupported() );
			dgs.startDrag( ev, DragSource.DefaultLinkDrop,
				(Image)img, new Point(0,0),
				// Transfer the node object.
				new Transferable() {
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[] { 
								new DataFlavor(  StationData.class, "StationData" ) };
					}
					public boolean isDataFlavorSupported( DataFlavor flav ) {
						return flav.equals( new DataFlavor( StationData.class, "StationData" ) );
					}
					public Object getTransferData( DataFlavor flav ) {
						return nd;
					}
				},
				new DragSourceListener() {
					public void dragEnter( DragSourceDragEvent evv ) {
					}
					public void dragOver( DragSourceDragEvent evv ) {
					}
					public void dropActionChanged( DragSourceDragEvent evv ) {
					}
					public void dragExit( DragSourceEvent evv ) {
					}
					public void dragDropEnd( DragSourceDropEvent evv ) {
					}
				}
			);
		}
	}
		
	/**
	 *  When a drag occurs out of the passwd text component
	 *  at the indicated point, get the associated station data
	 */
	private StationData getDragCall( JTextComponent text, Point p ) {
		int position = text.viewToModel(p);
		int start = position;
		if( start > 0 )
			--start;
		int end = position;
		Document d= text.getDocument();
		int len = d.getLength();
		String line = null;
		try {
			while( start > 0 ) {
				String s = d.getText( start, 1 );
				char ch = s.charAt(0);
				if( ch == '\n' || ch == '\r' )
					break;
				--start;
			}
			while( end < len ) {
				String s = d.getText( end, 1 );
				char ch = s.charAt(0);
				if( ch == '\n' || ch == '\r' || ch == ' ' || ch == '\t' )
					break;
				++end;
			}
			line = d.getText( start, end-start ).trim();
		} catch( BadLocationException ex ) {
		}
		++start;
		String stdata = line;
		if( stdata.startsWith("->") || stdata.startsWith("> ") )
			stdata = stdata.substring(2);
		progress( "get station for: "+stdata );
		progress("line is: "+line );
		stdata = stdata.split(" ")[0];
		progress("stdata is: "+stdata);
		text.setSelectionStart( start );
		text.setSelectionEnd( end );
		return StationData.stationFor( stdata ); //(StationData)obj;
	}

	/**
	 *  Handle drops onto the info pane which are taken as connection
	 *  requests.
	 */
	private class InfoPaneTargetListener extends DropTargetAdapter {
		public void dragOver( DropTargetDragEvent ev ) {
			DataFlavor[]arr = ev.getCurrentDataFlavors();
			DataFlavor reqFlav = new DataFlavor( Entry.class, "Entry" );
			for( int i = 0; i < arr.length; ++i ) {
				if( arr[i].equals(reqFlav) )
					return;
			}
			ev.rejectDrag();
		}
		public void drop( final DropTargetDropEvent ev ) {
			try { 
			Transferable tf = ev.getTransferable();
				final Object obj = tf.getTransferData( new DataFlavor(  Entry.class, "Entry" ) );
				ev.dropComplete( obj instanceof Entry );
				if( obj instanceof Entry ) {
					final Entry ent = (Entry)obj;
					new ComponentUpdateThread( (JComponent)null ) {
						public Object construct() {
							try { 
									connectTo( ent.getStation(), false );
							} catch( Exception ex ) {
								reportException(ex);
							}
							return null;
						}
					}.start();
				}
			} catch( Exception ex ) {
				reportException(ex);
			}
		}
	}

	JLabel cursite;
	String curhost;
	/**
	 *  Login to the echolink server to register our call in the list
	 *  and get back the list of currently connected stations and
	 *  display that list
	 */
	private void logonAndSetup() {
		String call = pr.getCallSign();
		String password = pr.getPassword();
		if( call == null || call.length() == 0 ) {
			new SetupDialog(Javecho.this, pr, true);
			setTitle( "Javecho - "+pr.getCallSign() );
		}

		final JDialog dlg = new JDialog( this, "Logging On...", false );

		Packer pk = new Packer( dlg.getContentPane() );
		cursite = new JLabel("Please Wait While Host is Contacted...", JLabel.CENTER);
		Font bf = new Font( "serif", Font.BOLD, 14 );
		cursite.setFont( bf );
		JLabel l = new JLabel("Connecting...", JLabel.CENTER);
		pk.pack( l ).gridx(0).gridy(0).fillx().inset(10,10,10,10);
		l.setFont(bf);
		pk.pack( cursite ).gridx(0).gridy(1).fillx().inset(10,10,10,10);
		pk.pack( new JSeparator() ).gridx(0).gridy(2).fillx().inset(4,4,4,4);
		final JButton close = new JButton("Cancel");
		final boolean cancelled[] = new boolean[1];
		close.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				cancelled[0] = true;
				dlg.setVisible(false);
			}
		});
		pk.pack( close ).gridx(0).gridy(3).inset(4,4,4,4);
		dlg.pack();
		dlg.setLocationRelativeTo(this);
		final Object lock = new Object();
		final boolean open[] = new boolean[2];

		new ComponentUpdateThread<Boolean>( new JComponent[]{ stations, favorites } ) {
			public Boolean construct() {
				try {
					cancelled[0] = false;
					curhost = logon( cursite, cancelled );
					return new Boolean(true);
				} catch( Throwable ex ) {
					log.log( Level.SEVERE, ex.toString(), ex );
				}
				return new Boolean(false);
			}
			public void finished() {
				synchronized( lock ) {
					open[1] = getValue();
				}
				if( open[0] ) {
					dlg.setVisible(false);
					dlg.dispose();
//					lock.notify();
				}
			}
		}.start();

		synchronized( lock ) {
			if( open[1] == false ) {
				open[0] = true;
			}
		}
		if(open[1] == false)
				dlg.setVisible( true );
//				try {
//					lock.wait( );
//				} catch( Exception ex ) {
//				}
	}

	public boolean isConnected() {
		return ssa.getConnectCount() > 0;
	}
	
	public int getConnectCount() {
		return ssa.getConnectCount();
	}

	private void checkTreeSelection( JTree tree, TreeSelectionEvent ev, TreeModel mod ) {
//		if(ev.isAddedPath() == false)
//			return;
		boolean sel = false;
		Entry ent = null;
		if( tree.getSelectionPath() != null ) {
					Object node = tree.getSelectionPath().getLastPathComponent();
					if( mod.isLeaf(node) ) {
						ent = (Entry)node;
						sel = true;
					}
		}
		
		getAction("Conn").setEnabled( sel && 
			ssa.isConnectedTo(ent.getStation().getIPAddr()) == false );
		getAction("Disc").setEnabled( isConnected() );
		getAction("Trans").setEnabled( isConnected() );
		getAction("Info").setEnabled( sel );
	}

	/**
	 *  Handler clas for received audio data available.
	 */
	class SysopRecvAvailControl implements Runnable,SerialPortEventListener,LinkEventListener<Object> {
		SerialPort port;
		String rcvSerPort;
		boolean done;

		public void processEvent( LinkEvent<Object> ev ) {
			log.finer("Got LinkEvent: "+ev );
			switch( ev.getType() ) {
				case LinkEvent.VOX_CLOSE_EVENT:
					log.info("Vox receive closed");
					break;
				case LinkEvent.VOX_OPEN_EVENT:
					log.info("Vox receive opened");
					break;
			}
		}

		public SysopRecvAvailControl( String rcvSerPort ) {
			log.info("startPTTControl: "+rcvSerPort);
			if( pr.isUserMode() ) {
				throw new IllegalStateException("Not in Sysop mode");
			}
			this.rcvSerPort = rcvSerPort;
			openPort();
			new Thread( this ).start();
		}

		public void setDone(boolean how) {
			done = how;
		}

		private Object notifyLock = new Object();
		private boolean isCTS, isDSR, isCD;
		public void serialEvent( SerialPortEvent ev ) {
			log.fine("SerialEvent: "+ev);
			switch( ev.getEventType() ) {
				case SerialPortEvent.CTS:
					isCTS = ev.getNewValue();
					showActiveIndicator( rcvSerialCTS, isCTS );
					break;
				case SerialPortEvent.DSR:
					isDSR = ev.getNewValue();
					showActiveIndicator( rcvSerialDSR, isDSR );
					break;
				case SerialPortEvent.CD:
					isCD = ev.getNewValue();
					showActiveIndicator( rcvSerialCD, isCD );
					break;
			}

			synchronized( notifyLock ) {
				notifyLock.notifyAll();
			}
		}

		private void openPort() {
			try {
				port = getOpenedPort( rcvSerPort );
				port.setRTS(false);
				port.setDTR(false);
			} catch( Exception ex ) {
				IllegalArgumentException iae = new IllegalArgumentException("Port already in use: "+ex );
				throw ((IllegalArgumentException)iae.initCause(ex));
			}
		}

		public void run() {
			try {
				port.addEventListener(this);
				port.notifyOnCTS(true);
				port.notifyOnDSR(true);
				port.notifyOnCarrierDetect(true);
			} catch( TooManyListenersException ex ) {
				reportException(ex);
			}

//			try {
				while( !done && pr.isUserMode() == false ) {
					synchronized( notifyLock ) {
						try {
							notifyLock.wait( 5000 );
						} catch( Exception ex ) {
						}
					}
					if( pr.isRxCtrlSerialcd() ) {
						togglePtt( isCD );
					} else if( pr.isRxCtrlSerialdsr() ) {
						togglePtt( isDSR );
					} else if( pr.isRxCtrlSerialcts() ) {
						togglePtt( isCTS );
					}
				}
//			} finally {
//				try {
//					port.close();
//				} finally {
//					port = null;
//					sysopPtt = null;
//				}
//			}
		}

		public void togglePtt( boolean how ) {
//			if( unsquelch ) {
//				log.info("Audio Sender vox unsquelching "+
//					average+" > "+voxlim );
//				LinkEvent le = new LinkEvent( MicIO.this,
//					true, LinkEvent.VOX_OPEN_EVENT, (int)average );
//				mgr.je.sendEvent( le );
//			} else {
//				LinkEvent le = new LinkEvent( MicIO.this,
//					true, LinkEvent.VOX_CLOSE_EVENT, (int)average );
//				mgr.je.sendEvent( le );
//			}
			log.info("SysopRecvAvailControl togglePtt: "+how );

			setRxIndicator( how );
			setMode( (how^pr.isRxCtrlInvertsense()) ?
				LinkEvent.MODE_RECEIVE : LinkEvent.MODE_IDLE );
		}
	}

	Hashtable<String,SerialPort> ports = new Hashtable<String,SerialPort>();
	protected SerialPort getOpenedPort( String portName ) {
		SerialPort port;
		if( ( port = (SerialPort)ports.get(portName) ) != null ) {
			return port;
		}

		CommPortIdentifier cp = getCommPort( portName );
		try {
			port = (SerialPort)cp.open("Javecho sysop", 10);
		} catch( Exception ex ) {
			IllegalArgumentException iae = new IllegalArgumentException("Port already in use: "+ex );
			throw ((IllegalArgumentException)iae.initCause(ex));
		}
		ports.put( portName, port );
		return port;
	}

	private boolean connectedTo( StationData sd ) {
//		return jmf.isConnectedTo( sd.getIPAddr() );
		return ssa.isConnectedTo( sd.getIPAddr() );
	}

	Object selectedNode() {
		if( lastSelTree != null && lastSelTree.getSelectionPath() != null )
			return lastSelTree.getSelectionPath().getLastPathComponent();
		return null;
	}

	Entry findEntry( String name ) {
		// What are these '&' and '%' chars about I wonder...
		while( name.charAt(0) == '#' || name.charAt(0) == '&' || name.charAt(0) == '%' ) {
			name = name.substring(1);
		}
		for( int i = 0; i < staList.size(); ++i ) {
			Entry e = (Entry)staList.get(i);
			if( e.getStation().getID().equals(name) || e.getStation().getCall().equals(name))
				return e;
		}
		return null;
	}

	private void connectToSelected() {
		connectToSelected( lastSelTree );
	}

	private void connectToSelected( JTree tree ) {
//		new Throwable("connectToSelected").printStackTrace();
		TreePath path = tree.getSelectionPath();
		if( path == null )
			return;
		final Object node = path.getLastPathComponent();
		new ComponentUpdateThread<Boolean>( getAction("Conn") ) {
			@Override
			public Boolean construct() {
				if( node instanceof Entry ) {
					Entry ent = (Entry)node;
					StationData sd = ent.getStation();
					return new Boolean(connectTo( sd,false ));
				}
				return new Boolean(false);
			}
			@Override
			public void finished() {
				Boolean b = getValue();
				getAction("Conn").setEnabled(!b.booleanValue());
			}
		}.start();
	}
	
	private void disconnectFromSelected() {
		final Object node = selectedNode();
		log.finer("disconnect selected is: "+node);
		if( node instanceof Entry ) {
			final Entry ent = (Entry)node;
			final StationData sd = ent.getStation();
			log.fine("Disconnecting from: "+sd.getIPAddr() );
			new ComponentUpdateThread<Void>( getAction("Disc") ) {
				public Void construct() {
					try {
						ssa.disconnectFrom( sd.getIPAddr() );
						sendEvent( new LinkEvent<StationData>(
							this, false, LinkEvent.STATION_DISC_EVENT, 
							ssa.getConnectCount(), sd ) );
					} catch( Exception ex ) {
						reportException( ex );
					}
					return null;
				}
			}.start();
		}
	}

	public boolean getTxMode() {
		return txActive;
	}
	
	public void setTxMode(boolean how) {
		log.fine("set TXMode: "+how );
		txActive = how;
		if( txActive ) {
			setMode( LinkEvent.MODE_TRANSMIT );
			ssa.transmit();
		} else if( !txActive ) {
			setMode( LinkEvent.MODE_IDLE ); 
			ssa.receive();
		}
	}
	
	boolean connected;
	
	boolean txActive,rxActive;
	boolean txToggle = true;
	public static Level PROGRESS_LEVEL = new Level( "PROG",
		((Level.FINE.intValue() - 
			Level.INFO.intValue())/2) + 
			Level.INFO.intValue() ){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;};

	private void showStatus( final String str ) {
		runInSwing( new Runnable() {
			public void run() {
				oinfo.setText( str );
			}
		});
	}

	static void progress( final String str ) {
		proglog.log(PROGRESS_LEVEL, str );
//		if( pr.isAudioTrace() )
//			System.out.println(str);
	}

	Object bindlock = new Object();
	boolean areBinding = false;
	EventPackets ep;
	String logon(final JLabel lab, boolean cancelled[]) {
		connected = false;
		boolean badhost = true;
		String s = null;
		int trycnt = 0;
		while( !connected && !cancelled[0] && badhost ) {
			for( int i = 0; !connected && !cancelled[0] && i < pr.servers.size(); ++i ) {
				s = (String)pr.servers.elementAt(i);
				progress("Trying (host['"+i+"' of '"+pr.servers.size()+"']="+badhost+"): "+s );
				final String fs = s;
				SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						if( lab != null ) {
							showStatus(fs);
							lab.setText(fs);
							lab.revalidate();
							lab.getParent().repaint();
						}
					}
				});
				try {
					String call = pr.getCallSign();
					String password = pr.getPassword();
					String location = pr.getQTH();
					if( call != null && password != null && location != null && s != null ) {
						progress("Create Service Access");
						acc = new ServerAccess( Javecho.this, pr ); //call, password, location );
						progress("setup access for \""+s+"\"" );
						acc.setup( s );
						try {
							progress("logon to echolink server: "+s );
							badhost = acc.sendLogon(false) == false;
							if( !badhost ) {
								connected = true;
								progress( "Refresh station list" );
								refreshList(staList == null || staList.size() < 10 );
							} else if( i == pr.servers.size()-1 ) {
								cancelled[0] = true;
								showStatus( "Can't logon");
							} else {
								showStatus( s+": I/O err?");
							}
						} catch( ConnectException ex ) {
							ConnectException e = new ConnectException(s);
							e.initCause(ex);
							reportException(e,false);
							staList = new Vector<Entry>();
							runInSwing( new Runnable() {
								public void run() {
									trmod.setData( staList );
									locmod.setData( staList );
								}
							});
							badhost = false;
							try {
								Thread.sleep( 3000 );
							} catch( Exception exx ) {
							}
							continue;
						} catch( UnknownHostException ex ) {
							reportException(ex, false);
							staList = new Vector<Entry>();
							runInSwing( new Runnable() {
								public void run() {
									trmod.setData( staList );
									locmod.setData( staList );
								}
							});
							badhost = true;
							continue;
						} catch( final IOException ex ) {
							reportException(ex, false);
							staList = new Vector<Entry>();
							runInSwing( new Runnable() {
								public void run() {
									trmod.setData( staList );
									locmod.setData( staList );
									oinfo.setText( ex+"" );
								}
							});
							badhost = true;
							break;
						}
						site = s;
					} else {
						throw new NullPointerException("No data provided" );
					}
				} catch( Exception ex ) {
					reportException(ex);
				}
			}
			// if still not connected pause to slow things down
			if( !connected ) {
				if( ++trycnt > 4 ) {
					JOptionPane.showMessageDialog( Javecho.this,
						"Your networking is not setup, or you\n"+
						"are not connected to the internet\n"+
						"please resolve the problem and try\n"+
						"connecting again.",
						"Networking Error",
						JOptionPane.ERROR_MESSAGE );
					break;
				}
				try {
					Thread.sleep(2000);
				} catch( Exception ex ) {
				}
			}
		}

		if( ssa == null ) {
			progress("Create ConnectionManager");
			ssa = new ConnectionManager(this, null, pr);
		}

//		if( cancelled[0] && !connected ) {
//			return;
//			System.exit(1);
//		}
		
		new Thread() {
			public void run() {
				synchronized( bindlock ) {
					if( areBinding || ep != null )
						return;
					areBinding = true;
				}
				try {
					doBind();
				} finally {
					areBinding = false;
				}
			}
			
			private void doBind() {
				boolean bound = false;
				while( !bound ) {
					try {
						progress("Create packet listener");
						ep = new EventPackets(
								Javecho.this, new RtpRtcpHandler() {
							public void handleData( String ipaddr, byte[]data, int len ) {
								if( ssa != null )
									ssa.handleData( ipaddr, data, len );
							}
							public void handleControl( String ipaddr, byte[]data, int len ) {
								doControl( ipaddr, data, len );
							}
						}, pr );
						progress("setup packet listener");
						ep.setup();
						progress("set EventPackets in ConnectionManager");
						ssa.setEventPacketsInstance(ep);
						bound = true;
					} catch( BindException ex ) {
						reportException(ex);
					} catch( Exception ex ) {
						reportException(ex);
					}
				}
			}
		}.start();
		return s;
	}

	void reportException( Throwable ex, boolean show ) {
		String cl = ex.getClass().getName();
		int idx = cl.lastIndexOf('.');
		cl = cl.substring(idx+1);
		reportException( ex, cl+" Occured", JOptionPane.ERROR_MESSAGE, show );
	}

	public void reportException( Throwable ex ) {
		reportException( ex, true );
	}

	private void reportException( final Throwable ex, final String title, final int type ) {
		reportException( ex, title, type, true );
	}

	private void reportException( final Throwable ex, final String title, final int type, boolean show ) {
		log.log( Level.WARNING, ex.toString(), ex );
		// Don't raise dialog in sysop mode either
		if( !show || pr.isUserMode() == false )
			return;
		String msg = ex.getMessage();
		if( msg == null )
			msg = ex.toString();
		else
			msg = ex.getClass().getName()+":\n"+msg;
		Throwable t = null;
		Throwable et = ex;
		while( (t = et.getCause()) != null ) {
			if( t != null ) {
				String str = t.getMessage();
				if( str == null )
					str = t.toString();
				else
					str = t.getClass().getName()+"\n"+str;
				msg += "\n"+str;
			}
			et = t;
		}
		final String tmsg = msg;
		runInSwing( new Runnable() {
			public void run() {
				showMessageDialog( tmsg, title, type );
			}
		} );
	}
	
	public boolean isMoreConnections() {
		if( pr.isAllowConferences() )
			return ssa.getConnectCount() < pr.getConferenceCount();
		return ssa.getConnectCount() == 0;
	}
	
	JEditorPane pn;
	Object olock = new Object();
	void showMessages( String title, String hdr, List<?> entries ) {
		if( entries.size() == 0 )
			return;
		String str = "<html><body>";
		String body = "";
		for( int i = 0; i < entries.size(); ++i ) {
			if( i > 0 )
				body += pr.isUserMode() ? "<br>" : "\r\n";
			if( entries.get(i) instanceof Entry ) {
				body += ((Entry)entries.get(i)).getStation().getCall().trim();
			} else {
				body += entries.get(i).toString();
			}
		}
		if( pr.isUserMode() == false ) {
			progress( body );
			return;
		}
		if( hdr != null )
			str += "<b>"+hdr+"</b><p>";
		str += "<font color=\"blue\">";
		str += body + "</font></body></html>";
		synchronized( olock ) {
			if( pn == null ) {
				pn = new JEditorPane("text/html", str);
				showMessageDialog( pn, title, JOptionPane.INFORMATION_MESSAGE );
			} else {
				final String fstr = str;
				runInSwing( new Runnable() {
					public void run() {
						pn.setText( fstr );
					}
				});
				return;
			}
		}
		pn = null;
	}

	QSOHistory qsoHist;
	void showCallSigns() {
		qsoHist.showFrame();
	}
	
	boolean actionIsSelected( String name ) {
		Action a = getAction(name);
		if( a instanceof LabeledToggleAction ) {
			return ((LabeledToggleAction)a).isSelected();
		}
		return false;
	}

	private void configureState() {
		LabeledToggleAction busy = (LabeledToggleAction)getAction("Busy");
		getAction("Trans").setEnabled( !busy.isSelected() );
		getAction("Conn").setEnabled( !busy.isSelected() );
		getAction("Disc").setEnabled( !busy.isSelected() );
		getAction("Connect To...").setEnabled( !busy.isSelected() );
		if( !busy.isSelected() ) {
				getAction("Disc").setEnabled(isConnected());
				getAction("Trans").setEnabled(isConnected());
				getAction("Conn").setEnabled(isMoreConnections());
		busy.setEnabled( !actionIsSelected("Trans") &&
			!actionIsSelected("Conn") );
		}
	}
//	
//	public void setLoginNeeded() {
//		loginNeeded = true;
//	}
//	boolean loginNeeded;

	private void defineActions() {

		actions.put( "Export", new LabeledAction("Export Favorites...", 
			"Export Favorites..." ) {
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				List<Entry>v = frmod.data;

				Preferences exn = prefs.node("export/friends");

				boolean withIP = prefs.getBoolean("withIp", false);
				boolean withHeader = prefs.getBoolean("withHeader", true);
				final String dir = prefs.get("toDir", System.getProperty("user.dir") );
				final String toFile = prefs.get("toFile", System.getProperty("user.dir")+File.separator+"stations.csv" );

				final JDialog dlg = new JDialog( Javecho.this, "Configure Export", true );
				Packer pk = new Packer( dlg.getContentPane() );
				int y = -1;
				pk.pack( new JLabel( "Export to:" ) ).gridx(0).gridy(++y).east();
				final JTextField file = new JTextField(toFile);
				pk.pack( file ).gridx(1).gridy(y).fillx();
				final JButton change = new JButton("Browse...");
				change.addActionListener( new ActionListener() {
					public void actionPerformed( ActionEvent ev ) {
						JFileChooser f = new JFileChooser();
						f.setCurrentDirectory( new File( dir ) );
						f.setSelectedFile( new File( new File(dir), toFile ) );
						f.setFileFilter( new FileFilter() {
							public boolean accept( File f ) { 
								if( f.isDirectory() )
									return true;
								return f.getName().toLowerCase().endsWith(".csv");
							}  
							public String getDescription() {
								return "exported stations (.csv)";
							}
						});
						f.setDialogTitle( "Select Export File"  );
						if( f.showOpenDialog( dlg) == JFileChooser.APPROVE_OPTION ) {
							File ff = f.getSelectedFile();
							file.setText( ff.toString() );
							String d = ff.getParent();
							String fl = ff.getName();
							prefs.put("toDir", d);
							prefs.put("toFile", fl );

						}
					}
				});
				pk.pack( change ).gridx(2).gridy(y);
				JCheckBox addr, header;
				pk.pack( addr = new JCheckBox("Include IP Addresses?", withIP ) ).gridx(0).gridy(++y).fillx().weightx(0);
				pk.pack( header = new JCheckBox("Include Header?", withHeader ) ).gridx(0).gridy(++y).fillx().weightx(0);
				pk.pack( new JSeparator() ).gridx(0).gridy(++y).gridw(3).fillx();
				final JButton okay = new JButton("Okay");
				final JButton cancel = new JButton("Cancel");
				final boolean cancelled[] = new boolean[1];
				okay.addActionListener( new ActionListener() {
					public void actionPerformed( ActionEvent ev ) {
						cancelled[0] = false;
						dlg.setVisible(false);
					}
				});
				cancel.addActionListener( new ActionListener() {
					public void actionPerformed( ActionEvent ev ) {
						cancelled[0] = true;
						dlg.setVisible(false);
					}
				});
				dlg.addWindowListener( new WindowAdapter() {
					public void windowClosing( WindowEvent ev ) {
						cancelled[0] = true;
					}
				});
				pk.pack( okay ).gridx(0).gridy(++y).west();
				pk.pack( cancel ).gridx(2).gridy(y).east();
				dlg.pack();
				dlg.setLocationRelativeTo( Javecho.this );
				dlg.setVisible(true);
				if( cancelled[0] )
					return;
				withIP = addr.isSelected();
				withHeader = header.isSelected();
				prefs.putBoolean("withIp", withIP );
				prefs.putBoolean("withHeader", withHeader );
				String name = file.getText();
				try {
					
					FileWriter fw = new FileWriter( name );
					try {
						PrintWriter op = new PrintWriter( fw );
						if( header.isSelected() ) {
							op.println("Type,"+StationData.exportHeader(withIP));
						}
						for( int i = 0; i < v.size(); ++i ) {
							Entry e = v.get(i);
							StationData sd = e.getStation();
							op.println( StationData.quotedCSV(Entry.typeName(e.getType()))+","+sd.export(withIP) );
						}
						op.close();
					} finally {
						fw.close();
					}
				} catch( Exception ex ) {
					reportException(ex);
				}
			}
		});

		actions.put( "About JavEcho...", new LabeledAction("About Javecho...", 
			"About Javecho..." ) {
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				showMessages( "About Javecho", null,
					new Vector<String>( Arrays.asList( new String[] {
					"Javecho v"+version,
					"Copyright (c) 2002-2004, Gregg Wonderly - W5GGW",
					"All rights Reserved",
					"Questions to w5ggw@arrl.net" })));
			}
		});

		actions.put( "Server Message...", new LabeledAction("Server Message...", 
			"Server Message..." ) {
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				showMessages( "System Messages",
					"The following information was returned by "+
					"the remote server:", sysmsg );
			}
		});

		actions.put( "Callsign Log...", new LabeledAction("Callsign Log...", 
			"Callsign Log..." ) {
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				showCallSigns();
			}
		});

		actions.put( "Alarm Log", new LabeledAction("Alarm Log", 
			"Alarm Log" ) {
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				alarmMgr.showAlarmLog();
			}
		});

		actions.put( "Trans", new LabeledAction( loadIcon("xmit.gif"),
			"Transmit", false ) {
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				log.finest("action: current txmode is: "+getTxMode() );
				new ComponentUpdateThread( this ) {
					public Object construct() {
						try {
							setTxMode(!getTxMode());
						} catch( InvalidSoundSystemConfigurationException ex ) {
							reportException(ex);
							setTxMode(false );
							String call = pr.getCallSign();
							String text = "My sound card doesn't support 8khz, Mono, "+
								"big-endian/little-endian audio,"+
								"so I can't transmit";
							byte data[] = new byte[7+call.length()+text.length()+3];
							byte[]arr = ("oNDATA"+call+">"+text).getBytes();
							System.arraycopy( arr, 0, data, 0, arr.length );
							data[arr.length] = '\r';
							data[arr.length+1] = '\n';
							data[arr.length+2] = 0;
							try {
								ssa.sendPacket( data, ssa.CHAT_TYPE );
							} catch( Exception exx ) {
								reportException(exx);
							}
						}		
						return null;
					}
				}.start();
			}
		});

		actions.put( "Connect To...", new LabeledAction( loadIcon("connect.gif"), 
			"Connect To...", true ) {
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				try {
					connectToChoose();
					getAction("Conn").setEnabled(false);
				} finally {
					configureState();
				}
			}
		});

		actions.put( "Tone Generator...", new LabeledAction( "Tone Generator...", 
			"Tone Generator...", true ) {
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
			}
		});

		actions.put( "Sysop Settings...",
			new LabeledAction( "Sysop Settings...", 
				"Sysop Settings...", true ) {
			/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				new SysopSettings( Javecho.this, pr );
				log.info("isUserMode ? "+pr.isUserMode() );
				log.info("isRxCtrlVox ? "+pr.isRxCtrlVox() );
				try {
					average.setVox( pr.isUserMode() ? false :
						pr.isRxCtrlVox() );
				} catch(Exception ex) {
					log.log(Level.WARNING,ex.toString(),ex);
				}
			}
		});

		actions.put( "Conn", new LabeledAction( loadIcon("connect.gif"), 
			"Connect", false ) {
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				try {
					connectToSelected();
					getAction("Conn").setEnabled(false);
				} finally {
					configureState();
				}
			}
//			public void setEnabled( boolean how ) {
//				super.setEnabled(how);
//				new Throwable().printStackTrace();
//			}
		});

		actions.put( "Disc", new LabeledAction( loadIcon("disco.gif"), 
			"Disconnect", false ) {
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				new ComponentUpdateThread( getAction("Disc") ) {
					public Object construct() {
						progress("Doing disconnect");
						try {
							if( ssa.getConnectCount() == 1 ) {
								progress("Disconnect from all");
								ssa.disconnectAll();
								sendEvent( new LinkEvent(
									this, false, LinkEvent.STATION_DISC_EVENT, 
									ssa.getConnectCount() ) );
							} else {
								progress("Disconnect from ?");
								disconnectFrom();
							}
						} catch( Exception ex ) {
							reportException(ex);
						} finally {
							try {
								progress("Checking connections");
								checkConnections();
							} finally {
								configureState();
							}
						}
						return null;
					}
				}.start();
			}
		});

		actions.put( "Refresh", new LabeledAction( loadIcon("refresh.gif"), 
			"Refresh" ) {
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
//				if( !connected ) {
//					logonAndSetup();
//				} else {
					refreshList(true);
//				}
			}
		});

		actions.put( "Info", new LabeledAction( loadIcon("info.gif"), "Info" ) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				Object o = selectedNode();
				if( o instanceof Entry == false )
					return;
				Entry e = (Entry)o;
				stationInfo( e.getStation() );
			}
		});

		actions.put( "Find", new LabeledAction( loadIcon("find.gif"), "Find" ) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				new FindStationDialog( Javecho.this, new FindHandler() {
					public void locate( String val ) {
						Vector<Entry> v = new Vector<Entry>();
						val = val.toLowerCase();
						for( int i = 0 ;i < staList.size(); ++i ) {
							Entry e = (Entry)staList.get(i);
							if( e.getStation().getCall().toLowerCase().startsWith( val ) ) {
								v.addElement( e );
							}
						}
						if( v.size() == 0 ) {
							Toolkit.getDefaultToolkit().beep();
							return;
						}
						stations.clearSelection();
						stTabs.setSelectedIndex(1);
						boolean found = false;
//						TreeModel mod = stations.getModel();
						Object root = trmod.getRoot();
						for( int i = 0; i < v.size(); ++i ) {
							Entry e = v.elementAt(i);
							if( searchTree( stations, e, trmod, root, new TreePath(new Object[]{root}) ) ) {
								found = true;
							}
//							for( int j = 0; j < count; ++j ) {
//								TreePath p = stations.getPathForRow( j );
//								Object o = p.getLastPathComponent();
//								if( o == e ) {
//									stations.addSelectionPath( p );
//									stations.expandPath(p.getParentPath());
//									stations.makeVisible(p);
//									found = true;
//									break done;
//								}
//							}
						}
						if( !found ) {
							Toolkit.getDefaultToolkit().beep();
						}
					}
				});
			}
		});

		actions.put( "Exit", new LabeledAction( "Exit" ) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				checkExit();
			}
		});

		actions.put( "Alarms...", new LabeledAction( loadIcon("alarms.gif"), "Alarms..." ) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				alarmMgr.showAlarmEditor();
			}
		});

		actions.put( "Setup", new LabeledAction( loadIcon("setup.gif"), "Setup..." ) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				progress("Setup...");
				new SetupDialog(Javecho.this, pr, false);
				getAction("Sysop Settings...").setEnabled( !pr.isUserMode() );
				setTitle( "JavEcho - "+pr.getCallSign() );
				average.setVox( pr.isUserMode() ? false :
					pr.isRxCtrlVox() );
			}
		});

		actions.put( "Prefs", new LabeledAction( loadIcon("prefs.gif"), "Preferences..." ) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				new PreferencesDialog(Javecho.this, pr);
			}
		});

		actions.put( "Busy", new LabeledToggleAction( loadIcon("busy.gif"), "Make Me Busy" ) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				try {
					setSelected( !isSelected() );
					notifyListeners();
					progress("Busy: "+isSelected());
					new ComponentUpdateThread( this ) {
						public Object construct() {
							try {
								updateLogon( );
							} catch( Exception ex ) {
								reportException(ex);
							}
							return null;
						}
					}.start();
				} finally {
					configureState();
				}
			}
		});

		actions.put( "Connection Statistics", new LabeledAction( loadIcon("stats.gif"), 
			"Connection Statistics" ) {
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				if( cons != null ) {
					cons.setVisible(true);
					return;
				}
			}
		});

		actions.put( "Station Summary", new LabeledAction( loadIcon("station.gif"), 
			"Station Summary" ) {
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				if( stsumm != null ) {
					stsumm.setVisible(true);
					stsumm.refresh();
					return;
				}
				stsumm = new StationSummary( Javecho.this );
			}
		});

//		actions.put( "Print", new LabeledAction( loadIcon("print.gif"), "Print" ) {
//			public void actionPerformed( ActionEvent ev ) {
//			}
//		});
//		menuAction( m, getAction("Contents"), KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
//		m.add( getAction("Search...") );
//		m.add( getAction("Index...") );
//		m.addSeparator();
//		m.add( getAction("JavEcho Web Site...") );
//		m.add( getAction("Help on the Web...") );
//		m.add( getAction("Software Updates...") );
//		m.addSeparator();
//		m.add( getAction("About JavEcho...") );

		actions.put( "JavEcho Web Site...", new LabeledAction( 
				"JavEcho Web Site...", "JavEcho Web Site..." ) {
			/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				if( helpFrame == null ) {
					helpFrame = new JFrame( "Javecho Web Site" );
					HTMLBrowser br = new HTMLBrowser(helpFrame, Javecho.this);
					try {
						br.setPage( new URL( "http://javecho.dev.java.net" ) );
					} catch( Exception ex ) {
						reportException(ex);
					}
					helpFrame.setContentPane(br);
					helpFrame.pack();
					helpFrame.setSize( 500, 300 );
					helpFrame.setLocationRelativeTo( Javecho.this );
					helpFrame.addWindowListener( new WindowAdapter() {
						public void windowClosing(WindowEvent ev) {
							helpFrame = null;
						}
					});
				} else {
					helpFrame.toFront();
				}
				helpFrame.setVisible(true);
			}
		});

		actions.put( "Help", new LabeledAction( loadIcon("help.gif"), "Help" ) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed( ActionEvent ev ) {
				if( helpFrame == null ) {
					helpFrame = new JFrame( "Echolink Help" );
					HTMLBrowser br = new HTMLBrowser(Javecho.this);
					try {
						br.setPage( new URL( "http://www.echolink.org/help" ) );
					} catch( Exception ex ) {
						reportException(ex);
					}
					helpFrame.setContentPane(br);
					helpFrame.pack();
					helpFrame.setSize( 500, 300 );
					helpFrame.setLocationRelativeTo( Javecho.this );
					helpFrame.addWindowListener( new WindowAdapter() {
						public void windowClosing(WindowEvent ev) {
							helpFrame = null;
						}
					});
				} else {
					helpFrame.toFront();
				}
				helpFrame.setVisible(true);
			}
		});
	}

	JFrame helpFrame;
	private ListListModel<Connection> connModel;
	private JList<Connection> connList;
	private boolean searchTree( JTree stations, Entry e, TreeModel mod, Object root, TreePath p ) {
		boolean found = false;
		log.finest("Checking in:["+root+"]: "+p );
		for( int i = 0; i < mod.getChildCount(root); ++i ) {
			Object nd = mod.getChild(root,i);
			if( mod.isLeaf( nd ) ) {
				if( nd == e ) {
					found = true;
					progress("found: "+e );
					Object[] tp = new Object[ p.getPath().length + 1 ];
					System.arraycopy( p.getPath(), 0, tp, 0, p.getPath().length );
					tp[p.getPath().length] = nd;
					TreePath pp = new TreePath( tp );
					log.finest("selecting path: "+pp );
					stations.addSelectionPath( pp );
					stations.expandPath(pp.getParentPath());
					stations.makeVisible(pp);
					stations.scrollPathToVisible(pp);
					stations.makeVisible(pp);
					stations.scrollPathToVisible(pp);
					stations.repaint();
				}
			} else {
				Object[] tp = new Object[ p.getPath().length + 1 ];
				System.arraycopy( p.getPath(), 0, tp, 0, p.getPath().length );
				tp[p.getPath().length] = nd;
				TreePath pp = new TreePath( tp );
				log.finest( "old path: "+p+", new path: "+pp );
				found |= searchTree( stations, e, mod, nd, pp );
			}
		}
		return found;
	}

	public void stationInfo( StationData station ) {
		String stname = station.getIPAddr();
		try {
			stname = InetAddress.getByName( stname ).getHostName();
		} catch( Exception ex ) {
			stname = ex.toString();
		}
		String str = 
			"Station:  "+station.getCall()+"\n\n"+
			"Address:  "+station.getIPAddr()+"\n\n"+
			"Hostname: "+stname;
		JOptionPane.showMessageDialog( Javecho.this, str, "Station Information",
			JOptionPane.INFORMATION_MESSAGE );
	}

	public Action getAction( String name ) {
		if( actions.get(name) == null ) {
			LabeledAction la = new LabeledAction( name, name, false ) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed( ActionEvent ev ){
					showMessageDialog( "Action Not Implemented" );
				}
			};
			la.setEnabled(false);
			return la;
		}
		return (Action)actions.get( name );
	}

	public void refreshList( final boolean wait ) {
		Throwable ex = new Throwable("refreshing list");
		log.log( Level.FINE, ex.toString(), ex );
		new ComponentUpdateThread<Void>( getAction("Refresh") ) {
			public Void construct() {
				doListRefresh( wait );
				return null;
			}
		}.start();
	}
	
	private void doListRefresh( final boolean wait ) {
		Component[] comps = null;
		if( stsumm == null )
			comps = new Component[] {stations,favorites,stinfo};
		else
			comps = new Component[] {stations,favorites,stinfo,stsumm};
		sysmsg = new Vector<Entry>();

		final JDialog dlg = new JDialog( this, "Loading Station List", false );
		Packer pk = new Packer( dlg.getContentPane() );
		final JLabel l = new JLabel( "Connection to Server for Station List" );
		l.setFont( new Font( "serif", Font.PLAIN, 18 ) );
		pk.pack( l ).gridx(0).gridy(0).fillboth().inset(10,10,10,10);

		new ComponentUpdateThread( comps ) {

			public Object construct() {
				int idx = 0;
				try {
					String ret[] = new String[1];
					StationData.clearOnLine();
					boolean connected = false;
					String s = null;
					Socket logonSock = null;

					do {
						if( !Javecho.this.connected ) {
							boolean cancelled[] = new boolean[1];
							logon(cursite, cancelled);
							if( !Javecho.this.connected || cancelled[0] ) {
								log.info("returning from connect: "+Javecho.this.connected+
									", cancelled: "+cancelled[0] );
								return null;
							}
						}
						curhost = s = (String)pr.servers.elementAt(idx);
						progress("try logon to "+s);
						log.info("Trying login to: "+s );
						l.setText( "Trying Server: "+s );
						acc.setup( s );
						try {
							logonSock = null;
							acc.sendLogon( ssa == null ? false : ssa.isConnected(), false );
//							if( logonSock.isClosed() ) {
//								throw new IllegalStateException("socket closed: "+logonSock );
//							}
							connected = true;
							progress( "Logged onto "+s );
						} catch( UnknownHostException ex ) {
							log.log(Level.WARNING,ex.toString(), ex);
							if( idx + 1 >= pr.servers.size() )
								break;
							idx = (idx + 1) % pr.servers.size();
						} catch( NoRouteToHostException ex ) {
							log.log(Level.WARNING,ex.toString(), ex);
							try {
								Thread.sleep( 2000 );
							} catch( Exception exx ) {
							}
						} catch( IOException ex ) {
							log.log(Level.WARNING,ex.toString(), ex);
							try {
								Thread.sleep( 2000 );
							} catch( Exception exx ) {
							}
							idx = (idx + 1) % pr.servers.size();
						}
					} while( !connected );

					log.info("Got connected to: "+s+", sock: "+logonSock );
					l.setText( "Requesting Station List...");
					Vector<Entry> v = acc.getList(pr.servers, s, ret, logonSock );
					site = ret[0];
					if( v != null ) 
						trmod.setData(staList = v);
					return v;
				} catch( ConnectException ex ) {
					log.log(Level.WARNING,ex.toString(), ex);
				} catch( IOException ex ) {
					log.log(Level.WARNING,ex.toString(), ex);
				} catch( Exception ex ) {
					reportException(ex);
				}
				return null;
			}

			public void finished() {
				try {
					if( getValue() != null ) {
						log.finest("Found "+staList.size()+" stations" );
						new ComponentUpdateThread<Void>( locations ) {
							public void setup() {
								super.setup();
								locations.setToolTipText("Sorting Stations...");
							}
							public Void construct() {
									locmod.setData( staList );
									return null;
							}
							public void finished() {
								super.finished();
								locations.setToolTipText("");
								if( wait )
									dlg.setVisible(false);
								stations.revalidate();
								favorites.revalidate();
								locations.revalidate();
								locations.repaint();
								favorites.repaint();
								stations.repaint();
								if( stsumm != null )
									stsumm.refresh();
							}
						}.start();
						if( stinfo != null ) {
							stinfo.setText( staList.size()+
								" stations on "+site+
								" ("+connPercent(staList)+
								"% are busy)" );
								stinfo.repaint();
						}
						boolean some = false;
						l.setText( "Checking for Success...");
						for( int i = 0; i < staList.size(); ++i ) {
							Entry e = (Entry)staList.get(i);
							if( e.getType() != Entry.TYPE_MSG ) {
								some = true;
								break;
							}
						}
						if( !some ) {
							showMessages( "System Messages",
								"The following information was returned by "+
								"the remote server:", sysmsg );
						}
//					} else {
//						if( wait ) {
//							dlg.setVisible(false);
//							dlg.dispose();
//						}
					}
				} finally {
					if( wait ) {
						dlg.setVisible(false);
						dlg.dispose();
					}
					super.finished();
				}
			}
		}.start();

		if( wait ) {
			dlg.pack();
			dlg.setLocationRelativeTo(this);
			dlg.setVisible(true);
			dlg.dispose();
		}
	}

	public void connectToChoose() {
		JPanel p = new JPanel();
		Packer pk = new Packer( p );
		int y = -1;
		final JRadioButton t1 = new JRadioButton( "Call Sign:"),
			t2 = new JRadioButton( "Node Number:");//,
//			t3 = new JRadioButton( "IP Address:");
		final JTextField call = new GrayedTextField()
			,num = new GrayedTextField()
//			,ip = new GrayedTextField()
			;
		pk.pack( t1).gridx(0).gridy(++y).west();
		pk.pack( call ).gridx(1).gridy(y).fillx();
		pk.pack( t2 ).gridx(0).gridy(++y).west();
		pk.pack( num ).gridx(1).gridy(y).fillx();
//		pk.pack( t3 ).gridx(0).gridy(++y).west();
//		pk.pack( ip ).gridx(1).gridy(y).fillx();
		pk.pack( new JSeparator() ).gridx(0).gridy(++y).gridw(2).fillx();
		ButtonGroup grp = new ButtonGroup();
		grp.add(t1);
		grp.add(t2);
//		grp.add(t3);
		
		ActionListener lis = new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				call.setEnabled( t1.isSelected() );
				num.setEnabled( t2.isSelected() );
//				ip.setEnabled( t3.isSelected() );
			}
		};
		t1.setSelected(true);
		lis.actionPerformed( new ActionEvent( t1, 1, "" ) );
		t1.addActionListener(lis);
		t2.addActionListener(lis);
//		t3.addActionListener(lis);
		int idx = JOptionPane.showConfirmDialog( Javecho.this,
			p, "Connect To...", JOptionPane.OK_CANCEL_OPTION );
		if( idx == JOptionPane.CANCEL_OPTION )
			return;
		if( t1.isSelected() ) {
			String cs = call.getText();
			Entry e = findEntry(cs);
			connectTo( e.getStation(), false );
		} else if( t2.isSelected() ) {
			String nd = num.getText();
			Entry e = findEntry( nd );
			connectTo( e.getStation(), false );
//		} else if( t3.isSelected() ) {
//			String addr = ip.getText();
//			StationData st = StationData.dataFor( addr, addr, addr, addr );
//			connectTo(st,false);
		}
	}
	
	static class GrayedTextField extends JTextField {
		private static final long serialVersionUID = 1L;

		public void setEnabled( boolean how ) {
			super.setEnabled( how );
			setOpaque(how);
		}
	}

	public Icon loadIcon( String name ) {
		URL u = getClass().getClassLoader().getResource( "images/"+name );
		if( u != null )
			return new ToolIcon( u );	
		return null;
	}
	
	static class ToolIcon extends ImageIcon {
		private static final long serialVersionUID = 1L;
		static int sz = 2;
		Icon icon;
		public ToolIcon( URL u ) {
			super(u);
			if( super.getIconWidth() > sz )
				sz = super.getIconWidth();
			if( super.getIconHeight() > sz )
				sz = super.getIconHeight();
		}
		public int getIconWidth() {
			return sz;
		}
		public int getIconHeight() {
			return sz;
		}
		public void paintIcon( Component c, Graphics g, int x, int y ) {
//			new Throwable().printStackTrace();
			int cx = x;
			int cy = y;
			int hx = super.getIconWidth();
			int hy = super.getIconHeight();
			if( hx < sz )
				cx += (sz - hx)/2;
			if( hy < sz )
				cy += (sz - hy)/2;
			super.paintIcon( c, g, cx, cy );
		}
	}

	static abstract class LabeledToggleAction extends LabeledAction {
		private static final long serialVersionUID = 1L;
		boolean sel;
		Vector<ActionListener> lis;
		public void addActionListener( ActionListener l ) {
			lis.addElement(l);
		}
		public void removeActionListener( ActionListener l ) {
			lis.removeElement(l);
		}
		private void notifyListeners( ActionEvent ev ) {
			for( int i = 0; i < lis.size(); ++i ) {
				ActionListener al = lis.elementAt(i);
				al.actionPerformed(ev);
			}
		}
		protected void notifyListeners() {
			notifyListeners( new ActionEvent(
				this,1,""+new Boolean(isSelected())) );
		}
		public void setSelected( boolean how ) {
			sel = how;
		}
		public boolean isSelected() {
			return sel;
		}
		public LabeledToggleAction( String desc ) {
			super(desc);
			lis = new Vector<ActionListener>();
		}
		public LabeledToggleAction( Icon icon, String desc ) {
			super(icon,desc);
			lis = new Vector<ActionListener>();
		}
		public LabeledToggleAction( String name, String desc ) {
			super(name,desc);
			lis = new Vector<ActionListener>();
		}
		public LabeledToggleAction( String name, String desc, boolean enabled ) {
			super( name, desc, enabled );
			lis = new Vector<ActionListener>();
		}
		public LabeledToggleAction( Icon icon, String desc, boolean enabled ) {
			super( icon, desc, enabled );
			lis = new Vector<ActionListener>();
		}
	}

	static abstract class LabeledAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public LabeledAction( String desc ) {
			if( desc != null ) {
	        		putValue( Action.NAME, desc );
	        		putValue( Action.SHORT_DESCRIPTION, desc );
			}
		}
		public LabeledAction( Icon icon, String desc ) {
	        if( icon != null )
	        	putValue( Action.SMALL_ICON, icon );
			if( desc != null )
	        	putValue( Action.NAME, desc );
	        		putValue( Action.SHORT_DESCRIPTION, desc );
		}
		public LabeledAction( String name, String desc ) {
	        if( name != null )
	        	putValue( Action.NAME, name );
	        		putValue( Action.SHORT_DESCRIPTION, name );
			if( desc != null )
	        		putValue( Action.SHORT_DESCRIPTION, desc );
		}
		public LabeledAction( String name, String desc, boolean enabled ) {
			this( name, desc );
			this.setEnabled( enabled );
		}
		public LabeledAction( Icon icon, String desc, boolean enabled ) {
			this( icon, desc );
			this.setEnabled( enabled );
		}
	}
	
	int connPercent( List<Entry> v ) {
		int cnt = 0;
		for( int i = 0; i < v.size(); ++i ) {
			if( ((Entry)v.get(i)).getStation().isBusy() )
				++cnt;
		}
		if( v.size() == 0 )
			return 0;
		return cnt*100/v.size();
	}
//	
//	void runInSwing( Runnable r ) {
//		if( SwingUtilities.isEventDispatchThread() ) {
//			r.run();
//		} else {
//			try {
//				SwingUtilities.invokeAndWait( r );
//			} catch( Exception ex ) {
//				ex.printStackTrace();
//			}
//		}
//	}
	
	void updateLogon() throws IOException {
		updateLogon( ssa.getConnectCount() > 1, pr.getCallSign() );
	}

	void updateLogon( boolean isConf, String name ) throws IOException {
		;
//		if( ssa.isConnected() == false )
//				acc.updateLogon( pr.getQTH(), false );

		if( isConf && pr.isShowNameConnConf() ) {
			acc.updateLogon( "In Conference "+name );
		} else if( pr.isUpdateLocationEntryWithStatus() && 
				pr.isAllowConferences() ) {
			acc.updateLogon( pr.getQTH()+" ("+
				ssa.getConnectCount()+"/"+
				pr.getConferenceCount()+")" );
		} else {
			acc.updateLogon( pr.getQTH() +
				" " +( (ssa.getConnectCount() > 0 ||
					((LabeledToggleAction)getAction("Busy")).isSelected() ) ?
				pr.getBusyStatusText() :
				pr.getFreeStatusText() ) );
		}
	}

	void checkConnections() {
//		new Throwable("Check Connections: "+ssa.getConnectCount() ).printStackTrace();
		if( !ssa.isConnected() ) {
			new ComponentUpdateThread<Void>( (JComponent)null ) {
				public void setup() {
					super.setup();
					conn.setText("");
					infoPane.setText("");
					ipaddr.setText("");
					getAction("Trans").setEnabled(false);
					setTxMode( false );
				}
				public Void construct() {
					try {
						updateLogon();
					} catch( Exception ex ) {
						reportException(ex);
					}
					return null;
				}
			}.start();
		}
	}

	boolean connectTo( final StationData sd, final boolean connectFrom ) {
		if( ssa.isConnectedTo( sd.getIPAddr() ) ) {
			log.info("Already connected to "+sd );
//			new Throwable("Already connected to: "+sd.getIPAddr() ).printStackTrace();
			return false;
		}
		if( sd.getIPAddr() == null ) {
			log.info("No IP Address supplied for: "+sd );
			return false;
		}
		progress( (connectFrom ?
			"Connection From" :"Connecting to: ")+
			sd.getCall()+" "+sd.getLocation() );
		final Object lock = new Object();
		final boolean connected[] = new boolean[1];
		final boolean refused[] = new boolean[1];
		final LinkEventListener<Object> l = new LinkEventListener<Object>() {
			@Override
			public void processEvent( LinkEvent<Object> ev ) {
				log.info("Got event: ("+ev+"), expected addr: "+sd.getIPAddr() );

				// Is this a connect for the requested station?
				if( !ev.isSend() && (ev.typ == LinkEvent.SDES_EVENT
						|| ev.typ == LinkEvent.NETDATA_EVENT
						|| ev.typ == LinkEvent.INFO_EVENT) &&
						 sd.getIPAddr().equals((String)ev.getSource()) ) {
					connected[0] = true;
					synchronized( lock ) {
						lock.notify();
					}
					runInSwing( new Runnable() {
						public void run() {
							progress( (connectFrom ?
									"Connection From" :"Connecting to: ")+
									sd.getCall()+" "+sd.getLocation() );

							conn.setText( "Connected to: "+sd.getCall()+
								" "+sd.getLocation() );
							getAction("Trans").setEnabled(true);
							getAction("Disc").setEnabled(true);
						}
					});
					if(cons != null )
						cons.addConnectedStation(sd);
					removeLinkEventListener( this );
				} else if( ev.typ == LinkEvent.DISC_EVENT ) {
					// Make sure this is the disconnect for this instance.
					if( sd.getIPAddr().equals((String)ev.getSource()) ) {
						connected[0] = false;
						removeLinkEventListener( this );
						refused[0] = true;
						try {
							ssa.disconnectFrom( sd.getIPAddr() );
							sendEvent( new LinkEvent<StationData>(
								this, false, LinkEvent.STATION_DISC_EVENT, 
								ssa.getConnectCount(), sd ) );
						} catch( IOException ex ) {
							log.log(Level.WARNING,ex.toString(), ex);
						}
						runInSwing( new Runnable() {
							public void run() {
								conn.setText( ( connectFrom ?
									"Can't answer Connect From " :
									"Connection refused to ")+
									sd.getCall()+" "+sd.getLocation() );
								conn.repaint();
								getAction("Conn").setEnabled(true);
								getAction("Trans").setEnabled(false);
								getAction("Disc").setEnabled(false);
							}
						});
//						if(cons != null )
//							cons.removeConnectedStation(sd);
						synchronized( lock ) {
							lock.notify();
						}
					}
				}
			}
		};
		final Throwable reason[] = new Throwable[1];
		
		log.info("Starting connect in ComponentUpdateThread");
		new ComponentUpdateThread<Object>((JComponent)null) {
			public void setup() {
				super.setup();
				log.info("Setting up components before connect");
				conn.setText( (connectFrom ?
					"Connection from: " :
					"Connecting to: ")+sd.getCall()+" "+sd.getLocation() );
				last.setText("");
				conn.repaint();
				ipaddr.setText( sd.getIPAddr()+":"+sd.getDataPort()+"/"+sd.getControlPort() );
				getAction("Trans").setEnabled(!txActive);
			}
			public Object construct() {
				try {
//					conn.setText( "Connecting to: "+sd.getCall()+
//						" "+sd.getLocation() );
					log.info("Connecting to "+sd.getCall()+" at "+sd.getIPAddr()+", from: "+connectFrom);
					connectTo( sd.getCall(), sd.getIPAddr(), l, 
						sd.getCall().charAt(0) == '*',
						sd.getDataPort(), 
						sd.getControlPort(),
						connectFrom );
					log.info("connected with resolved IP: "+sd.resolvedIPAddress() );
					return sd.resolvedIPAddress();
				} catch( java.net.UnknownHostException ex ) {
					// If we can't resolve, just use the IP address
					return sd.getIPAddr();
				} catch( Throwable ex ) {
					// If the connect fails, disconnect....
					return ex;
				}
			}
			public void finished() {
				try {
					Object val = getValue();
					log.info("Connection finishing with: "+val);
					if( val instanceof Throwable ) {
						reason[0] = (Throwable)val;
						log.log(Level.WARNING, val.toString(), ((Throwable)val) );
						ssa.disconnectFrom( sd.getIPAddr() );
						sendEvent( new LinkEvent<StationData>(
							this, false, LinkEvent.STATION_DISC_EVENT, 
							ssa.getConnectCount(), sd ) );
						if( val instanceof ConnectionRefusedException == false ) {
							showMessageDialog(
								(Throwable)val, (connectFrom ?
								"Can't Answer Connect From " :
								"Error connecting to ")+
								sd.getCall()+" ("+sd.getIPAddr()+")",
								JOptionPane.ERROR_MESSAGE );
						}
						synchronized( lock ) {
							lock.notify();
						}
						return;
					}
					String str = (String)contacts.get(sd.getCall());
					if( str != null ) {
						statList.setText( str );
					} else {
						statList.setText("");
					}
					statList.repaint();
					qsoHist.addEntry( sd );
					saveFavorites();
//					ipaddr.setText( (String)val );
					ipaddr.setText( val+":"+sd.getDataPort()+"/"+sd.getControlPort() );
					log.info("Checking connections");
					checkConnections();
					log.info("Sending connect event");
					sendEvent( new LinkEvent<StationData>(
						this, !connectFrom, LinkEvent.STATION_CONN_EVENT, 
							ssa.getConnectCount(), sd ) );
				} catch( Exception ex ) {
					reportException(ex);
					synchronized( lock ) {
						lock.notify();
					}
				} finally {
					super.finished();
				}
			}
		}.start();

		synchronized( lock ) {
			try {
				lock.wait(pr.getConnectAttemptTimeout()*1000);
			} catch( Exception ex ) {
				reportException(ex);
			}
		}
		if( !connected[0] && !refused[0] ) {
//			new Throwable( "Connection timeout? ").printStackTrace();
			runInSwing( new Runnable() {
				public void run() {
					if( reason[0] instanceof ConnectionNotPossibleException == false ) {
						conn.setText( "Connection Not Allowed" );
					} else if( reason[0] == null || reason[0] instanceof ConnectionNotPossibleException == false ) {
						conn.setText( "Connection Timed Out" );
				} else {
						conn.setText( "Connection Failed"+(reason[0] == null ? "" : (": "+reason[0])) );
				}
 
//					new Throwable( "Connection did timeout? ").printStackTrace();
				}
			});
			removeLinkEventListener( l );
		} else {
			try {
				audio.connected();
			} catch( Exception ex ) {
				reportException(ex);
			}
		}
		progress("Returning connected[0]="+connected[0]);
		return connected[0];
	}
	
	public void disconnectFrom() throws IOException {
		if( isConnected() == false )
			return;
		List<Connection> v = ssa.getConnectedList();
		final JDialog d = new JDialog( this, "Select Stations to Disconnect", true );
		JPanel p = new JPanel();
		connList = new JList<Connection>(connModel= new ListListModel<Connection>(v));
		Packer pk = new Packer(p);
		pk.pack(new JScrollPane(connList)).fillboth();
		connList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		p.setBorder(BorderFactory.createTitledBorder("Connected Stations") );
		pk = new Packer( d.getContentPane() );
		pk.pack( p ).gridx(0).gridy(0).fillx().gridw(2);
		pk.pack( new JSeparator() ).gridx(0).gridy(1).fillx().gridw(2);
		final JButton
			okay = new JButton("Okay"),
			cancel = new JButton("Cancel");
		final boolean cancelled[] = new boolean[1];
		okay.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				cancelled[0] = false;
				d.setVisible(false);
			}
		});
		cancel.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				cancelled[0] = true;
				d.setVisible(false);
			}
		});
		d.addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent ev ) {
				cancelled[0] = true;
			}
		});
		pk.pack( okay ).gridx(0).gridy(2).west();
		pk.pack( cancel ).gridx(1).gridy(2).east();
		d.pack();
		d.setLocationRelativeTo( this );
		d.setVisible(true);
		if( cancelled[0] )
			return;
		int[]arr = connList.getSelectedIndices();
		for( int i = 0; i < arr.length; ++i ) {
			Connection conn = v.get(arr[i]);
			disconnectFrom( conn.getAddress() );
		}
		checkConnections();
	}
	
	public void showCountry( String country ) {
		//locations.
		Object root = locmod.getRoot();
		Entry ne = null;
		for( int i = 0; i < staList.size(); ++i ) {
			Entry e = staList.get(i);
			String ce = CountryAccess.countryFor( e.getStation().getCall() );
			log.finer("Find country("+ce+") for call: "+e.getStation().getCall() );
			if( ce != null && ce.equals(country) ) {
				ne = e;
				break;
			}
		}
		if( ne != null ) {
			locations.clearSelection();
			if( searchTree( locations, ne, locmod, root, new TreePath(new Object[]{root}) ) ) {
				stTabs.setSelectedIndex(2);
				stTabs.repaint();
			}
		}
	}

	public void disconnectFrom( String addr ) throws IOException {
//		jmf.disconnectFrom( addr );
		
		StationData sd = StationData.stationForAddress( addr );
		if(cons != null && sd != null )
			cons.removeConnectedStation(sd);

//		if( ann != null && pr.isUserMode() == false ) {
//			switch( pr.getOptionsAnnounceDisconnects() ) {
//				case 0: break;
//				case 1:
//					raisePtt();
//					ann.disconnected( pr, sd.getCall() );
//					break;
//				case 2:
//					if( ssa.getConnectCount() == 0 ) {
//						raisePtt();
//						ann.disconnected( pr, sd.getCall() );
//					}
//					break;
//			}
//		}
		ssa.disconnectFrom( addr );
		sendEvent( new LinkEvent<StationData>(
			this, false, LinkEvent.STATION_DISC_EVENT, 
				ssa.getConnectCount(), sd ) );
		checkConnections();
		try {
			audio.disconnect();
		} catch( Exception ex ) {
			reportException(ex);
		}
	}

	public void connectTo( String name, String addr, LinkEventListener<Object> l,
			boolean isConf, int data, int ctrl, boolean connectFrom ) throws Exception {
		StationData dt = StationData.stationFor( pr.getCallSign() );
		if( dt == null ) {
			IllegalArgumentException ex = new IllegalArgumentException(
				"Can't connect from your unconnected station: "+pr.getCallSign() );
			if( connectFrom ) {
				log.log(Level.WARNING,ex.toString(), ex);
			} else {
				throw ex;
			}
		}
		if( name.equals( pr.getCallSign() ) ) {//dt.getIPAddr().equals(addr) )
			throw new IllegalArgumentException(
				"Can't connect to your own station: ("+pr.getCallSign()+"/"+name+")");
		}

		addLinkEventListener( l );
		if( ssa.getConnectCount() == 0 ) {
			addChatText("->"+addr);
		}

		log.log(Level.FINE, "Connecting: "+name,
			new Throwable( "Connect: " + name ) );
		if( ann != null && pr.isUserMode() == false ) {
			switch( pr.getOptionsAnnounceContacts() ) {
				case 0: break;
				case 1:
					raisePtt( pr.getPttControlParms() );
					ann.connected( pr, name );
					break;
				case 2:
					if( ssa.getConnectCount() <= 1 ) {
						raisePtt( pr.getPttControlParms() );
						ann.connected( pr, name );
					}
					break;
			}
		}
		String info = getCurrentInfo();
		ssa.connectTo( name, addr, info, isConf, data, ctrl, connectFrom );
		updateLogon( isConf, name );
	}
	
	public String getCurrentInfo() {
		String info = pr.getInfoText();
		if( ssa == null )
			return "not connected";
		if( ssa.getConnectCount() > 1 ) {
			info = "Station "+pr.getCallSign() + "\r\r";
			List<Connection> v = ssa.getConnectedList();
			for( int i = 0; i < v.size(); ++i ) {
				Connection c = v.get(i);
				info += c.isTrans() ? "> " : "";
				info += c.getName()+"\r";
			}
			info += "\r\r"+
				pr.getUserName()+"\r"+
				pr.getQTH();
		} else {
			info = "Station "+pr.getCallSign() + "\r\r" +
				pr.getUserName()+"\r"+
				pr.getQTH()+"\r\r"+
				info;
		}
		return info;
	}

	static class NamedList<T> extends ArrayList<T> {
		private static final long serialVersionUID = 1L;
		String name;
		public NamedList( String name ) {
			this.name = name;
		}
		public String toString() {
			return name;
		}
		public int hashCode() {
			return name.hashCode();
		}
	}
	
	static class AlarmManager {
		private boolean almOnce = false;
		private boolean firstAlm = true;
		Logger log = Logger.getLogger(Javecho.class.getName());
		Javecho je;
		
		public AlarmManager( Javecho j ) {
			je = j;
		}
	
		AlarmEditor almEd;
		void showAlarmEditor() {
			almEd.showFrame();
		}
		
		AlarmLog almLog;
		void showAlarmLog() {
			almLog.showFrame();
		}

		public void checkAlarms( StationData dt ) {
			if( almOnce || firstAlm )
				return;
			log.finer("Check for alarm of "+dt );
			log.finest("alarms for: "+almEd.getHistory() );
			if( almEd.getHistory().contains( dt.getCall() ) == false ) {
				log.finer("No alarm entry for: "+dt.getCall() );
				return;
			}
			almOnce = true;
			log.finer("Adding status for: "+dt );
			almLog.addEntry( dt );
			try {
				log.fine("Sounding Alarm for: "+dt);
				je.audio.alarm();
				if( almLog.isVisible() == false ) {
					almLog.setVisible(true);
				}
			} catch( Exception ex ) {
				log.log( Level.WARNING, ex.toString(), ex );
			}
		}
	}
	
	AlarmManager alarmMgr = new AlarmManager(Javecho.this);

	private class MyModel implements TreeModel {
		volatile List<Entry> data;
		volatile String root;
		NamedList<NamedList> nodes = new NamedList<NamedList>("Loading...");
		List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
		String name;
		volatile boolean dbg = true;
		NamedList<Entry> stations;
		NamedList<Entry> links;
		NamedList<Entry> repeat;
		NamedList<Entry> conf;
//		NamedList<Entry> msg;

		public Entry findStation( String call ) {
			for( int i = 0; i < data.size(); ++i ) {
				Entry e = data.get(i);
				if( e.getStation().getCall().equals(call) )
					return e;
			}
			return null;
		}

		public List<Entry> getContents() {
			return data;
		}

		public String toString() {
			return name;
		}

		public MyModel( List<Entry> v, String name, boolean debug ) {
			this.name = name;
			root = name;
			dbg = debug;
			setData( v );
		}

		public void setData( List<Entry> v ) {
			data = new ArrayList<Entry>();
			fillData(v);
			updateAll();
		}

		public int getChildCount( Object node ) {
			if(dbg) progress( this+": getChildCount("+node+")" );
			if( node == root ) {
				if(dbg) progress(this+":"+nodes+": size="+nodes.size() );
				return nodes.size();
			}
			if( node instanceof NamedList == false )
				return 0;
			NamedList v = (NamedList)node;
			if(dbg) progress(this+": "+v+": size="+v.size() );
			return v.size();
		}

		public boolean isLeaf( Object node ) {
			if(dbg) progress(this+": ["+node+
				"] is leaf? "+(node instanceof Entry) );
			return node instanceof Entry;
		}

		public int getIndexOfChild( Object parent, Object node ) {
			if(dbg) progress( this+": getIndexOfChild("+parent+","+node+")" );
			int idx = nodes.indexOf(parent);
			if( parent == root ) {
				if(dbg) progress( parent+": index of "+node+" = "+idx );
				return idx;
			}
			if( idx == -1 ) {
				if(dbg) progress( parent+" OOOOPPPPSSSS at "+node+
					", no child index" );
				return -1;
			}
			NamedList v = nodes.get(idx);
			if(dbg) progress( parent+
				": index of "+node+" = "+v.indexOf( node ) );
			return v.indexOf( node );
		}

		public void addTreeModelListener( TreeModelListener lis ) {
			if(dbg) progress( this+": addTreeModelListener("+lis+")" );
			listeners.add( lis );
			TreeModelEvent ev = new TreeModelEvent( this, new Object[]{ root } );
				lis.treeStructureChanged( ev );
				lis.treeNodesChanged( ev );
		}

		public void removeTreeModelListener( TreeModelListener lis ) {
			if(dbg) progress( this+": removeTreeModelListener("+lis+")" );
			listeners.remove( lis );
		}

		public void valueForPathChanged( TreePath path, Object value ) {
			if(dbg) progress( this+
				": valueForPathChanged("+path+","+value+")" );
		}

		public synchronized Object getChild( Object parent, int idx ) {
			if(dbg) progress( this+": getChild("+parent+","+idx+")" );
			if( parent == root ) {
				if(dbg) progress(parent+": child at "+idx+" = "+
					nodes.get(idx) );
				return nodes.get(idx);
			}
			NamedList v = (NamedList)parent;
			if(dbg) progress(parent+": child at "+
				idx+" = "+v.get(idx) );
			return v.get(idx);
		}

		public synchronized Object getRoot() {
			return root;
		}

		void fillData(List<Entry> v) {
			if( stations == null )
				stations = new NamedList<Entry>("Stations");
			else
				stations.clear();
			if( links == null )
				links = new NamedList<Entry>("Links");
			else
				links.clear();
			if( repeat == null )
				repeat = new NamedList<Entry>("Repeaters");
			else
				repeat.clear();
			if( conf == null )
				conf = new NamedList<Entry>("Conferences");
			else
				conf.clear();
			nodes = new NamedList<NamedList>("nodes");
			if( name.equals("Favorites") == true || pr.isRepeatersInStationList() )
				nodes.add( repeat );
			if( name.equals("Favorites") == true || pr.isLinksInStationList() )
				nodes.add( links );
			if( name.equals("Favorites") == true || pr.isUsersInStationList() )
				nodes.add( stations );
			if( name.equals("Favorites") == true || pr.isConferencesInStationList() )
				nodes.add( conf );
			addData(v);
			Comparator<Entry> e = new EntryComparator();
			if( name.equals("Favorites") == true || pr.isRepeatersInStationList() )
				Collections.sort( repeat, e );
			if( name.equals("Favorites") == true || pr.isLinksInStationList() )
				Collections.sort( links, e );
			if( name.equals("Favorites") == true || pr.isUsersInStationList() )
				Collections.sort( stations, e );
			if( name.equals("Favorites") == true || pr.isConferencesInStationList() )
				Collections.sort( conf, e );
		}

		private void addData( List<Entry> v ) {
			alarmMgr.almOnce = false;
			for( int i = 0; i < v.size(); ++i ) {
				Entry e = v.get(i);
				addData( e, false );
			}
			alarmMgr.firstAlm = false;
		}

		void updateAll() {
			updatePath( new TreePath( new Object[]{ root, stations } ) );
			updatePath( new TreePath( new Object[]{ root, links } ) );
			updatePath( new TreePath( new Object[]{ root, repeat } ) );
			updatePath( new TreePath( new Object[]{ root, conf } ) );
		}

		void updatePath(TreePath pth) {
			TreeModelEvent ev = new TreeModelEvent( this, pth );
			for( int i = 0; i < listeners.size(); ++i ) {
				TreeModelListener lis = (TreeModelListener)listeners.get(i);
				lis.treeStructureChanged( ev );
				lis.treeNodesChanged( ev );
			}
		}

		void deletedPath(TreePath pth) {
			TreeModelEvent ev = new TreeModelEvent( this, pth );
			for( int i = 0; i < listeners.size(); ++i ) {
				TreeModelListener lis = (TreeModelListener)listeners.get(i);
				lis.treeNodesRemoved( ev );
			}
		}

		public void addData( Entry e ) {
			addData( e, true );
		}

		private void addData( Entry e, boolean update ) {
			if( data.contains(e) == true ) {
//				if ( pr.isBeeping() ) {
//					Toolkit.getDefaultToolkit().beep();
//				} else 
				if( e.getStation().getCall().trim().length() > 0 ) {
					progress( e.getStation().getCall()+" already in list "+
						"Station Not Added"  );
				}
				return;
			}
			if( e.getType() != Entry.TYPE_MSG ) {
				if( e.getStation().disconnected() )
					alarmMgr.checkAlarms(e.getStation());
				else if( e.getStation().connected() )
					alarmMgr.checkAlarms(e.getStation());
				else if( e.getStation().wentIdle() )
					alarmMgr.checkAlarms(e.getStation());
				else if( e.getStation().wentBusy() )
					alarmMgr.checkAlarms(e.getStation());
			}
			boolean add = false;
			if( pr.isConferencesInStationList() && e.getType() == Entry.TYPE_CONF ) {
				add = true;
			} else if( pr.isUsersInStationList() && e.getType() == Entry.TYPE_STATION ) {
				add = true;
			} else if( pr.isLinksInStationList() && e.getType() == Entry.TYPE_LINK ) {
				add = true;
			} else if( pr.isRepeatersInStationList() && e.getType() == Entry.TYPE_REPEATER ) {
				add = true;
			} else if( e.getType() == Entry.TYPE_MSG ) {
				add = true;
			}
			if( name.equals("Favorites") == false && !add )
				return;
			data.add(e);
			if(update) Collections.sort(data,new EntryComparator());
			NamedList<Entry> w = null;
			if( e.getType() == Entry.TYPE_LINK )
				w = links;
			else if( e.getType() == Entry.TYPE_REPEATER )
				w = repeat;
			else if( e.getType() == Entry.TYPE_STATION )
				w = stations;
			else if( e.getType() == Entry.TYPE_CONF )
				w = conf;
			else if( e.getType() == Entry.TYPE_MSG ){
				sysmsg.add(e);
				log.fine("add server error message: "+e );
				return;//w = msg;
			}
			if( w == null )
				throw new NullPointerException( e.getType()+": Entry type unknown" );
			w.add(e);
			if( update ) {
				Collections.sort(w,new EntryComparator());
				updatePath( new TreePath( new Object[]{ root, w } ) );
			}
		}

		public void removeData( Entry e ) {
			removeData( e, true );
		}

		private void removeData( Entry e, boolean update ) {
			NamedList<?> w = null;
			if( e.getType() == Entry.TYPE_LINK )
				w = links;
			else if( e.getType() == Entry.TYPE_REPEATER )
				w = repeat;
			else if( e.getType() == Entry.TYPE_STATION )
				w = stations;
			else if( e.getType() == Entry.TYPE_CONF )
				w = conf;
			else if( e.getType() == Entry.TYPE_MSG )
				return;//w = msg;
			if( w == null )
				throw new NullPointerException( e.getType()+": Entry type unknown" );
			w.remove(e);
			if( update )
				deletedPath( new TreePath( new Object[]{ root, w, e } ) );
		}
	}

	private class NodeList {
		ArrayList<Object> list;
		String name;
		public NodeList( String name, ArrayList<Object> l ) {
			this.name = name;
			this.list = l;
		}
		public int size() {
			return list.size();
		}
		public Object get(int idx) {
			return list.get(idx);
		}
		public int indexOf( Object obj ) {
			return list.indexOf(obj);
		}
		public int count() {
			int cnt = 0;
			for(int i = 0; i < list.size(); ++i ) {
				Object o = list.get(i);
				if( o instanceof NodeList )
					cnt += ((NodeList)o).count();
				else
					++cnt;
			}
			return cnt;
		}
		public String toString() {
			return name +" ("+count()+")";
		}
	}

	private class MyLocModel implements TreeModel {
		List<Entry> data;
		NodeList root;
		List nodes = new ArrayList();
		List<TreeModelListener> listeners = 
			new ArrayList<TreeModelListener>();
		String name;
		volatile boolean dbg;
//		ArrayList continents;
//		Hashtable countries;
		
		public Entry findStation( String call ) {
			List<Entry>ls;
			synchronized(data) {
				ls = new ArrayList<Entry>(data);
			}
			for( Entry e : ls ) {
				if( e.getStation().getCall().equals(call) )
					return e;
			}
			return null;
		}

		public List getContents() {
			return data;
		}

		public String toString() {
			return name;
		}

		public MyLocModel( List<Entry> v, boolean debug ) {
			root = new NodeList( "Stations", new ArrayList<Object>());
			dbg = debug;
			setData( v );
		}

		public synchronized void setData( List<Entry> v ) {
			data = new ArrayList<Entry>();
			fillData(v);
			updateAll();
		}

		public int getChildCount( Object node ) {
			return ((NodeList)node).size();
		}

		public boolean isLeaf( Object node ) {
			return node instanceof NodeList == false;
		}

		public int getIndexOfChild( Object parent, Object node ) {
			return ((NodeList)parent).indexOf( node );
		}

		@Override
		public void addTreeModelListener( TreeModelListener lis ) {
			if(dbg) progress( this+": addTreeModelListener("+lis+")" );
			runInSwing( ()->{
				listeners.add( lis );
				TreeModelEvent ev = new TreeModelEvent( Javecho.this, new Object[]{ root } );
				lis.treeStructureChanged( ev );
				lis.treeNodesChanged( ev );
			});
		}

		public void removeTreeModelListener( TreeModelListener lis ) {
			if(dbg) progress( this+": removeTreeModelListener("+lis+")" );
			listeners.remove( lis );
		}

		public void valueForPathChanged( TreePath path, Object value ) {
			if(dbg) progress( this+
				": valueForPathChanged("+path+","+value+")" );
			
		}

		public Object getChild( Object parent, int idx ) {
			return ((NodeList)parent).get(idx);
		}

		public Object getRoot() {
			return root;
		}

		void fillData(List<Entry> v) {
			Map<String,String> cn = 
				new HashMap<String,String>();
			cn.put("AF","Africa");
			cn.put("AN","Antartica");
			cn.put("AS","Asia");
//			cn.put("AS/AF","Asia/Africa");
			cn.put("EU","Europe");
//			cn.put("EU/AS", "Euro Asia");
			cn.put("NA", "North America");
			cn.put("OC", "Oceania");
			cn.put("SA", "South America");
			ArrayList<String> l = Collections.list( CountryAccess.continents() );
			Collections.sort(l);
			root.list = new ArrayList<Object>();

			Map<String,NodeList> cs = new HashMap<String,NodeList>();
			Map<String,NodeList> ct = new HashMap<String,NodeList>();
			List<NodeList> rlv = new ArrayList<NodeList>();

			for( String n : l ) {
				NodeList nl;
				cs.put( n, nl = new NodeList(
					(String)cn.get(n), new ArrayList<Object>() ) );
				ct.put( n, nl );
				root.list.add( nl );
			}

			for( Entry e : v ){
				StationData sd = e.getStation();
				String call = sd.getCall();
				if( e.type == Entry.TYPE_CONF || e.type == Entry.TYPE_MSG )
					continue;
				boolean add = false;
				String type = "unknown";
				if( pr.isConferencesInStationList() && e.getType() == Entry.TYPE_CONF ) {
					add = true;
					type = "Conf";
				} else if( pr.isUsersInStationList() && e.getType() == Entry.TYPE_STATION ) {
					add = true;
					type = "Station";
				} else if( pr.isLinksInStationList() && e.getType() == Entry.TYPE_LINK ) {
					add = true;
					type = "Link";
				} else if( pr.isRepeatersInStationList() && e.getType() == Entry.TYPE_REPEATER ) {
					add = true;
					type = "Repeater";
				}
				if( !add )
					continue;
				CountryAccess.CountryEntry ce = CountryAccess.entryForCall(sd.getCall());
				if( ce == null ) {
					progress( "No country for ("+type+"): \""+sd.getCall()+"\"");
					continue;
				}
				if( ce.zone == null ) {
					progress("no zone for: "+ce.name );
					ce = CountryAccess.entryForCall("W5GGW");
					//continue;
				}
				NodeList al = (NodeList)cs.get(ce.zone.continent);
				NodeList cl;
				if( (cl = (NodeList)ct.get( ce.name ) ) == null ) {
					cl = new NodeList( ce.name, new ArrayList<Object>() );
					ct.put( ce.name, cl );
					al.list.add(cl);
				}
				if( ce.name.equals("United States") ) {
					String str = sd.getCall();
					int r = -1;
					for( int j = 0; j < str.length(); ++j) {
						if( "0123456789".indexOf(str.charAt(j)) >= 0 ) {
							r = str.charAt(j) - '0';
							break;
						}
					}
					NodeList rl = null;
					if(r >= 0 && r < rlv.size()) {
						log.log(Level.FINE,"rlv has {0} items, getting {1}", new Object[]{rlv.size(), r} );
						rlv.get(r);
					}
					if( rl == null ) {
						rl = new NodeList( "Region "+r,
							new ArrayList<Object>() );
						cl.list.add( rl );
						ct.put( ""+r, rl );
						if( r < rlv.size() )
							rlv.set( r, rl );
						else {
							while( r >= rlv.size() ) {
								rlv.add(null);
							}
							rlv.set( r, rl );
						}
					}
					cl = rl;
				}
				cl.list.add( e );
			}
			for( NodeList nl : ct.values() ) {
				log.finer("sorting: "+nl.name+"..." );
				Collections.sort( nl.list, new Comparator<Object>() {
					public int compare( Object o1, Object o2 ) {
						if( o1 instanceof Entry ) {
							if( o2 instanceof Entry ) {
								Entry e1 = (Entry)o1;
								Entry e2 = (Entry)o2;
								if( e1.type != e2.type )
									return e2.type - e1.type;
					
								return e1.getStation().getCall().compareTo( e2.getStation().getCall() );
							} else {
								return o1.toString().compareTo( o2.toString() );
//								throw new ClassCastException( o1.getClass().getName()+" <> "+o2.getClass().getName() );
							}
//						} else if( o2 instanceof Entry ) {
//							throw new ClassCastException( o1.getClass().getName()+" <> "+o2.getClass().getName() );
						} else {
							return o1.toString().compareTo( o2.toString() );
						}
					}
				});
				log.finer("done");
			}
			Iterator<Object> ii = root.list.iterator();
			ArrayList<NodeList> rmv = new ArrayList<NodeList>();
			while( ii.hasNext() ) {
				NodeList ln = (NodeList)ii.next();
				if( ln.list.size() == 0 )
					rmv.add(ln);
			}
			Iterator<NodeList> nii = rmv.iterator();
			while( nii.hasNext() ) {
				root.list.remove(nii.next());
			}
			log.finer("Sorting completed....");
		}

		private void addData( List<Entry> v ) {
			for( int i = 0; i < v.size(); ++i ) {
				Entry e = v.get(i);
				addData( e, false );
			}
		}

		void updateAll() {
			updatePath( new TreePath( new Object[]{ root } ) );
		}

		void updatePath( final TreePath pth) {
			runInSwing( new Runnable() {
				public void run() {
					TreeModelEvent ev = new TreeModelEvent( this, pth );
					for( int i = 0; i < listeners.size(); ++i ) {
						TreeModelListener lis = listeners.get(i);
						lis.treeStructureChanged( ev );
						lis.treeNodesChanged( ev );
					}
				}
			});
		}

		void deletedPath(TreePath pth) {
			TreeModelEvent ev = new TreeModelEvent( this, pth );
			for( int i = 0; i < listeners.size(); ++i ) {
				TreeModelListener lis = listeners.get(i);
				lis.treeNodesRemoved( ev );
			}
		}

		public void addData( Entry e ) {
			addData( e, true );
		}

		private void addData( Entry e, boolean update ) {
		}

		public void removeData( Entry e ) {
			removeData( e, true );
		}

		private void removeData( Entry e, boolean update ) {
		}
	}
	
	private class MyCellRenderer implements TreeCellRenderer {
		TreeCellRenderer rend;
		Icon icons[];
		public MyCellRenderer( TreeCellRenderer rend ) {
			this.rend = rend;
			icons = new Icon[6];
			icons[Entry.TYPE_LINK] = loadIcon( "link.gif" );
			icons[Entry.TYPE_REPEATER] = loadIcon( "repeater.gif" );
			icons[Entry.TYPE_STATION] = loadIcon( "person.gif" );
			icons[Entry.TYPE_CONF] = loadIcon( "confer.gif" );
			icons[Entry.TYPE_MSG] = loadIcon( "person.gif" );
		}
		public Component getTreeCellRendererComponent(JTree table, Object value,
				boolean isSelected, boolean expanded, boolean isLeaf,
					int row, boolean hasFocus ) {
			JLabel l = (JLabel)rend.getTreeCellRendererComponent( table, value, 
				isSelected, expanded, isLeaf, row, hasFocus );
			log.finer("value: "+value+", Entry? "+(value instanceof Entry) );
			if( !isSelected )
			l.setOpaque(false);
			if( value instanceof Entry == false ) {
//				l.setText( value.toString() );
//				l.setForeground( Color.blue );
//				l.setBackground( Color.yellow );
//				l.setOpaque(true);
				return l;
			}
			Entry ent = (Entry)value;
			String str = ent.getStation().getCall();
			if( ent.isConnected() ) {
				str += " - "+
					ent.getStation().getLocation()+" ["+ent.getStation().getID()+"]";
				if( ent.getStation().isBusy() )
					str += " Busy";
			} else {
				str += " disconnected";
			}
			log.finest(str+": type: "+ent.getType() );
			l.setText( str );
//			l.setForeground( isSelected ?
//				(ent.isConnected() ? Color.white : Color.red ) :
//				(ent.isConnected() ? Color.black : Color.gray ) );
			if( isSelected ) {
				l.setOpaque(true);
				if( ent.getStation().isBusy() ) {
					l.setForeground( Color.black );
					l.setBackground( Color.red );
				} else if( ent.isConnected() ) {
					l.setForeground( Color.white );
					l.setBackground( Color.blue );
				} else {
					l.setForeground( Color.gray );
					l.setBackground( Color.red );
				}
			} else {
				if( ent.getStation().isBusy() ) {
					l.setForeground( Color.red );
				} else if( ent.isConnected() ) {
					l.setForeground( Color.black );
				} else {
					l.setForeground( Color.gray );
				}
			}
			l.setIcon( icons[ent.getType()] );
			l.revalidate();
			l.setSize( l.getPreferredSize() );
			l.validate();
			table.validate();
			table.revalidate();
			table.setSize( table.getSize() );
//			Dimension sz = l.getSize();
//			int w = sz.width;
//			int h = sz.height;
//			Font f = l.getFont();
//			FontMetrics fm = getGraphics().getFontMetrics(f);
//			l.setSize( h, fm.stringWidth( l.getText() ) + 10 );
//			if( column == 0 ) {
//				Entry ent = (Entry)value;
//				l.setText( ent.getStation().getCall() );
//				l.setIcon( icons[ent.getType()] );
//			} else {
//				l.setIcon( null );
//			}
			return l;
		}
	}
}