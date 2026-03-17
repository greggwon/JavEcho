package org.wonderly.ham.echolink;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
import javax.swing.event.*;
import java.util.logging.*;
import org.wonderly.awt.*;
import org.wonderly.log.*;
import java.util.Timer;
import org.wonderly.io.*;

// import org.wonderly.jini2.*;
// import net.jini.config.*;
// import net.jini.id.*;

import java.util.List;

/**
 *  This class is a primative, testing echolink server.  It provides
 *  The ability to create a test session between several stations and
 *  evaluate operations.  As an asside, it can also be used for a private
 *  server.  With some more code, it could provide some administration
 *  capabilities to allow new users to be added and existing users to
 *  be removed.
 */
public class EcholinkServerImpl 
		// extends PersistentJiniService
		implements EcholinkServer {
	ServerSocket sock;
	int port = 5200;
	boolean done;
	JButton add, remove, edit, approve, reject, apprrej, rej, pendrej, rmvrej;
	JList lst, plst,rlst;
	JFrame frm;
	Timer watcher;
	static final long CONN_TIMEOUT = 400000;
	int nextId = 1;
	SimpleListModel<StationInfo> stats, pends, rejs;
	List<StationInfo> statlist = new ArrayList<>();
	List<StationInfo> pendlist = new ArrayList<>();
	List<StationInfo> rejlist = new ArrayList<>();
	Map<String,StationInfo> stations = new HashMap<>();
	Map<String,StationInfo> connected = new HashMap<>();
	Map<InetAddress,StationInfo> byaddr = new HashMap<>();
	Map<InetAddress,String[]> reasons = new HashMap<>();
	static final Logger log = Logger.getLogger( "org.wonderly.ham.echolink.server");

	static EcholinkServerImpl svr;
	public static void main( String args[] ) throws Exception {
		svr = new EcholinkServerImpl( args );
		svr.StartEchoLink();
	}
	
	class SimpleListModel<T> extends AbstractListModel<T> {
		volatile List<T> data;
		public void setListData( List<T> dt ) {
			data = dt;
			ListDataListener[]l =this.getListDataListeners();
			ListDataEvent ev = new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, 0,dt.size()-1 );
			for( int i = 0; i < l.length; ++i ) {
				l[i].contentsChanged(ev);
			}
		}
		public SimpleListModel( List<T> lst ) {
			data = lst;
		}
		@Override
		public int getSize() {
			return data.size();
		}

		@Override
		public T getElementAt(int index) {
			return data.get(index);
		}
	}

	void bldFrame() {
		frm = new JFrame("EchoLink Server");
		Packer pk = new Packer( frm.getContentPane() );
		lst = new JList<>( stats = new SimpleListModel<>(statlist) );
		plst = new JList<>( pends = new SimpleListModel<>(pendlist) );

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
		approve.addActionListener((ActionEvent ev) -> {
			int idxs[] = plst.getSelectedIndices();
			for( int i = 0; idxs != null && i < idxs.length; ++i ) {
				StationInfo si = pendlist.get(idxs[i]);
				statlist.add( si );
				stations.put( si.user, si );
				saveStations();
				si.setPending(false);
				pendlist.remove(si);
			}

			pends.setListData( pendlist );
			stats.setListData( statlist );
			saveStations();
		});
		apprrej.addActionListener((ActionEvent ev) -> {
			int idxs[] = rlst.getSelectedIndices();
			for( int i = 0; idxs != null && i < idxs.length; ++i ) {
				StationInfo si = rejlist.get(idxs[i]);
				statlist.add( si );
				si.setPending(false);
				rejlist.remove(si);
			}
			rejs.setListData( rejlist );
			stats.setListData( statlist );
			saveStations();
		});
		pendrej.addActionListener((ActionEvent ev) -> {
			int idxs[] = rlst.getSelectedIndices();
			for( int i = 0; idxs != null && i < idxs.length; ++i ) {
				StationInfo si = rejlist.get(idxs[i]);
				pendlist.add( si );
				si.setPending(true);
				rejlist.remove(si);
			}
			rejs.setListData( rejlist );
			pends.setListData( pendlist );
			saveStations();
		});
		rmvrej.addActionListener((ActionEvent ev) -> {
			int idxs[] = rlst.getSelectedIndices();
			//List<StationInfo>v = new ArrayList<StationInfo>();
			if( idxs == null || idxs.length == 0 )
				return;
			if( JOptionPane.showConfirmDialog( frm, 
					"Really Delete "+(
							idxs.length > 1 ?
									(idxs.length+" stations") :
									rejlist.get(idxs[0] ))+"?",
					"Delete Confirmation",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE ) == JOptionPane.CANCEL_OPTION ) {
				return;
			}
			for( int i = idxs.length-1; i >= 0; --i ) {
				StationInfo si = rejlist.get(idxs[i]);
				deleteStation( si );
				rejlist.remove(idxs[i]);
			}
			rejs.setListData( rejlist );
			saveStations();
		});
		reject.addActionListener((ActionEvent ev) -> {
			int idxs[] = plst.getSelectedIndices();
			for( int i = 0; idxs != null && i < idxs.length; ++i ) {
				StationInfo si = pendlist.get(idxs[i]);
				rejlist.add( si );
				si.setPending(true);
				pendlist.remove(si);
			}
			pends.setListData( pendlist );
			rejs.setListData( rejlist );
			saveStations();
		});
		pendrej.setEnabled(false);
		reject.setEnabled(false);
		approve.setEnabled(false);
		rmvrej.setEnabled(false);
		plst.addListSelectionListener((ListSelectionEvent ev) -> {
			approve.setEnabled( plst.getSelectedValue() != null );
			reject.setEnabled( plst.getSelectedValue() != null );
		});
		rlst = new JList();
		rlst.addListSelectionListener((ListSelectionEvent ev) -> {
			apprrej.setEnabled( rlst.getSelectedValue() != null );
			rmvrej.setEnabled( rlst.getSelectedValue() != null );
			pendrej.setEnabled( rlst.getSelectedValue() != null );
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
		rej.addActionListener((ActionEvent ev) -> {
			int idxs[] = lst.getSelectedIndices();
			for( int i = 0; idxs != null && i < idxs.length; ++i ) {
				StationInfo si = statlist.get(idxs[i]);
				rejlist.add( si );
				si.setPending(true);
				statlist.remove(si);
			}
			rejs.setListData( rejlist );
			stats.setListData( statlist );
			saveStations();
		});
		rej.setEnabled(false);
		add.addActionListener((ActionEvent ev) -> {
			StationInfo ent = new StationInfo( "", "", nextId(), 0 );
			EditDialog dlg = new EditDialog( frm, ent, false );
			dlg.performEdit();
			if( dlg.isCancelled() == false ) {
				addStation( ent );
				saveStations();
			}
		});
		remove.addActionListener((ActionEvent ev) -> {
			StationInfo ent = (StationInfo)lst.getSelectedValue();
			int idx = JOptionPane.showConfirmDialog( frm,
					"Really Delete \""+ent.user+"\"?",
					"Delete Confirmation",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE );
			if( idx == JOptionPane.OK_OPTION ) {
				statlist.remove(ent);
				deleteStation(ent);
				stats.setListData(statlist);
				saveStations();
			}
		});
		edit.addActionListener((ActionEvent ev) -> {
			StationInfo ent = (StationInfo)lst.getSelectedValue();
			EditDialog dlg = new EditDialog( frm, ent, true );
			dlg.performEdit();
			saveStations();
		});
		add.setEnabled(true);
		remove.setEnabled(false);
		edit.setEnabled(false);
		lst.addListSelectionListener((ListSelectionEvent ev) -> {
			rej.setEnabled( lst.getSelectedIndex() != -1 );
			remove.setEnabled( lst.getSelectedIndex() != -1 );
			edit.setEnabled( lst.getSelectedIndex() != -1 );
		});
		lpk.pack( p ).gridx(1).gridy(0).fillboth().weightx(0).inset(3,3,3,3);
		frm.pack();
		frm.addWindowListener( new WindowAdapter() {
			@Override
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
		final boolean editing;
		final StationInfo station;
		final JFrame parent;
		public boolean isCancelled() {
			return cancelled;
		}
		public EditDialog( JFrame par, StationInfo who, boolean edit ) {
			super( par, "Edit "+who, true );
			parent = par;
			Packer pk = new Packer( this );
			int y = -1;
			pk.pack( new JLabel( "Name:" ) ).gridx(0).gridy(++y);
			pk.pack( name = new JTextField(who.user) ).gridx(1).gridy(y).fillx();
			if( editing = edit )
				name.setEditable(false);
			station = who;
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
			okay.addActionListener((ActionEvent ev) -> {
				cancelled = false;
				setVisible(false);
			});
			cancel.addActionListener((ActionEvent ev) -> {
				cancelled = true;
				setVisible(false);
			});
			pk.pack( okay ).gridx(0).gridy(++y).west();
			pk.pack( cancel ).gridx(1).gridy(y).east();
		}
		public void performEdit() {
			pack();
			setLocationRelativeTo( parent );
			setVisible(true);
			if( cancelled == false ) {
				if( editing == false ) {
					station.user = name.getText();
				}
				station.passwd = pass.getText();
				station.info = email.getText();
				station.loc = loc.getText();
			}
		}
	}

	void checkTimeouts() {
		for( int i = 0; i < statlist.size(); ++i ) {
			StationInfo inf = statlist.get(i);
			long now = System.currentTimeMillis();
			if( now - inf.lastTime > CONN_TIMEOUT ) {
				try {
					discoStation(inf);
				} catch( RuntimeException ex ) {
					log.log(Level.SEVERE, ex.toString(), ex );
				}
			}
		}
	}

	public EcholinkServerImpl( String args[] ) throws 
			// ConfigurationException, 
			IOException {
		//super(args);
		Handler h = new ConsoleHandler();
		h.setFormatter( new StreamFormatter() );
		log.addHandler(h);
		log.setUseParentHandlers(false);
	}
	public void StartEchoLink() throws IOException {
		bldFrame();
		watcher = new Timer();
		watcher.schedule( new TimerTask() {
			@Override
			public void run() {
				try {
					checkTimeouts();
				} catch( RuntimeException ex ) {
					log.log(Level.SEVERE, ex.toString(), ex );
				}
			}
		}, 20000, 20000 );
		sock = new ServerSocket(port);
	}
	
	public void RunServer() throws IOException {
		loadStations();

		//startService();

		while( !done ) {
			log.log(Level.INFO, "Accepting on: {0}", sock);
			final Socket s = sock.accept();
			log.log(Level.INFO, "Got connection: {0}", s);
			new Thread() {
				@Override
				public void run() {
					try {
						processSession(s);
					} catch( SocketException ex ) {
						log.log(Level.INFO, "{0}: closing: {1}", new Object[]{s, ex});
					} catch( IOException ex ) {
						log.log(Level.FINE, "{0}: closing: {1}", new Object[]{s, ex});
					} finally {
						try {
							s.close();
						} catch( IOException ex ) {
							log.log(Level.FINE, "{0}: closing: {1}", new Object[]{s, ex});
						}
					}
				}
			}.start();
		}
	}

	private void processSession( Socket s ) throws IOException {
		log.log(Level.INFO, "process session: {0}", s);
//		String addr = s.getInetAddress().getHostAddress();
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
			log.log(Level.INFO, "{0}: handling command: {1}", new Object[]{s, (char)c});
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
			log.log(Level.INFO, "{0}: finished command: {1}", new Object[]{s, (char)c});
		}
	}

	private void logonStatus( InputStream is,
					final OutputStream os, Socket s, boolean withPort ) throws IOException {
		try (is; os) {
			byte[]data = new byte[400];
			int c;
			int i = 0;
			int c1 = 0, c2, cnt = 0;
			while( ( c = is.read() ) != 0xAC ) {
				log.log(Level.FINE, "Callsign Read: ({0})''{1}''", new Object[]{c, (char)c});
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
			}	String call = new String(data,0,i);
			log.fine("Read end of callsign");
			if( is.read() != 0xAC )
				throw new IOException("Missing second 0xAC after call");
			i = 0;
			log.fine( "reading password");
			while( ( c = is.read() ) != 0xd ) {
				if( c == -1 )
					throw new EOFException("Premature EOF" );
				data[i++] = (byte)c;
			}	String passwd = new String(data,0,i);
			log.fine( "reading info");
			i = 0;
			while( ( c = is.read() ) != 0xd ) {
				if( c == -1 )
					throw new EOFException("Premature EOF" );
				data[i++] = (byte)c;
			}	String info = new String(data,0,i);
			log.fine( "reading location");
			i = 0;
			while( ( c = is.read() ) != 0xd ) {
				if( c == -1 )
					throw new EOFException("Premature EOF" );
				data[i++] = (byte)c;
			}	String location = new String(data,0,i);
			log.log(Level.FINE, "Looking for port({0})", withPort);
			port = 5198;
			s.setSoTimeout( 2000 );
			if( withPort && (c = is.read()) != -1 ) {
				i = 0;
				data[i++] = (byte)c;
				log.log(Level.FINE, "port might exist: {0}", (char)c);
				while( ( c = is.read() ) != 0xd ) {
					if( c == -1 )
						throw new EOFException("Premature EOF" );
					data[i++] = (byte)c;
				}
				String portno = new String(data,0,i);
				log.log(Level.INFO, "read port: {0}", portno);
				try {
					port = Integer.parseInt( portno );
				} catch( NumberFormatException ex ) {
					log.log(Level.INFO, "Read: {0} instead of port number", portno);
					log.log( Level.FINE, ex.toString(), ex );
				}
			}	log.log(Level.INFO, "Read data: {0}, pass: {1}, info: {2}, loc: {3}", new Object[]{call, passwd, info, location});
			registerLogin( call, passwd, info, location, s.getInetAddress(), port );
			os.write("OK".getBytes());
		}
		s.close();
	}

	private void registerLogin( String user, String passwd, 
					String info, String loc, InetAddress sock, int port ) {
		StationInfo si = stationExists( user, passwd );
		log.log(Level.INFO, "register login (si={0}) {1}? {2}", new Object[]{si, sock, byaddr.get(sock)});
		if( si == null || si.isPending() ) {
			si = stationFor( user );
			log.log(Level.INFO, "login station ({0})? {1}", new Object[]{user, si});
			if( si != null ) {
				log.log(Level.INFO, "Setting reason to: {0}", si.reason);
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
					log.log(Level.INFO, "Unknown user: {0}", user);
					pendlist.add( si );
					si.reason = "Authorization Pending";
					si.setPending(true);
					pends.setListData( pendlist );
				} else {
					log.log(Level.INFO, "Pending user: {0}", user);
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
		runInSwing(() -> {
			stats.setListData( statlist );
			lst.repaint();
		});
	}

//	private void addStation( String user, String pass, int id, int port ) {
//		StationInfo s = new StationInfo( user, pass, id, port );
//		addStation(s);
//	}
	
	private void addStation( final StationInfo s ) {
		stations.put( s.user, s );
		saveStations();
		runInSwing(() -> {
			statlist.add( s );
			stats.setListData( statlist );
			lst.repaint();
		});
	}

	private void deleteStation( final StationInfo inf ) {
		stations.remove( inf.user );
		statlist.remove( inf );
		rejlist.remove(inf);
		pendlist.remove(inf);
		discoStation( inf );
	}

	private void discoStation( final StationInfo inf ) {
		if( inf.addr != null ) {
			byaddr.remove( inf.addr );
			inf.setConnected( false );
			reasons.remove( inf.addr );
			connected.remove( inf.user );
		}
		runInSwing(() -> {
			inf.addr = null;
			stats.setListData( statlist );
			lst.repaint();
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
				try {
					os.writeInt(1);
					os.writeObject( stations );
					os.writeObject( statlist );
					os.writeObject( pendlist );
					os.writeObject( rejlist );
				} finally {
					os.close();
				}
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
					new HashtableReader<>(is);
				stations = hr.read();
				VectorReader<StationInfo> vr = new VectorReader<>();
				statlist = vr.read(is);
				pendlist = vr.read(is);
				rejlist = vr.read(is);
				Iterator<StationInfo>e = stations.values().iterator();
				while( e.hasNext() ) {
					StationInfo si = e.next();
					if( !statlist.contains(si) &&
						!pendlist.contains(si) &&
						!rejlist.contains(si) ) {
						log.log(Level.INFO, "deleting lost station: {0}", si);
						deleteStation(si);
					}
					si.addr = null;
					si.connected = false;
					si.lastTime = 0;
//					statlist.addElement(e.nextElement());
				}
				stats.setListData( statlist );
				pends.setListData( pendlist );
				rejs.setListData( rejlist );
				is.close();
			} finally {
				fi.close();
			}
		} catch( IOException | ClassNotFoundException ex ) {
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
		@Override
		public boolean equals( Object obj ) {
			if( obj instanceof StationInfo == false )
				return false;
			return user.equals( ((StationInfo)obj).user );
		}
		@Override
		public int hashCode() {
			return user.hashCode();
		}
		public void setConnected( boolean how ) {
			connected = how;
		}
		@Override
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
			log.log(Level.FINE, "Constructing: {0}, id: {1}, port: {2}", new Object[]{user, id, port});
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
			} catch( InterruptedException | InvocationTargetException ex ) {
				log.log(Level.SEVERE,ex.toString(), ex);
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
		log.log(Level.INFO, "Wrong passwd for: {0}: \"{1}\" <> \"{2}\"", new Object[]{user, si.passwd, passwd});
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
		log.log(Level.INFO, "Check connected {0}? {1}", new Object[]{addr, byaddr.get(addr)});
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
		log.log(Level.INFO, "{0}: list stations", sock);
		if( isConnected( sock.getInetAddress() ) == false ) {
			log.log(Level.INFO, "{0}: is not connected: {1}", new Object[]{sock, stationFor(sock.getInetAddress())});
//			StationInfo si = stationFor( sock.getInetAddress() );
			try {
				os.write(( "@@@\r").getBytes() );
				String []res = (String[])reasons.get(sock.getInetAddress());
				if( res == null )
					res = new String[] { "Not connected" };
				writeMessages( os, res );
			} finally {
				os.close();
				is.close();
				sock.close();
			}
			return;
		}
		try {
//			writeln( os, "OK" );
			os.write("@@@\r".getBytes());
			writeln(os,""+connected.size() );
			Iterator<StationInfo> e = connected.values().iterator();
			while( e.hasNext() ) {
				StationInfo si = e.next();
				writeln( os, si.user );
//					"\015"+(conn ? "BUSY" : "ONLINE")+Javecho.myVersion()+"J(" +
//					sf.format( c.getTime() ) +
//					")\015"
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
		} finally {
			os.close();
			is.close();
			sock.close();
		}
	}
}