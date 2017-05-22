package org.wonderly.ham.echolink;

import java.util.*;
import javax.swing.*;
import org.wonderly.awt.*;
import java.awt.event.*;
import java.awt.*;

public class QSOHistory extends JDialog {
	Vector<HistoryEntry> hist;
	JList list;
	JFrame par;
	boolean dirty;
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void setDirty( boolean how ) {
		dirty = how;
	}

	public Vector<HistoryEntry> getHistory() {
		return hist;
	}
	
	public void setHistory( Vector<HistoryEntry> v ) {
		hist = v;
		list.setListData(hist);
	}
	
	public void addEntry( StationData sd ) {
		hist.addElement( new HistoryEntry(sd) );
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

	public Point getLocation() {
		return super.getLocation();
	}

	public Dimension getSize() {
		return super.getSize();
	}

	public void showFrame() {
		setVisible(true);
	}

	public QSOHistory( JFrame par ) {
		super( par, "QSO History", false );
		this.par = par;
		hist = new Vector<HistoryEntry>();
		Packer pk = new Packer( getContentPane() );
		list = new JList( );
		list.setListData( hist );
		pk.pack( new JScrollPane( list ) ).gridx(0).gridy(0).fillboth();
		pk.pack( new JSeparator() ).gridx(0).gridy(1).fillx().inset(4,4,4,4);
		final JButton close = new JButton("Close");
		close.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				setVisible(false);
			}
		});
		pk.pack( close ).gridx(0).gridy(2).inset(2,2,2,2);
		pack();
			setLocationRelativeTo( par );
	}
}