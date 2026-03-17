/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wonderly.ham.echolink.ui;

import javax.swing.text.*;
import java.awt.dnd.*;
import org.wonderly.ham.echolink.*;

/**
 * Handle drag of text from the station data JTextComponent
 * @author gregg
 */
public class StationDataDragRecognizer extends TextDragRecognizer {
	public StationDataDragRecognizer(JTextComponent text, Javecho je, DragSource dgs) {
		super(text,je, dgs);
	}
}