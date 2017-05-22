package org.wonderly.ham.echolink;

import javax.swing.*;
import org.wonderly.awt.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;
import javax.sound.sampled.*;
import org.wonderly.swing.*;

/**
 *  This class provides the setup dialog for the echolink client.
 */
public class SetupDialog extends JDialog {
	private Javecho je;
	private Parameters prm;
	private boolean first;

	public SetupDialog( Javecho je, Parameters params, boolean first ) {
		super( je, "Setup", true );
		this.first = first;
		prm = params;
		this.je = je;
		Packer pk = new Packer( getContentPane() );
		JTabbedPane tabs = new JTabbedPane();
		int y = -1;
		pk.pack( tabs ).gridx(0).gridy(++y).fillboth();
		tabs.add( "My Station", buildMyStation() );
		tabs.add( "Servers", buildServers() );
		tabs.add( "Timing", buildTiming() );
		tabs.add( "Audio", buildAudio() );
		tabs.add( "KML/Google", buildKML() );
		tabs.add( "Advanced", buildAdvanced() );
		
		pk.pack( new JSeparator() ).gridx(0).gridy(++y).fillx();
		JPanel bt = new JPanel();
		Packer bpk = new Packer( bt );
		final JButton okay = new JButton("Okay" ),
			cancel = new JButton("Cancel"),
			help = new JButton("Help");
		bpk.pack( okay ).gridx(0).gridy(0).inset(0,0,0,10);
		bpk.pack( cancel ).gridx(1).gridy(0).inset(0,0,0,40);
		bpk.pack( help ).gridx(2).gridy(0);
		pk.pack( bt ).gridx(0).gridy(++y).fillx();
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
				setupHelp();
			}
		});

		pack();
		if( first )
			call.requestFocus();

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent ev ) {
				cancelled[0] = true;
			}
		});
		setLocationRelativeTo( je );
		setVisible(true);
		if( cancelled[0] == false ) {
			Vector v = prm.getServerList();
			prm.getServers().removeAllElements();
			for( int i = 0; i < srvbx.length; ++i ) {
				int idx = srvbx[i].getSelectedIndex();
				if( idx >= v.size() ) {
					prm.setServerN( i, null );
				} else {
					prm.setServerN( i, 
						(String)srvbx[i].getSelectedItem() );
				}
			}
			prm.setRetryTimeout( numberFrom(
				retryTimeout, prm.getRetryTimeout() ) );
			prm.setCallSign( call.getText().toUpperCase() );
			prm.setPassword( pass.getText().toUpperCase() );
			prm.setUserName( name.getText() );
			prm.setQTH( loc.getText() );
			prm.setEmail( email.getText() );
			prm.setUserMode( isUser.isSelected() );
			prm.setConnectAttemptTimeout( numberFrom(
				connAtt, prm.getConnectAttemptTimeout() ) );
			prm.setPTTTimeout( numberFrom( 
				transTime, prm.getPTTTimeout() ) );
			prm.setInactiveTimeout( numberFrom(
				inactTime, prm.getInactiveTimeout() ) );
			prm.setReceiveTimeLimit( numberFrom( 
				rcvTimeLimit, prm.getReceiveTimeLimit() ) );
			prm.setReceiveHangTimeout( numberFrom( 
				rcvHang, prm.getReceiveHangTimeout() ) );
			prm.setAudioTrace( trcAud.isSelected() );
//			prm.setHoldAudioResources( hldAud.isSelected() );
			prm.setAudioAmplification(
				((Number)audAmp.getValue()).doubleValue() );
			prm.setRaiseOnMutedContact( raiseMuted.isSelected() );
			prm.setBeeping( bellOnError.isSelected() );
			prm.setDataPort( Integer.parseInt( dataPort.getText() ) );
			prm.setControlPort( Integer.parseInt( controlPort.getText() ) );
			prm.setLatLon( latLon.getText() );
			prm.setAudioDevice( aubx.getSelectedIndex() );
			prm.setUseSelectedAudio( useSelAudio.isSelected() );
			prm.setKMLPort( Integer.parseInt( kmlport.getText() ) );
			prm.setKMLBindAddress( kmlsrvr.getText() );
			prm.saveData();
		}
	}
	
	int numberFrom( JTextField fld, int old ) {
		try {
			return Integer.parseInt( fld.getText() );
		} catch( NumberFormatException ex ) {
			je.reportException(ex);
			return old;
		}
	}

	void setupHelp() {
		Runnable r = new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog( je, "Help not implemented yet",
					"Help not available", JOptionPane.INFORMATION_MESSAGE );
			}
		};
		if( SwingUtilities.isEventDispatchThread() ) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait( r );
			} catch( Exception ex ) {
			}
		}
	}

	JTextField kmlport, kmlsrvr;
	JPanel buildKML() {
		JPanel p = new JPanel();
		Packer pk = new Packer(p);
	
		int y = -1;
		JPanel bp = new JPanel();
		Packer bpk = new Packer( bp );
		bp.setBorder( BorderFactory.createTitledBorder( "KML Server Bind Information") );
		bpk.pack( new JLabel("Address: ") ).gridx(0).gridy(0);
		bpk.pack( kmlport = new JTextField(prm.getKMLPort()+"") ).gridx(1).gridy(0).fillx();
		bpk.pack( new JLabel("Port; ") ).gridx(0).gridy(1);
		bpk.pack( kmlsrvr = new JTextField(prm.getKMLBindAddress()) ).gridx(1).gridy(1).fillx();
		kmlport.setEnabled(false);
		kmlsrvr.setEnabled(false);
		pk.pack( bp ).gridx(0).gridy(0).fillx();
		
		JPanel lp = new JPanel();
		Packer lpk = new Packer( lp );
		lpk.pack( new JScrollPane( new JTextArea() ) ).gridx(0).gridy(1).fillboth();
		lp.setBorder( BorderFactory.createTitledBorder( "Connection Log" ) );
//		pk.pack( new JPanel() ).gridx(0).gridy(100).filly();
		return p;
	}

	JCheckBox trcAud, hldAud, raiseMuted, bellOnError;
	JSpinner audAmp;
	JTextField dataPort, controlPort, latLon;
	JPanel buildAdvanced() {
		JPanel p = new JPanel();
		Packer pk = new Packer(p);
	
		int y = -1;
		pk.pack( trcAud = new JCheckBox( "Trace Audio Processing" ) 
			).gridx(0).gridy(++y).fillx().gridw(2);
		trcAud.setSelected( prm.isAudioTrace() );
		hldAud = new JCheckBox( "Hold Audio Resources" );
//		pk.pack( hldAud ).gridx(0).gridy(++y).fillx().gridw(2);
		pk.pack( new JSeparator()
			).gridx(0).gridy(++y).gridw(2).fillx().inset(4,4,4,4);
		pk.pack( raiseMuted = new JCheckBox( "To Front When Muted And Audio" ) 
			).gridx(0).gridy(++y).fillx().gridw(2);
		pk.pack( bellOnError = new JCheckBox( "Use Bell for Errors" )
			).gridx(0).gridy(++y).fillx().gridw(2);
		raiseMuted.setSelected( prm.isRaiseOnMutedContact());
//		hldAud.setSelected(prm.isHoldAudioResources() );
		bellOnError.setSelected( prm.isBeeping() );
		pk.pack( new JSeparator() 
			).gridx(0).gridy(++y).gridw(2).fillx().inset(4,4,4,4);
		pk.pack( new JLabel("Audio Amplification" ) ).gridx(0).gridy(++y);
		pk.pack( audAmp = new JSpinner( new SpinnerNumberModel(
			prm.getAudioAmplification(), 0, 5, .02 ) ) ).gridx(1).gridy(y).fillx();

		pk.pack( new JSeparator() 
			).gridx(0).gridy(++y).gridw(2).fillx().inset(4,4,4,4);
		pk.pack( new JLabel( "Data Port:" ) ).gridx(0).gridy(++y).east();
		pk.pack( dataPort = new JTextField() 
			).gridx(1).gridy(y).fillx();
		pk.pack( new JLabel( "Control Port:" ) 
			).gridx(0).gridy(++y).east();
		pk.pack( controlPort = new JTextField() 
			).gridx(1).gridy(y).fillx();
		dataPort.setText( prm.getDataPort() +"" );
		controlPort.setText( prm.getControlPort() +"" );
		
		pk.pack( new JSeparator() 
			).gridx(0).gridy(++y).gridw(2).fillx().inset(4,4,4,4);
		pk.pack( new JLabel( "Station Location (lat/lon):" )
			).gridx(0).gridy(++y).east();
		pk.pack( latLon = new JTextField() ).gridx(1).gridy(y).fillx();
		latLon.setToolTipText( "Format is ddmm.mm[NS]/dddmm.mm[WE]" );
		latLon.setText( prm.getLatLon() );

		pk.pack( new JSeparator() ).gridx(0).gridy(++y).
			gridw(2).fillx().inset(4,4,4,4);
		pk.pack( new JPanel() ).gridx(0).gridy(++y).filly();
		
		return p;
	}


	JTextField call, pass, name, loc, email;
	JRadioButton isUser, isSysop;
	JPanel buildMyStation() {
		JPanel p = new JPanel();
		Packer pk = new Packer(p);

		JPanel mp = new JPanel();
		Packer mpk = new Packer( mp );
		mp.setBorder( BorderFactory.createTitledBorder("Mode") );
		mpk.pack( isUser = new JRadioButton("Single-User") 
			).gridx(0).gridy(0).inset(0,0,0,20);
		mpk.pack( isSysop = new JRadioButton("Sysop") ).gridx(1).gridy(0);
		ButtonGroup grp = new ButtonGroup();
		isUser.setEnabled(true);
		//isSysop.setEnabled(false);
		String c = prm.getCallSign();
		isSysop.setEnabled( c != null && ( je.isSysopModeAvailable() &&
			( c.endsWith("-L") || c.endsWith("-R") ) ) );
		isSysop.setToolTipText( isSysop.isEnabled() ?
			"Select to Put Link or Repeater in Sysop Mode" :
			"Only Link and Repeater Nodes can use Sysop Mode" );
		grp.add(isUser);
		grp.add(isSysop);
//		isSysop.setToolTipText( "Sysop Mode not implement yet" );
		isUser.setSelected( prm.isUserMode() );
		isSysop.setSelected( !prm.isUserMode() );

		pk.pack( mp ).gridx(0).gridy(0);
		JPanel sp = new JPanel();
		pk.pack( sp ).gridx(0).gridy(1).fillboth();
		Packer spk = new Packer( sp );
		int y = -1;
		spk.pack( new JLabel("Callsign:",JLabel.LEFT) 
			).gridx(0).gridy(++y).west();

		call = new JTextField(prm.getCallSign());
		pass = new JPasswordField(prm.getPassword());
		name = new JTextField(prm.getUserName());
		loc = new JTextField(prm.getQTH());
		email = new JTextField(prm.getEmail());
		call.setEditable(first);

		final JButton 
			change = new JButton("Change Call"),
			showPass = new JButton("?");
			showPass.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					JOptionPane.showMessageDialog( SetupDialog.this, 
						"Current Password: \""+pass.getText()+"\"",
						"Current Password", 
						JOptionPane.INFORMATION_MESSAGE);
				}
			});

		spk.pack( call ).gridx(1).gridy(y).fillx();
		spk.pack( change ).gridx(2).gridy(y);
		change.setEnabled( !first );

		change.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				final String ocall = prm.getCallSign();
				final boolean wasNode = 
					ocall.endsWith("-L") || ocall.endsWith("-R");
				final String ncall = JOptionPane.showInputDialog(
					je, "Change Call To?", call.getText() );
				if( ncall != null ) {
					new ComponentUpdateThread( change ) {
						public Object construct() {
							try {
								je.serverAccess().sendLogoff();
							} catch( Exception ex ) {
								je.reportException(ex);
							}
							prm.setCallSign( ncall );
							call.setText( ncall );
							try {
								je.serverAccess().sendLogon(false);
							} catch( Exception ex ) {
								je.reportException(ex);
							}
							try {
								boolean isNode = ncall.endsWith("-L") || 
									ncall.endsWith("-R");
								if( wasNode && !isNode && isSysop.isSelected() ) {
									isUser.setSelected(true);
									isSysop.setEnabled(false);
									isSysop.setToolTipText( 
										"Only Links and Repeaters can use Sysop mode");
								} else if( !wasNode && isNode && je.isSysopModeAvailable() ) {
									isSysop.setEnabled(true);
									isSysop.setToolTipText(
										"Select to enable sysop mode from nodes" );
								}
							} catch( Exception ex ) {
								je.reportException(ex);
							}
							return null;
						}
					}.start();
				}
			}
		});
		spk.pack( new JLabel("Password:",JLabel.LEFT) ).gridx(0).gridy(++y).west();
		spk.pack( pass ).gridx(1).gridy(y).fillx();
		spk.pack( showPass ).gridx(2).gridy(y);
		
		spk.pack( new JLabel("Name:",JLabel.LEFT) ).gridx(0).gridy(++y).west();
		spk.pack( name ).gridx(1).gridy(y).fillx();

		spk.pack( new JLabel("Location:",JLabel.LEFT) ).gridx(0).gridy(++y).west();
		spk.pack( loc ).gridx(1).gridy(y).fillx();

		spk.pack( new JLabel("Email Addr:",JLabel.LEFT) ).gridx(0).gridy(++y).west();
		spk.pack( email ).gridx(1).gridy(y).fillx();
		return p;
	}

	JComboBox srvbx[];
	JTextField retryTimeout;
	JPanel buildServers() {
		JPanel p = new JPanel();
		Packer pk = new Packer(p);

		final Vector<String> lv = prm.getServerList();
		Vector<String> sv = prm.getServers();
		Vector<String> v = new Vector<String>();
		for( int i = 0; i < lv.size(); ++i ) {
			v.addElement( lv.elementAt(i) );
		}
		v.addElement("[none]");
		int y = -1;
		final JComboBox bx[] = new JComboBox[lv.size()];
		srvbx = bx;
		for( int i = 0; i < lv.size(); ++i ) {
			pk.pack( new JLabel( "Pref "+(i+1)+":" ) ).gridx(0).gridy(++y).west();
			bx[i] = new JComboBox( v );
			String s = prm.getServerN(i);
			if( s != null )
				bx[i].setSelectedItem( s );
			else
				bx[i].setSelectedIndex( lv.size() );
			pk.pack( bx[i] ).gridx(1).gridy(y).fillx().gridw(2);
			// Allow the last one to be changed to a different host
			if( i + 1 >= lv.size() ) {
				bx[i].setEditable(true);
			}
		}
		JButton reset = new JButton("Reset To Defaults...");
		reset.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int c = JOptionPane.showConfirmDialog( je, 
					"Reset to Default Server List?",
					"Reset Confirmation", 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE );
				if( c == JOptionPane.YES_OPTION ) {
					for( int i = 0; i < bx.length; ++i ) {
						bx[i].setSelectedItem( lv.elementAt(i) );
						bx[i].repaint();
					}
				}
			}
		});
		pk.pack( reset ).gridx(1).gridy(++y).gridw(2);
		pk.pack( new JLabel("Retry Timeout (sec)") ).gridx(1).gridy(++y);
		pk.pack( retryTimeout = new JTextField("10") ).gridx(2).gridy(y).fillx();
		
		return p;
	}

	JTextField connAtt, transTime, inactTime, rcvTimeLimit, rcvHang;
	JPanel buildTiming() {
		JPanel p = new JPanel();
		Packer pk = new Packer(p);

		int y = -1;
		pk.pack( new JPanel() ).gridx(0).gridy(++y).filly();

		pk.pack( new JLabel("Connect Attempt:") ).gridx(0).gridy(++y).west();
		pk.pack( connAtt = new JTextField(prm.getConnectAttemptTimeout()+""
			) ).gridx(1).gridy(y).fillx();
		pk.pack( new JLabel("sec") ).gridx(2).gridy(y).west();

		pk.pack( new JLabel("Transmit Timeout:") ).gridx(0).gridy(++y).west();
		pk.pack( transTime = new JTextField(prm.getPTTTimeout()+""
			) ).gridx(1).gridy(y).fillx();
		pk.pack( new JLabel("sec") ).gridx(2).gridy(y).west();

		pk.pack( new JLabel("Receive Time Limit:") ).gridx(0).gridy(++y).west();
		pk.pack( rcvTimeLimit = new JTextField(prm.getReceiveTimeLimit()+""
			) ).gridx(1).gridy(y).fillx();
		pk.pack( new JLabel("sec") ).gridx(2).gridy(y).west();

		pk.pack( new JLabel("Inactivity Timeout:") ).gridx(0).gridy(++y).west();
		pk.pack( inactTime = new JTextField(prm.getInactiveTimeout()+""
			) ).gridx(1).gridy(y).fillx();
		pk.pack( new JLabel("sec") ).gridx(2).gridy(y).west();

		pk.pack( new JLabel("Receive Hang Time:") ).gridx(0).gridy(++y).west();
		pk.pack( rcvHang = new JTextField(prm.getReceiveHangTimeout()+""
			) ).gridx(1).gridy(y).fillx();
		pk.pack( new JLabel("msec") ).gridx(2).gridy(y).west();

		pk.pack( new JPanel() ).gridx(0).gridy(++y).filly();

		return p;
	}

	JComboBox aubx;
	JCheckBox fulldup;
	JCheckBox useSelAudio;
	JPanel buildAudio() {
		final JPanel p = new JPanel();
		Packer pk = new Packer(p);

		int y = -1;
		pk.pack( useSelAudio = new JCheckBox( "Select Sound Device:" ) 
			).gridx(0).gridy(++y).west();
		useSelAudio.setSelected( prm.useSelectedAudio() );
		DataLine.Info info = null, sinfo = null;
 		AudioFormat format = new AudioFormat( 8000,16,1,true,true );
       	info = new DataLine.Info( TargetDataLine.class, format );
       	sinfo = new DataLine.Info( SourceDataLine.class, format );
        if( AudioSystem.isLineSupported(info) == false ) {
        	System.out.println( info+": target line not supported");
		} else {
			System.out.println("using "+info+" target line type" );
		}
        if( AudioSystem.isLineSupported(sinfo) == false ) {
        	System.out.println( sinfo+": source line not supported");
		} else {
			System.out.println("using "+sinfo+" source line type" );
		}
        aubx = new JComboBox();
        aubx.setEnabled( useSelAudio.isSelected() );
        useSelAudio.addActionListener( new ActionListener() {
        	public void actionPerformed( ActionEvent ev ) {
		        aubx.setEnabled( useSelAudio.isSelected() );
        	}
        });
	
        Mixer.Info infos[] = AudioSystem.getMixerInfo();
        Vector<String> ab = new Vector<String>();
        if( infos.length == 0 ) {
        	String os = System.getProperty("os.name");
        	boolean linux = os.toLowerCase().indexOf("linux") >= 0;
        	JOptionPane.showMessageDialog( je,
        		"No Audio Mixer's were found"+
        		(linux?"\n\nIs ALSA audio installed?":
        		"\n\nDo you have a sound card installed?"),
        		"Audio Detect Failed",
        		JOptionPane.ERROR_MESSAGE );
        }
        for( int i = 0; i < infos.length; ++i ) {
        	try {
        		Mixer mix = AudioSystem.getMixer( infos[i] );
        		if( !nonEmptyLines( mix.getSourceLineInfo( sinfo ), true )
        				) {
        			continue;
        		}
	        	String ln = mix.getMixerInfo().getName();
        		if( ab.contains(ln) == false ) {
        			aubx.addItem( i+": "+ln );
        			ab.addElement(ln);
        		}
        	} finally {
        	}
        }
        
//        System.out.println( "Format = "+format );
        int iii = prm.getAudioDevice();
        try {
        	aubx.setSelectedIndex( iii );
        } catch( Exception ex ) {
        }

		pk.pack( aubx ).gridx(1).gridy(y).fillx().gridw(2);

		pk.pack( fulldup = new JCheckBox( "Open in Full Duplex" ) 
			).gridx(1).gridy(++y).fillx().gridw(2);
		fulldup.setEnabled(false);
		fulldup.setToolTipText( "No Supported");
		pk.pack( new JPanel()).gridx(0).gridy(++y).inset( 20, 0, 0, 0 );

		Hashtable<Integer,JLabel> h = new Hashtable<Integer,JLabel>();
		h.put( new Integer( 1 ), new JLabel("Min") );
		h.put( new Integer( 10 ), new JLabel("Max") );

		pk.pack( new JLabel( "Network Buffering" ) ).gridx(0).gridy(++y).west();
		final JSlider netbuf = new JSlider( 1, 20, prm.getNetBuffering() );
		final JLabel netval = new JLabel( netbuf.getValue()+"" );
		netval.setBorder( BorderFactory.createEtchedBorder() );
		netval.setFont( new Font( "serif", Font.PLAIN, 18 ) );
		netbuf.setMinorTickSpacing(1);
		netbuf.setMajorTickSpacing(1);
		netbuf.setPaintTrack(true);
		netbuf.setSnapToTicks(true);
		netbuf.setLabelTable( new Hashtable() );
		netbuf.setPaintTicks(true);
		netbuf.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent ev ) {
				netval.setText(""+(netbuf.getValue()));
				netval.revalidate();
				p.revalidate();
			}
		});
