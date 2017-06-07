package org.wonderly.ham.echolink;

import java.util.*;
import java.util.List;

import javax.swing.*;
import org.wonderly.awt.*;
import org.wonderly.swing.ListListModel;

import java.awt.event.*;
import java.awt.*;

public class QSOHistory extends JDialog {
	List<HistoryEntry> hist;
	JList<HistoryEntry> list;
	ListListModel<HistoryEntry>mod;
	JFrame par;
	boolean dirty;
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void setDirty( boolean how ) {
		dirty = how;
	}

	public List<HistoryEntry> getHistory() {
		return hist;
	}
	
	public void setHistory( List<HistoryEntry> v ) {
		hist = v;
		mod.setContents(hist);
	}
	
	public void addEntry( StationData sd ) {
		hist.add( new HistoryEntry(sd) );
		mod.setContents( hist );
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
		hist = new ArrayList<HistoryEntry>();
		Packer pk = new Packer( getContentPane() );
		list = new JList<HistoryEntry>( mod = new ListListModel<HistoryEntry>(hist) );
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