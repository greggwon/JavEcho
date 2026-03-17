/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wonderly.ham.echolink.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;
import javax.swing.JComponent;
import org.wonderly.ham.echolink.Entry;
import org.wonderly.swing.ComponentUpdateThread;
import org.wonderly.ham.echolink.*;

/**
 * Handle drops onto the info pane which are taken as connection requests.
 *
 * @author gregg
 */
public class InfoPaneTargetListener extends DropTargetAdapter {
	final Javecho je;
	public InfoPaneTargetListener( Javecho je ) {
		this.je = je;
	}
	@Override
	public void dragOver(DropTargetDragEvent ev) {
		DataFlavor[] arr = ev.getCurrentDataFlavors();
		DataFlavor reqFlav = new DataFlavor(Entry.class, "Entry");
		for (int i = 0; i < arr.length; ++i) {
			if (arr[i].equals(reqFlav))
				return;
		}
		ev.rejectDrag();
	}

	@Override
	public void drop(final DropTargetDropEvent ev) {
		try {
			Transferable tf = ev.getTransferable();
			final Object obj = tf.getTransferData(new DataFlavor(Entry.class, "Entry"));
			ev.dropComplete(obj instanceof Entry);
			if (obj instanceof Entry) {
				final Entry ent = (Entry) obj;
				new ComponentUpdateThread((JComponent) null) {
					@Override
					public Object construct() {
						try {
							je.connectTo(ent.getStation(), false);
						} catch (Exception ex) {
							je.reportException(ex);
						}
						return null;
					}
				}.start();
			}
		} catch (UnsupportedFlavorException | IOException | RuntimeException ex) {
			je.reportException(ex);
		}
	}
}

