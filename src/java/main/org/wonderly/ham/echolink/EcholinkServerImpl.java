package org.wonderly.ham.echolink;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.logging.*;
import org.wonderly.awt.*;
import org.wonderly.log.*;
import java.util.Timer;
import org.wonderly.io.*;

import org.wonderly.jini2.*;
import net.jini.config.*;
import net.jini.id.*;

/**
 *  This class is a primative, testing echolink server.  It provides
 *  The ability to create a test session between several stations and
 *  evaluate operations.  As an asside. it can also be used for a private
 *  server.  With some more code, it could provide some administration
 *  capabilities to allow new users to be added and existing users to
 *  be removed.
 */
public class EcholinkServerImpl extends PersistentJiniService implements EcholinkServer {
	ServerSocket sock;
	int port = 5200;
	boolean done;
	JButton add, remove, edit, approve, reject, apprrej, rej, pendrej, rmvrej;
	JList lst, plst,rlst;
	JFrame frm;
	Timer watcher;
	static final long CONN_TIMEOUT = 400000;
	int nextId = 1;
	Vector<StationInfo> statlist = new Vector<StationInfo>();
	Vector<StationInfo> pendlist = new Vector<StationInfo>();
	Vector<StationInfo> rejlist = new Vector<StationInfo>();
	Hashtable<String,StationInfo> stations = new Hashtable<String,StationInfo>();
	Hashtable<String,StationInfo> connected = new Hashtable<String,StationInfo>();
	Hashtable<InetAddress,StationInfo> byaddr = new Hashtable<InetAddress,StationInfo>();
	Hashtable<InetAddress,String[]> reasons = new Hashtable<InetAddress,String[]>();
	Logger log = Logger.getLogger( "org.wonderly.ham.echolink.server");

	public static void main( String args[] ) throws Exception {
		new EcholinkServerImpl( args );
	}

