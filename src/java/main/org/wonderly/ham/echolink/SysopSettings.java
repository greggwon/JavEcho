package org.wonderly.ham.echolink;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import org.wonderly.awt.*;
import org.wonderly.swing.*;
import java.util.*;
import java.io.*;
import java.util.prefs.*;
import java.util.logging.*;
import org.wonderly.swing.prefs.*;
import javax.swing.filechooser.FileFilter;
import javax.comm.*;

/**
 *  This dialog encapsulats all the sysop mode settings.  It manipulates
 *  The preferences node structure under the userNode identified by
 *  this classname and then the current callsign.  This keeps the settings
 *  specific to a particular link/repeater.
 * <p>
 *  <b>Sysop Setting Prefernces Nodes</b>
 *  <br>
 *  <b>Root:</b> <i>"org/wonderly/ham/echolink/SysopSettings/"+<code>Javecho.getParms().getCallSign()<code></i>
<table border="1">
<tr><th colspan="3">DTMF
<tr><td><td>enableremotepad<td>false</td>
<tr><td><td>disabled<td>false</td>
<tr><td><td>deadkeyprefix<td>10</td>
<tr><td><td>mininterdigittime<td>0</td>
<tr><td><td>external<td>true</td>
<tr><td><td>usedeadkeyprefix<td>false</td>
<tr><td><td>internal<td>false</td>
<tr><td><td>disableduringptt<td>false</td>
<tr><td><td>logall<td>false</td>
<tr><td><td>automute<td>false</td>
<tr><td><td>snratio<td>12</td>
<tr><td><td>twistdb<td>4</td>
<tr><td><td>finetuning<td>0</td>
<tr><td><td>freqtolerance<td>2</td>

<tr><th colspan="3">DTMF/actions
<tr><td><td>connect<td></td>
<tr><td><td>connectbycall<td></td>
<tr><td><td>disconnect<td></td>
<tr><td><td>linkdown<td></td>
<tr><td><td>linkup<td></td>
<tr><td><td>listenonlyoff<td></td>
<tr><td><td>listenonlyon<td></td>
<tr><td><td>playinfo<td></td>
<tr><td><td>profileselect<td></td>
<tr><td><td>querybycall<td></td>
<tr><td><td>querybynode<td></td>
<tr><td><td>randomconf<td></td>
<tr><td><td>randomfavconf<td></td>
<tr><td><td>randomfavlink<td></td>
<tr><td><td>randomfavnode<td></td>
<tr><td><td>randomfavuser<td></td>
<tr><td><td>randomlink<td></td>
<tr><td><td>randomnode<td></td>
<tr><td><td>randomuser<td></td>
<tr><td><td>reconnect<td></td>
<tr><td><td>status<td></td>

<tr><th colspan="3">DTMF/shortcuts
<tr><td><td>count<td>1</td>

<tr><th colspan="3">DTMF/shortcuts/0
<tr><td><td>call<td>w5ggw</td>
<tr><td><td>code<td>21</td>

<tr><th colspan="3">Ident
<tr><td><td>whileactive<td>false</td>
<tr><td><td>eachconnect<td>false</td>
<tr><td><td>eachdisconnect<td>false</td>
<tr><td><td>whileinactive<td>false</td>
<tr><td><td>waitclearfreq<td>false</td>
<tr><td><td>endxmit<td>false</td>

<tr><th colspan="3">Ident/audio
<tr><td><td>file<td>W5GGW.wav</td>
<tr><td><td>on<td>false</td>

<tr><th colspan="3">Ident/endxmit
<tr><td><td>time<td>10</td>

<tr><th colspan="3">Ident/morse
<tr><td><td>on<td>false</td>
<tr><td><td>id<td>W5GGW</td>
<tr><td><td>pitch<td>438</td>
<tr><td><td>volume<td>-146</td>
<tr><td><td>speed<td>20</td>

<tr><th colspan="3">Ident/speech
<tr><td><td>on<td>true</td>
<tr><td><td>id<td>W5GGW</td>

<tr><th colspan="3">Ident/whileactive
<tr><td><td>time<td>6</td>

<tr><th colspan="3">Ident/whileinactive
<tr><td><td>time<td>10</td>

<tr><th colspan="3">Info
<tr><td><td>haat<td>0</td>
<tr><td><td>frequency<td></td>
<tr><td><td>pltone<td></td>
<tr><td><td>directivity<td>0</td>
<tr><td><td>antennagain<td>0</td>
<tr><td><td>powerout<td>0</td>

<tr><th colspan="3">Info/Lat
<tr><td><td>deg<td>00</td>
<tr><td><td>min<td>00.00</td>
<tr><td><td>region<td>0</td>

<tr><th colspan="3">Info/Lon
<tr><td><td>min<td>00.00</td>
<tr><td><td>region<td>0</td>
<tr><td><td>deg<td>00</td>

<tr><th colspan="3">Info/Report

<tr><th colspan="3">Info/Report/APRS
<tr><td><td>unproto<td>0</td>
<tr><td><td>autoinit<td>false</td>
<tr><td><td>comment<td></td>
<tr><td><td>tncport<td>0</td>
<tr><td><td>on<td>false</td>
<tr><td><td>statustext<td>false</td>

<tr><th colspan="3">Options
<tr><td><td>playwelcomeonconnect<td>false</td>
<tr><td><td>playcourtesytone<td>false</td>
<tr><td><td>maxkeydown<td>0</td>
<tr><td><td>includecallsign<td>true</td>
<tr><td><td>deadcarrier<td>0</td>
<tr><td><td>welcomeMessageFile<td>welcome.wav</td>

<tr><th colspan="3">Options/announce
<tr><td><td>predelay<td>150</td>
<tr><td><td>contacts<td>2</td>
<tr><td><td>disconnects<td>2</td>
<tr><td><td>muting<td>0</td>

<tr><th colspan="3">Options/reminder
<tr><td><td>play<td>false</td>
<tr><td><td>intervalsecs<td>120</td>

<tr><th colspan="3">Remote

<tr><th colspan="3">Remote/dialin
<tr><td><td>password<td></td>
<tr><td><td>monitor<td>false</td>
<tr><td><td>port<td>0</td>
<tr><td><td>level<td>0</td>
<tr><td><td>timeout<td>30</td>
<tr><td><td>answeronring<td>1</td>
<tr><td><td>active<td>false</td>

<tr><th colspan="3">Remote/web
<tr><td><td>user<td></td>
<tr><td><td>tcpport<td>8080</td>
<tr><td><td>passwd<td></td>
<tr><td><td>access<td>false</td>

<tr><th colspan="3">RxCtrl
<tr><td><td>antitrip<td>false</td>
<tr><td><td>serialcd<td>false</td>
<tr><td><td>vox<td>true</td>
<tr><td><td>antithump<td>500</td>
<tr><td><td>voxdelay<td>1000</td>
<tr><td><td>serialport<td>0</td>
<tr><td><td>invertsense<td>false</td>
<tr><td><td>serialdsr<td>false</td>
<tr><td><td>duration<td>50</td>
<tr><td><td>serialcts<td>false</td>
<tr><td><td>manual<td>false</td>
<tr><td><td>clearfreqdelay<td>3000</td>

<tr><th colspan="3">Signals

<tr><th colspan="3">Signals/events
<tr><td><td>selected<td>0</td>

<tr><th colspan="3">Signals/events/Activity Reminder
<tr><td><td>default<td>true</td>
<tr><td><td>msg<td>Activity Reminder.wav</td>

<tr><th colspan="3">Signals/events/Connected
<tr><td><td>default<td>true</td>
<tr><td><td>msg<td>Connected.wav</td>

<tr><th colspan="3">Signals/events/Courtesy Tone
<tr><td><td>default<td>true</td>
<tr><td><td>msg<td>Courtesy Tone.wav</td>

<tr><th colspan="3">Signals/events/Disconnected
<tr><td><td>default<td>true</td>
<tr><td><td>msg<td>Disconnected.wav</td>

<tr><th colspan="3">Signals/events/Link Down
<tr><td><td>default<td>true</td>
<tr><td><td>msg<td>Link Down.wav</td>

<tr><th colspan="3">Signals/events/Link Up
<tr><td><td>default<td>true</td>
<tr><td><td>msg<td>Link Up.wav</td>

<tr><th colspan="3">Signals/events/Station Info
<tr><td><td>default<td>true</td>
<tr><td><td>msg<td>Station Info.wav</td>

<tr><th colspan="3">Signals/speech
<tr><td><td>speed<td>1</td>

<tr><th colspan="3">Signals/toneburst
<tr><td><td>duration<td>500</td>
<tr><td><td>send<td>0</td>
<tr><td><td>freq<td>0</td>

<tr><th colspan="3">TxCtrl
<tr><td><td>serialspeed<td>false</td>
<tr><td><td>serialport<td>0</td>
<tr><td><td>keyonlocal<td>false</td>

<tr><th colspan="3">TxCtrl/ptt
<tr><td><td>rts<td>false</td>
<tr><td><td>vox<td>false</td>
<tr><td><td>asciiserial<td>true</td>
<tr><td><td>dtr<td>false</td>

</table>
 */
public class SysopSettings extends JDialog {
	private Javecho je;
	private Preferences prefs;
	private SwingPreferencesMapper pm;
	private Logger log = Logger.getLogger( "org.wonderly.ham.javecho.sysop" );
	private String station;

	public SysopSettings( Javecho jav, Parameters parms ) {
		super( jav, "Sysop Settings", true );
		je = jav;
		prefs = Preferences.userNodeForPackage( getClass() );
		prefs = prefs.node("SysopSettings");
		if( jav != null )
			prefs = prefs.node( station = jav.getParms().getCallSign() );
		else
			prefs = prefs.node( station = "W5GGW");
		pm = new SwingPreferencesMapper( prefs );
//		log.setLevel( Level.FINEST );
//		if( log.getHandlers().length == 0 ) {
//			log.getParent().setLevel(Level.FINEST);
//		} else {
//			log.getHandlers()[0].setLevel( Level.FINEST );
//		}
		bldDialog(getContentPane());
		try {
			prefs.flush();
		} catch( Exception ex ) {
			log.log( Level.SEVERE, ex.toString(), ex );
		}
	}

