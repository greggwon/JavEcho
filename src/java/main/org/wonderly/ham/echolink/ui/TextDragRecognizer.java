/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wonderly.ham.echolink.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import javax.swing.text.JTextComponent;
import org.wonderly.ham.echolink.StationData;
import org.wonderly.ham.echolink.Javecho;

/**
 *
 * @author gregg
 */
public class TextDragRecognizer implements DragGestureListener {
	JTextComponent text;
	Javecho je;
	DragSource dgs;
	public TextDragRecognizer(JTextComponent text, Javecho je, DragSource dgs) {
		this.text = text;
		this.je = je;
		this.dgs = dgs;
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent ev) {
		Point p = ev.getDragOrigin();
		final StationData nd = je.getDragCall(text, p);
		if (nd == null)
			return;
		int w, h;
		Graphics g = je.getGraphics();
		Font f = new Font("serif", Font.PLAIN, 14);
		FontMetrics fm = g.getFontMetrics(f);
		w = fm.stringWidth(nd.getCall());
		h = fm.getHeight();
		Image img = je.createImage(w, h);
		Graphics gr = img.getGraphics();
		gr.setColor(Color.white);
		gr.fillRect(0, 0, w, h);
		gr.setColor(Color.blue);
		gr.drawString(nd.getCall(), 0, fm.getMaxAscent());
		je.progress("img: " + img + ", w: " + w + ", h: " + h + ", can drag? " + DragSource.isDragImageSupported());
		dgs.startDrag(ev, DragSource.DefaultLinkDrop, (Image) img, new Point(0, 0),
				// Transfer the node object.
			new Transferable() {
				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] { new DataFlavor(StationData.class, "StationData") };
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor flav) {
					return flav.equals(new DataFlavor(StationData.class, "StationData"));
				}

				@Override
				public Object getTransferData(DataFlavor flav) {
					return nd;
				}
			}, new DragSourceListener() {
				@Override
				public void dragEnter(DragSourceDragEvent evv) {
				}

				@Override
				public void dragOver(DragSourceDragEvent evv) {
				}

				@Override
				public void dropActionChanged(DragSourceDragEvent evv) {
				}

				@Override
				public void dragExit(DragSourceEvent evv) {
				}

				@Override
				public void dragDropEnd(DragSourceDropEvent evv) {
				}
			}
		);
	}
}

