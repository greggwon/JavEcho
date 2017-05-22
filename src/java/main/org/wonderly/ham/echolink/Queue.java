package org.wonderly.ham.echolink;

import java.util.logging.*;

/**
 *  <b>Logger:</b><code>org.wonderly.ham.echolink.queue</code>
 */
class Queue {
	AudioEntry head;
	AudioEntry tail;
	int cnt;
	Logger log = Logger.getLogger( "org.wonderly.ham.echolink.queue");
	
	public void progress( String str ) {
		log.fine(str);
	}

	public void enqueue(byte[]data) {
		progress("enqueue: "+data.length+", into: "+cnt );
		synchronized(this) {
			++cnt;
			if( tail != null ) {
				tail.next = new AudioEntry(data);
				tail = tail.next;
			} else {
				head = tail = new AudioEntry(data);
			}
			notifyAll();
		}
		Thread.yield();
	}
	public synchronized void removeAllElements() {
		head = tail = null;
	}
	public synchronized int queueSize() {
		progress("queue size: "+(head == null ? 0 : cnt));
		if( head == null )
			cnt = 0;
		return cnt;
	}
	public synchronized boolean isEmpty() {
		return head == null;
	}
	public byte[] pop() {
		progress("pop: cnt: "+cnt+", head: "+head );
		synchronized(this) {
			AudioEntry en = head;
			if( en == null )
				return null;
			--cnt;
			head = head.next;
			if( head == null )
				tail = null;
			return en.data;
		}
	}
}