	public static void main( String args[] ) {
		SysopSettings ss = new SysopSettings( null, null );
		ss.addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent ev ) {
				System.exit(3);
			}
		});
	}

	private JButton okay, cancel;
	private void bldDialog( Container p ) {
		Packer pk = new Packer( p );
		JTabbedPane tabs = new JTabbedPane();
		pk.pack( tabs ).gridx(0).gridy(0).fillboth().gridw(2);
		tabs.add( "Receive", bldRxCtrl() );
		tabs.add( "Transmit", bldTxCtrl() );
		tabs.add( "DTMF", bldDTMF() );
		tabs.add( "Ident", bldIdent() );
		tabs.add( "Options", bldOptions() );
		tabs.add( "Signals", bldSignals() );
		tabs.add( "Remote", bldRemote() );
		tabs.add( "RF Info", bldRFInfo() );
		pk.pack( okay = new JButton("Okay") ).gridx(0).gridy(1).west();
		okay.requestFocus();
		pk.pack( cancel = new JButton("Cancel") ).gridx(1).gridy(1).east();
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
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent ev ) {
				cancelled[0] = true;
			}
		});
		pack();
		setSize( 400, 500 );
		setLocationRelativeTo( je );
		okay.requestFocus();
		setVisible(true);
		log.info("Dialog closed, cancelled: " + cancelled[0] );

		if( cancelled[0] == false ) {
			// store values back in preferences.
			log.info("Committing Preferences");
			pm.commit();
		}
	}
	
	private class GrayedTextField extends JTextField {
		public GrayedTextField( String val ) {
			super(val);
		}
		public GrayedTextField() {
			super();
		}
		public void setEnabled( boolean how ) {
			super.setEnabled(how);
			setOpaque(how);
		}
	}

	private class NumericSpinner extends JSpinner {
		SpinnerNumberModel mod;
		public NumericSpinner() {
			super( new SpinnerNumberModel() );
			mod = (SpinnerNumberModel)getModel();
		}
		public NumericSpinner(int val) {
			super( new SpinnerNumberModel() );
			mod = (SpinnerNumberModel)getModel();
			mod.setValue( new Integer( val ) );
		}
		public void setEnabled( boolean how ) {
			super.setEnabled(how);
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)getEditor();
			editor.getTextField().setOpaque(how);
		}
		public int getIntValue() {
			return ((Integer)mod.getValue()).intValue();
		}
		public void setIntValue( int val ) {
			mod.setValue( new Integer( val ) );
		}
	}

	private JRadioButton manual, vox, serialCD, serialCTS, serialDSR;
	private NumericSpinner voxDelay, antiThump, clrFreqDelay;
	private JComboBox serialPort;
	private JCheckBox invertSense;
	private JCheckBox antiTrip;
	private NumericSpinner tripDuration;
	private JPanel bldRxCtrl() {
		JPanel p = new JPanel();
		Packer pk = new Packer( p );
		JPanel cdp = new JPanel();
		pk.pack( cdp ).gridx(0).gridy(0).fillx();
		Packer  cdpk = new Packer( cdp );
		cdp.setBorder( 
			BorderFactory.createTitledBorder("Carrier Detect") );

		JPanel cmod = new JPanel();
		Packer cmpk = new Packer( cmod );
		ButtonGroup cmodgrp = new ButtonGroup();
		int by[] = new int[]{-1};
		manual = vertRadio( "Manual", cmpk, cmodgrp, by );
		pm.map( "RxCtrl/manual", false, manual );
		vox = vertRadio( "VOX", cmpk, cmodgrp, by );
		pm.map( "RxCtrl/vox", true, vox );
		serialCD = vertRadio( "Serial CD", cmpk, cmodgrp, by );
		pm.map( "RxCtrl/serialcd", false, serialCD );
		serialCTS = vertRadio( "Serial CTS", cmpk, cmodgrp, by );
		pm.map( "RxCtrl/serialcts", false, serialCTS );
		serialDSR = vertRadio( "Serial DSR", cmpk, cmodgrp, by );
		pm.map( "RxCtrl/serialdsr", false, serialDSR );
		cdpk.pack( cmod ).gridx(0).gridy(0).filly().inset(10,10,10,10);
		

		JPanel cdelay = new JPanel();
		Packer cdlpk = new Packer( cdelay );
		int y = -1;
		JLabel l;

		cdlpk.pack( l = new JLabel( "Vox Delay (ms):" ) 
			).gridx(0).gridy(++y).east();
		cdlpk.pack( voxDelay = new NumericSpinner() 
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( voxDelay );
		pm.map( "RxCtrl/voxdelay", 1000, voxDelay );

		cdlpk.pack( l= new JLabel( "Anti-Thump (ms):" ) 
			).gridx(0).gridy(++y).east();
		cdlpk.pack( antiThump = new NumericSpinner() 
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( antiThump );
		pm.map( "RxCtrl/antithump", 500, antiThump );

		cdlpk.pack( l=new JLabel( "ClrFreq Delay (ms):" ) 
			).gridx(0).gridy(++y).east();
		cdlpk.pack( clrFreqDelay = new NumericSpinner() 
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( clrFreqDelay );
		pm.map( "RxCtrl/clearfreqdelay", 3000, clrFreqDelay );

		cdlpk.pack( l=new JLabel( "Serial Port:" )
			).gridx(0).gridy(++y).east();
		cdlpk.pack( serialPort = new JComboBox()
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( serialPort );
		fillSerialPorts( serialPort );
		pm.map( new PrefManager<Component>() {
			public boolean prepare( Component comp ) {
				return true;
			}
			public boolean commit( Component comp ) {
				String prt = (String)((JComboBox)comp).getSelectedItem();
				if( prt == null )
					prt = "";
				prefs.put( "RxCtrl/serialport",
					prt );
				return true;
			}
			public void setValueIn( Component comp ) {
				JComboBox box = (JComboBox)comp;
				box.setSelectedItem( 
					(String)prefs.get("RxCtrl/serialport","") );
				if( box.getSelectedIndex() == -1 &&
						box.getItemCount() > 0 ) {
					box.setSelectedIndex(0);
				}
			}
		}, serialPort );

		cdlpk.pack( invertSense = new JCheckBox( "Invert Sense") 
			).gridx(1).gridy(++y).fillx();
		pm.map( "RxCtrl/invertsense", false, invertSense );

		cdpk.pack( cdelay 
			).gridx(1).gridy(0).fillboth().inset(10,10,10,10);

		JPanel sqpan = new JPanel();
		sqpan.setBorder(
			BorderFactory.createTitledBorder( "Squelch Crash" ) );
		Packer sqpk = new Packer( sqpan );

		sqpk.pack( antiTrip = new JCheckBox("Anti-Trip" )
			).gridx(0).gridy(0).fillx();
		pm.map( "RxCtrl/antitrip", false, antiTrip );

		sqpk.pack( l = new JLabel("Duration:") ).gridx(1).gridy(0);
		sqpk.pack( tripDuration = new NumericSpinner()
			).gridx(2).gridy(0).fillx();
		l.setLabelFor( tripDuration );
		pm.map( "RxCtrl/duration", 50, tripDuration );

		ActionListener lis = new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				rxTypeSel();
			}
		};
		rxTypeSel();
		ModalComponent tripComp = new ModalComponent( antiTrip );
		tripComp.add(tripDuration);
		tripComp.add(l);
		tripComp.configure();

		manual.addActionListener( lis );
		vox.addActionListener( lis );
		serialCD.addActionListener( lis );
		serialCTS.addActionListener( lis );
		serialDSR.addActionListener( lis );
		pk.pack( sqpan ).gridx(0).gridy(1).fillx();
		pk.pack( new JPanel() ).gridx(0).gridy(2).filly();
		return p;
	}

	private void rxTypeSel() {
		if( manual.isSelected() == true ) {
			voxDelay.setEnabled(false);
			antiThump.setEnabled(false);
			clrFreqDelay.setEnabled(false);
			serialPort.setEnabled(false);
			invertSense.setEnabled(false);
			antiTrip.setEnabled(false);
			tripDuration.setEnabled(false);
		} else if( vox.isSelected() ) {
			voxDelay.setEnabled(true);
			antiThump.setEnabled(true);
			clrFreqDelay.setEnabled(true);
			serialPort.setEnabled(false);
			invertSense.setEnabled(false);
			antiTrip.setEnabled(true);
			tripDuration.setEnabled(antiTrip.isSelected());
		} else {
			voxDelay.setEnabled(false);
			antiThump.setEnabled(false);
			clrFreqDelay.setEnabled(true);
			serialPort.setEnabled(true);
			invertSense.setEnabled(true);
			antiTrip.setEnabled(true);
			tripDuration.setEnabled(antiTrip.isSelected());
		}
	}

	private JRadioButton vertRadio( String name, 
			Packer pk, ButtonGroup grp, int y[] ) {
		JRadioButton b = new JRadioButton(name);
		grp.add(b);
		pk.pack( b ).gridx(0).gridy(++y[0]).fillx().weightx(0);
		return b;
	}

	private JCheckBox vertCheckBox( String name, Packer pk, int y[] ) {
		JCheckBox b = new JCheckBox(name);
		pk.pack( b ).gridx(0).gridy(++y[0]).fillx().weightx(0);
		return b;
	}

	public PTTControlParms getPttSettings() {
		return new PTTControlParms( pttVox.isSelected(), 
			pttAscii.isSelected() ? PTTControlParms.SerialPttType.ASCII :
			pttRTS.isSelected() ? PTTControlParms.SerialPttType.RTS :
				PTTControlParms.SerialPttType.DTR, rfSerPort.getSelectedItem().toString() );
	}

	public enum SerialPttType { ASCII, RTS, DTR };

	private JRadioButton pttVox, pttAscii, pttRTS, pttDTR;
	private JComboBox rfSerPort;
	private JCheckBox rfSpeedSel, rfKeyLocal;
	private JPanel bldTxCtrl() {
		JPanel p = new JPanel();
		Packer pk = new Packer( p );
		JPanel ptt = new JPanel();

		ptt.setBorder(
			BorderFactory.createTitledBorder( "PTT Activation" ) );
		Packer ptpk = new Packer( ptt );
		ButtonGroup ptgrp = new ButtonGroup();
		int by[] = new int[] { -1 };

		JPanel ptttype = new JPanel();
		ptpk.pack( ptttype
			).gridx(0).gridy(0).filly().inset(10,10,10,10);
		Packer pttpk = new Packer( ptttype );

		pttVox = vertRadio( "External VOX", pttpk, ptgrp, by );
		pm.map( "TxCtrl/ptt/vox", false, pttVox );
		ModalComponent txVoxComp = new ModalComponent( pttVox );

		pttAscii = vertRadio( "ASCII Serial", pttpk, ptgrp, by );
		pm.map( "TxCtrl/ptt/asciiserial", true, pttAscii );
		ModalComponent txAsciiComp = new ModalComponent( pttAscii );

		pttRTS = vertRadio( "RTS", pttpk, ptgrp, by );
		pm.map( "TxCtrl/ptt/rts", false, pttRTS );
		ModalComponent txRTSComp = new ModalComponent( pttRTS );

		pttDTR = vertRadio( "DTR", pttpk, ptgrp, by );
		pm.map( "TxCtrl/ptt/dtr", false, pttDTR );
		ModalComponent txDTRComp = new ModalComponent( pttDTR );
		
		txVoxComp.relate( txAsciiComp );
		txVoxComp.relate( txRTSComp );
		txVoxComp.relate( txDTRComp );
//		
//		txAsciiComp.relate( txVoxComp );
//		txAsciiComp.relate( txRTSComp );
//		txAsciiComp.relate( txDTRComp );
//		
//		txRTSComp.relate( txAsciiComp );
//		txRTSComp.relate( txVoxComp );
//		txRTSComp.relate( txDTRComp );
//		
//		txDTRComp.relate( txAsciiComp );
//		txDTRComp.relate( txRTSComp );
//		txDTRComp.relate( txVoxComp );
		txVoxComp.configure();
		txAsciiComp.configure();
		txRTSComp.configure();
		txDTRComp.configure();

		pk.pack( ptt ).gridx(0).gridy(0).fillx();
		
		JLabel l;
		JPanel span = new JPanel();
		Packer sppk = new Packer( span );
		ptpk.pack( span 
			).gridx(1).gridy(0).fillboth().inset(10,10,10,10);
		sppk.pack( l = new JLabel("Serial Port:")
			).gridx(0).gridy(0).fillx();
		sppk.pack( rfSerPort = new JComboBox()
			).gridx(0).gridy(1).fillx();
		fillSerialPorts( rfSerPort );
		pm.map( new PrefManager<Component>() {
			public boolean prepare( Component comp ) {
				return true;
			}
			public boolean commit( Component comp ) {
				prefs.put( "TxCtrl/serialport",
					(String)((JComboBox)comp).getSelectedItem() );
				return true;
			}
			public void setValueIn( Component comp ) {
				JComboBox box = (JComboBox)comp;
				box.setSelectedItem( 
					(String)prefs.get("TxCtrl/serialport","") );
				if( box.getSelectedIndex() == -1 &&
						box.getItemCount() > 0 ) {
					box.setSelectedIndex(0);
				}
			}
		}, rfSerPort );
		l.setLabelFor( rfSerPort );
		txAsciiComp.add(l);
		txAsciiComp.add(rfSerPort);
		txRTSComp.add(l);
		txRTSComp.add(rfSerPort);
		txDTRComp.add(l);
		txDTRComp.add(rfSerPort);
//		pm.map( "TxCtrl/serialport", 0, rfSerPort );

		sppk.pack( rfSpeedSel = new JCheckBox( "9600 bps")
			).gridx(0).gridy(2).fillx();
		pm.map( "TxCtrl/serialspeed", false, rfSpeedSel );
		txAsciiComp.add(rfSpeedSel);
		
		pk.pack( rfKeyLocal = new JCheckBox("Key PTT on Local Transmit")
			).gridx(0).gridy(1).fillx();
		pm.map( "TxCtrl/keyonlocal", false, rfKeyLocal );
		txAsciiComp.configure();
		txRTSComp.configure();
		txDTRComp.configure();
		txVoxComp.configure();

		pk.pack( new JPanel() ).gridx(0).gridy(2).filly();
		return p;
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

	private void fillSerialPorts( JComboBox ports ) {
		try {
			Enumeration e =  CommPortIdentifier .getPortIdentifiers();
			Vector<String>v = new Vector<String>();
			while( e.hasMoreElements() ) {
				CommPortIdentifier cp = ( CommPortIdentifier )e.nextElement();
				if( cp.getPortType() == cp.PORT_SERIAL ) {
					v.addElement(cp.getName());
				}
			}
			
			Collections.sort( v );
			for( int i = 0; i < v.size(); ++i ) {
				ports.addItem( v.elementAt(i) );
			}
			//		ports.addItem( cp.getName() );
			
			ports.setEnabled(true);
			if(true)return;
			ports.addItem("COM1");
			ports.addItem("COM2");
			ports.addItem("COM3");
			ports.addItem("COM4");
		} catch( Exception ex ) {
			log.log( Level.SEVERE, ex.toString(), ex );
			runInSwing( new Runnable() {
				public void run() {
				}
			});
			ports.removeAllItems();
			ports.addItem("Serial Config Error?");
			ports.setEnabled(false);
		}
	}
	
	class DTMFAction {
		String act, dtmf;
		public DTMFAction( String act, String dtmf ) {
			this.act = act;
			this.dtmf = dtmf;
		}
	}

	private JRadioButton dtmfExt, dtmfInt, dtmfDis;
	private JCheckBox optLogAll, optAutoMute, optDisPTT, optRemPad;
	private JTable tonActs;
	private NumericSpinner minInter;
	private JCheckBox useDeadKeyPrefix;
	private JComboBox deadKeyPrefix;
	private JPanel bldDTMF() {
		JPanel p = new JPanel();
		Packer pk = new Packer( p );

		JPanel decoder = new JPanel();
		decoder.setBorder( 
			BorderFactory.createTitledBorder( "DTMF Decoder") );
		Packer dcpk = new Packer( decoder );
		ButtonGroup dtgrp = new ButtonGroup();
		int by[] = new int[]{ -1 };
		dtmfExt = vertRadio( "External", dcpk, dtgrp, by );
		pm.map( "DTMF/external", true, dtmfExt );

		dtmfInt = vertRadio( "Internal", dcpk, dtgrp, by );
		pm.map( "DTMF/internal", false, dtmfInt );

		dtmfDis = vertRadio( "Disabled", dcpk, dtgrp, by );
		pm.map( "DTMF/disabled", false, dtmfDis );

		pk.pack( decoder ).gridx(0).gridy(0).fillx().weightx(0);
		pk.pack( new JPanel() 
			).gridx(0).gridy(1).filly().weighty(0);

		JPanel p2 = new JPanel();
		pk.pack( p2 
			).gridx(1).gridy(0).fillboth().gridh(2).weighty(0);
		Packer ppk = new Packer( p2 );
		int y = -1;
		JLabel l;
		ppk.pack( l = new JLabel("Min Interdigit Time (ms):")
			).gridx(0).gridy(++y).inset(0,10,0,0);
		ppk.pack( minInter = new NumericSpinner()
			).gridx(1).gridy(y).fillx().inset(0,0,0,5);
		l.setLabelFor( minInter );
		pm.map( "DTMF/mininterdigittime", 0, minInter );
			
		JPanel p3 = new JPanel();
		by[0] = -1;
		Packer opk = new Packer( p3 );
		optLogAll = vertCheckBox( "Log All Commands", opk, by );
		pm.map( "DTMF/logall", false, optLogAll );

		optAutoMute = vertCheckBox( "Auto Mute", opk, by );
		pm.map( "DTMF/automute", false, optAutoMute );

		optDisPTT = vertCheckBox( "Disable During PTT", opk, by );
		pm.map( "DTMF/disableduringptt", false, optDisPTT );

		optRemPad = vertCheckBox( "Enable Remote Pad", opk, by );
		pm.map( "DTMF/enableremotepad", false, optRemPad );
		
		ppk.pack( p3 ).gridx(0).gridy(1).filly().weighty(0);

		final JButton adv = new JButton("Advanced..." );
		ppk.pack( adv 
			).gridx(1).gridy(1).fillx().weightx(0).inset(0,0,0,5);
		adv.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				advancedDTMF( pm );
			}
		});
		
		JPanel p4 = new JPanel();
		Packer fpk = new Packer( p4 );
		pk.pack( p4 ).gridx(0).gridy(2).gridw(2).fillboth();

		final DTMFAction dtmfActs[] = new DTMFAction[] {
			new DTMFAction( "Connect", "" ),
			new DTMFAction( "ConnectByCall", "C" ),
			new DTMFAction( "Disconnect",    "#" ),
			new DTMFAction( "DisconnectAll", "##" ),
			new DTMFAction( "LinkDown",      "" ),
			new DTMFAction( "LinkUp",        "" ),
			new DTMFAction( "ListenOnlyOff", "0510" ),
			new DTMFAction( "ListenOnlyOn",  "0511" ),
			new DTMFAction( "PlayInfo",      "*" ),
			new DTMFAction( "ProfileSelect", "B#" ),
			new DTMFAction( "QueryByCall",   "07" ),
			new DTMFAction( "QueryByNode",   "06" ),
			new DTMFAction( "RandomConf",    "02" ),
			new DTMFAction( "RandomFavConf", "021" ),
			new DTMFAction( "RandomFavLink", "011" ),
			new DTMFAction( "RandomFavNode", "001" ),
			new DTMFAction( "RandomFavUser", "031" ),
			new DTMFAction( "RandomLink",    "01" ),
			new DTMFAction( "RandomNode",    "00" ),
			new DTMFAction( "RandomUser",    "03" ),
			new DTMFAction( "Reconnect",     "09" ),
			new DTMFAction( "Status",        "08" )
		};

		final Vector<String> actions = new Vector<String>();
		
		fpk.pack( new JScrollPane( tonActs = 
				new JTable( new DefaultTableModel() {
			public int getColumnCount() {
				return 2;
			}
			public int getRowCount() {
				return dtmfActs.length;
			}
			public String getColumnName(int col) {
				return new String[] { "Function", "Sequence" }[col];
			}
			public Object getValueAt( int row, int col ) {
				switch(col) {
					case 0:
						return dtmfActs[row].act;
					case 1:
						return actions.elementAt(row);
				}
				return "???";
			}
			public void setValueAt( Object val, int row, int col ) {
				if( col == 1 ) {
					actions.setElementAt( val.toString(), row );
				}
			}
		} ) ) ).gridx(0).gridy(0).fillboth().gridh(6).inset( 10, 10, 10, 10 );
		pm.map( new PrefManager<Component>() {
			public boolean prepare( Component comp ) {
				return true;
			}
			public boolean commit( Component comp ) {
				Preferences pr = prefs.node("DTMF");
				pr = pr.node("actions");
				for( int i = 0; i < dtmfActs.length; ++i ) {
					pr.put( dtmfActs[i].act.toLowerCase(), actions.elementAt(i) );
				}
				return true;
			}
			public void setValueIn( Component comp ) {
				Preferences pr = prefs.node("DTMF");
				pr = pr.node("actions");
				actions.setSize( dtmfActs.length );
				for( int i = 0; i < dtmfActs.length; ++i ) {
					actions.setElementAt( pr.get( dtmfActs[i].act.toLowerCase(),
						dtmfActs[i].dtmf ), i );
				}
			}
		}, tonActs );

		y = -1;
		final JButton resDefs = new JButton("Reset to Defaults");
		resDefs.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
			}
		});
		fpk.pack( resDefs
			).gridx(1).gridy(++y).fillx().weightx(0).inset(10,5,0,5);
		fpk.pack( new JPanel() ).gridx(1).gridy(++y).filly();
		fpk.pack( useDeadKeyPrefix = new JCheckBox("Dead-Key Prefix:") 
			).gridx(1).gridy(++y).fillx().weightx(0).inset(0,5,0,5);
		pm.map( "DTMF/usedeadkeyprefix", false, useDeadKeyPrefix );
		fpk.pack( deadKeyPrefix = new JComboBox() 
			).gridx(1).gridy(++y).fillx().weightx(0).inset(0,5,0,5);
		final char keys[] = new char[] {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'*', '#', 'A', 'B', 'C', 'D' };
		for( int i = 0; i < keys.length; ++i ) {
			deadKeyPrefix.addItem( keys[i]+"" );
		}
		pm.map( "DTMF/deadkeyprefix", 10, deadKeyPrefix );
		fpk.pack( new JPanel() ).gridx(1).gridy(++y).filly();
		final JButton stationShorts = 
			new JButton("Station Shortcuts...");
		stationShorts.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				stationShortCuts( pm );
			}
		});
		fpk.pack( stationShorts
			).gridx(1).gridy(++y).fillx().weightx(0).inset(0,5,0,5);
		return p;
	}

	private JTable shrtTbl;
	private int shrtCnt = 0;
	private DefaultTableModel shrtMod;
	private void stationShortCuts( PreferencesMapper ppm ) {
		final JDialog dlg = new JDialog( this, 
			"Station Shortcuts", true );
		Packer pk = new Packer( dlg.getContentPane() );
		int y = -1;
		JLabel l;
		Preferences pnn = 
			ppm.getPreferencesNode().node("DTMF");
		final Preferences pr = pnn.node("shortcuts" );
		PreferencesMapper pm = new PreferencesMapper( pr );
		shrtCnt = pr.getInt("count",0);
		pk.pack( new JScrollPane( shrtTbl = new JTable(
				shrtMod = new DefaultTableModel() {
			public int getColumnCount() {
				return 2;
			}
			public int getRowCount() {
				return shrtCnt;
			}
			public String getColumnName( int col ) {
				switch( col ) {
					case 0: return "Callsign";
					case 1: return "Code";
				}
				return "unknown";
			}
			public Object getValueAt( int row, int col ) {
				Preferences pn = pr.node(row+"");
				switch(col) {
					case 0:	return pn.get( "call", "" );
					case 1: return pn.get( "code", "" );
				}
				return "";
			}
			public void setValueAt( Object obj, int row, int col ) {
				Preferences pn = pr.node(row+"");
				switch( col ) {
					case 0:
						pn.put( "call", obj.toString() );
						break;
					case 1:
						pn.put( "code", obj.toString() );
						break;
				}
			}
			public boolean isCellEditable(int row, int column) {
				return true;
			}
		} ) ) ).gridx(0).gridy(++y).fillboth().inset(6,6,6,6);
		
		JPanel bp = new JPanel();
		Packer bpk = new Packer( bp );
		
		pk.pack( bp ).gridx(1).gridy(y).filly().inset(6,6,6,6);
		JButton addNew = new JButton( "Add New" );
		addNew.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				Preferences pn = pr.node( ""+shrtCnt );
				pn.put("call","");
				pn.put("code","");
				TableModelEvent tev = new TableModelEvent(
					shrtMod, shrtCnt, shrtCnt, 
					TableModelEvent.ALL_COLUMNS,
					TableModelEvent.INSERT );
				++shrtCnt;
				pr.putInt("count",shrtCnt);
				shrtMod.newRowsAdded( tev );
				shrtTbl.requestFocus();
				shrtTbl.setSurrendersFocusOnKeystroke(true);
				shrtTbl.editCellAt( shrtCnt-1, 0 );
			}
		});
		bpk.pack( addNew 
			).gridx(0).gridy(0).fillx().weightx(0);
		final JButton remove = new JButton( "Remove" );
		final JButton removeAll = new JButton( "Remove All" );

		remove.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int rows[] = shrtTbl.getSelectedRows();
				for( int i = rows.length-1; i >= 0; --i ) {
					for( int j = rows[i]; j < shrtCnt-1; ++j ) {
						Preferences pc = pr.node(j+"");
						Preferences pn = pr.node((j+1)+"");
						pc.put( "call", pn.get("call","") );
						pc.put( "code", pn.get("code","") );
					}
					--shrtCnt;
				}
				pr.putInt("count",shrtCnt);
				TableModelEvent tev = new TableModelEvent(
					shrtMod );
				shrtMod.newDataAvailable( tev );
				shrtTbl.clearSelection();

				removeAll.setEnabled(shrtCnt > 0);				
			}
		});
		bpk.pack( remove 
			).gridx(0).gridy(1).fillx().weightx(0).inset(4,0,4,0);

		removeAll.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				pr.putInt("count",shrtCnt=0);
				TableModelEvent tev = new TableModelEvent(
					shrtMod );
				shrtMod.newDataAvailable( tev );
				shrtTbl.clearSelection();
				removeAll.setEnabled(false);
				remove.setEnabled(false);
			}
		});
		bpk.pack( removeAll 
			).gridx(0).gridy(2).fillx().weightx(0);

		remove.setEnabled(false);
		removeAll.setEnabled(shrtCnt > 0);
		shrtTbl.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent ev ) {
				remove.setEnabled( shrtTbl.getSelectedRowCount() > 0 );
				removeAll.setEnabled( shrtCnt > 0 );
			}
		});
		bpk.pack( new JPanel() ).gridx(0).gridy(3).filly();

		pk.pack( new JSeparator() 
			).gridx(0).gridy(++y).gridw(2).fillx().inset(6,6,6,6);

		JButton okay = new JButton( "Okay" );
		okay.requestFocus();
		JButton cancel = new JButton( "Cancel" );
		pk.pack( okay ).gridx(0).gridy(++y).west();
