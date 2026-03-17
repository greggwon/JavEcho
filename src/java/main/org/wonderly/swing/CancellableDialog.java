/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wonderly.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 *
 * @author gregg
 */
public abstract class CancellableDialog extends JDialog {
	protected volatile boolean cancelled = false;
	public CancellableDialog( JFrame parent, String title, boolean isModal ) {
		super( parent, title, isModal );
		setCancelled( false );
	}
	public CancellableDialog( JDialog parent, String title, boolean isModal ) {
		super( parent, title, isModal );
		setCancelled( false );
	}
	public void setCancelled( boolean how ) {
		cancelled = how;
	}
	public void onCancelled() {
	}
	public boolean isCancelled() {
		return cancelled;
	}
	public abstract void onClose();
	public void performDialog() {
		setVisible(true);
		if( isCancelled() == false ) {
			onClose();
		} else {
			onCancelled();
		}
	}
}
