package org.wonderly.swing;

import javax.swing.*;
import java.awt.*;

public class InfoLabel extends JLabel {
	public InfoLabel() {
		this(null);
	}
	
	public InfoLabel(String lab) {
		super(lab);
		setForeground(SystemColor.activeCaptionText);
	}
}