//		pk.pack( cancel ).gridx(1).gridy(y).east();
		final boolean cancelled[] = new boolean[1];
		okay.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				cancelled[0] = false;
				if( shrtTbl.isEditing() )
					((DefaultCellEditor)
					shrtTbl.getCellEditor()).stopCellEditing();
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
		dlg.pack();
		dlg.setSize( 300, 300 );
		dlg.setLocationRelativeTo(this);
		dlg.setVisible(true);
		
		if(cancelled[0] == false ) {
			pm.commit();
		}
	}

	private GrayedTextField dtmfFineTune, dtmfFreqToler, dtmfSNRatio, dtmfDBTwist;
	private void advancedDTMF( PreferencesMapper ppm ) {
		final JDialog dlg = new JDialog( this, 
			"Advanced DTMF Settings", true );
		Packer pk = new Packer( dlg.getContentPane() );
		int y = -1;
		JLabel l;

		SwingPreferencesMapper pm = new SwingPreferencesMapper( 
			ppm.getPreferencesNode().node("DTMF" ) );
		pk.pack( l = new JLabel("Fine Tuning (%):") 
			).gridx(0).gridy(++y);
		pk.pack( dtmfFineTune = new GrayedTextField()
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( dtmfFineTune );
		pm.map( "finetuning", 0, dtmfFineTune );

		pk.pack( l = new JLabel("Freq Tolerance (%):")
			).gridx(0).gridy(++y);
		pk.pack( dtmfFreqToler = new GrayedTextField()
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( dtmfFreqToler );
		pm.map( "freqtolerance", 2, dtmfFreqToler );

		pk.pack( l = new JLabel("S/N Radio (db):")
			).gridx(0).gridy(++y);
		pk.pack( dtmfSNRatio = new GrayedTextField()
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( dtmfSNRatio );
		pm.map( "snratio", 12, dtmfSNRatio );

		pk.pack( l = new JLabel("Twist (db):")
			).gridx(0).gridy(++y);
		pk.pack( dtmfDBTwist = new GrayedTextField() 
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( dtmfDBTwist );
		pm.map( "twistdb", 4, dtmfDBTwist );

		JButton resDefs = new JButton( "Reset to Default" );
		pk.pack( resDefs ).gridx(0).gridy(++y);
		pk.pack( new JSeparator()
			).gridx(0).gridy(++y).gridw(2).fillx().inset(6,6,6,6);
		resDefs.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				dtmfFineTune.setText( "0" );
				dtmfFreqToler.setText( "2" );
				dtmfSNRatio.setText( "12" );
				dtmfDBTwist.setText( "4" );
			}
		});
		
		JButton okay = new JButton( "Okay" );
		JButton cancel = new JButton( "Cancel" );
		pk.pack( okay ).gridx(0).gridy(++y).west();
		pk.pack( cancel ).gridx(1).gridy(y).east();
		okay.requestFocus();
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
		dlg.pack();
		dlg.setLocationRelativeTo(this);
		dlg.setVisible(true);
		
		if(cancelled[0] == false ) {
			pm.commit();
		}
	}

	public void testStationIdMorse(String id) {
		try {
			log.info("pttSettings: "+getPttSettings() );
			je.sendMorse( getPttSettings(), id );
		} catch( Exception ex ) {
			log.log( Level.SEVERE, ex.toString(), ex );
		}
	}

	public void testStationIdSpoken(String id) {
		je.transmitSpeech( getPttSettings(), id );
	}
	
	public void testStationIdFile(String file) throws IOException {
		je.transmitAudio( getPttSettings(), file );
	}

	private JRadioButton idMorse, idSpoken, idFile;
	private JCheckBox optIdEachConn, optIdEachDisc, optIdEndTrans, 
		optIdWhileActive, optIdWhileInActive;
	private GrayedTextField identMorse, identCall, identFile;
	private NumericSpinner identTransTime,identActiveTime,identInactiveTime;
	private JCheckBox identClrFreq;
	private JPanel bldIdent() {
		JPanel p = new JPanel();
		Packer pk = new Packer( p );

		JPanel span = new JPanel();
		Packer spk = new Packer( span );
		span.setBorder( BorderFactory.createTitledBorder(
			"Station Identification" ) );
		ButtonGroup idgrp = new ButtonGroup();
		int by[] = new int[]{ -1 };
		idMorse = vertRadio( "Morse:", spk, idgrp, by );
		ModalComponent morseComp = new ModalComponent( idMorse );

		idSpoken = vertRadio( "Spoken Voice:", spk, idgrp, by );
		ModalComponent spokenComp = new ModalComponent( idSpoken );

		idFile = vertRadio( "External file:", spk, idgrp, by );
		ModalComponent fileComp = new ModalComponent( idFile );

		spk.pack( identMorse = new GrayedTextField() 
			).gridx(1).gridy(0).fillx();
		pm.map( "Ident/morse/id", station, identMorse );
		morseComp.add( identMorse );

		spk.pack( identCall = new GrayedTextField()
			).gridx(1).gridy(1).fillx();
		pm.map( "Ident/speech/id", station, identCall );
		spokenComp.add( identCall );

		JButton  identSet = new JButton("Set...");
		
		spk.pack( identSet ).gridx(1).gridy(2).west();
		spk.pack( identFile = new GrayedTextField() 
			).gridx(0).gridy(3).gridw(3).fillx().inset(3,3,3,3);
		identSet.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				File f = selectAudioFile( prefs, "Audio Files" );
				if( f != null ) {
					identFile.setText( f.toString() );
				}
			}
		});
		pm.map( "Ident/audio/file", station+".wav", identFile );
