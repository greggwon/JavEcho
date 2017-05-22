package org.wonderly.ham.echolink;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.sound.sampled.*;

import org.wonderly.swing.*;
import org.wonderly.awt.*;
import java.util.*;
import java.util.logging.*;

/**
 *  A model for the volume gain control
 */
class ControlRangeModel implements BoundedRangeModel {
	Vector<ChangeListener> listeners = new Vector<ChangeListener>();
	boolean dirty;
	FloatControl gainCtl;
	JLabel min, max;
	float unmuted;
	boolean muted;
	int minv, maxv, setv;
	int ext = 20;
	String units;
	float scalef = 10.0f;
	int scalei = 10;
	Logger log = Logger.getLogger( getClass().getName() );
	Javecho je;
	JLabel gainVal;
	private double lastOutGain = Double.MAX_VALUE;

	public ControlRangeModel( Javecho je, JLabel min, JLabel max, JLabel gainVal ) {
		this.min = min;
		this.max = max;
		this.je = je;
		this.gainVal = gainVal;
	}
	public void setMuted( boolean how ) {
		if( muted == how )
			return;
		log.finer("Set muting: "+how);
		muted = how;
		if( gainCtl != null ) {
			if( how ) {
				muteIt();
			} else {
				gainCtl.setValue( unmuted );
			}
		}
	}
	private void muteIt() {
		if( gainCtl != null ) {
			unmuted = gainCtl.getValue();
//				progress("Set muting at: "+gainCtl.getMinimum());
			gainCtl.setValue(gainCtl.getMinimum());
		}
	}
	private void notify( ChangeEvent e ) {
		for( int i = 0; i < listeners.size(); ++i ) {
		((ChangeListener)listeners.elementAt(i)).stateChanged(e);
		}
	}
	public void setControl( FloatControl ctl, JPanel gainPan ) {
//			progress("Set Control: "+ctl );
		gainCtl = ctl;
		if( gainCtl != null ) {
			min.setText( gainCtl.getMinLabel() );
			max.setText( gainCtl.getMaxLabel() );
			gainPan.revalidate();
			minv = (int)(gainCtl.getMinimum() * scalei);
			maxv = (int)(gainCtl.getMaximum() * scalei);
			units = gainCtl.getUnits();

			ext = Math.abs(getMaximum()-getMinimum())/20;
			try {
				if( lastOutGain != Double.MAX_VALUE ) {
					// set to last user selected value.
					gainCtl.setValue( (float)lastOutGain );
				} else {
					// Force to 80% gain...
					gainCtl.setValue( (float)((((maxv - minv) * .85)/scalef ) + gainCtl.getMinimum()) );
				}
			} catch( Exception ex ) {
				log.log(Level.WARNING,ex.toString(), ex);
			}
			lastOutGain = gainCtl.getValue();
			gainVal.setText( String.format( "%.1f %s",
				gainCtl.getValue(), units ) );
			if( muted ) {
				if( je.getParms().isRaiseOnMutedContact() ) {
					je.progress("Raising");
					je.toFront();
					je.repaint();
				}
				muteIt();
			}
		}
	}
	public int getMinimum() {
//			progress("getMinimum: " );
		return gainCtl != null ? (int)(gainCtl.getMinimum()*scalei) : minv;
	}
	public void setMinimum(int v) {
//			progress("setMinimum: "+v );
	}
	public int getValue() {
//			progress("getValue: " );
		return gainCtl != null ? (int)(gainCtl.getValue()*scalei) : setv;
	}
	public void setValueIsAdjusting(boolean v) {
//			progress("adjusting: "+v );
		notify( new ChangeEvent(this) );
		dirty = v;
	}
	public boolean getValueIsAdjusting() {
		return dirty;
	}
	public void setValue(int v) {
		setv = v;
		log.finer("setValue["+muted+"]: "+(v/scalei) );
		if( gainCtl != null ) {
			if( muted )
				unmuted = (float)(v/scalef);
			else {
				try {
					gainCtl.setValue((float)(v/scalef));
				} catch( IllegalArgumentException ex ) {
					log.log( Level.FINEST, ex.toString(), ex );
					gainCtl.setValue(gainCtl.getMaximum());
				} catch( Exception ex ) {
					je.reportException(ex);
				}
				lastOutGain = gainCtl.getValue();
			}
		}
		gainVal.setText( String.format( "%.1f %s",
			gainCtl.getValue(), units ) );
	}
	public int getMaximum() {
		return gainCtl != null ? (int)(gainCtl.getMaximum()*scalei) : maxv;
	}
	public void setMaximum(int v) {
	}
	public int getExtent() {
		return ext;
	}
	public void setExtent(int v) {
		ext = v;
	}
	public void setRangeProperties(int min, int max, int val, int space, boolean draw) {
	}
	public void addChangeListener( ChangeListener lis ) {
		listeners.addElement(lis);
	}
	public void removeChangeListener( ChangeListener lis ) {
		listeners.removeElement(lis);
	}
}
