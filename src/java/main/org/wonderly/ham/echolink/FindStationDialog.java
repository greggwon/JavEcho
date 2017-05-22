package org.wonderly.ham.echolink;

import org.wonderly.awt.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import org.wonderly.swing.*;

public class FindStationDialog extends JDialog {
	static Vector list = new Vector();
	FindHandler hand;
	public FindStationDialog( JFrame parent, final FindHandler hand ) {
		super( parent, "Find Station by Call", false );
		this.hand = hand;
		Packer pk =new Packer( getContentPane() );
		final JComboBox bx = new JComboBox( list );
		final JButton find = new JButton("Find");
		pk.pack( bx ).gridx(0).gridy(0).fillx().inset(10,4,10,4 );
		bx.addActionListener( new ActionListener() {	
			public void actionPerformed( ActionEvent ev ) {
				new ComponentUpdateThread( find ) {
					public Object construct() {
						hand.locate( (String)bx.getSelectedItem() );
						return null;
					}
				}.start();
			}
		});
		bx.setEditable(true);
		pk.pack( find ).gridx(1).gridy(0).inset(10,4,10,4);
		find.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				new ComponentUpdateThread( find ) {
					public Object construct() {
						hand.locate( (String)bx.getSelectedItem() );
						return null;
					}
				}.start();
			}
		});
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
}