//		identFile.setEditable(false);
		fileComp.add( identSet );
		fileComp.add( identFile );
		fileComp.configure();
		
		JButton morseSettings = new JButton("Settings...");
		morseComp.add( morseSettings );
		morseSettings.setMargin( new Insets( 2,2,2,2 ) );
		morseSettings.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				morseSettings( pm, identMorse.getText() );
			}
		});

		pm.map( "Ident/audio/on", false, idFile );
		pm.map( "Ident/morse/on", false, idMorse );
		pm.map( "Ident/speech/on", true, idSpoken );
		spk.pack( morseSettings
			).gridx(2).gridy(0).fillx().weightx(0).inset(0,4,0,4);
		pk.pack( span ).gridx(0).gridy(0).fillx();
		final JButton identTest = new JButton("Test");
		pk.pack( identTest ).gridx(1).gridy(0).fillx().weightx(0).inset(0,4,0,4);
		identTest.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				new ComponentUpdateThread( identTest ) {
					public Object construct() {
						try {
							if( idMorse.isSelected() ) {
								testStationIdMorse(identMorse.getText());
							} else if( idSpoken.isSelected() ) {
								testStationIdSpoken(identCall.getText());
							} else if( idFile.isSelected() ) {
								testStationIdFile(identFile.getText());
							}
						} catch( Exception ex ) {
							je.reportException(ex);
						}
						return null;
					}
				}.start();
			}
		});
		JPanel op = new JPanel();
		Packer opk = new Packer( op );
		by[0] = -1;
		op.setBorder( BorderFactory.createTitledBorder( "Identify") );
		optIdEachConn = vertCheckBox(
			"Each time a station connects", opk, by );
		pm.map( "Ident/eachconnect", false, optIdEachConn );
		
		optIdEachDisc = vertCheckBox(
			"Each time a station disconnects", opk, by );
		pm.map( "Ident/eachdisconnect", false, optIdEachDisc );

		optIdEndTrans = vertCheckBox(
			"At end of transmission, every", opk, by );
		pm.map( "Ident/endxmit", 
			false, optIdEndTrans );
		ModalComponent idEndComp = new ModalComponent(
			optIdEndTrans );

		optIdWhileActive = vertCheckBox( "While active, every",
			opk, by );
		pm.map( "Ident/whileactive", 
			false, optIdWhileActive );
		ModalComponent idActiveComp = new ModalComponent( 
			optIdWhileActive );

		optIdWhileInActive = vertCheckBox( 
			"While not active, every", opk, by );
		pm.map( "Ident/whileinactive", 
			false, optIdWhileInActive );
		ModalComponent idInActiveComp = new ModalComponent(
			optIdWhileInActive );

		pk.pack( op ).gridx(0).gridy(1).fillx().gridw(2);
		pk.pack( new JPanel() ).gridx(0).gridy(2).filly();

		JLabel l;
		opk.pack( identTransTime = new NumericSpinner() 
			).gridx(1).gridy(2).fillx();
		pm.map( "Ident/endxmit/time", 10, identTransTime );
		opk.pack( l = new JLabel("min") 
			).gridx(2).gridy(2).inset(0,2,0,0);
		idEndComp.add( identTransTime );
		l.setLabelFor( identTransTime );
		idEndComp.add( l );
		idEndComp.configure();
		
		opk.pack( identActiveTime = new NumericSpinner() 
			).gridx(1).gridy(3).fillx();
		pm.map( "Ident/whileactive/time", 6, identActiveTime );
		opk.pack( l=new JLabel("min") 
			).gridx(2).gridy(3).inset(0,2,0,0);

		idActiveComp.add( identActiveTime );
		l.setLabelFor( identActiveTime );
		idActiveComp.add( l );
		idActiveComp.configure();

		opk.pack( identInactiveTime = new NumericSpinner()
			).gridx(1).gridy(4).fillx();

		pm.map( "Ident/whileinactive/time", 10, identInactiveTime );
		opk.pack( l=new JLabel("min") 
			).gridx(2).gridy(4).inset(0,2,0,0);

		idInActiveComp.add( identInactiveTime );
		l.setLabelFor( identInactiveTime );
		idInActiveComp.add( l );

		opk.pack( identClrFreq = new JCheckBox(
			"Wait for clear frequency") ).gridx(0).gridy(5).gridw(2);

		pm.map( "Ident/waitclearfreq", false, identClrFreq );
		idInActiveComp.add(identClrFreq);
		idInActiveComp.configure();

		morseComp.relate( spokenComp );
		morseComp.relate( fileComp );

		spokenComp.relate( morseComp );
		spokenComp.relate( fileComp );

		fileComp.relate( spokenComp );
		fileComp.relate( morseComp );

		morseComp.configure();
		fileComp.configure();
		spokenComp.configure();

		return p;
	}

	private JSlider morsePitch, morseSpeed, morseLevel;
	private void morseSettings( PreferencesMapper ppm,
			final String morseId ) {
		final JDialog dlg = new JDialog( this, 
			"Morse Settings", true );
		Packer pk = new Packer( dlg.getContentPane() );
		int y = -1;
		JLabel l;

		SwingPreferencesMapper pm = new SwingPreferencesMapper( 
			ppm.getPreferencesNode().node("Ident").node("morse" ) );

		pk.pack( l = new JLabel( "Speed", JLabel.CENTER)
			).gridx(0).gridy(++y).fillx().gridw(4).inset(10,0,0,0);
		final JLabel spdVal = new JLabel("0", JLabel.CENTER );
		pk.pack( new JPanel() ).gridx(0).gridy(++y).fillx();
		pk.pack( spdVal ).gridx(1).gridy(y).fillx().weightx(0);
		pk.pack( new JLabel(" wpm") ).gridx(2).gridy(y);
		pk.pack( new JPanel() ).gridx(3).gridy(y).fillx();
		pk.pack( morseSpeed = new JSlider( 5, 35 ) 
			).gridx(0).gridy(++y).gridw(4).fillx();
		pm.map( "speed", 20, morseSpeed );
		l.setLabelFor( morseSpeed );
		morseSpeed.setPaintTicks(true);
		morseSpeed.setMajorTickSpacing(5);
		morseSpeed.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent ev ) {
				spdVal.setText( morseSpeed.getValue()+"" );
				je.setMorseWPM( morseSpeed.getValue() );
			}
		});
		spdVal.setText( morseSpeed.getValue()+"" );

		pk.pack( l = new JLabel( "Pitch", JLabel.CENTER )
			).gridx(0).gridy(++y).fillx().gridw(4).inset(10,0,0,0);
		final JLabel ptchVal = new JLabel("0", JLabel.CENTER );
		pk.pack( new JPanel() ).gridx(0).gridy(++y).fillx();
		pk.pack( ptchVal ).gridx(1).gridy(y).fillx().weightx(0);
		pk.pack( new JLabel("hz") ).gridx(2).gridy(y);
		pk.pack( new JPanel() ).gridx(3).gridy(y).fillx();
		pk.pack( morsePitch = new JSlider( 300, 3000 ) 
			).gridx(0).gridy(++y).gridw(4).fillx();
		l.setLabelFor( morsePitch );
		pm.map( "pitch", 438, morsePitch );
		morsePitch.setPaintTicks(true);
		morsePitch.setMajorTickSpacing(100);
		morsePitch.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent ev ) {
				ptchVal.setText( morsePitch.getValue()+"" );
				try {
					je.setMorseIdFreq( morsePitch.getValue() );
				} catch( UnsupportedOperationException ex ) {
					JOptionPane.showMessageDialog( dlg, ex );
					morsePitch.setEnabled(false);
				}
			}
		});
		ptchVal.setText( morsePitch.getValue()+"" );

		pk.pack( l = new JLabel( "Volume", JLabel.CENTER ) 
			).gridx(0).gridy(++y).fillx().gridw(4).inset(10,0,0,0);
		final JLabel lvlVal = new JLabel("0", JLabel.CENTER );
		pk.pack( new JPanel() ).gridx(0).gridy(++y).fillx();
		pk.pack( lvlVal ).gridx(1).gridy(y).fillx().weightx(0);
		pk.pack( new JLabel("db") ).gridx(2).gridy(y);
		pk.pack( new JPanel() ).gridx(3).gridy(y).fillx();
		pk.pack( morseLevel = new JSlider( -200, 0 ) 
			).gridx(0).gridy(++y).gridw(4).fillx();
		l.setLabelFor( morseLevel );
		morseLevel.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent ev ) {
				lvlVal.setText( (morseLevel.getValue()/10.0)+"" );
				try {
					je.setMorseIdVolume( morseLevel.getValue() );
				} catch( IOException ex ) {
				}
			}
		});
		pm.map( "volume", -146, morseLevel );
		lvlVal.setText( (morseLevel.getValue()/10.0)+"" );
		morseLevel.setPaintTicks(true);
		morseLevel.setMajorTickSpacing(20);
		
		pk.pack( new JSeparator() 
			).gridx(0).gridw(4).gridy(++y).fillx().inset(4,4,4,4);

		JButton okay = new JButton( "Okay" );
		JButton cancel = new JButton( "Cancel" );
		final JButton test = new JButton( "Test" );
		okay.requestFocus();
		pk.pack( okay ).gridx(0).gridy(++y).west();
		pk.pack( test ).gridx(1).gridw(2).gridy(y).east().inset(0,4,0,4);
		pk.pack( cancel ).gridx(3).gridy(y).east();
		final boolean cancelled[] = new boolean[1];
		okay.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				cancelled[0] = false;
				dlg.setVisible(false);
			}
		});
		test.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				new ComponentUpdateThread( test ) {
					public Object construct() {
						testStationIdMorse(morseId);
						return null;
					}
				}.start();
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
		dlg.pack();
		dlg.setLocationRelativeTo(this);
		dlg.setVisible(true);
		
		if(cancelled[0] == false ) {
			pm.commit();
		}
	}

	private JComboBox connAnnounce, discAnnounce, muteAnnounce;
	private JCheckBox connIncludeCall, discIncludeCall;
	private JCheckBox playWelcomeOnConnect, playCourtesy, playActiveRemind;
	private JButton welMsg;
	private NumericSpinner maxKeyDownValue,deadCarrierValue,annPreDelay;
	private NumericSpinner welcomeInterval;
	private GrayedTextField welFile;
	private File msgDir;
	private JPanel bldOptions() {
		JPanel p = new JPanel();
		Packer pk = new Packer( p );

		JPanel ap = new JPanel();
		Packer apk = new Packer( ap );
		int ay = -1;
		JLabel l;
		apk.pack( l = new JLabel("Announce connects:") 
			).gridx(0).gridy(++ay).east().inset(0,3,0,3);
		apk.pack( connAnnounce = new JComboBox() 
			).gridx(1).gridy(ay).fillx();
		l.setLabelFor( connAnnounce );
		connAnnounce.addItem( "None");
		connAnnounce.addItem( "All Users");
		connAnnounce.addItem( "First conferencee only");
		pm.map( "Options/announce/contacts", 2, connAnnounce );
		apk.pack( connIncludeCall = 
			new JCheckBox( "Include callsign" ) 
			).gridx(1).gridy(++ay).west();
		pm.map( "Options/includecallsign", true, connIncludeCall );
		apk.pack( l = new JLabel("Announce disconnects:")
			).gridx(0).gridy(++ay).east().inset(0,3,0,3);
		apk.pack( discAnnounce = new JComboBox() 
			).gridx(1).gridy(ay).fillx();
		l.setLabelFor( discAnnounce );
		discAnnounce.addItem( "None");
		discAnnounce.addItem( "All Users");
		discAnnounce.addItem( "Last conferencee only");
		pm.map( "Options/announce/disconnects", 2, discAnnounce );
		apk.pack( discIncludeCall = 
			new JCheckBox( "Include callsign" ) 
			).gridx(1).gridy(++ay).west();
		pm.map( "Options/includecallsign",
			true, discIncludeCall );

		apk.pack( l = new JLabel("Announce Muting:") 
			).gridx(0).gridy(++ay).east().inset(0,3,0,3);
		apk.pack( muteAnnounce = new JComboBox()
			).gridx(1).gridy(ay).fillx();
		l.setLabelFor( muteAnnounce );
		muteAnnounce.addItem( "No muting");
		muteAnnounce.addItem( "Mute if freq is busy");
		muteAnnounce.addItem( "Defer if freq is busy");
		muteAnnounce.addItem( "Suppress all");
		pm.map( "Options/announce/muting", 0, muteAnnounce );
		pk.pack( ap ).gridx(0).gridy(0).fillboth();

		JPanel ppan = new JPanel();
		Packer ppk = new Packer( ppan );
		int my = -1;
		ppk.pack( playWelcomeOnConnect = new JCheckBox(
			"Play welcome message to connecting station" )
			).gridx(0).gridw(3).gridy(++my).fillx();
		pm.map( "Options/playwelcomeonconnect", 
			false, playWelcomeOnConnect );
		ModalComponent playWelComp = new ModalComponent( 
			playWelcomeOnConnect );
		ppk.pack( l = new JLabel("Welcome Message File") 
			).gridx(0).gridy(++my).gridw(3).fillx();
		ppk.pack( welFile = new GrayedTextField() 
			).gridx(0).gridy(++my).gridw(3).fillx();
		l.setLabelFor( welFile );
		pm.map( "Options/welcomeMessageFile",
			"welcome.wav", welFile );
		ppk.pack( welMsg = new JButton("Select...") 
			).gridx(4).gridy(my).inset(0,5,0,0);
		playWelComp.add( welFile );
		playWelComp.add( welMsg );
		playWelComp.configure();
		welMsg.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				File f = selectAudioFile( prefs, "Audio Files" );
				if( f != null ) {
					welFile.setText( f.toString() );
				}
			}
		});

		ppk.pack( playCourtesy = new JCheckBox("Play courtesy tone")
			).gridx(0).gridy(++my).gridw(2).gridw(3).fillx();
		pm.map( "Options/playcourtesytone", false, playCourtesy );
		
		ppk.pack( playActiveRemind = new JCheckBox(
			"Play activity reminder every") 
			).gridx(0).gridy(++my).fillx();
		pm.map( "Options/reminder/play", 
			false, playActiveRemind );
		ModalComponent playActComp = new ModalComponent( 
			playActiveRemind );
		ppk.pack( welcomeInterval = new NumericSpinner()
			).gridx(1).gridy(my).fillx();
		playActComp.add( welcomeInterval );
		playActComp.configure();
		ppk.pack( l= new JLabel("secs") ).gridx(2).gridy(my).inset(0,3,0,0);
		pm.map( "Options/reminder/intervalsecs", 120, welcomeInterval );
		l.setLabelFor( welcomeInterval );

		pk.pack( ppan ).gridx(0).gridy(1).fillboth();

		pk.pack( new JSeparator() ).gridx(0).gridy(2).fillx();

		JPanel kp = new JPanel();
		Packer kpk = new Packer( kp );
		int y = -1;
		kpk.pack( l=new JLabel("Max key-down time (sec):")
			).gridx(0).gridy(++y).east().inset(0,3,0,3);
		kpk.pack( maxKeyDownValue = new NumericSpinner()
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( maxKeyDownValue );
		pm.map( "Options/maxkeydown", 0 , maxKeyDownValue );
		kpk.pack( l=new JLabel("Dead-carrier timeout (sec):")
			).gridx(0).gridy(++y).east().inset(0,3,0,3);
		kpk.pack( deadCarrierValue = new NumericSpinner() 
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( deadCarrierValue );
		pm.map( "Options/deadcarrier", 0 , deadCarrierValue );
		kpk.pack( l=new JLabel("Announcement pre-delay (ms):")
			).gridx(0).gridy(++y).east().inset(0,3,0,3);
		kpk.pack( annPreDelay = new NumericSpinner()
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( annPreDelay );
		pm.map( "Options/announce/predelay", 150 , annPreDelay );
		pk.pack( kp ).gridx(0).gridy(3).fillboth();
		return p;
	}

	File selectAudioFile( final Preferences prefs, final String descr ) {
		if( msgDir == null ) {
			Preferences pn = prefs.node("Options");
			msgDir = new File( pn.get( "msgdir",
				System.getProperty("user.dir") ) );
		}
		JFileChooser fc = new JFileChooser(msgDir);
		fc.setFileFilter( new FileFilter() {
			public String getDescription() {
				return "Audio Files";
			}
			public boolean accept( File file ) {
				if( file.isDirectory() ) return true;
				String name = file.getName();
				if( name.toLowerCase().endsWith(".wav") )
					return true;
				if( name.toLowerCase().endsWith(".mp3") ) 
					return true;
				if( name.toLowerCase().endsWith(".au") ) 
					return true;
				return false;
			}
		});
		if( fc.showOpenDialog( SysopSettings.this ) == 
				fc.APPROVE_OPTION ) {
			Preferences pn = prefs.node("Options");
			pn.put( "msgdir", 
				(msgDir = fc.getSelectedFile()).getParent() );
			return fc.getSelectedFile();
		}
		return null;
	}

	
	private JRadioButton evDefault, evCustom;
	private JButton evPlayMsg, evSelect;
	private JList evList;
	private GrayedTextField evCurMsg;
	private Vector<String> evs = new Vector<String>();
	private JComboBox evSpeechSpeed, sigToneBurst;
	private JComboBox sigToneFreq;
	private JSpinner sigToneDuration;
	private JPanel bldSignals() {
		JPanel p = new JPanel();
		Packer pk = new Packer( p );
		JLabel l;
		int y=-1;

		pk.pack( l = new JLabel("Select an event from the list, "+
			"then choose" ) 
			).gridx(0).gridy(++y).fillx().west().inset(0,5,0,0);
		pk.pack( l = new JLabel("either Default or Custom")
			).gridx(0).gridy(++y).fillx().west().inset(0,5,0,0);

		int ey = -1;
		JPanel ep = new JPanel();
		Packer epk = new Packer( ep );
		epk.pack( l = new JLabel("Events Signaled:")
			).gridx(0).gridy(++ey).fillx().west().inset(10,5,0,0);
		epk.pack( new JScrollPane( evList = new JList() )
			).gridx(0).gridy(++ey).gridh(6).fillboth().inset(0,5,0,0);
		l.setLabelFor( evList );
		final String events[] = new String[] {
			"Connected",
			"Disconnected",
			"Station Info",
			"Link Up",
			"Link Down",
			"Courtesy Tone",
			"Activity Reminder"
		};
		for( int i = 0; i < events.length; ++i ) {
			evs.addElement( events[i] );
		}
		evList.setListData( evs );
		pm.map( "Signals/events/selected", 0, evList );
		final boolean evDefs[] = new boolean[7];
		final String evMsg[] = new String[7];
		pm.map( new PrefManager<Component>() {
				public boolean prepare( Component comp ) {
					return true;
				}
				public boolean commit( Component comp ) {
					for (int i = 0; i < events.length; ++i ) {
						String event = events[i];
						Preferences pn = prefs.node("Signals");
						pn = pn.node("events");
						pn = pn.node(event);
						pn.putBoolean( "default", evDefs[i] );
						pn.put( "msg", evMsg[i] );
					}
					return true;
				}
				public void setValueIn( Component comp ) {
					for (int i = 0; i < events.length; ++i ) {
						String event = events[i];
						Preferences pn = prefs.node("Signals");
						pn = pn.node("events");
						pn = pn.node(event);
						evDefs[i] = pn.getBoolean( "default", true );
						evMsg[i] = pn.get( "msg", event+".wav" );
					}
				}
			}, l );
//		epk.pack( new JPanel() 
//			).gridx(2).gridy(ey).filly().gridh(2).weighty(0);
		epk.pack( evPlayMsg = new JButton("Play") ).gridx(1).gridy(ey);
		evPlayMsg.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				new ComponentUpdateThread( evPlayMsg ) {
					public Object construct() {
						try {
							int idx = evList.getSelectedIndex();
							if( !evDefs[idx] ) {
								je.transmitAudio( getPttSettings(), evMsg[idx] );
							} else {
							}
						} catch( Exception ex ) {
							je.reportException(ex);
						}
						return null;
					}
				}.start();
			}
		});
	
		ButtonGroup dcgrp = new ButtonGroup();
		
		epk.pack( evDefault = new JRadioButton("Default") 
			).gridx(1).gridy(++ey).west();
		epk.pack( evCustom = new JRadioButton("Custom") 
			).gridx(1).gridy(++ey).west();
		evSelect = new JButton("Select...");

		final ModalComponent defComp = new ModalComponent( evDefault );
		final ModalComponent customComp = new ModalComponent( evCustom );
		evList.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent ev ) {
				if( ev.getValueIsAdjusting() )
					return;
				int idx = evList.getSelectedIndex();
				evSelect.setEnabled(idx != -1);
				evDefault.setEnabled(idx != -1);
				evCustom.setEnabled(idx != -1);
				evPlayMsg.setEnabled(idx != -1);
				if( idx != -1 ) {
					evCurMsg.setEnabled( !evDefs[idx] );
					if( evDefs[idx] )
						evCurMsg.setText("");
					else
						evCurMsg.setText( evMsg[idx] );
					evDefault.setSelected( evDefs[idx] );
					evCustom.setSelected( !evDefs[idx] );
					evSelect.setEnabled( !evDefs[idx] );
				}
				defComp.configure();
			}
		});
		int idx = evList.getSelectedIndex();
		evPlayMsg.setEnabled(idx!=-1);
		evSelect.setEnabled(idx!=-1);
		evDefault.setEnabled(idx!=-1);
		evCustom.setEnabled(idx!=-1);
		evCurMsg = new GrayedTextField();
		evCurMsg.setEditable(false);
		if( idx != -1 && !evDefs[idx] )
			evCurMsg.setText( evMsg[idx] );
		if( idx != -1 ) {
			evDefault.setSelected( evDefs[idx] );
			evCustom.setSelected( !evDefs[idx] );
			evSelect.setEnabled( !evDefs[idx] );
		}
		evDefault.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int idx = evList.getSelectedIndex();
				evDefs[idx] = evDefault.isSelected();
				evSelect.setEnabled( !evDefs[idx] );
				evCurMsg.setText( "" );
			}
		});
		evCustom.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int idx = evList.getSelectedIndex();
				evDefs[idx] = evDefault.isSelected();
				evSelect.setEnabled( !evDefs[idx] );
				evCurMsg.setText( evMsg[idx] );
			}
		});
		dcgrp.add( evDefault );
		dcgrp.add( evCustom );
		defComp.relate( customComp );

		epk.pack( evSelect
			).gridx(2).gridy(ey).west().inset(0,5,0,5);
		epk.pack( l = new JLabel("Audio File:")
			).gridx(1).gridy(++ey).fillx().gridw(2);
		epk.pack( evCurMsg
			).gridx(1).gridy(++ey).fillx().gridw(2);
		l.setLabelFor( evCurMsg );
		evSelect.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				final int idx = evList.getSelectedIndex();
				File f = selectAudioFile( prefs, "Audio Files" );
				if( f != null ) {
					evMsg[idx] = f.toString();
					evDefs[idx] = false;
					evCurMsg.setText( f.toString() );
				}
			}
		});
		customComp.relate( defComp );

		customComp.add( evSelect );
		customComp.add( evCurMsg );
		customComp.add( l );

		customComp.configure();
		defComp.configure();

		JPanel jp = new JPanel();
