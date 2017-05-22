package org.wonderly.ham.echolink;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import org.wonderly.swing.*;
import org.wonderly.awt.*;
import java.util.*;
import java.util.List;

public class StationSummary extends JDialog {
	private static final long serialVersionUID = 1L;
	static final String types[] = new String[] { "",
		"Links", "Repeaters", "Users",
		"Conf Srvrs", "Totals" 
	};
	MyModel mod;
	JTable tbl;
	int row, first, second;
	
	interface Refreshable {
		public void refresh();
	}
	Vector<Refreshable> refs = new Vector<Refreshable>();

	public StationSummary( final Javecho je ) {
		super( je, "Station Summary", false );
		Packer pk = new Packer( getContentPane() );
		int y = -1;
		JPanel sp = new JPanel();
		sp.setBorder(BorderFactory.createTitledBorder( "Stations By Type") );
		Packer spk = new Packer( sp );
		pk.pack( sp ).gridx(0).gridy(0).fillx();
		spk.pack( new JLabel("Type") ).gridx(0).gridy(++y);
		spk.pack( new JLabel("Free") ).gridx(1).gridy(y);
		spk.pack( new JLabel("Busy") ).gridx(2).gridy(y);
		spk.pack( new JLabel("Total") ).gridx(3).gridy(y);
		spk.pack( new JSeparator() ).gridx(0).gridy(++y).fillx().inset(4,4,4,4);
		spk.pack( new JSeparator() ).gridx(1).gridy(y).fillx().inset(4,4,4,4);
		spk.pack( new JSeparator() ).gridx(2).gridy(y).fillx().inset(4,4,4,4);
		spk.pack( new JSeparator() ).gridx(3).gridy(y).fillx().inset(4,4,4,4);
		for( int i = 0; i < types.length-1; ++i ) {
			if( i == types.length - 2 )
				spk.pack( new JSeparator() ).gridx(0).gridy(++y).gridw(4).fillx().inset(0,0,4,0);
			Counter ct = new Counter( i+1, spk, ++y, je );
			refs.addElement(ct);
		}
		
		JPanel cp = new JPanel();
		cp.setBorder( BorderFactory.createTitledBorder( 
			"Stations By Country") );
		Packer cpk = new Packer( cp );
		pk.pack( cp ).gridx(0).gridy(1).fillboth();
		mod = new MyModel( je );
		refs.addElement(mod);
		cpk.pack( new JScrollPane( tbl = new JTable(mod) {
			private static final long serialVersionUID = 1L;

			public void setEnabled( boolean how ) {
				super.setEnabled(how);
				setOpaque(how);
			}
		}) ).gridx(0).gridy(++y).gridw(4).fillboth();
		tbl.getSelectionModel().addListSelectionListener( new ListSelectionListener() {			
			public void valueChanged( ListSelectionEvent ev ) {
				System.out.println("Table Selection: "+ev);
				if( ev.getValueIsAdjusting() )
					return;
				row = ev.getLastIndex();
			}
		});
		final JLabel tlab = new JLabel();
		tbl.setDefaultRenderer( Object.class, new TableCellRenderer() {
			public Component getTableCellRendererComponent(
				JTable table, Object value,
					boolean sel, boolean focus, int row, int col ) {
				tlab.setText( value.toString() );
				if( sel ) {
					tlab.setForeground( Color.white );
					tlab.setBackground( Color.blue );
				} else {
					tlab.setBackground( table.isEnabled() ?
						Color.white : tbl.getParent().getBackground() );
					tlab.setForeground( Color.black );
				}
				return tlab;
			}
		});
		tbl.addMouseListener( new MouseAdapter() {
			public void mouseClicked( MouseEvent ev ) {
				if( ev.getClickCount() == 1 ) {
					first = row;
				}
				if( ev.getClickCount() == 2 ) {
					second = row;
					if( second != first )
						return;
					new ComponentUpdateThread<Void>( tbl ) {
						public Void construct() {
							je.showCountry( (String)mod.getValueAt( second, 0 ) );
							return null;
						}
					}.start();
				}
			}
		});
		pack();
		setSize( 250, 350 );
		setLocationRelativeTo( je );
		setVisible(true);
		new Thread() {
			public void run() {
				refresh();
			}
		}.start();
	}
	
	public void refresh() {
		new Thread() {
			public void run() {
				for(int i = 0; i < refs.size(); ++i) {
					((Refreshable)refs.elementAt(i)).refresh();
				}
			}
		}.start();
	}

	class MyModel extends DefaultTableModel implements Refreshable {
		private static final long serialVersionUID = 1L;
		Javecho je;
		ArrayList<RowEnt> rows;
		boolean rebuilding;

