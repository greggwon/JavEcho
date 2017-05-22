package org.wonderly.ham.echolink;

import java.util.logging.*;
import javax.sound.sampled.*;

class LineWrapper implements SourceDataLine,TargetDataLine {
	DataLine l;
	Logger log = Logger.getLogger("org.wonderly.ham.echolink.audio");
	
	private void show( String str ) {
		log.fine( str );
	}
	public LineWrapper( SourceDataLine line ) {
		show("\n\nSourceDataLine LineWrapper");
		l = line;
	}
	public LineWrapper( TargetDataLine line ) {
		show("\n\nTargetDataLine LineWrapper");
		l = line;
	}
	public void addLineListener( LineListener ll ) {
		show("addLineListener");
		l.addLineListener(ll);
	}
	public void close() {
		show("close");
		l.close();
//			progress("\n..."+l+" closed" );
	}
	public Control getControl( Control.Type con ) {
		show("getControl");
		return l.getControl( con ) ;
	}
	public Control[] getControls() {
		show("getControls");
		return l.getControls();
	}
	public Line.Info getLineInfo() {
		show("getLineInfo");
		return l.getLineInfo();
	}
	public boolean isControlSupported( Control.Type control ) {
		show("isControlSupported");
		return l.isControlSupported( control );
	}
	public boolean isOpen() {
		show("isOpen");
		return l.isOpen();
	}
	public int write(byte[]b,int off, int len) {
		return ((SourceDataLine)l).write(b,off,len);
	}
	public int read(byte[]b,int off, int len) {
		return ((TargetDataLine)l).read(b,off,len);
	}
	public long getLongFramePosition() {
		return l.getLongFramePosition();
	}

	public void drain() {
		show("drain");
		l.drain();
	}
	public AudioFormat getFormat() {
		return l.getFormat();
	}
	public void flush() {
		show("flush");
		l.flush();
	}
	public int getFramePosition() {
		return l.getFramePosition();
	}
	public float getLevel() {
		return l.getLevel();
	}
	public int available() {
		return l.available();
	}
	public int getBufferSize() {
		return l.getBufferSize();
	}
	public long getMicrosecondPosition() {
		return l.getMicrosecondPosition();
	}
	public void start() {
		show("start");
		l.start();
	}
	public void stop() {
		show("stop");
		l.stop();
	}
	public boolean isRunning() {
		show("isRunning");
		return l.isRunning();
	}
	public boolean isActive() {
		show("isActive");
		return l.isActive();
	}
	public void open() throws LineUnavailableException {
		show("open");
		l.open();
	}
	public void open(AudioFormat fmt) throws LineUnavailableException {
		show("open fmt");
		if( l instanceof SourceDataLine )
			((SourceDataLine)l).open(fmt);
		else
			((TargetDataLine)l).open(fmt);
	}
	public void open(AudioFormat fmt, int buf) throws LineUnavailableException {
		show("open fmt buf");
		if( l instanceof SourceDataLine )
			((SourceDataLine)l).open(fmt,buf);
		else
			((TargetDataLine)l).open(fmt,buf);
	}
	public void removeLineListener( LineListener ll ) {
		l.removeLineListener(ll);
	}
}