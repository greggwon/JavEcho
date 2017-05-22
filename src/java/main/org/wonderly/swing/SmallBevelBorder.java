package org.wonderly.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import org.wonderly.awt.*;

public class SmallBevelBorder extends BevelBorder {
	
	public static void main( String args[] ) {
		JFrame f = new JFrame("Testing");
		f.pack();
		f.addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(1);
			}
		});
		JPanel p = new JPanel();
		JPanel p2 = new JPanel();
		p.setBorder( new SmallBevelBorder( 1 ) );
		p2.setBorder( new SmallBevelBorder( 0 ) );
//		p.setBackground( Color.blue.brighter() );
//		p2.setBackground( Color.red.darker() );
		Packer pk = new Packer( f.getContentPane() );
		pk.pack( p ).gridx(0).gridy(0).fillboth().inset(4,4,4,4);
		pk.pack( p2 ).gridx(1).gridy(0).fillboth().inset(4,4,4,4);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
	
	public SmallBevelBorder() {
		super(1);
	}

    public SmallBevelBorder(int bevelType) {
        super( bevelType );
    }

    public SmallBevelBorder(int bevelType, Color highlight, Color shadow) {
        this(bevelType, highlight.brighter(), highlight, shadow, shadow.brighter());
    }

    public SmallBevelBorder(int bevelType, Color highlightOuterColor, 
                       Color highlightInnerColor, Color shadowOuterColor, 
                       Color shadowInnerColor) {
        super( bevelType, highlightOuterColor, highlightInnerColor,
        	shadowOuterColor, shadowInnerColor );
    }

    protected void paintRaisedBevel(Component c, Graphics g, int x, int y,
                                    int width, int height)  {
        Color oldColor = g.getColor();
        int h = height;
        int w = width;

        g.translate(x, y);

        g.setColor(Color.white);//getHighlightOuterColor(c));
        g.drawLine(0, 0, 0, h-2);
        g.drawLine(1, 0, w-2, 0);

//        g.setColor(getHighlightInnerColor(c));
//        g.drawLine(1, 1, 1, h-3);
//        g.drawLine(2, 1, w-3, 1);

        g.setColor(getShadowInnerColor(c));
        g.drawLine(0, h-1, w-1, h-1);
        g.drawLine(w-1, 0, w-1, h-2);

//        g.setColor(getShadowInnerColor(c));
//        g.drawLine(1, h-2, w-2, h-2);
//        g.drawLine(w-2, 1, w-2, h-3);

        g.translate(-x, -y);
        g.setColor(oldColor);
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = 1;
        return insets;
    }

    public Insets getBorderInsets(Component c) {
		return new Insets(1, 1, 1, 1);
    }

    protected void paintLoweredBevel(Component c, Graphics g, int x, int y,
                                        int width, int height)  {
        Color oldColor = g.getColor();
        int h = height;
        int w = width;

        g.translate(x, y);

        g.setColor(getShadowOuterColor(c));
        g.drawLine(0, 0, 0, h-1);
        g.drawLine(1, 0, w-1, 0);
//
//        g.setColor(getShadowOuterColor(c));
//        g.drawLine(1, 1, 1, h-2);
//        g.drawLine(2, 1, w-2, 1);

        g.setColor(getHighlightOuterColor(c));
        g.drawLine(1, h-1, w-1, h-1);
        g.drawLine(w-1, 1, w-1, h-2);

//        g.setColor(getHighlightInnerColor(c));
//        g.drawLine(2, h-2, w-2, h-2);
//        g.drawLine(w-2, 2, w-2, h-3);
//
        g.translate(-x, -y);
        g.setColor(oldColor);

    }
}