package org.wonderly.ham.echolink;

import java.util.*;
import javax.swing.*;
import org.wonderly.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.*;

public class AlarmEditor extends JDialog {
	Vector<String> hist;
	JList list;
	JFrame par;
	boolean dirty;
	public boolean isDirty() {
		return dirty;
	}
	
	public void setDirty( boolean how ) {
		dirty = how;
	}

	public Point getLocation() {
		return super.getLocation();
	}

	public Dimension getSize() {
		return super.getSize();
	}

	public Vector getHistory() {
		return hist;
	}

	public void setHistory( Vector<String> v ) {
		hist = v;
		list.setListData(hist);
	}
	
	public void addEntry( String call ) {
		if( hist.contains(call) )
			hist.removeElement(call);
		hist.addElement( call );
		list.setListData( hist );
		if( isVisible() ) {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					list.ensureIndexIsVisible( hist.size() - 1 );
				}
			});
		}
		dirty = true;
	}

	
	public void showFrame() {
		setVisible(true);
	}

	public AlarmEditor( final JFrame par) {
		super( par, "Alarms", false );
		this.par = par;
		hist = new Vector<String>();
		Packer pk = new Packer( getContentPane() );
		list = new JList( );
		list.setListData( hist );
		JTextArea msg = new JTextArea( 
			"Alarm if any of the following stations "+
			"come online or change status:");
		msg.setWrapStyleWord(true);
		msg.setLineWrap(true);
		msg.setOpaque(false);
		msg.setEditable(false);
		pk.pack( msg ).gridx(0).gridy(0).fillx().gridh(2).inset(4,4,4,4);
		msg.setBorder( BorderFactory.createEtchedBorder() );
		pk.pack( new JScrollPane( list ) ).gridx(0).gridy(2).fillboth().gridh(5).inset(4,4,4,4);
		final JButton okay = new JButton("Close");
		final JButton add = new JButton("Add");
		final JButton remove = new JButton("Remove");
		final JButton removeall = new JButton("Remove All");
//		final JButton cancel = new JButton("Cancel");

		pk.pack( okay ).gridx(1).gridy(0).inset(2,2,2,2).fillx().weightx(0);
		pk.pack( new JPanel() ).gridx(1).gridy(1).inset(2,2,2,2).filly().weighty(0);
		pk.pack( new JPanel() ).gridx(1).gridy(2).filly();
		pk.pack( add ).gridx(1).gridy(3).inset(2,2,2,2).fillx().weightx(0);
		pk.pack( remove ).gridx(1).gridy(4).inset(2,2,2,2).fillx().weightx(0);
		pk.pack( removeall ).gridx(1).gridy(5).inset(2,2,2,2).fillx().weightx(0);
		pk.pack( new JPanel() ).gridx(1).gridy(6).filly();
		remove.setEnabled(false);
		list.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent ev ) {
				remove.setEnabled( list.getSelectedIndex() != -1 );
			}
		});

		okay.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				setVisible(false);
			}
		});
		add.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				String str = JOptionPane.showInputDialog( par, "Call Sign?" );
				if( str == null )
					return;
				hist.addElement(str);
				list.setListData(hist);
				dirty = true;
			}
		});
		remove.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				hist.removeElementAt( list.getSelectedIndex() );
				list.setListData( hist );
				dirty = true;
			}
		});
		removeall.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				int idx = JOptionPane.showConfirmDialog( AlarmEditor.this, "Remove All Entries?", 
					"Remove All Confirmation",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
				if( idx == JOptionPane.CANCEL_OPTION )
					return;
				list.setListData( hist = new Vector<String>() );
				dirty = true;
			}
		});
//		cancel.addActionListener( new ActionListener() {
//			public void actionPerformed( ActionEvent ev ) {
//				setVisible(false);
//			}
//		});
		pack();
		setLocationRelativeTo( par );
	}
}