		class RowEnt {
			String name;
			int cnt;
			public RowEnt( String name, int cnt ) {
				this.name = name;
				this.cnt = cnt;
			}
			public String toString() {
				return name+": "+cnt;
			}
		}
		public void refresh() {
			new ComponentUpdateThread<Void>( tbl ) {
				public void setup() {
					super.setup();
					setTableTip( "Rebuilding Station List...");
					rebuilding = true;
					newDataAvailable(new TableModelEvent(MyModel.this));
					tbl.repaint();
				}
				public Void construct() {
					rebuildList();
					return null;
				}
				public void finished() {
					try {
						rebuilding = false;
						setTableTip( null );
						newDataAvailable(new TableModelEvent(MyModel.this));
						tbl.repaint();
					} finally {
						super.finished();
					}
				}
			}.start();
		}
		public MyModel( final Javecho je ) {
			this.je = je;
		}
		private void rebuildList() {
			final Map<String,Integer> h = 
				new HashMap<String,Integer>();
			List<Entry> v = je.getStationList();
			setTableTip("Counting sites...");
			System.out.println("model rebuilding country list: "+v.size() );
			for( int i = 0; i < v.size(); ++i ) {
				Entry e = v.get(i);
				String call = e.getStation().getCall();
				if( call == null || call.length() < 2 || call.charAt(0) == '*' || e.getType() == Entry.TYPE_MSG )
					continue;
				String c;
				c = CountryAccess.countryFor( call );
				Integer sv;
				int vl = 0;
				if( c == null ) {
					System.out.println(" no country for: \""+
						e.getStation().getCall()+"\" ("+
						(byte)e.getStation().getCall().charAt(0)+")" );
					continue;
				}
				if( (sv = (Integer)h.get(c)) != null )
					vl = sv.intValue();
				h.put( c, new Integer( vl + 1 ) );
			}
			rows = new ArrayList<RowEnt>();
			for( String key : h.keySet() ) {
				rows.add( new RowEnt( key,((Integer)h.get(key)).intValue() ) );
			}
			setTableTip("Sorting sites...");
			Collections.sort(rows,new Comparator<RowEnt>() {
				public int compare( RowEnt o1, RowEnt o2 ) {
					return o2.cnt - o1.cnt;
				}
			});
			setTableTip("Counts by station location");
			newDataAvailable( new TableModelEvent( this ) );
		}
		public boolean isCellEditable( int row, int col ) {
			return false;
		}
		public String getColumnName( int col ) {
			return new String[]{"Country", "Count"}[col];
		}
		public int getColumnCount() {
			return 2;
		}
		public int getRowCount() {
			if( rebuilding || rows == null )
				return 10;
//			if( rows == null )
//				return 0;
			return rows.size();
		}
		public Object getValueAt( int row, int column ) {
			RowEnt re = null;
			if( rows != null && row < rows.size() )
				re = rows.get(row);
			if( re == null )
				return "";
			if( column == 0 )
				return re.name;
			return re.cnt+"";
		}
	}
	
	private void setTableTip( final String tip ) {
		runInSwing( new Runnable() {
			public void run() {
				tbl.setToolTipText( tip );
			}
		});
	}

	static class Counter implements Refreshable {
		JLabel free, busy, total;
		Javecho je;
		String name;
		int type;
		int lfree = -1, lbusy = -1, ltotal = -1;
		public void refresh() {
			recount();
		}				

		public Counter( int typ, Packer pk, int y, final Javecho je ) {
			this.je = je;
			type = typ;
			if( typ == 5 )
				name = "Totals";
			else
				name = Entry.typeName(typ)+"s";
			pk.pack( new JLabel( name+":" ) ).gridx(0).gridy(y).east().fillx().inset(0,5,0,0);
			pk.pack( free = new JLabel("0", JLabel.RIGHT ) ).
				gridx(1).gridy(y).east().fillx().inset(0,4,0,4);
			pk.pack( busy = new JLabel("0", JLabel.RIGHT ) ).
				gridx(2).gridy(y).east().fillx().inset(0,4,0,4);
			pk.pack( total = new JLabel("0", JLabel.RIGHT ) ).
				gridx(3).gridy(y).east().fillx().inset(0,4,0,4);
		}
		
		void recount() {
			try {
				List<Entry> v = je.getStationList();
				int tot = 0;
				int bus = 0;
				for( int i = 0; i < v.size(); ++i ) {
					Entry e = (Entry)v.get(i);
					if( ( type != 5 && e.getType() != type ) || e.getType() == Entry.TYPE_MSG )
						continue;
					++tot;
					if( e.isBusy() ) ++bus;
				}
				final int ntotal = tot, nfree = tot-bus, nbusy = bus;
				SwingUtilities.invokeAndWait( new Runnable() {
					public void run() {
						lfree = colorForValue( free, nfree, lfree );
						ltotal = colorForValue( total, ntotal, ltotal );
						lbusy = colorForValue( busy, nbusy, lbusy );
					}
				});
			} catch( Throwable ex ) {
			}
		}
		int colorForValue( JLabel lab, int cur, int last ) {
			lab.setText( ""+cur );
			if( last == -1 || last == cur )
				lab.setForeground( Color.black );
			else if( last < cur )
				lab.setForeground( Color.green.darker() );
			else
				lab.setForeground( Color.red );
			lab.revalidate();
			lab.repaint();
			return cur;
		}
	}
	public void runInSwing( final Runnable r ) {
		if( SwingUtilities.isEventDispatchThread() ) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait( r );
			} catch( Exception ex ) {
			}
		}
	}
}