package org.wonderly.ham.echolink;

import javax.swing.*;
import org.wonderly.awt.*;
import org.wonderly.swing.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import javax.sound.sampled.*;
import java.util.logging.*;

public class PreferencesDialog extends JDialog {
	private Javecho je;
	private Parameters prm;
	Logger log = Logger.getLogger( getClass().getName() );

	public PreferencesDialog( final Javecho je, Parameters params ) {
		super( je, "Preferences", true );
		this.je = je;
		prm = params;
		Packer pk = new Packer( getContentPane() );
		JTabbedPane tabs = new JTabbedPane();
		int y = -1;
		pk.pack( tabs ).gridx(0).gridy(++y).fillboth();
		tabs.add("Listing", buildListing() );
		tabs.add("Connections", buildConnections() );
		tabs.add("Media", buildMedia() );
		tabs.add("Security", buildSecurity() );
		tabs.add("Signals", buildSignals() );
		
		pk.pack( new JSeparator() ).gridx(0).gridy(++y).fillx();
		
		JPanel bp = new JPanel();
		Packer bpk = new Packer( bp );
		pk.pack( bp ).gridx(0).gridy(++y).fillx();
		bpk.pack( new JPanel() ).gridx(0).gridy(0).fillx();
		final JButton okay = new JButton("Okay");
		final JButton cancel = new JButton("Cancel");
		final JButton help = new JButton("Help");
		bpk.pack( okay ).gridx(1).gridy(0);
		bpk.pack( cancel ).gridx(2).gridy(0);
		bpk.pack( help ).gridx(3).gridy(0);
		pack();
		setLocationRelativeTo(je);
		
		final boolean cancelled[] = new boolean[1];
		okay.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				cancelled[0] = false;
				setVisible(false);
			}
		});
		cancel.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				cancelled[0] = true;
				setVisible(false);
			}
		});
		cancel.requestFocus();
		help.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				JOptionPane.showMessageDialog( je, "No Help available yet!");
			}
		});
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent ev ) {
				cancelled[0] = true;
			}
		});
		setVisible(true);
		if( cancelled[0] == true )
			return;
		prm.setStationListUpdateAuto( autoListUpd.isSelected() );
		prm.setListUpdateInterval( Integer.parseInt( listInterval.getText() ) );
		prm.setListUpdateWhenConnected( autoWhenBusy.isSelected() );
		prm.setRepeatersInStationList(showReps.isSelected( ) );
		prm.setLinksInStationList(showLinks.isSelected( ) );
		prm.setUsersInStationList(showUsers.isSelected( ) );
		prm.setConferencesInStationList(showConfs.isSelected( ) );
		prm.setBusyStationsInList(showBusy.isSelected( ) );
		prm.setFreeStationsInList(showFree.isSelected( ) );
		prm.setAlarmedStationsOnlyInList(showAlarmOnly.isSelected( ) );
		prm.setStationsOnInOnOffList(listOn.isSelected( ) );
		prm.setStationsOffInOnOffList(listOff.isSelected( ) );
		prm.setFreeStationsInFreeBusyList(listFree.isSelected( ) );
		prm.setBusyStationsInFreeBusyList(listBusy.isSelected( ) );
		prm.setAllowConferences(allowConf.isSelected( ) );
		prm.setConferenceCount( Integer.parseInt( confCount.getValue( ).toString() ) );
		prm.setUpdateLocationEntryWithStatus(updateLocate.isSelected( ) );
		prm.setStationListSentToAllStations(sendStationList.isSelected( ) );
		prm.setFreeStatusText(freeStatus.getText( ) );
		prm.setBusyStatusText(busyStatus.getText( ) );
		prm.setShowNameConnConf(showConConf.isSelected( ) );
		prm.setInfoFileName( infoFile.getText( ) );
		prm.setAcceptFromRepeaters(acceptReps.isSelected( ) );
		prm.setAllowMulti( allowMulti.isSelected() );
		prm.setAcceptFromLinks(acceptLinks.isSelected( ) );
		prm.setAcceptFromUsers(acceptUsers.isSelected( ) );
		prm.setAcceptFromConfs(acceptConfs.isSelected( ) );
		if( acceptOnly.isSelected() && deniedCallsList.size() == 0 ) {
			JOptionPane.showMessageDialog( je, 
				"You have selected to block all but\n"+
				"selected calls, but there are no calls in\n"+
				"the accepted calls list.", "No Calls Will Be Received",
				JOptionPane.ERROR_MESSAGE );
		}
		prm.setAcceptOnlyCalls(acceptOnly.isSelected( ) );
		prm.setDeniedCallsList(deniedCallsList);
		prm.setDeniedCountries(deniedCountriesList);
		prm.setSoundForConnected( buts[0][0].isSelected() == false );
		prm.setCustomSoundForConnected( sounds[0] &&
			buts[0][2].isSelected() ? sndFiles[0] : null );
		prm.setSoundForDisconnected( buts[1][0].isSelected() == false );
		prm.setCustomSoundForDisconnected( sounds[1] && 
			buts[1][2].isSelected() ? sndFiles[1] : null );
		prm.setSoundForAlarm( buts[2][0].isSelected() == false );
		prm.setCustomSoundForAlarm( sounds[2] &&
			buts[2][2].isSelected() ? sndFiles[2] : null );
		prm.setSoundForOver( buts[3][0].isSelected() == false );
		prm.setCustomSoundForOver( sounds[3] && 
			buts[3][2].isSelected() ? sndFiles[3] : null );
		prm.setHomepageURL( homepage.getText().trim().length() > 0 ? 
			homepage.getText() : null );
		prm.setCameraURL( camera.getText().trim().length() > 0 ?
			camera.getText() : null );
		prm.setOpenHomePage( openHomePage.isSelected() );
		prm.setShowCamera( showCamera.isSelected() );
		prm.setSendingCurrentPage( sendCurrentPage.isSelected() );
		prm.setFollowingUsersPage( followUsersPage.isSelected() );
		prm.setToggleSendFollow( toggleSendFollow.isSelected() );
		prm.saveData();
	}
	
	JCheckBox openHomePage, showCamera, 
		sendCurrentPage, followUsersPage,
		toggleSendFollow;
	JTextField homepage, camera;
	private JPanel buildMedia() {
		JPanel p = new JPanel();
		Packer pk = new Packer(p);
		int y = -1;

		pk.pack( homepage = new JTextField() ).gridx(0).gridy(++y).fillx();
		pk.pack( openHomePage = new JCheckBox("Open Home Page at Startup")
			).gridx(1).gridy(y).west();
		
		pk.pack( camera = new JTextField() ).gridx(0).gridy(++y).fillx();
		pk.pack( showCamera = new JCheckBox("Show Camera Picture") 
			).gridx(1).gridy(y).west();
//		showCamera.setEnabled(false);
//		camera.setEditable(false);
		JPanel sp = new JPanel();
		Packer spk = new Packer(sp);
		sp.setBorder( BorderFactory.
			createTitledBorder("Browser Remote Control Settings") );
		pk.pack( sp ).gridx(0).gridy(++y).fillx().weightx(0).gridw(2);
		spk.pack( sendCurrentPage = new JCheckBox(
			"Send Users Current Browser Page") 
			).gridx(0).gridy(++y).fillx().west();
		spk.pack( followUsersPage = new JCheckBox(
			"Follow Transmitting Stations Page") 
			).gridx(0).gridy(++y).fillx().west();
		spk.pack( toggleSendFollow = new JCheckBox(
			"Toggle Send/Follow with PTT") 
			).gridx(0).gridy(++y).fillx().west();
		homepage.setText( prm.getHomepageURL() );
		camera.setText( prm.getCameraURL() );
		openHomePage.setSelected( prm.isOpenHomePage() );
		showCamera.setSelected(prm.isShowCamera());
		sendCurrentPage.setSelected( prm.isSendingCurrentPage() );
		followUsersPage.setSelected( prm.isFollowingUsersPage() );
		toggleSendFollow.setSelected( prm.isToggleSendFollow() );
		// Justify everything to the top.
		pk.pack( new JPanel() ).gridx(0).gridy(++y).filly();
		return p;
	}

	JCheckBox autoListUpd;
	JTextField listInterval;
	JCheckBox autoWhenBusy;
	JCheckBox showReps;
	JCheckBox showLinks;
	JCheckBox showUsers;
	JCheckBox showConfs;
	JCheckBox showBusy;
	JCheckBox showFree;
	JCheckBox showAlarmOnly;
	JCheckBox listOn;
	JCheckBox listOff;
	JCheckBox listFree;
	JCheckBox listBusy;
	private JPanel buildListing() {
		JPanel p = new JPanel();
		Packer pk = new Packer(p);
		int y = -1;
		
		JPanel lup = new JPanel();
		Packer lupk = new Packer( lup);
		pk.pack( lup ).gridx(0).gridy(++y).gridw(2).fillx();
		lup.setBorder( BorderFactory.createTitledBorder( "List Update") );
		lupk.pack( autoListUpd = new JCheckBox(
			"Update station list automatically") 
				).gridx(0).gridy(0).gridw(2).west().inset(0,5,10,0);
		autoListUpd.setSelected( prm.isStationListUpdateAuto() );
		JPanel upp = new JPanel();
		Packer upk = new Packer( upp );
		upk.pack( new JLabel("Update every") ).gridx(0).gridy(1).inset(0,5,10,10);
		listInterval = new JTextField() {
			public void setEnabled( boolean how ) {
				super.setEnabled(how);
				setOpaque(how);
			}
		};
		upk.pack( listInterval ).gridx(1).gridy(1).inset(0,0,10,10).fillx();
		upk.pack( new JLabel( "sec") ).gridx(2).gridy(1).inset(0,0,10,30);
		lupk.pack( upp ).gridx(0).gridy(1).fillx();
		lupk.pack( autoWhenBusy = new JCheckBox("Even while connected")
			).gridx(1).gridy(1).inset(0,0,10,0);
		listInterval.setText( prm.getListUpdateInterval()+"" );
		autoWhenBusy.setSelected( prm.isListUpdateWhenConnected() );
		autoListUpd.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				listInterval.setEnabled( autoListUpd.isSelected() );
			}
		});
			listInterval.setEnabled( autoListUpd.isSelected() );

		JPanel lp = new JPanel();
		lp.setBorder( BorderFactory.createTitledBorder("Show in list") );
		Packer lpk = new Packer( lp );
		pk.pack( lp ).gridx(0).gridy(++y).gridw(2).fillx();
		lpk.pack( showReps = new JCheckBox(
			"Repeaters (-R)") ).gridx(0).gridy(0).west();
		lpk.pack( showLinks = new JCheckBox(
			"Links (-L)") ).gridx(0).gridy(1).west();
		lpk.pack( showUsers = new JCheckBox(
			"Users") ).gridx(0).gridy(2).west();
		lpk.pack( showConfs = new JCheckBox(
			"Conference Servers") ).gridx(0).gridy(3).west();
		showReps.setSelected( prm.isRepeatersInStationList() );
		showLinks.setSelected( prm.isLinksInStationList() );
		showUsers.setSelected( prm.isUsersInStationList() );
		showConfs.setSelected( prm.isConferencesInStationList() );

		lpk.pack( new JPanel() ).gridx(1).gridy(0).fillx();
		lpk.pack( showBusy = new JCheckBox(
			"Stations Busy") ).gridx(2).gridy(0).west();
		lpk.pack( showFree = new JCheckBox(
			"Stations Free") ).gridx(2).gridy(1).west();
		lpk.pack( showAlarmOnly = new JCheckBox(
			"Alarmed Only") ).gridx(2).gridy(2).west();
		showBusy.setSelected( prm.isBusyStationsInList() );
		showFree.setSelected( prm.isFreeStationsInList() );
		showAlarmOnly.setSelected( prm.isAlarmedStationsOnlyInList() );

		JPanel op = new JPanel();
		Packer opk = new Packer(op);
		pk.pack( op ).gridx(0).gridy(++y).fillx();
		op.setBorder( BorderFactory.createTitledBorder(
			"Show in On/Off List") );
		opk.pack( listOn = new JCheckBox(
			"Stations On") ).gridx(0).gridy(0).west();
		opk.pack( listOff = new JCheckBox(
			"Stations Off") ).gridx(0).gridy(1).west();
		listOn.setSelected( prm.isStationsOnInOnOffList() );
		listOff.setSelected( prm.isStationsOffInOnOffList() );

		JPanel sp = new JPanel();
		Packer spk = new Packer(sp);
		pk.pack( sp ).gridx(1).gridy(y).fillx();
		sp.setBorder( BorderFactory.createTitledBorder(
			"Show in Free/Busy List") );
		spk.pack( listFree = new JCheckBox(
			"Stations Free") ).gridx(0).gridy(0).west();
		spk.pack( listBusy = new JCheckBox(
			"Stations Busy") ).gridx(0).gridy(1).west();
		listFree.setSelected( prm.isFreeStationsInFreeBusyList() );
		listBusy.setSelected( prm.isBusyStationsInFreeBusyList() );

		return p;
	}
	
	JCheckBox allowConf;
	JSpinner confCount;
	JCheckBox updateLocate;
	JCheckBox sendStationList;
	JCheckBox allowMulti;
	JTextField freeStatus;
	JTextField busyStatus;
	JCheckBox showConConf;
	JComboBox pttControl;
	JLabel infoFile;
	private JPanel buildConnections() {
		JPanel p = new JPanel();
		Packer pk = new Packer(p);
		int y = -1;
		
		JPanel cp = new JPanel();
		cp.setBorder(BorderFactory.createTitledBorder("Conferencing"));
		pk.pack( cp ).gridx(0).gridy(++y).fillx();
		Packer cpk = new Packer( cp );
		cpk.pack( allowConf = new JCheckBox("Allow conferences")
			).gridx(0).gridy(0).gridw(3).fillx();
		cpk.pack( new JLabel("Limit to") 
			).gridx(0).gridy(1).inset(0,0,0,5);
//		confCount = new JTextField("6") {
//			public void setEnabled( boolean how ) {
//				super.setEnabled(how);
//				setOpaque(how);
//			}
//		};
		SpinnerNumberModel snm = new SpinnerNumberModel( 1, 1, 1000, 1 );
		confCount = new JSpinner( snm ) {
			public void setEnabled( boolean how ) {
				super.setEnabled(how);
				setOpaque(how);
			}
		};
//		confCount.addChangeListener( new ChangeListener() {
//			public void stateChanged( ChangeEvent ev ) {
//				confCount.setText( confCount.getValue() +"" );
//			}
//		});
		
		cpk.pack( confCount ).gridx(1).gridy(1).inset(0,0,0,5).fillx();
//		cpk.pack( fcntspin ).gridx().gridy(1).inset(0,0,0,5);
		cpk.pack( new JLabel("other stations")
			).gridx(2).gridy(1).fillx().west();
		cpk.pack( updateLocate = new JCheckBox(
			"Update Location entry with status")
			).gridx(0).gridy(2).gridw(3).fillx();
		cpk.pack( sendStationList = new JCheckBox(
			"Send station list to all stations")
			).gridx(0).gridy(3).gridw(3).fillx();
		cpk.pack( allowMulti = new JCheckBox(
			"Allow multi-conferencing")
			).gridx(0).gridy(4).gridw(3).fillx();
		allowConf.setSelected( prm.isAllowConferences() );
		confCount.setValue( new Integer( prm.getConferenceCount() ) );
		confCount.setEnabled( allowConf.isSelected() );
		allowConf.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				confCount.setEnabled( allowConf.isSelected() );
				allowMulti.setEnabled( allowConf.isSelected() );
				updateLocate.setEnabled( allowConf.isSelected() );
				sendStationList.setEnabled( allowConf.isSelected() );
			}
		});
		confCount.setEnabled( allowConf.isSelected() );
		updateLocate.setSelected( prm.isUpdateLocationEntryWithStatus() );
		sendStationList.setSelected( prm.isStationListSentToAllStations() );
		allowMulti.setSelected( prm.isAllowMulti() );
		allowMulti.setEnabled( allowConf.isSelected() );
		updateLocate.setEnabled( allowConf.isSelected() );
		sendStationList.setEnabled( allowConf.isSelected() );

		JPanel lp = new JPanel();
		lp.setBorder( BorderFactory.createTitledBorder(
			"Location/Description") );
		pk.pack( lp ).gridx(0).gridy(++y).fillx();
		Packer lpk = new Packer( lp );
		lpk.pack( new JLabel("Free:") ).gridx(0).gridy(0);
		lpk.pack( freeStatus = new JTextField("    ") 
			).gridx(1).gridy(0).fillx();
		lpk.pack( new JLabel("Busy:") ).gridx(0).gridy(1);
		lpk.pack( busyStatus = new JTextField("    ") 
			).gridx(1).gridy(1).fillx();
		lpk.pack( showConConf = new JCheckBox(
			"Show name of connected conference") 
				).gridx(0).gridy(2).gridw(2).inset(0,5,0,0).fillx();
		freeStatus.setText( prm.getFreeStatusText() );
		busyStatus.setText( prm.getBusyStatusText() );
		showConConf.setSelected( prm.isShowNameConnConf() );

		JPanel pp = new JPanel();
		pp.setBorder(BorderFactory.createTitledBorder("PTT Control") );
		Packer ppk = new Packer( pp );
		ppk.pack( new JLabel("Space Bar TX Control:")
			).gridx(0).gridy(0).inset(0,0,0,10);
		ppk.pack( pttControl = new JComboBox(
			new String[] {"ToolBar Button", "Tap On / Tap Off", "Momentary" } 
			) ).gridx(1).gridy(0).fillx();
		pttControl.setEnabled(false);
		pk.pack(pp).gridx(0).gridy(++y).fillx();
		
		JPanel ip = new JPanel();
		ip.setBorder(BorderFactory.createTitledBorder("Station Info") );
		Packer ipk = new Packer(ip);
		pk.pack( ip ).gridx(0).gridy(++y).fillx();
		ipk.pack( new JLabel("Station Information File:")
			).gridx(0).gridy(0).inset(0,5,0,10);
		ipk.pack( infoFile = new JLabel(
			System.getProperty("user.home")+File.separatorChar+
			".javecho+"+File.separatorChar+"info.txt")
			).gridx(1).gridy(0).inset(0,0,0,10).fillx();
		infoFile.setFont( new Font( "serif", Font.BOLD, 12 ) );
		if( prm.getInfoFileName() == null ||
			prm.getInfoFileName().trim().length() == 0 ) {
			prm.setInfoFileName( new File( 
				new File( System.getProperty("user.home")+
				File.separatorChar + ".javecho" ), "info.txt" ).toString() );
		}
		infoFile.setText( prm.getInfoFileName() );
		infoFile.revalidate();

		final JButton chs = new JButton("Choose...");
		final JButton edit = new JButton("Edit...");

		ipk.pack( chs ).gridx(2).gridy(0).fillx().weightx(0);
		chs.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				JFileChooser c = new JFileChooser();
				c.setFileFilter( new FileFilter() {
					public String getDescription() {
						return "*.txt files";
					}
					public boolean accept( java.io.File f ) {
						if( f.isDirectory() )
							return true;
						return f.getName().toLowerCase().endsWith(".txt");
					}
				});
				if( c.showOpenDialog( je ) != c.APPROVE_OPTION )
					return;
				File f = c.getSelectedFile();
				infoFile.setText( f.getName() );
				infoFile.revalidate();
				infoFile.repaint();
			}
		});

		ipk.pack( edit ).gridx(2).gridy(1).fillx().weightx(0);
		edit.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				JTextArea f = new JTextArea();
				f.setWrapStyleWord(true);
				f.setLineWrap(true);
				try {
					FileReader fr = new FileReader( infoFile.getText() );
					try {
						BufferedReader rd = new BufferedReader( fr );
						String str;
						StringBuffer txt = new StringBuffer();
						while( ( str = rd.readLine() ) != null ) {
							txt.append(str);
						}
						f.setText( txt.toString() );
					} finally {
						fr.close();
					}

					final JDialog d = new JDialog( je, "Edit Info Text", true );
					Packer pk = new Packer( d.getContentPane() );
					pk.pack( new JScrollPane(f)
						).gridx(0).gridy(0).gridw(2).fillboth();
					pk.pack( new JSeparator() 
						).gridx(0).gridy(1).fillx().gridw(2).inset(4,4,4,4);
					final JButton okay = new JButton( "Okay" ),
						cancel = new JButton("Cancel");
					pk.pack( okay).gridx(0).gridy(2).west();
					pk.pack( cancel ).gridx(1).gridy(2).east();
					d.pack();
					d.setLocationRelativeTo( je );
					final boolean cancelled[] = new boolean[1];
					okay.addActionListener( new ActionListener() {
						public void actionPerformed(ActionEvent ev ) {
							cancelled[0] = false;
							d.setVisible(false);
						}
					});
					cancel.addActionListener( new ActionListener() {
						public void actionPerformed(ActionEvent ev ) {
							cancelled[0] = true;
							d.setVisible(false);
						}
					});
					d.addWindowListener( new WindowAdapter() {
						public void windowClosing(WindowEvent ev) {
							cancelled[0] = true;
						}
					});
					d.setVisible(true);
					if( cancelled[0] == true ) 
						return;

					FileWriter fw = new FileWriter( infoFile.getText() );
					try {
						fw.write( f.getText() );
					} finally {
						fw.close();
					}
					if( je.isConnected() == false )
						je.setInfo( f.getText() );
				} catch( IOException ex ) {
					je.reportException( ex );
				}
			}
		});
		return p;
	}

	JCheckBox acceptReps;
	JCheckBox acceptLinks;
	JCheckBox acceptUsers;
	JCheckBox acceptConfs;
	JRadioButton acceptOnly;
	JRadioButton denyCalls;
	Vector<String> deniedCallsList;
	JList deniedCalls;
	JList<CountryAccess.CountryEntry> allCountries;
	JList<CountryAccess.CountryEntry> deniedCountries;
	java.util.List<CountryAccess.CountryEntry> allCountriesList;
	java.util.List<CountryAccess.CountryEntry> deniedCountriesList;
	private JPanel buildSecurity() {
		JPanel p = new JPanel();
		Packer pk = new Packer(p);
		int y = -1;
		
		JPanel ap = new JPanel();
		Packer apk = new Packer(ap);
		pk.pack( ap ).gridx(0).gridy(++y).fillboth().weighty(0);
		ap.setBorder( BorderFactory.createTitledBorder(	"Accept Conns From") );
		apk.pack( acceptReps = new JCheckBox(
			"Repeaters (-R)" ) ).gridx(0).gridy(0).west();
		apk.pack( acceptLinks = new JCheckBox(
			"Links (-L)") ).gridx(0).gridy(1).west();
		apk.pack( acceptUsers = new JCheckBox( 
			"Users" ) ).gridx(0).gridy(2).west();
		apk.pack( acceptConfs = new JCheckBox( 
			"Conferences" ) ).gridx(0).gridy(3).west();
		apk.pack( new JPanel() ).gridx(0).gridy(4).filly();
		acceptReps.setSelected( prm.isAcceptFromRepeaters() );
		acceptLinks.setSelected( prm.isAcceptFromLinks() );
		acceptUsers.setSelected( prm.isAcceptFromUsers() );
		acceptConfs.setSelected( prm.isAcceptFromConfs() );
		
		JPanel dp = new JPanel();
		dp.setBorder( BorderFactory.createTitledBorder("Callsign Control") );
		pk.pack( dp ).gridx(1).gridy(y).fillx();
		Packer dpk = new Packer( dp );
		dpk.pack( acceptOnly = new JRadioButton("Accept Only These Calls")
			).gridx(0).gridy(0).gridw(2).fillx();
		dpk.pack( denyCalls = new JRadioButton("Deny These Calls" ) 
			).gridx(0).gridy(1).fillx().gridw(2);
		ButtonGroup grp = new ButtonGroup();
		grp.add( acceptOnly );
		grp.add( denyCalls );
//		ActionListener lis = new ActionListener() {
//			public void actionPerformed( ActionEvent ev ) {
//				deniedCalls.setEnabled( acceptOnly.isSelected() == false );
//			}
//		};
//		acceptOnly.addActionListener( lis );
//		denyCalls.addActionListener( lis );
		acceptOnly.setSelected( prm.isAcceptOnlyCalls() );
		denyCalls.setSelected( !prm.isAcceptOnlyCalls() );

		deniedCallsList = new Vector<String>(
				prm.getDeniedCallsList());
		deniedCalls = new JList(deniedCallsList);
//		{
//			public void setEnabled( boolean how ) {
//				super.setEnabled(how);
//				setOpaque(how);
//			}
//		};
//		deniedCalls.setEnabled( acceptOnly.isSelected() == false );
		dpk.pack( new JScrollPane( deniedCalls ) 
			).gridh(4).gridx(0).gridy(2).fillboth();
		final JButton addNew = new JButton("Add New");
		final JButton rmv = new JButton("Remove");
		final JButton rmvAll = new JButton("Remove All");
		dpk.pack( addNew ).gridx(1).gridy(2).fillx().weightx(0);
		dpk.pack( rmv ).gridx(1).gridy(3).fillx().weightx(0);
		dpk.pack( rmvAll ).gridx(1).gridy(4).fillx().weightx(0);
		addNew.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				String call = JOptionPane.showInputDialog( je,
					"Enter Call to Deny" );
				if( call != null ) {
					deniedCallsList.addElement( call );
					deniedCalls.clearSelection();
				}
			}
		});
		rmv.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int idx = deniedCalls.getSelectedIndex();
				if( idx != -1 ) {
					deniedCallsList.removeElementAt(idx);
					deniedCalls.clearSelection();
				}
			}
		});
		rmv.setEnabled(false);
		deniedCalls.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent ev ) {
				rmv.setEnabled( deniedCalls.getSelectedIndex() != -1 );
			}
		});
		rmvAll.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				deniedCallsList.removeAllElements();
			}
		});
		dpk.pack( new JPanel()).gridx(1).gridy(5).fillx().weightx(0);
			
		JPanel ip = new JPanel();
		Packer ipk = new Packer( ip );
		ip.setBorder( 
			BorderFactory.createTitledBorder(
			"International Prefixes" ) );
		pk.pack( ip ).gridx(0).gridy(++y).gridw(2).fillboth();
		ipk.pack( new JLabel("Accept") ).gridx(0).gridy(0).west();
		ipk.pack( new JLabel("Deny") ).gridx(2).gridy(0).west();
		java.util.List<CountryAccess.CountryEntry> v = CountryAccess.getCountries();
		Collections.sort(v);
		deniedCountriesList = prm.getDeniedCountries();
		for( int i = 0; i < deniedCountriesList.size(); ++i ) {
			v.remove( CountryAccess.entryFor( 
				deniedCountriesList.get(i).name ) );
		}
		ipk.pack( new JScrollPane( 
			allCountries = new JList<CountryAccess.CountryEntry>( new ListListModel<CountryAccess.CountryEntry>( allCountriesList = v) ) ) 
			).gridx(0).gridy(1).gridh(4).fillboth();

		ipk.pack( new JPanel() ).gridx(1).gridy(1).filly();
		final JButton add = new JButton(">>");
		final JButton rmvc = new JButton("<<");
		Insets i0 = new Insets(0,0,0,0);
		add.setMargin( i0 );
		rmvc.setMargin( i0 );
		ipk.pack( add ).gridx(1).gridy(2).inset(10,0,0,10);
		ipk.pack( rmvc ).gridx(1).gridy(3).inset(0,10,0,10);
		ipk.pack( new JPanel() ).gridx(1).gridy(4).filly();
		ipk.pack( new JScrollPane( 
				deniedCountries = new JList<CountryAccess.CountryEntry>(new ListListModel<CountryAccess.CountryEntry>(deniedCountriesList)) 
			) ).gridx(2).gridy(1).gridh(4).fillboth();
		add.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int idx = allCountries.getSelectedIndex();
				if( idx == -1 )
					return;
				deniedCountriesList.add(
					allCountriesList.get( idx ) );
				allCountriesList.remove( idx );
				allCountries.clearSelection();
			}
		});
		rmvc.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int idx = deniedCountries.getSelectedIndex();
				if( idx == -1 )
					return;
				allCountriesList.add(
					deniedCountriesList.get( idx ) );
				deniedCountriesList.remove( idx );
				deniedCountries.clearSelection();
			}
		});
		add.setEnabled(false);
		rmvc.setEnabled(false);
		deniedCountries.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent ev ) {
				rmvc.setEnabled(deniedCountries.getSelectedIndex() != -1);
				allCountries.clearSelection();
				add.setEnabled(false);
			}
		});
		allCountries.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent ev ) {
				add.setEnabled(allCountries.getSelectedIndex() != -1);
				deniedCountries.clearSelection();
				rmv.setEnabled(false);
			}
		});
		pk.pack( new JPanel() ).gridx(0).gridy(++y).filly();
		return p;
	}
	static class VectorCloner<T> {
		public Vector<T> clone( Vector<T> vo ) {
			Vector<T> v = new Vector<T>(vo);
			return v;
		}
	}
	String arr[] = new String[] { 
		"Connected", "Disconnected", "Alarm", "Over" };
		JTextField sndf[] = new JTextField[arr.length];
	JRadioButton buts[][];
		boolean sounds[] = new boolean[4];
		boolean cust[] = new boolean[4];
		String sndFiles[] = new String[4];
	private JPanel buildSignals() {
		JPanel p = new JPanel();
		Packer pk = new Packer(p);
		int y = -1;

		buts = new JRadioButton[arr.length][];
		pk.pack( new JLabel("Configure signals "+
			"(sounds) for the following events:")
			).gridx(0).gridy(++y).gridw(6).west().inset(10,0,10,0);

		cust[0] = prm.getCustomSoundForConnected() != null;
		cust[1] = prm.getCustomSoundForDisconnected() != null;
		cust[2] = prm.getCustomSoundForAlarm() != null;
		cust[3] = prm.getCustomSoundForOver() != null;
		sndFiles[0] = prm.getCustomSoundForConnected();
		sndFiles[1] = prm.getCustomSoundForDisconnected();
		sndFiles[2] = prm.getCustomSoundForAlarm();
		sndFiles[3] = prm.getCustomSoundForOver();
		sounds[0] = prm.isSoundForConnected();
		sounds[1] = prm.isSoundForDisconnected();
		sounds[2] = prm.isSoundForAlarm();
		sounds[3] = prm.isSoundForOver();
		for( int i = 0; i < arr.length; ++i ) {
			final int idx = i;
			buts[i] = new JRadioButton[3];
			pk.pack( new JLabel( arr[i]+": " ) ).gridx(0).gridy(++y).east();
			Icon ic = je.loadIcon("sound.gif");
			JButton bpl = null;
			if( ic == null )
				bpl = new JButton("Play");
			else
				bpl = new JButton( ic );
			final JButton pl = bpl;
			pl.setMargin( new Insets(0,0,0,0) );
			pl.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent ev ) {
					new ComponentUpdateThread( pl ) {
						public Object construct() {
					try {
						playSoundFor( idx );
					} catch( Exception ex ) {
						log.log( Level.SEVERE, ex.toString(), ex );
					}
					return null;
						}
					}.start();
				}
			});
			pk.pack( pl ).gridx(1).gridy(y).inset(5,5,0,5);
			pk.pack( buts[i][0] = new JRadioButton("None") 
				).gridx(2).gridy(y).inset(0,0,0,5);
			pk.pack( buts[i][1] = new JRadioButton("Default")
				).gridx(3).gridy(y).inset(0,0,0,5);
			pk.pack( buts[i][2] = new JRadioButton("Custom") 
				).gridx(4).gridy(y).inset(0,0,0,5);
			ButtonGroup g = new ButtonGroup();
			g.add(buts[i][0]);
			g.add(buts[i][1]);
			g.add(buts[i][2]);
			buts[i][0].setSelected( !sounds[i] );
			pl.setEnabled(false);
			if( sounds[i] ) {
				buts[i][1].setSelected( !cust[i] );
				pl.setEnabled(true);
				buts[i][2].setSelected( cust[i] );
			}
			ActionListener plis = new ActionListener() {
				public void actionPerformed( ActionEvent ev ) {
					pl.setEnabled( buts[idx][0].isSelected() == false );
				}
			};
			buts[i][0].addActionListener(plis);
			buts[i][1].addActionListener(plis);
			buts[i][2].addActionListener(plis);
