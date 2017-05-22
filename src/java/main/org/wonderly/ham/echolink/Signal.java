package org.wonderly.ham.echolink;

public enum Signal {
	CONNECTED(0),
	DISCONNECTED(1),
	STATION_INFO(2),
	LINK_UP(3),
	LINK_DOWN(4),
	COURTESY_TONE(5),
	ACTIVITY_REMINDER(6);

	int val;
	public String toString() {
		return super.toString()+"("+val+")";
	}

	Signal(int v) {
		val = v;
	}
}