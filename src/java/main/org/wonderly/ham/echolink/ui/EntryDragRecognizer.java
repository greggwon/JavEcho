/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wonderly.ham.echolink.ui;

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
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.wonderly.ham.echolink.Entry;
import org.wonderly.ham.echolink.*;

/**
 *
 * @author gregg
 */
public class EntryDragRecognizer implements DragGestureListener {
	JTree tree;
	Javecho je;
	DragSource dgs;

	public EntryDragRecognizer(JTree tree, Javecho je, DragSource dgs) {
		this.tree = tree;
		this.je = je;
		this.dgs = dgs;
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent ev) {
		je.progress("Drag started: " + ev);
		Point p = ev.getDragOrigin();
		TreePath pth = tree.getPathForLocation(p.x, p.y);
		if (pth == null)
			return;
		Object obj = pth.getLastPathComponent();
		if (obj == null || obj instanceof Entry == false) {
			return;
		}
		tree.setSelectionPath(pth);
		final Entry nd = (Entry) obj;
		dgs.startDrag(ev, DragSource.DefaultLinkDrop, (Image) null, new Point(0, 0),
			// Transfer the node object.
			new Transferable() {
				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] { new DataFlavor(Entry.class, "Entry") };
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor flav) {
					return flav.equals(new DataFlavor(Entry.class, "Entry"));
				}

				@Override
				public Object getTransferData(DataFlavor flav) {
					return nd;
				}
			}, new DragSourceListener() {
				@Override
				public void dragEnter(DragSourceDragEvent dsde) {
				}

				@Override
				public void dragOver(DragSourceDragEvent dsde) {
				}

				@Override
				public void dropActionChanged(DragSourceDragEvent dsde) {
				}

				@Override
				public void dragExit(DragSourceEvent dse) {
				}

				@Override
				public void dragDropEnd(DragSourceDropEvent dsde) {
				}
			}
		);
	}
}