	void bldFrame() {
		frm = new JFrame("EchoLink Server");
		Packer pk = new Packer( frm.getContentPane() );
		lst = new JList(statlist);
		plst = new JList(pendlist);

		JPanel lp = new JPanel();
		lp.setBorder( BorderFactory.createTitledBorder( "Known Users" ) );

		JPanel pp = new JPanel();
		pp.setBorder( BorderFactory.createTitledBorder( "Pending Users") );

		Packer lpk = new Packer( lp );
		Packer pnpk = new Packer( pp );
		
		lpk.pack(new JScrollPane(lst)).gridx(0).gridy(0).fillboth();
		pnpk.pack(new JScrollPane(plst)).gridx(0).gridy(0).fillboth();
		JSplitPane jsp = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		jsp.setBottomComponent( pp );
		jsp.setTopComponent( lp );
		JPanel rbp = new JPanel();
		Packer rbpk = new Packer(rbp);
		rbpk.pack( apprrej=new JButton("Approve") ).gridx(0).gridy(0).fillx().weightx(0);
		rbpk.pack( rmvrej=new JButton("Remove") ).gridx(0).gridy(1).fillx().weightx(0);
		rbpk.pack( pendrej=new JButton("Set Pending") ).gridx(0).gridy(2).fillx().weightx(0);
		JPanel ap = new JPanel();
		Packer apk = new Packer(ap);
		apk.pack( approve=new JButton("Approve") ).gridx(0).gridy(0).fillx().weightx(0);
		apk.pack( reject=new JButton("Reject") ).gridx(0).gridy(1).fillx().weightx(0);
		approve.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int idxs[] = plst.getSelectedIndices();
				Vector<StationInfo>v = new Vector<StationInfo>();
				for( int i = 0; idxs != null && i < idxs.length; ++i ) {
					StationInfo si = pendlist.elementAt(idxs[i]);
					statlist.addElement( si );
					stations.put( si.user, si );
					saveStations();
					si.setPending(false);
					v.addElement(si);
				}
				for( int i = 0; i < v.size(); ++i ) {
					pendlist.removeElement( v.elementAt(i) );
				}
				plst.setListData( pendlist );
				lst.setListData( statlist );
				saveStations();
			}
		});
		apprrej.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int idxs[] = rlst.getSelectedIndices();
				Vector<StationInfo>v = new Vector<StationInfo>();
				for( int i = 0; idxs != null && i < idxs.length; ++i ) {
					StationInfo si = rejlist.elementAt(idxs[i]);
					statlist.addElement( si );
					si.setPending(false);
					v.addElement(si);
				}
				for( int i = 0; i < v.size(); ++i ) {
					rejlist.removeElement( v.elementAt(i) );
				}
				rlst.setListData( rejlist );
				lst.setListData( statlist );
				saveStations();
			}
		});
		pendrej.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int idxs[] = rlst.getSelectedIndices();
				Vector<StationInfo>v = new Vector<StationInfo>();
				for( int i = 0; idxs != null && i < idxs.length; ++i ) {
					StationInfo si = rejlist.elementAt(idxs[i]);
					pendlist.addElement( si );
					si.setPending(true);
					v.addElement(si);
				}
				for( int i = 0; i < v.size(); ++i ) {
					rejlist.removeElement( v.elementAt(i) );
				}
				rlst.setListData( rejlist );
				plst.setListData( pendlist );
				saveStations();
			}
		});
		rmvrej.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int idxs[] = rlst.getSelectedIndices();
				Vector<StationInfo>v = new Vector<StationInfo>();
				if( idxs == null || idxs.length == 0 )
					return;
				if( JOptionPane.showConfirmDialog( frm, 
					"Really Delete "+(
						idxs.length > 1 ? 
							(idxs.length+" stations") :
							rejlist.elementAt(idxs[0] ))+"?",
					"Delete Confirmation",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE ) == JOptionPane.CANCEL_OPTION ) {
					return;
				}
				for( int i = idxs.length-1; idxs != null && i >= 0; --i ) {
					StationInfo si = rejlist.elementAt(idxs[i]);
					deleteStation( si );
					rejlist.removeElementAt(idxs[i]);
				}
				rlst.setListData( rejlist );
				saveStations();
			}
		});
		reject.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int idxs[] = plst.getSelectedIndices();
				Vector<StationInfo>v = new Vector<StationInfo>();
				for( int i = 0; idxs != null && i < idxs.length; ++i ) {
					StationInfo si = pendlist.elementAt(idxs[i]);
					rejlist.addElement( si );
					si.setPending(true);
					v.addElement(si);
				}
				for( int i = 0; i < v.size(); ++i ) {
					pendlist.removeElement( v.elementAt(i) );
				}
				plst.setListData( pendlist );
				rlst.setListData( rejlist );
				saveStations();
			}
		});
		pendrej.setEnabled(false);
		reject.setEnabled(false);
		approve.setEnabled(false);
		rmvrej.setEnabled(false);
		plst.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent ev ) {
				approve.setEnabled( plst.getSelectedValue() != null );
				reject.setEnabled( plst.getSelectedValue() != null );
			}
		});
		rlst = new JList();
		rlst.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent ev ) {
				apprrej.setEnabled( rlst.getSelectedValue() != null );
				rmvrej.setEnabled( rlst.getSelectedValue() != null );
				pendrej.setEnabled( rlst.getSelectedValue() != null );
			}
		});
		apprrej.setEnabled(false);
		JPanel rp = new JPanel();
		Packer rpk = new Packer( rp );
		rp.setBorder( BorderFactory.createTitledBorder( "Rejected Users" ) );
		rpk.pack( new JScrollPane( rlst ) ).fillboth();
		rpk.pack( rbp ).gridx(1).gridy(0).fillboth().weightx(0).inset(3,3,3,3);
		JSplitPane rsp = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		rsp.setTopComponent( jsp );
		rsp.setBottomComponent( rp );
		pnpk.pack( ap ).gridx(1).gridy(0).fillx().weightx(0).inset(3,3,3,3);
		pk.pack( rsp ).gridx(0).gridy(0).fillboth();
