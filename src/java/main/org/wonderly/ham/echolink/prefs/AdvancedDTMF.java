/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wonderly.ham.echolink.prefs;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import org.wonderly.awt.Packer;
import org.wonderly.ham.echolink.SysopSettings;
import org.wonderly.swing.prefs.PreferencesMapper;
import org.wonderly.swing.prefs.SwingPreferencesMapper;
import org.wonderly.ham.echolink.*;
import org.wonderly.swing.*;
/**
 *
 * @author gregg
 */
public class AdvancedDTMF extends CancellableDialog {
	private final SwingPreferencesMapper pm;
	private final SysopSettings.GrayedTextField dtmfFineTune, dtmfFreqToler, dtmfSNRatio, dtmfDBTwist;
	public AdvancedDTMF( Javecho je, SysopSettings settings, PreferencesMapper ppm ) {
		super( settings, 
			"Advanced DTMF Settings", true );
		Packer pk = new Packer( getContentPane() );
		int y = -1;
		JLabel l;

		pm = new SwingPreferencesMapper( 
			ppm.getPreferencesNode().node("DTMF" ) );
		pk.pack( l = new JLabel("Fine Tuning (%):") 
			).gridx(0).gridy(++y);
		pk.pack( dtmfFineTune = new SysopSettings.GrayedTextField()
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( dtmfFineTune );
		pm.map( "finetuning", 0, dtmfFineTune );

		pk.pack( l = new JLabel("Freq Tolerance (%):")
			).gridx(0).gridy(++y);
		pk.pack( dtmfFreqToler = new SysopSettings.GrayedTextField()
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( dtmfFreqToler );
		pm.map( "freqtolerance", 2, dtmfFreqToler );

		pk.pack( l = new JLabel("S/N Radio (db):")
			).gridx(0).gridy(++y);
		pk.pack( dtmfSNRatio = new SysopSettings.GrayedTextField()
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( dtmfSNRatio );
		pm.map( "snratio", 12, dtmfSNRatio );

		pk.pack( l = new JLabel("Twist (db):")
			).gridx(0).gridy(++y);
		pk.pack( dtmfDBTwist = new SysopSettings.GrayedTextField() 
			).gridx(1).gridy(y).fillx();
		l.setLabelFor( dtmfDBTwist );
		pm.map( "twistdb", 4, dtmfDBTwist );

		JButton resDefs = new JButton( "Reset to Default" );
		pk.pack( resDefs ).gridx(0).gridy(++y);
		pk.pack( new JSeparator()
			).gridx(0).gridy(++y).gridw(2).fillx().inset(6,6,6,6);
		resDefs.addActionListener((ActionEvent ev) -> {
			dtmfFineTune.setText( "0" );
			dtmfFreqToler.setText( "2" );
			dtmfSNRatio.setText( "12" );
			dtmfDBTwist.setText( "4" );
		});
		
		JButton okay = new JButton( "Okay" );
		JButton cancel = new JButton( "Cancel" );
		pk.pack( okay ).gridx(0).gridy(++y).west();
		pk.pack( cancel ).gridx(1).gridy(y).east();
		okay.requestFocus();
		okay.addActionListener((ActionEvent ev) -> {
			setCancelled( false );
			setVisible(false);
		});
		cancel.addActionListener((ActionEvent ev) -> {
			setCancelled( true );
			setVisible(false);
		});
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing( WindowEvent ev ) {
				setCancelled( true );
			}
		});
		pack();
		setLocationRelativeTo(settings);
	}
	@Override
	public void onClose() {	
		pm.commit();
	}	
}