//			final JButton snd = new JButton("...");
//			snd.setMargin( new Insets(0,5,0,5) );
//			snd.addActionListener( new ActionListener() {
//				public void actionPerformed( ActionEvent ev ) {
//					setupSound( idx );
//				}
//			});
//			pk.pack( snd ).gridx(5).gridy(y);
		}

		pk.pack( new JSeparator() ).gridx(0).gridy(++y).
			gridw(5).fillx().inset(10,0,10,0);
		for( int i = 0; i < arr.length; ++i ) {
			final int idx = i;
			pk.pack( new JLabel( arr[i]+": " ) ).gridx(0).gridy(++y).east();
			sndf[i] = new JTextField( sndFiles[i] );
			pk.pack( sndf[i] ).gridx(1).gridy(y).gridw(3).fillx();
			final JButton snd = new JButton("...");
			snd.setMargin( new Insets(0,5,0,5) );
			snd.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent ev ) {
					setupSound( idx );
				}
			});
			pk.pack( snd ).gridx(4).gridy(y);
		}
		pk.pack( new JPanel() ).gridx(0).gridy(++y).filly();
		return p;
	}

	public void playSoundFor( int sndNo ) throws IOException {
		String fileName;
		if( buts[sndNo][2].isSelected() ) {
			fileName = sndf[sndNo].getText();
		} else if( buts[sndNo][1].isSelected() ) {
			fileName = prm.getDefaultSoundFor( sndNo );
		} else {
			return;
		}
		if( fileName == null ) {
			AudioProvider ap = je.getAudioProvider();
			switch( sndNo ) {
				case 0:  // connected
					ap.connected();
					break;
				case 1:  // disconnected
					ap.disconnect();
					break;
				case 2:  // alarm
					ap.alarm();
					break;
				case 3:  // over
					ap.over();
					break;
			}
			
			return;			
		}
		try {
			AudioFileFormat aff = 
				AudioSystem.getAudioFileFormat( new File( fileName ) );
			if( AudioSystem.isFileTypeSupported( aff.getType() ) == false ) {
				JOptionPane.showMessageDialog( je,
					"Audio File Type not Supported by Installed Codecs",
					"No Audio File Support", JOptionPane.ERROR_MESSAGE );
				return;
			}
		} catch( UnsupportedAudioFileException ex ) {
			log.log( Level.SEVERE, ex.toString(), ex );
		} catch( IOException ex ) {
			log.log( Level.SEVERE, ex.toString(), ex );
		} catch( Exception ex ) {
			log.log( Level.SEVERE, ex.toString(), ex );
		}		
	}

	public void setupSound( int sndNo ) {
		JFileChooser cf = new JFileChooser();
		int resp = cf.showOpenDialog( je );
		if( resp != cf.APPROVE_OPTION )
			return;
		sndf[sndNo].setText( cf.getSelectedFile().toString() );
		sndf[sndNo].revalidate();
		sndf[sndNo].repaint();
	}
}