/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wonderly.ham.echolink.prefs;

import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import org.wonderly.awt.Packer;
import org.wonderly.swing.ComponentUpdateThread;
import org.wonderly.swing.prefs.PreferencesMapper;
import org.wonderly.swing.prefs.SwingPreferencesMapper;

import org.wonderly.swing.*;
import org.wonderly.ham.echolink.*;

/**
 *
 * @author gregg
 */
public class MorseSettings extends CancellableDialog {

	private final JSlider morsePitch, morseSpeed, morseLevel;
	private final SwingPreferencesMapper pm;
	SysopSettings settings;
	Javecho je;
	
	public MorseSettings( Javecho je, SysopSettings settings, PreferencesMapper ppm,
			final String morseId ) {
		super( settings, "Morse Settings", true );
		this.je = je;
		this.settings = settings;
		Packer pk = new Packer( getContentPane() );
		int y = -1;
		JLabel l;

		pm = new SwingPreferencesMapper( 
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
		morseSpeed.addChangeListener((ChangeEvent ev) -> {
			spdVal.setText( morseSpeed.getValue()+"" );
			je.setMorseWPM( morseSpeed.getValue() );
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
		morsePitch.addChangeListener((ChangeEvent ev) -> {
			ptchVal.setText( morsePitch.getValue()+"" );
			try {
				je.setMorseIdFreq( morsePitch.getValue() );
			} catch( UnsupportedOperationException ex ) {
				JOptionPane.showMessageDialog( this, ex );
				morsePitch.setEnabled(false);
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
		morseLevel.addChangeListener((ChangeEvent ev) -> {
			lvlVal.setText( (morseLevel.getValue()/10.0)+"" );
			try {
				je.setMorseIdVolume( morseLevel.getValue() );
			} catch( IOException ex ) {
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

		okay.addActionListener((ActionEvent ev) -> {
			setCancelled( false );
			setVisible(false);
		});
		test.addActionListener((ActionEvent ev) -> {
			new ComponentUpdateThread( test ) {
				@Override
				public Object construct() {
					settings.testStationIdMorse(morseId);
					return null;
				}
			}.start();
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
