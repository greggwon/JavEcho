/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wonderly.ham.echolink.prefs;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import org.wonderly.awt.Packer;
import org.wonderly.swing.prefs.PreferencesMapper;
import org.wonderly.ham.echolink.*;
import org.wonderly.swing.*;

/**
 *
 * @author gregg
 */
public class StationShortcuts extends CancellableDialog {
	private final JTable shrtTbl;
	private volatile int shrtCnt = 0;
	private final DefaultTableModel shrtMod;
	private final PreferencesMapper pm;
	public StationShortcuts( Javecho je, SysopSettings settings, PreferencesMapper ppm ) {
		super( settings, "Station Shortcuts", true );
		Packer pk = new Packer( getContentPane() );
		int y = -1;
		JLabel l;
		Preferences pnn = 
			ppm.getPreferencesNode().node("DTMF");
		final Preferences pr = pnn.node("shortcuts" );
		pm = new PreferencesMapper( pr );
		shrtCnt = pr.getInt("count",0);
		pk.pack( new JScrollPane( shrtTbl = new JTable(
				shrtMod = new DefaultTableModel() {
			@Override
			public int getColumnCount() {
				return 2;
			}
			@Override
			public int getRowCount() {
				return shrtCnt;
			}
			@Override
			public String getColumnName( int col ) {
				switch( col ) {
					case 0: return "Callsign";
					case 1: return "Code";
				}
				return "unknown";
			}
			@Override
			public Object getValueAt( int row, int col ) {
				Preferences pn = pr.node(row+"");
				switch(col) {
					case 0:	return pn.get( "call", "" );
					case 1: return pn.get( "code", "" );
				}
				return "";
			}
			@Override
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
			@Override
			public boolean isCellEditable(int row, int column) {
				return true;
			}
		} ) ) ).gridx(0).gridy(++y).fillboth().inset(6,6,6,6);
		
		JPanel bp = new JPanel();
		Packer bpk = new Packer( bp );
		
		pk.pack( bp ).gridx(1).gridy(y).filly().inset(6,6,6,6);
		JButton addNew = new JButton( "Add New" );
		addNew.addActionListener((ActionEvent ev) -> {
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
		});
		bpk.pack( addNew 
			).gridx(0).gridy(0).fillx().weightx(0);
		final JButton remove = new JButton( "Remove" );
		final JButton removeAll = new JButton( "Remove All" );

		remove.addActionListener((ActionEvent ev) -> {
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
		});
		bpk.pack( remove 
			).gridx(0).gridy(1).fillx().weightx(0).inset(4,0,4,0);

		removeAll.addActionListener((ActionEvent ev) -> {
			pr.putInt("count",shrtCnt=0);
			TableModelEvent tev = new TableModelEvent(
					shrtMod );
			shrtMod.newDataAvailable( tev );
			shrtTbl.clearSelection();
			removeAll.setEnabled(false);
			remove.setEnabled(false);
		});
		bpk.pack( removeAll 
			).gridx(0).gridy(2).fillx().weightx(0);

		remove.setEnabled(false);
		removeAll.setEnabled(shrtCnt > 0);
		shrtTbl.getSelectionModel().addListSelectionListener((ListSelectionEvent ev) -> {
			remove.setEnabled( shrtTbl.getSelectedRowCount() > 0 );
			removeAll.setEnabled( shrtCnt > 0 );
		});
		bpk.pack( new JPanel() ).gridx(0).gridy(3).filly();

		pk.pack( new JSeparator() 
			).gridx(0).gridy(++y).gridw(2).fillx().inset(6,6,6,6);

		JButton lokay = new JButton( "Okay" );
		lokay.requestFocus();
		JButton lcancel = new JButton( "Cancel" );
		pk.pack(lokay ).gridx(0).gridy(++y).west();
//		pk.pack( cancel ).gridx(1).gridy(y).east();

		lokay.addActionListener((ActionEvent ev) -> {
			setCancelled( false );
			if( shrtTbl.isEditing() )
				((DefaultCellEditor)
						shrtTbl.getCellEditor()).stopCellEditing();
			setVisible(false);
		});
		lcancel.addActionListener((ActionEvent ev) -> {
			cancelled = true;
			setVisible(false);
		});
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing( WindowEvent ev ) {
				cancelled = true;
			}
		});
		pack();
		setSize( 300, 300 );
		setLocationRelativeTo(settings);
	}
	@Override
	public void onClose() {
		pm.commit();
	}
}
