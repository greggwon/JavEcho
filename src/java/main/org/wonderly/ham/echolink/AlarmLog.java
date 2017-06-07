package org.wonderly.ham.echolink;

import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import org.wonderly.awt.*;
import org.wonderly.swing.*;
import java.awt.event.*;
import java.awt.*;

public class AlarmLog extends JDialog {
	List<StationData> hist;
	JList list;
	JFrame par;
	Javecho je;

	public List<StationData> getHistory() {
		return hist;
	}
	
	public void setHistory( List<StationData> v ) {
		hist = v;
		list.setListData(hist.toArray());
	}
	
	public void addEntry( StationData call ) {
		if( hist.contains(call) )
			hist.remove(call);
		hist.add( call );
		list.setListData( hist.toArray() );
		if( isVisible() ) {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					list.ensureIndexIsVisible( hist.size() - 1 );
				}
			});
		}
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

	StationData first;
	StationData second;
	public AlarmLog( Javecho jecho, JFrame par ) {
		super( par, "Alarm Log", false );
		this.je = jecho;
		this.par = par;
		hist = new Vector<StationData>();
		Packer pk = new Packer( getContentPane() );
		list = new JList( );
		list.setListData( hist.toArray() );
		pk.pack( new JScrollPane( list ) ).gridx(0).gridy(0).fillboth().inset(4,4,4,4);
//		list.addListSelectionListener( new ListSelectionListener() {
//			public void valueChanged( ListSelectionEvent ev ) {
//				if( ev.getValueIsAdjusting() )
//					return;
//				StationData sd = (StationData)list.getSelectedValue();
//				if( sd == null )
//					return;
//				System.out.println("first == second? "+(first==second)+", first: "+first+", second: "+second );
//			}
//		});
		list.addMouseListener( new MouseAdapter() {
			public void mouseClicked( MouseEvent ev ) {
				if( ev.getClickCount() == 1 ) {
					first = (StationData)list.getSelectedValue();
					second = null;
					System.out.println("first: "+first );
				} else if( ev.getClickCount() == 2 ) {
					second = (StationData)list.getSelectedValue();
					System.out.println("second: "+second );
					if( first == second && first != null ) {
						new ComponentUpdateThread( list ) {
							public Object construct() {
								try {
									je.connectTo( first, false );
								} catch( Exception ex ) {
									je.reportException(ex);
								}
								return null;
							}
						}.start();
					}
				}
			}
		});
		final JButton close = new JButton("Close");
		pk.pack( new JSeparator() ).gridx(0).gridy(1).fillx().inset(4,4,4,4);
		pk.pack( close ).gridx(0).gridy(2);
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