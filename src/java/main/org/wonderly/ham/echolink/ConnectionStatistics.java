package org.wonderly.ham.echolink;

import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import org.wonderly.awt.*;
import java.awt.event.*;

public class ConnectionStatistics
		extends JDialog 
		implements AudioEventListener,
			LinkEventListener {
	Javecho je;
	JTable slist;
	Vector<StationStats> smod = new Vector<StationStats>();
	JLabel sCtl, sDt;
	JLabel rCtl, rDt, rSeq, rMis;
	JProgressBar pSnd, pNet, pAud;
	DefaultTableModel tmod;
	
	class StationStats {
		StationData dt;
		int sCtlI, sDtI;
		int rCtlI, rDtI, rSeqI, rMisI;
		public StationStats( StationData entry ) {
			dt = entry;
		}
		public String toString() {
			return dt.toString();
		}
		public boolean equals( Object obj ) {
			if( obj instanceof StationStats == false )
				return false;
			return ((StationStats)obj).dt.equals(dt);
		}
		public int hashCode() {
			return dt.hashCode();
		}
	}

	public synchronized void addConnectedStation( StationData en ) {
		StationStats ss = new StationStats( en );
		if( smod.contains( ss ) == false ) {
			smod.addElement(ss);
			runInSwing( new Runnable() {
				public void run() {
					tmod.newDataAvailable(new TableModelEvent(tmod));
					slist.repaint();
				}
			});
		}
	}

	public synchronized void removeConnectedStation( StationData en ) {
		for( int i = 0; i < smod.size(); ++i ) {
			StationStats ss = smod.elementAt(i);
			if( ss.dt.equals(en) ) {
				smod.removeElementAt(i);
				break;
			}
		}
		runInSwing( new Runnable() {
			public void run() {
				tmod.newDataAvailable(new TableModelEvent(tmod));
				slist.repaint();
			}
		});
	}

	public void processEvent( LinkEvent ev ) {
		if( ev.isSend() )
			sendEvent(ev);
		else
			recvEvent(ev);
	}
	
	public synchronized void setTransmittingStation( StationData en ) {
		for( int i = 0; i < smod.size(); ++i ) {
			StationStats ss = smod.elementAt(i);
			if( ss.dt.equals(en) ) {
				curSt = ss;
				final int si = i;
				runInSwing( new Runnable() {
					public void run() {
						slist.getSelectionModel().setSelectionInterval(si,si);
						slist.repaint();
					}
				});
				break;
			}
		}
	}
	
	private StationStats curSt;
	private void sendEvent( LinkEvent ev ) {
		if( curSt == null )
			return;
		switch( ev.getType() ) {
		case MICDATA_EVENT:
			runInSwing( new Runnable() {
				public void run() {
					curSt.sDtI++;
					sDt.setText( curSt.sDtI+"" );
					sDt.repaint();
				}
			});
			break;
		case CONN_EVENT:
		case DISC_EVENT:
		case INFO_EVENT:
			runInSwing( new Runnable() {
				public void run() {
					curSt.sCtlI++;
					sCtl.setText( curSt.sCtlI+"" );
					sCtl.repaint();
				}
			});
			break;
		}
	}
	
	private void recvEvent( LinkEvent ev ) {
		switch( ev.getType() ) {
		case NETDATA_EVENT:
			runInSwing( new Runnable() {
				public void run() {
					curSt.rDtI++;
					rDt.setText( curSt.rDtI+"" );
					rDt.repaint();
				}
			});
			break;
		case MISSED_DATA:
			runInSwing( new Runnable() {
				public void run() {
					++curSt.rMisI;
					rMis.setText( curSt.rMisI+"" );
					rMis.repaint();
				}
			});
			break;
		case OUT_OF_SEQUENCE_DATA:
			runInSwing( new Runnable() {
				public void run() {
					++curSt.rSeqI;
					rSeq.setText( curSt.rSeqI+"" );
					rSeq.repaint();
				}
			});
			break;
		case CONN_EVENT:
		case DISC_EVENT:
		case INFO_EVENT:
			runInSwing( new Runnable() {
				public void run() {
					curSt.rCtlI++;
					rCtl.setText( curSt.rCtlI+"" );
					rCtl.repaint();
				}
			});
			break;
		}
	}

	public ConnectionStatistics( Javecho je ) {
		super( je, "Connection Statistics", false );
		this.je = je;
		je.addLinkEventListener(this);
		je.addAudioEventListener(this);
		Packer pk = new Packer( getContentPane() );
		int y = -1;
		slist = new JTable( tmod = new DefaultTableModel() {
			public int getRowCount() {
				return smod.size();
			}
			public int getColumnCount() {
				return 2;
			}
			public String getColumnName( int col ) {
				return new String[] { "Station", "Net Info" }[col];
			}
			public Object getValueAt( int row, int col ) {
				StationData sd = ((StationStats)smod.elementAt(row)).dt;
				if( sd == null )
					return "";
				switch( col ) {
					case 0:
						return sd.toString();
					case 1:
						return sd.getIPAddr();
				}
				return "unknown";
			}
		});
		slist.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		slist.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent ev ) {
				int i = slist.getSelectionModel().
					getLeadSelectionIndex();
				if( smod.size() > i && i >= 0 ) {
					StationStats ss = smod.elementAt(i);
					curSt = ss;
				} else {
					curSt = null;
				}
			}
		});
		pk.pack( new JScrollPane( slist ) ).gridx(0).gridy(++y).gridw(3).fillboth();
		JPanel sp = new JPanel();
		sp.setBorder( BorderFactory.createTitledBorder("Sent") );
		Packer spk = new Packer( sp );
		spk.pack( new JLabel( "Control Packets:" ) ).gridx(0).gridy(0).west();
		spk.pack( sCtl = new JLabel( "0",JLabel.RIGHT ) ).gridx(1).gridy(0).fillx();
		spk.pack( new JLabel( "Data Packets:") ).gridx(0).gridy(1).west();
		spk.pack( sDt = new JLabel( "0",JLabel.RIGHT ) ).gridx(1).gridy(1).fillx();
		pk.pack( sp ).gridx(0).gridy(++y).fillx().gridw(2);
		
		JPanel rp = new JPanel();
		Packer rpk = new Packer( rp );
		pk.pack( rp ).gridx(2).gridy(y).fillx().gridh(3);
		int ry = -1;
		rp.setBorder( BorderFactory.createTitledBorder( "Received") );
		rpk.pack( new JLabel( "Control Packets:" ) ).gridx(0).gridy(++ry).west();;
		rpk.pack( rCtl = new JLabel( "0", JLabel.RIGHT ) ).gridx(1).gridy(ry).fillx();
		rpk.pack( new JLabel( "Data Packets:" ) ).gridx(0).gridy(++ry).west();
		rpk.pack( rDt = new JLabel( "0", JLabel.RIGHT ) ).gridx(1).gridy(ry).fillx();
		rpk.pack( new JLabel( "Out of Sequence:" ) ).gridx(0).gridy(++ry).west();
		rpk.pack( rSeq = new JLabel( "0", JLabel.RIGHT ) ).gridx(1).gridy(ry).fillx();
		rpk.pack( new JLabel( "Missed:") ).gridx(0).gridy(++ry).west();
		rpk.pack( rMis = new JLabel( "0", JLabel.RIGHT ) ).gridx(1).gridy(ry).fillx();
		
		pk.pack( new JLabel("Send:") ).gridx(0).gridy(++y).west().inset(0,4,0,0);
		pk.pack( pSnd = new JProgressBar() ).gridx(1).gridy(y).fillx();
		pk.pack( new JLabel("Net:") ).gridx(0).gridy(++y).west().inset(0,4,0,0);
		pk.pack( pNet = new JProgressBar() ).gridx(1).gridy(y).fillx();
		pk.pack( new JLabel("Audio:") ).gridx(0).gridy(++y).west().inset(0,4,0,0);
		pk.pack( pAud = new JProgressBar() ).gridx(1).gridy(y).fillx();
		
		final JButton clr = new JButton("Clear");
		clr.setMargin( new Insets(0,0,0,0) );
		pk.pack( clr ).gridx(2).gridy(y).east();
		pack();
		setSize( 300, 400 );
		setLocationRelativeTo( this.je );
	}

	public void setSoundTotal( final int val ) {
		runInSwing( new Runnable() {
			public void run() {
				pAud.setMaximum( val );
			}
		});
	}
	public void setSoundCurrent( final int val ) {
		runInSwing( new Runnable() {
			public void run() {
				pAud.setValue(val);
			}
		});
	}

	public void setNetTotal( final int val ) {
		runInSwing( new Runnable() {
			public void run() {
				pNet.setMaximum( val );
			}
		});
	}
	public void setNetCurrent( final int val ) {
		runInSwing( new Runnable() {
			public void run() {
				pNet.setValue(val);
			}
		});
	}

	public void setSendTotal( final int val ) {
		runInSwing( new Runnable() {
			public void run() {
				pSnd.setMaximum( val );
			}
		});
	}
	public void setSendCurrent( final int val ) {
		runInSwing( new Runnable() {
			public void run() {
				pSnd.setValue(val);
			}
		});
	}
	
	private void runInSwing( final Runnable r ) {
		if( SwingUtilities.isEventDispatchThread() ) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(r);
			} catch( Exception ex ) {
			}
		}
	}
}