//		jp.setOpaque(true);
//		jp.setBackground(Color.red);
		epk.pack( jp 
			).gridx(1).gridy(++ey).filly();
		pk.pack( ep ).gridx(0).gridy(++y).fillboth();
		
		JPanel sp = new JPanel();
		Packer spk = new Packer( sp );
		spk.pack( l = new JLabel("Speech Speed") 
			).gridx(0).gridy(0).inset(0,5,0,5);
		spk.pack( evSpeechSpeed = new JComboBox() 
			).gridx(1).gridy(0).fillx();
		l.setLabelFor( evSpeechSpeed );
		evSpeechSpeed.addItem("Slow");
		evSpeechSpeed.addItem("Normal");
		evSpeechSpeed.addItem("Fast");
		pm.map( "Signals/speech/speed", 1, evSpeechSpeed );
		final JButton evPlaySpeech = new JButton( "Play" );
		spk.pack( evPlaySpeech 
			).gridx(2).gridy(0).inset(0,10,0,10);
		spk.pack( new JPanel() ).gridx(3).gridy(0).fillx();
		pk.pack( sp ).gridx(0).gridy(++y).fillx().inset(10,5,10,0);

		JPanel tp = new JPanel();
		tp.setBorder( BorderFactory.createTitledBorder( 
			"Tone Burst") );
		Packer tpk = new Packer(tp);
		tpk.pack( l = new JLabel("Send:") ).gridx(0).gridy(0).west();
		tpk.pack( sigToneBurst = new JComboBox() 
			).gridx(1).gridy(0).fillx().gridw(2).inset(0,0,4,0);
		l.setLabelFor( sigToneBurst );
		sigToneBurst.addItem( "Never" );
		sigToneBurst.addItem( "On initial connect" );
		sigToneBurst.addItem( "At beginning of each TX" );
		pm.map( "Signals/toneburst/send", 0, sigToneBurst );
		tpk.pack( l = new JLabel("Freq (Hz):")
			).gridx(0).gridy(1).west().inset(0,0,0,5);
		tpk.pack( sigToneFreq = new JComboBox() ).gridx(1).gridy(1).fillx();
		l.setLabelFor( sigToneFreq );
		sigToneFreq.addItem( "1000" );
		sigToneFreq.addItem( "1450" );
		sigToneFreq.addItem( "1750" );
		sigToneFreq.addItem( "2100" );
		pm.map( "Signals/toneburst/freq", 0, sigToneFreq );
		JLabel l2;
		tpk.pack( l2 = new JLabel("Duration (ms):")
			).gridx(2).gridy(1).inset(0,5,0,5);
		tpk.pack( sigToneDuration = new JSpinner() ).gridx(3).gridy(1).fillx();
		l2.setLabelFor( sigToneDuration );
		pm.map( "Signals/toneburst/duration", 500, sigToneDuration );
		pk.pack( tp ).gridx(0).gridy(++y).fillx().inset(0,5,0,5);
		pk.pack( new JPanel() ).gridx(0).gridy(++y).filly();
		ModalComponent sigToneBurstComp = new ModalComponent( sigToneBurst,
			new int[] { 1, 2 } );
		sigToneBurstComp.add( sigToneDuration );
		sigToneBurstComp.add( sigToneFreq );
		sigToneBurstComp.add( l );
		sigToneBurstComp.add( l2 );
		sigToneBurstComp.configure();
		return p;
	}
	
	private JCheckBox remWebCtrl, remDialIn, remMonAudio;
	private GrayedTextField remTcpPort, remWebUser, remWebPasswd, remPassCode, remAnsTimeout;
	private JComboBox remDialModem;
	private NumericSpinner remAnswerOn;
	private JSlider remAudioLevel;
	private JPanel bldRemote() {
		JPanel p = new JPanel();
		Packer pk = new Packer( p );
		JPanel wp = new JPanel();
		Packer wpk = new Packer( wp );
		int y = -1;
		JLabel l;

		int wy = -1;
		wpk.pack( remWebCtrl = 
			new JCheckBox("Enable Web remote control") 
			).gridx(0).gridy(++wy).gridw(2);
		ModalComponent remWebComp = new ModalComponent( remWebCtrl );
		pm.map( "Remote/web/access", false, remWebCtrl );
		wpk.pack( l = new JLabel("TCP Port:") 
			).gridx(0).gridy(++wy).east().inset(0,4,0,4);
		remWebComp.add(l);
		wpk.pack( remTcpPort = new GrayedTextField("8080") 
			).gridx(1).gridy(wy).fillx();
		pm.map( "Remote/web/tcpport", "8080", remTcpPort );
		remWebComp.add(remTcpPort);
		wpk.pack( l = new JLabel("Username:") 
			).gridx(0).gridy(++wy).east().inset(0,4,0,4);
		remWebComp.add(l);
		wpk.pack( remWebUser = new GrayedTextField()
			).gridx(1).gridy(wy).fillx();
		remWebComp.add(remWebUser);
		pm.map( "Remote/web/user", "", remWebUser );
		wpk.pack( l = new JLabel("Password:") 
			).gridx(0).gridy(++wy).east().inset(0,4,0,4);
		remWebComp.add(l);
		wpk.pack( remWebPasswd = new GrayedTextField()
			).gridx(1).gridy(wy).fillx();
		remWebComp.add(remWebPasswd);
		pm.map( "Remote/web/passwd", "", remWebPasswd );

		pk.pack( wp ).gridx(0).gridy(++y).fillx();
		
		pk.pack( new JSeparator() 
			).gridx(0).gridy(++y).fillx().inset(4,4,4,4);
		
		JPanel dp = new JPanel();
		Packer dpk = new Packer( dp );
		remWebComp.configure();
		
		int dy = -1;
		dpk.pack( remDialIn = 
			new JCheckBox("Enable dial-in remote control") 
			).gridx(0).gridy(++dy).gridw(3).fillx().inset(0,4,0,4);
		ModalComponent remDialComp = new ModalComponent( remDialIn );
		pm.map( "Remote/dialin/active", false, remDialIn );
		dpk.pack( l = new JLabel("Voice-modem device:")
			).gridx(0).gridy(++dy).gridw(1).west().inset(0,4,0,4);
		remDialComp.add(l);
		dpk.pack( remDialModem = new JComboBox() 
			).gridx(1).gridy(dy).fillx().gridw(2);
		fillSerialPorts( remDialModem );
		pm.map( new PrefManager<Component>() {
			public boolean prepare( Component comp ) {
				return true;
			}
			public boolean commit( Component comp ) {
				prefs.put( "Remote/dialin/port",
					(String)((JComboBox)comp).getSelectedItem() );
				return true;
			}
			public void setValueIn( Component comp ) {
				JComboBox box = (JComboBox)comp;
				box.setSelectedItem( 
					(String)prefs.get("Remote/dialin/port","") );
				if( box.getSelectedIndex() == -1 &&
						box.getItemCount() > 0 ) {
					box.setSelectedIndex(0);
				}
			}
		}, rfSerPort );
		remDialComp.add(remDialModem);
//		pm.map( "Remote/dialin/port", 0, remDialModem );
		dpk.pack( l = new JLabel("Answer on:") 
			).gridx(0).gridy(++dy).east().inset(8,4,0,4);
		remDialComp.add(l);
		dpk.pack( remAnswerOn = new NumericSpinner() 
			).gridx(1).gridy(dy).fillx();
		remDialComp.add(remAnswerOn);
		pm.map( "Remote/dialin/answeronring", 1, remAnswerOn );
		dpk.pack( l = new JLabel(" ring(s)")
			).gridx(2).gridy(dy).inset(8,0,0,4);
		remDialComp.add(l);
	
		dpk.pack( l = new JLabel("Timeout (sec):")
			).gridx(0).gridy(++dy).east().inset(8,4,0,4);
		remDialComp.add(l);
		dpk.pack( remAnsTimeout = new GrayedTextField() 
			).gridx(1).gridy(dy).gridw(2).fillx().inset(8,0,0,4);
		remDialComp.add(remAnsTimeout);
		pm.map( "Remote/dialin/timeout", 30, remAnsTimeout );
		
		dpk.pack( l = new JLabel("Audio Level:") 
			).gridx(0).gridy(++dy).east().inset(8,4,0,4);
		remDialComp.add(l);
		dpk.pack( remAudioLevel = new JSlider() 
			).gridx(1).gridy(dy).gridw(2).fillx().inset(8,0,0,4);
		remDialComp.add(remAudioLevel);
		remAudioLevel.setMinimum( -10 );
		remAudioLevel.setMaximum( 6 );
		pm.map( "Remote/dialin/level", 0, remAudioLevel );
		dpk.pack( remMonAudio = new JCheckBox("Audio Monitor") 
			).gridx(1).gridy(++dy).gridw(3).fillx();
		remDialComp.add(remMonAudio);
		pm.map( "Remote/dialin/monitor", false, remMonAudio );
		dpk.pack( l = new JLabel( "Passcode:")
			).gridx(0).gridy(++dy).east().inset(8,4,0,4);
		remDialComp.add(l);
		dpk.pack( remPassCode = new GrayedTextField() 
			).gridx(1).gridy(dy).gridw(2).fillx().inset(8,0,0,4);
		remDialComp.add(remPassCode);
		pm.map("Remote/dialin/password", "", remPassCode );
		dpk.pack( new JPanel() ).gridx(0).gridy(++dy).filly();
		remDialComp.configure();
		pk.pack( dp ).gridx(0).gridy(++y).fillboth();
		return p;
	}

	private GrayedTextField infoLatDeg, infoLatMin, infoLonDeg, infoLonMin;
	private JComboBox infoLatRegion, infoLonRegion, infoPowerOut, infoDBGain;
	private JComboBox infoHaat, infoDirection, infoTNCPort, infoAPRSPath;
	private JCheckBox infoRepAPRS, infoAutoInit, infoAPRSStatus;
	private GrayedTextField infoFreq, infoPLTone, infoAPRSComment;
	private JPanel bldRFInfo() {
		JPanel p = new JPanel();
		Packer pk = new Packer( p );
		int y = -1;
		JLabel l;
		
		JPanel lp = new JPanel();
		Packer lpk = new Packer( lp );
		
		int ly = -1;
		lpk.pack( new JLabel("Deg")
			).gridx(1).gridy(++ly).inset(0,3,0,3);
		lpk.pack( new JLabel("Min") 
			).gridx(2).gridy(ly).inset(0,3,0,3);
		lpk.pack( new JLabel("ddd") 
			).gridx(1).gridy(++ly).inset(0,3,0,3);
		lpk.pack( new JLabel("mm.nn")
			).gridx(2).gridy(ly).inset(0,3,0,3);
		lpk.pack( l= new JLabel("Lat:") 
			).gridx(0).gridy(++ly).inset(0,3,0,0);
		lpk.pack( infoLatDeg = new GrayedTextField("00") 
			).gridx(1).gridy(ly).fillx().weightx(0).inset(0,2,0,2);
		pm.map( "Info/Lat/deg", "00", infoLatDeg );
		lpk.pack( infoLatMin = new GrayedTextField("00.00") 
			).gridx(2).gridy(ly).fillx().weightx(0).inset(0,2,0,2);
		pm.map( "Info/Lat/min", "00.00", infoLatMin );
		lpk.pack( infoLatRegion = 
			new JComboBox(new String[]{ "North", "South"})
			).gridx(3).gridy(ly).inset(2,2,2,2);
		pm.map( "Info/Lat/region", 0, infoLatRegion );
		lpk.pack( l = new JLabel("Freq (MHz):")
			).gridx(4).gridy(ly).inset(0,10,0,5).east();
		lpk.pack( infoFreq = new GrayedTextField() ).gridx(5).gridy(ly).fillx();
		pm.map( "Info/frequency", "", infoFreq );

		lpk.pack( new JLabel("Lon:")
			).gridx(0).gridy(++ly).inset(0,3,0,0);
		lpk.pack( infoLonDeg = new GrayedTextField("00") 
			).gridx(1).gridy(ly).fillx().weightx(0).inset(0,2,0,2);
		pm.map( "Info/Lon/deg", "00", infoLonDeg );
		lpk.pack( infoLonMin = new GrayedTextField("00.00")
			).gridx(2).gridy(ly).fillx().weightx(0).inset(0,2,0,2);
		pm.map( "Info/Lon/min", "00.00", infoLonMin );
		lpk.pack( infoLonRegion =
			new JComboBox(new String[]{ "West", "East"})
			).gridx(3).gridy(ly).inset(2,2,2,2);
		pm.map( "Info/Lon/region", 0, infoLonRegion );
		lpk.pack( l = new JLabel("PL (if any):") 
			).gridx(4).gridy(ly).inset(0,10,0,5).east();
		lpk.pack( infoPLTone = new GrayedTextField() ).gridx(5).gridy(ly).fillx();
		pm.map("Info/pltone", "", infoPLTone );
		pk.pack( lp ).gridx(0).gridy(++y).fillx();
		
		JPanel hp = new JPanel();
		Packer hpk = new Packer( hp );
		int hy = -1;
		hpk.pack( l = new JLabel( "Power (W):" )
			).gridx(0).gridy(++hy).east();
		hpk.pack( infoPowerOut = new JComboBox(new Integer[] { 0, 1, 4, 9, 16, 25, 
			36, 49, 64, 81, 100, 120, 150, 200} 
			) ).gridx(1).gridy(hy).fillx();
		pm.map("Info/powerout", 0, infoPowerOut );
		hpk.pack( l = new JLabel( "Antenna Gain (dB):" ) 
			).gridx(2).gridy(hy).inset(0,10,0,4).east();
		hpk.pack( infoDBGain = 
			new JComboBox(new Integer[] { 0,1,2,3,4,5,6,7,8,9}) 
			).gridx(3).gridy(hy).fillx();
		pm.map("Info/antennagain", 0, infoDBGain );

		hpk.pack( l = new JLabel( "HAAT (ft):" ) 
			).gridx(0).gridy(++hy).east();
		hpk.pack( infoHaat =
			new JComboBox(new Integer[] {10,20,40,80,160,320,
			640,1280,2560,5120}	) ).gridx(1).gridy(hy).fillx();
		pm.map("Info/haat", 0, infoHaat );
		hpk.pack( l = new JLabel( "Directivity:" ) 
			).gridx(2).gridy(hy).inset(0,10,0,4).east();
		hpk.pack( infoDirection = 
			new JComboBox(new String[] {
			"Omni","NE","E","SE","S","SW","W","NW","N"
			}) ).gridx(3).gridy(hy).fillx();
		pm.map("Info/directivity", 0, infoDirection );
		pk.pack(hp).gridx(0).gridy(++y).inset(20,4,5,4).fillx();

		JPanel tp = new JPanel();
		tp.setBorder( BorderFactory.createTitledBorder( 
			"Report Status") );
		Packer tpk = new Packer( tp );
		int ay = -1;
		tpk.pack( infoRepAPRS = new JCheckBox( "Via APRS") 
			).gridx(0).gridy(++ay).fillx().gridw(3);
		pm.map("Info/Report/APRS/on", false, infoRepAPRS );
		ModalComponent aprsComp = new ModalComponent( infoRepAPRS );
		tpk.pack( l = new JLabel("TNC Interface:")
			).gridx(0).gridy(++ay).east().inset(10,0,0,0);
		aprsComp.add( l );
		// Get PORT list from javax.comm code that will list ports
		// prefix list with "UI-View32"
		tpk.pack( infoTNCPort = new JComboBox() 
			).gridx(1).gridy(ay).fillx().inset(10,0,0,0);
		aprsComp.add( infoTNCPort );
		fillSerialPorts( infoTNCPort );
//		pm.map("Info/Report/APRS/tncport", 0, infoTNCPort );
		pm.map( new PrefManager<Component>() {
			public boolean prepare( Component comp ) {
				return true;
			}
			public boolean commit( Component comp ) {
				prefs.put( "Info/Report/APRS/tncport",
					(String)((JComboBox)comp).getSelectedItem() );
				return true;
			}
			public void setValueIn( Component comp ) {
				JComboBox box = (JComboBox)comp;
				box.setSelectedItem( 
					(String)prefs.get("Info/Report/APRS/tncport","") );
				if( box.getSelectedIndex() == -1 &&
						box.getItemCount() > 0 ) {
					box.setSelectedIndex(0);
				}
			}
		}, infoTNCPort );
		tpk.pack( infoAutoInit = new JCheckBox("Auto Initialize") 
			).gridx(2).gridy(ay).inset(10,0,0,0);
		aprsComp.add( infoAutoInit );
		pm.map("Info/Report/APRS/autoinit", false,
			infoAutoInit );
		final ModalComponent autoInitComp =
			new ModalComponent( infoAutoInit );
		tpk.pack( l = new JLabel("Unproto Path:")
			).gridx(0).gridy(++ay).east().inset(10,0,0,0);
		//aprsComp.add( l );
		tpk.pack( infoAPRSPath = new JComboBox(new String[] {
			"[direct]", "RELAY", "WIDE", "RELAY,WIDE","WIDE2-2"
			}) ).gridx(1).gridy(ay).fillx().inset(10,0,0,0);
		autoInitComp.add( infoAPRSPath );
		autoInitComp.add( l );
		autoInitComp.configure();
		final JLabel ul = l;
		infoRepAPRS.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				log.fine("Toggled APRS to: "+infoRepAPRS.isSelected() );
				autoInitComp.configure();
				ul.setEnabled( infoAutoInit.isSelected() && 
					infoRepAPRS.isSelected() );
				infoAPRSPath.setEnabled( infoRepAPRS.isSelected() &&
					infoAutoInit.isSelected() );
			}
		});

		pm.map("Info/Report/APRS/unproto", 0, infoAPRSPath );
		//aprsComp.add( infoAPRSPath );
		
		tpk.pack( infoAPRSStatus = new JCheckBox("Include name of "+
				"connection station(s) in status")
			).gridx(0).gridy(++ay).fillx().gridw(3).inset(10,0,0,0);
		aprsComp.add( infoAPRSStatus );
		pm.map("Info/Report/APRS/statustext",
			false, infoAPRSStatus );
		
		tpk.pack( l = new JLabel("Comment:")
			).gridx(0).gridy(++ay).inset(10,0,0,0);
		aprsComp.add( l );

		tpk.pack( infoAPRSComment = new GrayedTextField() 
			).gridx(1).gridy(ay).fillx().inset(10,0,0,0);
		aprsComp.add( infoAPRSComment );
		pm.map("Info/Report/APRS/comment", "", infoAPRSComment );

		tpk.pack( l = new JLabel("(max 8 chars)")
			).gridx(2).gridy(ay).inset(10,0,0,0);
		aprsComp.add( l );		
		aprsComp.configure();

		pk.pack( tp ).gridx(0).gridy(++y).fillx().inset(10,4,0,4);
		pk.pack( new JPanel() ).gridx(0).gridy(++y).filly();
		return p;
	}
}