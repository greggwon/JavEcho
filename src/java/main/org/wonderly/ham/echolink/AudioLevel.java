package org.wonderly.ham.echolink;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.logging.*;

import org.wonderly.awt.*;

/**
 *  Audio level display component
 */
public class AudioLevel extends JPanel {
	int maximum;
	JProgressBar b;
	JSlider vox;
	CardLayout cards;
	JPanel cardp;
	Javecho je;
	Logger log = Logger.getLogger( getClass().getName() );

	public void setVox( boolean how ) {
		log.info("Vox receive: "+how );
		try {
			if(how) {
				je.setMode(LinkEvent.MODE_SYSOPRECEIVE);
				if( je.ssa != null ) je.ssa.voxTransmit();
			} else {
				if( je.ssa != null ) je.ssa.receive();
				je.setMode(LinkEvent.MODE_SYSOPIDLE);
			}
		} catch( Exception ex ) {
			log.log( Level.SEVERE, ex.toString(), ex );
		}
		cards.show( cardp, how ? "vox" : "blank" );
		cardp.repaint();
	}

	JLabel volVal;
	public AudioLevel( final Javecho je, int low, int high ) {
		this.je = je;
		Packer pk = new Packer( this );
		b = createBar(low, high);
		pk.pack( b ).gridx(0).gridy(0).fillx();
		volVal = new JLabel("0000");
		volVal.setOpaque(false);
		pk.pack( volVal ).gridx(1).gridy(0).inset(2,2,2,2).fillx().weightx(0);
		cardp = new JPanel();
		cards = new CardLayout();
		cardp.setLayout(cards);
		cardp.setOpaque(false);
		pk.pack( cardp ).gridx(0).gridy(1).fillx().gridw(2);
		JPanel bp;
		cardp.add( "blank", bp = new JPanel() );
		bp.setOpaque(false);
		setOpaque(false);
//			bp.setOpaque(true);
//			bp.setBackground( getBackground() );
		log.fine("initial vox limit: "+je.pr.getVoxLimit());
		JPanel vp = new JPanel();
		vp.setOpaque(false);
		Packer vpk = new Packer(vp);

		final JLabel voxVal = new JLabel(je.pr.getVoxLimit()+"");
		voxVal.setOpaque(false);
		volVal.setBorder(BorderFactory.createLoweredBevelBorder());
		voxVal.setBorder(BorderFactory.createLoweredBevelBorder());

		vox = new JSlider( 0, 3200, je.pr.getVoxLimit() );
		vox.setOpaque(false);
		vox.setMajorTickSpacing(100);
		vox.setPaintTicks(true);
		vpk.pack( vox ).gridx(0).gridy(0).fillx();
		vpk.pack( voxVal ).gridx(1).gridy(0).inset(2,2,2,2).fillx().weightx(0);
		cardp.add( "vox", vp );
//			vox.setShowValue(true);
		
		vox.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent ev ) {
				int val = vox.getValue();
				log.fine("update vox limit: "+val);
				je.pr.setVoxLimit( val );
				voxVal.setText( val+"" );
			}
		});
		b.setBorder(null);
	}
//		public Dimension getPreferredSize() {
//			return new Dimension( 290, 40 );
//		}
	public void setValues( int val, int max ) {
		b.setValue(val);
		volVal.setText(
			(((Integer.parseInt(volVal.getText())*3)+val)/4)+"");
		this.maximum = max;
	}
	
	JProgressBar createBar(int low, int high) {
		return new JProgressBar( low, high ) {
			public void paintComponent( Graphics g ) {
				Graphics2D g2 = (Graphics2D)g;
				int v = getValue();
				int min = getMinimum();
				int max = getMaximum();
				int w = getSize().width - getInsets().left - getInsets().right;
				int h = getSize().height - getInsets().top - getInsets().bottom;
				int x = getInsets().left;
				int y = getInsets().top;
				Paint op = g2.getPaint();
				GradientPaint fill = new GradientPaint(
					x, y, new Color( 80, 90, 255 ), 
					x, h, new Color( 20, 22, 80 ) );
				GradientPaint fill2 = new GradientPaint(
					x, y, Color.red.brighter(), 
					x, h, Color.red.darker() );
				GradientPaint fill3 = new GradientPaint(
					x, y, getBackground().brighter(), 
					x, h, getBackground().darker() );
				g2.setPaint(fill3);
				g.fillRect( x, y, w, h );
				g.setColor( Color.black );
				g.drawRect( x, y, w-1, h-1 );
				g2.setPaint(fill);
				x+=1;
				y+=1;
				w -= 2;
				h -= 2;
				int xw = (v * w)/(max-min);
				int mw = ((maximum) * w)/(max-min);
				g.fillRect( x, y, mw, h );
				if( mw > xw ) {
					g2.setPaint(fill3);
					g.fillRect( x+xw, y, mw-xw, h );
					g2.setPaint( fill2 );
					g.fillRect( x+mw-5, y, 5, h );
				}
				g2.setPaint( op );
			}
		};
	}
}