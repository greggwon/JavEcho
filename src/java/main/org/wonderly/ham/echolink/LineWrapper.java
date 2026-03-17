package org.wonderly.ham.echolink;

import java.util.logging.*;
import javax.sound.sampled.*;

class LineWrapper implements SourceDataLine,TargetDataLine {
	DataLine l;
	static final Logger log = Logger.getLogger("org.wonderly.ham.echolink.audio");
	
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
	@Override
	public void addLineListener( LineListener ll ) {
		show("addLineListener");
		l.addLineListener(ll);
	}
	@Override
	public void close() {
		show("close");
		l.close();
	}
	@Override
	public Control getControl( Control.Type con ) {
		show("getControl");
		return l.getControl( con ) ;
	}
	@Override
	public Control[] getControls() {
		show("getControls");
		return l.getControls();
	}
	@Override
	public Line.Info getLineInfo() {
		show("getLineInfo");
		return l.getLineInfo();
	}
	@Override
	public boolean isControlSupported( Control.Type control ) {
		show("isControlSupported");
		return l.isControlSupported( control );
	}
	@Override
	public boolean isOpen() {
		show("isOpen");
		return l.isOpen();
	}
	@Override
	public int write(byte[]b,int off, int len) {
		return ((SourceDataLine)l).write(b,off,len);
	}
	@Override
	public int read(byte[]b,int off, int len) {
		return ((TargetDataLine)l).read(b,off,len);
	}
	@Override
	public long getLongFramePosition() {
		return l.getLongFramePosition();
	}

	@Override
	public void drain() {
		show("drain");
		l.drain();
	}
	@Override
	public AudioFormat getFormat() {
		return l.getFormat();
	}
	@Override
	public void flush() {
		show("flush");
		l.flush();
	}
	@Override
	public int getFramePosition() {
		return l.getFramePosition();
	}
	@Override
	public float getLevel() {
		return l.getLevel();
	}
	@Override
	public int available() {
		return l.available();
	}
	@Override
	public int getBufferSize() {
		return l.getBufferSize();
	}
	@Override
	public long getMicrosecondPosition() {
		return l.getMicrosecondPosition();
	}
	@Override
	public void start() {
		show("start");
		l.start();
	}
	@Override
	public void stop() {
		show("stop");
		l.stop();
	}
	@Override
	public boolean isRunning() {
		show("isRunning");
		return l.isRunning();
	}
	@Override
	public boolean isActive() {
		show("isActive");
		return l.isActive();
	}
	@Override
	public void open() throws LineUnavailableException {
		show("open");
		l.open();
	}
	@Override
	public void open(AudioFormat fmt) throws LineUnavailableException {
		show("open fmt");
		if( l instanceof SourceDataLine )
			((SourceDataLine)l).open(fmt);
		else
			((TargetDataLine)l).open(fmt);
	}
	@Override
	public void open(AudioFormat fmt, int buf) throws LineUnavailableException {
		show("open fmt buf");
		if( l instanceof SourceDataLine )
			((SourceDataLine)l).open(fmt,buf);
		else
			((TargetDataLine)l).open(fmt,buf);
	}
	@Override
	public void removeLineListener( LineListener ll ) {
		l.removeLineListener(ll);
	}
}