//		pk.pack( lp ).gridx(1).gridy(0).fillboth();
		JPanel p = new JPanel();
		Packer ppk = new Packer( p );
		int y = -1;
		
		ppk.pack( add = new JButton("Add") ).gridx(0).gridy(++y).fillx().weightx(0);
		ppk.pack( remove = new JButton("Remove") ).gridx(0).gridy(++y).fillx().weightx(0);
		ppk.pack( edit = new JButton("Edit") ).gridx(0).gridy(++y).fillx().weightx(0);
		ppk.pack( rej = new JButton("Reject") ).gridx(0).gridy(++y).fillx().weightx(0);
		rej.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int idxs[] = lst.getSelectedIndices();
				Vector<StationInfo>v = new Vector<StationInfo>();
				for( int i = 0; idxs != null && i < idxs.length; ++i ) {
					StationInfo si = statlist.elementAt(idxs[i]);
					rejlist.addElement( si );
					si.setPending(true);
					v.addElement(si);
				}
				for( int i = 0; i < v.size(); ++i ) {
					statlist.removeElement( v.elementAt(i) );
				}
				rlst.setListData( rejlist );
				lst.setListData( statlist );
				saveStations();
			}
		});
		rej.setEnabled(false);
		add.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				StationInfo ent = new StationInfo( "", "", nextId(), 0 );
				EditDialog dlg = new EditDialog( frm, ent, false );
				if( dlg.isCancelled() == false ) {
					addStation( ent );
					saveStations();
				}
			}
		});
		remove.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				StationInfo ent = (StationInfo)lst.getSelectedValue();
				int idx = JOptionPane.showConfirmDialog( frm,
					"Really Delete \""+ent.user+"\"?",
					"Delete Confirmation",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE );
				if( idx == JOptionPane.OK_OPTION ) {
					statlist.removeElement(ent);
					deleteStation(ent);
					lst.setListData(statlist);
					saveStations();
				}
			}
		});
		edit.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				StationInfo ent = (StationInfo)lst.getSelectedValue();
				EditDialog dlg = new EditDialog( frm, ent, true );
				saveStations();
			}
		});
		add.setEnabled(true);
		remove.setEnabled(false);
		edit.setEnabled(false);
		lst.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent ev ) {
				rej.setEnabled( lst.getSelectedIndex() != -1 );
				remove.setEnabled( lst.getSelectedIndex() != -1 );
				edit.setEnabled( lst.getSelectedIndex() != -1 );
			}
		});
		lpk.pack( p ).gridx(1).gridy(0).fillboth().weightx(0).inset(3,3,3,3);
		frm.pack();
		frm.addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent ev ) {
				saveStations();
				System.exit(1);
			}
		});
		frm.setLocationRelativeTo(null);
		frm.setVisible(true);
	}
	
	private int nextId() {
		return nextId++;
	}

	class EditDialog extends JDialog {
		JTextField name, email, loc,id;
		JPasswordField pass;
		boolean cancelled;
		public boolean isCancelled() {
			return cancelled;
		}
		public EditDialog( JFrame par, StationInfo who, boolean edit ) {
			super( par, "Edit "+who, true );
			Packer pk = new Packer( getContentPane() );
			int y = -1;
			pk.pack( new JLabel( "Name:" ) ).gridx(0).gridy(++y);
			pk.pack( name = new JTextField(who.user) ).gridx(1).gridy(y).fillx();
			if( edit )
				name.setEditable(false);
			pk.pack( new JLabel( "Password:" ) ).gridx(0).gridy(++y);
			pk.pack( pass = new JPasswordField(who.passwd) ).gridx(1).gridy(y).fillx();
			pk.pack( new JLabel( "Email:" ) ).gridx(0).gridy(++y);
			pk.pack( email = new JTextField(who.info) ).gridx(1).gridy(y).fillx();
			pk.pack( new JLabel( "Location:" ) ).gridx(0).gridy(++y);
			pk.pack( loc = new JTextField(who.loc) ).gridx(1).gridy(y).fillx();
			pk.pack( new JLabel( "Id:" ) ).gridx(0).gridy(++y);
			pk.pack( id = new JTextField(who.id+"") ).gridx(1).gridy(y).fillx();
			id.setEditable(false);
			pk.pack( new JSeparator() ).gridx(0).gridy(++y).gridw(2).fillx().inset(4,4,4,4);
			JButton okay = new JButton("Okay");
			JButton cancel = new JButton("Cancel");
			okay.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent ev ) {
					cancelled = false;
					setVisible(false);
				}
			});
			cancel.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent ev ) {
					cancelled = true;
					setVisible(false);
				}
			});
			pk.pack( okay ).gridx(0).gridy(++y).west();
			pk.pack( cancel ).gridx(1).gridy(y).east();
			pack();
			setLocationRelativeTo( par );
			setVisible(true);
			if( cancelled == false ) {
				if( edit == false ) {
					who.user = name.getText();
				}
				who.passwd = pass.getText();
				who.info = email.getText();
				who.loc = loc.getText();
			}
		}
	}

	void checkTimeouts() {
		for( int i = 0; i < statlist.size(); ++i ) {
			StationInfo inf = statlist.elementAt(i);
			long now = System.currentTimeMillis();
			if( now - inf.lastTime > CONN_TIMEOUT ) {
				try {
					discoStation(inf);
				} catch( Throwable ex ) {
					ex.printStackTrace();
				}
			}
		}
	}

	public EcholinkServerImpl( String args[] ) throws ConfigurationException, IOException {
		super(args);
		Handler h = new ConsoleHandler();
		h.setFormatter( new StreamFormatter() );
		log.addHandler(h);
		log.setUseParentHandlers(false);
		bldFrame();
		watcher = new Timer();
		watcher.schedule( new TimerTask() {
			public void run() {
				try {
					checkTimeouts();
				} catch( Throwable ex ) {
					ex.printStackTrace();
				}
			}
		}, 20000, 20000 );
		sock = new ServerSocket(port);
		loadStations();

		startService();

		while( !done ) {
			log.info("Accepting on: "+sock );
			final Socket s = sock.accept();
			log.info("Got connection: "+s );
			new Thread() {
				public void run() {
					try {
						processSession(s);
					} catch( SocketException ex ) {
						log.info(s+": closing: "+ex );
					} catch( Exception ex ) {
						ex.printStackTrace();
					} finally {
						try {
							s.close();
						} catch( Exception ex ) {
							ex.printStackTrace();
						}
					}
				}
			}.start();
		}
	}

	private void processSession( Socket s ) throws IOException {
		log.info( "process session: " + s );
		String addr = s.getInetAddress().getHostAddress();
//		StationInfo si = byaddr.get(s);
//		log.info( "byaddr("+s+"): "+si );
		OutputStream os = s.getOutputStream();
//		if( si == null || si.isPending() ) {
//			String msgs[] = new String[] { "Not connected" };
//			if( si != null ) {
//				msgs = new String[] { si.reason };
//			}
//			writeMessages( os, msgs );
//			os.close();
//			return;
//		}
		InputStream is = s.getInputStream();
		while( !done ) {
			int c = is.read();
			log.info(s+": handling command: "+(char)c);
			switch( c ) {
				case 'l':
				case 'L':
					logonStatus( is, os, s, false );
					break;
				case 'P':
					logonStatus( is, os, s, true );
					break;
				case 's':
				case 'S':
					listStations( is, os, s );
					break;
			}
			log.info(s+": finished command: "+(char)c);
		}
	}

	private void logonStatus( InputStream is,
					final OutputStream os, Socket s, boolean withPort ) throws IOException {
		byte[]data = new byte[400];
		int c;
		int i = 0;
		int c1 = 0, c2 = 0, cnt = 0;
		while( ( c = is.read() ) != 0xAC ) {
			log.fine("Callsign Read: ("+c+")'"+((char)c)+"'" );
			if( c == -1 )
				throw new EOFException("Premature EOF" );
			data[i++] = (byte)c;
			if( cnt++ == 0 ) {
				c1 = c;
				if( c1 == '1' ) {
					log.info("Sending protocol 1 login");
					os.write("-----BEGIN RSA PUBLIC KEY-----\r\n".getBytes());
					os.write("MIGJAoGBAK57uDy5++qDXH0EjQzcTC4Ug+1X3J3VRM2HTixcj8GKPI0PCK0n0CFwpfdb/k9S7Sfn\r\n".getBytes());
					os.write("gceBIqHIbrbi21dxSOelM+e9bpXJEFRgeaJ1l3ba2/oOoS3QdB90jpJQmh/AHq1FItc+7Qq8en8k\r\n".getBytes());
					os.write("DiOKbOeoB7NF3cWNp60L57jRbjzvAgMBAAE=\r\n".getBytes());
				}
			} else if( cnt == 2 ) {
				c2 = c;
				if( c1 == '1' && c2 == '?' ) {
					os.write('N');
					os.flush();
					i = 0;
				}
			}
		}

		String call = new String(data,0,i);
		log.fine("Read end of callsign");
		if( is.read() != 0xAC )
			throw new IOException("Missing second 0xAC after call");

		i = 0;
		log.fine( "reading password");
		while( ( c = is.read() ) != 0xd ) {
			if( c == -1 )
				throw new EOFException("Premature EOF" );
			data[i++] = (byte)c;
		}
		String passwd = new String(data,0,i);
		
		log.fine( "reading info");
		i = 0;
		while( ( c = is.read() ) != 0xd ) {
			if( c == -1 )
				throw new EOFException("Premature EOF" );
			data[i++] = (byte)c;
		}
		String info = new String(data,0,i);
		
		log.fine( "reading location");
		i = 0;
		while( ( c = is.read() ) != 0xd ) {
			if( c == -1 )
				throw new EOFException("Premature EOF" );
			data[i++] = (byte)c;
		}
		String location = new String(data,0,i);
		
		log.fine("Looking for port("+withPort+")" );
		int port = 5198;
		s.setSoTimeout( 2000 );
		if( withPort && (c = is.read()) != -1 ) {
			i = 0;
			data[i++] = (byte)c;
			log.fine( "port might exist: "+(char)c);
			while( ( c = is.read() ) != 0xd ) {
				if( c == -1 )
					throw new EOFException("Premature EOF" );
				data[i++] = (byte)c;
			}
			String portno = new String(data,0,i);
			log.info("read port: "+portno );
			try {
				port = Integer.parseInt( portno );
			} catch( Exception ex ) {
				log.info("Read: "+portno+" instead of port number");
				log.log( Level.FINE, ex.toString(), ex );
			}
		}

		log.info("Read data: "+call+", pass: "+passwd+", info: "+ info+", loc: "+location );

		registerLogin( call, passwd, info, location, s.getInetAddress(), port );
		os.write("OK".getBytes());
		os.close();
		is.close();
		s.close();
	}

	private void registerLogin( String user, String passwd, 
					String info, String loc, InetAddress sock, int port ) {
		StationInfo si = stationExists( user, passwd );
		log.info("register login (si="+si+") "+sock+"? "+byaddr.get(sock) );
		if( si == null || si.isPending() ) {
			si = stationFor( user );
			log.info("login station ("+user+")? "+si );
			if( si != null ) {
				log.info("Setting reason to: "+si.reason );
				reasons.put( sock, new String[] { si.reason } );
				byaddr.remove( sock );
				si.setPort( port );
			} else {
				si = new StationInfo(
					user, passwd, nextId(), port );
				stations.put( user, si );
				byaddr.remove( sock );
				if( pendlist.contains(si) == false ) {
					reasons.put( sock, new String[] {
						"Unknown username specified.",
						"The user name you specified,",
						"is not recognized.  Please",
						"check to make sure the correct",
						"username was specified."});
					log.info("Unknown user: "+user);
					pendlist.addElement( si );
					si.reason = "Authorization Pending";
					si.setPending(true);
					plst.setListData( pendlist );
				} else {
					log.info("Pending user: "+user);
				}
			}
			return;
		}

		if( connected.get(user) != null ) {
			if( info.startsWith("OFF") ) {
				if( byaddr.get( sock ) == si ) {
					discoStation(si);
				}
			} else {
				si.info = info;
				si.loc = loc;
			}
			si.setPort( port );
			lst.repaint();
			return;
		}
		si.lastTime = System.currentTimeMillis();
		si.setAddress( sock );
		si.info = info;
		si.loc = loc;
		si.setPort( port );
		reasons.remove( sock );
		connected.put(si.user,si);
		byaddr.put( sock, si );
		runInSwing( new Runnable() {
			public void run() { 
				lst.setListData( statlist );
				lst.repaint();
			}
		});
	}

	private void addStation( String user, String pass, int id, int port ) {
		StationInfo s = new StationInfo( user, pass, id, port );
		addStation(s);
	}
	
	private void addStation( final StationInfo s ) {
		stations.put( s.user, s );
		saveStations();
		runInSwing( new Runnable() {
			public void run() { 
				statlist.addElement( s );
				lst.setListData( statlist );
				lst.repaint();
			}
		});
	}

	private void deleteStation( final StationInfo inf ) {
		stations.remove( inf.user );
		statlist.removeElement( inf );
		rejlist.removeElement(inf);
		pendlist.removeElement(inf);
		discoStation( inf );
	}

	private void discoStation( final StationInfo inf ) {
		if( inf.addr != null ) {
			byaddr.remove( inf.addr );
			inf.setConnected( false );
			reasons.remove( inf.addr );
			connected.remove( inf.user );
		}
		runInSwing( new Runnable() {
			public void run() { 
				inf.addr = null;
				lst.setListData( statlist );
				lst.repaint();
			}
		});
	}
	
	private void saveStations() {
		File f = new File(System.getProperty("user.home")+
			File.separator+
			".javecho"+
			File.separator+
			"users.ser" );
		try {
			FileOutputStream fo = new FileOutputStream( f );
			try {
				ObjectOutputStream os = new ObjectOutputStream(fo);
				os.writeInt(1);
				os.writeObject( stations );
				os.writeObject( statlist );
				os.writeObject( pendlist );
				os.writeObject( rejlist );
				os.close();
			} finally {
				fo.close();
			}
		} catch( IOException ex ) {
			log.log(Level.SEVERE, ex.toString(), ex );
		}
	}
	
	@SuppressWarnings(value={"unchecked"})
	private void loadStations() {
		File f = new File(System.getProperty("user.home")+
			File.separator+
			".javecho"+
			File.separator+
			"users.ser" );
		try {
			FileInputStream fi = new FileInputStream( f );
			try {
				ObjectInputStream is = new ObjectInputStream( fi );
				int v = is.readInt();
				HashtableReader<String,StationInfo> hr = 
					new HashtableReader<String,StationInfo>(is);
				stations = hr.read();
				VectorReader<StationInfo> vr = new VectorReader<StationInfo>();
				statlist = vr.read(is);
				pendlist = vr.read(is);
				rejlist = vr.read(is);
				Enumeration<StationInfo>e = stations.elements();
				while( e.hasMoreElements() ) {
					StationInfo si = e.nextElement();
					if( !statlist.contains(si) &&
						!pendlist.contains(si) &&
						!rejlist.contains(si) ) {
						log.info("deleting lost station: "+si );
						deleteStation(si);
					}
					si.addr = null;
					si.connected = false;
					si.lastTime = 0;
//					statlist.addElement(e.nextElement());
				}
				lst.setListData( statlist );
				plst.setListData( pendlist );
				rlst.setListData( rejlist );
			} finally {
				fi.close();
			}
		} catch( Exception ex ) {
			log.log(Level.SEVERE, ex.toString(), ex );
		}
	}

	static class StationInfo implements Serializable {
		String user;
		String passwd;
		String info;
		String loc;
		String reason;
		int port = 5198;  // 5198 usually
		long lastTime;
		private InetAddress addr;
		boolean connected;
		boolean pending;
		int id;
		static Logger log = Logger.getLogger( StationInfo.class.getName() );
		
		public void setPort( int p ) {
			port = p;
		}
		public boolean equals( Object obj ) {
			if( obj instanceof StationInfo == false )
				return false;
			return user.equals( ((StationInfo)obj).user );
		}
		public int hashCode() {
			return user.hashCode();
		}
		public void setConnected( boolean how ) {
			connected = how;
		}
		public String toString() {
			String str = user;
			if( pending )
				str += " (pending)";
			else
				str += ((addr != null && connected)?" (connected="+port+")":"");
			return str;
		}
		public void setPending( boolean how ) {
			pending = how;
		}
		public boolean isPending() {
			return pending;
		}
		public StationInfo( String user, String passwd, int id, int port ) {
			this.user = user;
			this.passwd = passwd;
			this.id = id;
			this.port = port;
			log.fine("Constructing: "+user+", id: "+id+", port: "+port );
		}
		public void setAddress( InetAddress addr ) {
			this.addr = addr;
			connected = addr != null;
		}
	}
	
	private void runInSwing( Runnable r ) {
		if( SwingUtilities.isEventDispatchThread() ) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait( r );
			} catch( Exception ex ) {
			}
		}
	}

	private StationInfo stationExists( String user, String passwd ) {
		StationInfo si = (StationInfo)stations.get(user);
		if( si == null )
			return null;
		if( si.passwd.equals(passwd) ) {
			if( si.isPending() ) {
				if( rejlist.contains(si) ) {
					si.reason = "Access Rejected\n"+
						"Your login to the system has been\n"+
						"rejected.  Please contact the system\n"+
						"managers for more information";
				} else {
					si.reason = "Authorization is Pending\n"+
						"Your login is currently being\n"+
						"approved.  Please check back later\n"+
						"for your approval";
				}
				return null;
			}
			return 
			si;
		}
		log.info( "Wrong passwd for: "+user+": \""+si.passwd+"\" <> \""+passwd+"\"" );
		si.reason = "Incorrect password";
		return null;
	}
	
	private StationInfo stationFor( InetAddress addr ) {
		return (StationInfo)byaddr.get(addr);
	}
	
	private StationInfo stationFor( String user ) {
		return (StationInfo)stations.get( user );
	}

	private void writeln( OutputStream os, String str ) throws IOException {
		os.write( (str+"\r\n").getBytes() );
		os.flush();
	}
	
	private boolean isConnected( InetAddress addr ) {
		log.info("Check connected "+addr+"? "+byaddr.get(addr) );
		return byaddr.get(addr) != null;
	}
	
	private void writeMessages( OutputStream os, 
				String[]msgs ) throws IOException {
			writeln( os, ""+msgs.length);
			for( int i = 0; i < msgs.length; ++i ) {
				writeln( os, " " );
				writeln( os, msgs[i] );
				writeln( os, "0000" );
				writeln( os, "127.0.0.1" );
			}
	}

	private void listStations( InputStream is, 
				OutputStream os, Socket sock ) throws IOException {
		log.info(sock+": list stations");
		if( isConnected( sock.getInetAddress() ) == false ) {
			log.info(sock+": is not connected: "+
				stationFor(sock.getInetAddress()) );
			StationInfo si = stationFor( sock.getInetAddress() );
			os.write(( "@@@\r").getBytes() );
			String []res = (String[])reasons.get(sock.getInetAddress());
			if( res == null )
				res = new String[] { "Not connected" };
			writeMessages( os, res );
			os.close();
			return;
		}
//		writeln( os, "OK" );
		os.write("@@@\r".getBytes());
		writeln(os,""+connected.size() );
		Enumeration e = connected.elements();
		while( e.hasMoreElements() ) {
			StationInfo si = (StationInfo)e.nextElement();
			writeln( os, si.user );
//			"\015"+(conn ? "BUSY" : "ONLINE")+Javecho.myVersion()+"J(" +
//			sf.format( c.getTime() ) +
//			")\015"
			String which = "OFF";
			if( si.info.startsWith( "BUSY" ) )
				which = "BUSY";
			else if(si.info.startsWith("ONLINE") )
				which = "ON";
			int id1 = si.info.indexOf( "(" );
			int id2 = si.info.indexOf( ")" );
			String hr = si.info.substring( id1+1, id2 );
			String info = si.loc;
			if( info.length() > 28 )
				info = info.substring(0,28);
			else {
				info = info + 
					"                            ".substring(info.length());
			}
			writeln( os, info+"["+which+" "+hr+"] : "+si.port );
			writeln( os, si.id+"" );
			writeln( os, si.addr.getHostAddress() );
		}
		os.close();
		is.close();
		sock.close();
	}
}