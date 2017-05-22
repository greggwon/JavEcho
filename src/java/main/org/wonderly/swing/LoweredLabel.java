package org.wonderly.swing;

import java.awt.*;
import javax.swing.*;

public class LoweredLabel extends JLabel {
	public LoweredLabel() {
		this("0");
	}

	public Dimension getMinimumSize() {
		Dimension d = super.getMinimumSize();
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		Insets s = getInsets();
		d.width = fm.stringWidth("000")+s.left+s.right;
		return d;
	}
	public LoweredLabel(String lab, Font f ) {
		super( lab, JLabel.RIGHT );
		setup();
		setFont(f);
	}
	public LoweredLabel(String lab) {
		super( lab, JLabel.RIGHT );
		setFont( new Font( "serif", Font.PLAIN, 10 ) );
		setup();
	}
	private void setup() {
		setOpaque(true);
		setBackground( Color.white );
		setBorder( new SmallBevelBorder() );
	}
}