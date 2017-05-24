package org.wonderly.ham.echolink;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.wonderly.awt.*;
import org.wonderly.swing.*;

public class StatPanel extends JPanel {
	public int rCtlI, rDtI, rSeqI, rMisI;
	public int sCtlI, sDtI;

	public StatPanel( Font f, Javecho je ) {
		Packer pk = new Packer( this );
		JPanel inbp = new JPanel();
		inbp.setBackground( SystemColor.activeCaption );
		
		pk.pack( inbp ).gridx(0).gridy(0).inset(0,0,0,10);
		Packer inbpk = new Packer( inbp );
		int inx = -1;
		final JLabel
			rCtl = new LoweredLabel("0",f),
			rDt  = new LoweredLabel("0",f),
			rSeq = new LoweredLabel("0",f),
			rMis= new LoweredLabel("0",f);

		JLabel l;
		inbp.setBorder( BorderFactory.createEtchedBorder() );
		JButton ib;
		inbpk.pack( ib = new JButton( "IN" ) ).gridx(++inx).gridy(0);
		ib.setFont(f);
		ib.setMargin( new Insets(0,0,0,0) );
		ib.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				rCtlI = 0;
				rDtI = 0;
				rSeqI = 0;
				rMisI = 0;
				rCtl.setText("0");
				rDt.setText("0");
				rSeq.setText("0");
				rMis.setText("0");
			}
		});
//		l.setBorder( BorderFactory.createRaisedBevelBorder() );
		inbpk.pack( l=new InfoLabel(" ctl") ).gridx(++inx).gridy(0);
		l.setFont(f);
		inbpk.pack( rCtl ).gridx(++inx).gridy(0);
		inbpk.pack( l=new InfoLabel(" dt") ).gridx(++inx).gridy(0);
		l.setFont(f);
		inbpk.pack( rDt ).gridx(++inx).gridy(0);
		inbpk.pack( l=new InfoLabel(" seq") ).gridx(++inx).gridy(0);
		l.setFont(f);
		inbpk.pack( rSeq ).gridx(++inx).gridy(0);
		inbpk.pack( l=new InfoLabel(" drp") ).gridx(++inx).gridy(0);
		l.setFont(f);
		inbpk.pack( rMis ).gridx(++inx).gridy(0);
		
		inbp = new JPanel();
		inbp.setBackground( SystemColor.activeCaption.brighter() );
		inbp.setBorder( BorderFactory.createEtchedBorder() );
		pk.pack( inbp ).gridx(1).gridy(0).inset(0,0,0,10);
		inbpk = new Packer( inbp );
		inx = -1;
//	JLabel sDt, sCtl, rDt, rMis, rSeq, rCtl;
		final JLabel
			sCtl = new LoweredLabel("0",f),
			sDt = new LoweredLabel("0",f);
		JButton ob;
		inbpk.pack( ob = new JButton( "OUT" ) ).gridx(++inx).gridy(0);
		ob.setFont(f);
		ob.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ev ) {
				sCtlI = 0;
				sDtI = 0;
				sCtl.setText("0");
				sDt.setText("0");
			}
		});
		ob.setMargin( new Insets(0,0,0,0));
		inbpk.pack( l=new InfoLabel(" ctl") ).gridx(++inx).gridy(0);
		l.setFont(f);
		inbpk.pack( sCtl ).gridx(++inx).gridy(0);
		inbpk.pack( l=new InfoLabel(" dt") ).gridx(++inx).gridy(0);
		l.setFont(f);
		inbpk.pack( sDt ).gridx(++inx).gridy(0);
		
		je.addLinkEventListener( new LinkEventListener() {

			public void processEvent( LinkEvent ev ) {
				if( ev.isSend() )
					sendEvent(ev);
				else
					recvEvent(ev);
			}
			
			private void sendEvent( LinkEvent ev ) {
				switch( ev.getType() ) {
				case LinkEvent.MICDATA_EVENT:
					SwingStuff.runInSwingLater( new Runnable() {
						public void run() {
							sDtI++;
							sDt.setText( sDtI+"" );
							sDt.repaint();
						}
					});
					break;
				case LinkEvent.CONN_EVENT:
				case LinkEvent.DISC_EVENT:
				case LinkEvent.INFO_EVENT:
					SwingStuff.runInSwingLater( new Runnable() {
						public void run() {
							sCtlI++;
							sCtl.setText( sCtlI+"" );
							sCtl.repaint();
						}
					});
					break;
				}
			}
			
			private void recvEvent( LinkEvent ev ) {
				switch( ev.getType() ) {
				case LinkEvent.NETDATA_EVENT:
					SwingStuff.runInSwingLater( new Runnable() {
						public void run() {
							rDtI++;
							rDt.setText( rDtI+"" );
							rDt.repaint();
						}
					});
					break;
				case LinkEvent.MISSED_DATA:
					SwingStuff.runInSwingLater( new Runnable() {
						public void run() {
							++rMisI;
							rMis.setText( rMisI+"" );
							rMis.repaint();
						}
					});
					break;
				case LinkEvent.OUT_OF_SEQUENCE_DATA:
					SwingStuff.runInSwingLater( new Runnable() {
						public void run() {
							++rSeqI;
							rSeq.setText( rSeqI+"" );
							rSeq.repaint();
						}
					});
					break;
				case LinkEvent.CONN_EVENT:
				case LinkEvent.DISC_EVENT:
				case LinkEvent.INFO_EVENT:
					SwingStuff.runInSwingLater( new Runnable() {
						public void run() {
							rCtlI++;
							rCtl.setText( rCtlI+"" );
							rCtl.repaint();
						}
					});
					break;
				}
			}
		});
	}
}