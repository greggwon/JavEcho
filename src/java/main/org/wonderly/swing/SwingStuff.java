package org.wonderly.swing;

import javax.swing.*;
import java.util.logging.*;

/**
 *  Miscellaneous Swing helper methods.
 */
public class SwingStuff {
	private static final Logger log = Logger.getLogger( SwingStuff.class.getName() );

	/**
	 *  Run the passed runnable in a swing event dispatch thread.
	 */
	public static void runInSwing( final Runnable r ) {
		if( SwingUtilities.isEventDispatchThread() ) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(r);
			} catch( Exception ex ) {
				log.log( Level.SEVERE, ex.toString(), ex );
			}
		}
	}
	public static void runInSwingLater( final Runnable r ) {
		if( SwingUtilities.isEventDispatchThread() ) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeLater(r);
			} catch( Exception ex ) {
				log.log( Level.SEVERE, ex.toString(), ex );
			}
		}
	}
}