//		netbuf.setPaintLabels(true);
		pk.pack( netbuf ).gridx(1).gridy(y).fillx();
		pk.pack( netval ).gridx(2).gridy(y).fillx().weightx(0).inset(0,10,0,0);

		pk.pack( new JLabel( "PC Buffering" ) ).gridx(0).gridy(++y).west();
		final JSlider pcbuf = new JSlider( 1, 10, prm.getPCBuffering() );
		final JLabel pcval = new JLabel(pcbuf.getValue()+"");
		pcval.setFont( new Font( "serif", Font.PLAIN, 18 ) );
		pcval.setBorder( BorderFactory.createEtchedBorder() );
		pcbuf.setMinorTickSpacing(1);
		pcbuf.setMajorTickSpacing(1);
		pcbuf.setLabelTable( h );
		pcbuf.setSnapToTicks(true);
		pcbuf.setPaintTrack(true);
		pcbuf.setPaintTicks(true);
		pcbuf.setPaintLabels(true);
		pcbuf.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent ev ) {
				pcval.setText(""+(pcbuf.getValue()));
				pcval.revalidate();
				p.revalidate();
			}
		});
		pk.pack( pcbuf ).gridx(1).gridy(y).fillx();
		pk.pack( pcval ).gridx(2).gridy(y).fillx().weightx(0).inset(0,10,0,0);

		return p;
	}
	
	boolean nonEmptyLines( Line.Info[] lines, boolean src ) {
//		System.out.println("lines(src="+src+"): "+lines );
		if( lines == null )
			return false;
//		System.out.println("lines(src="+src+") #"+lines.length);
		return lines.length > 0;
	}
}