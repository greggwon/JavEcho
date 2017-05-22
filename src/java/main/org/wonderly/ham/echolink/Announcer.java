package org.wonderly.ham.echolink;

public interface Announcer {
	public void connected( Parameters p, String call );
	public void disconnected( Parameters p, String call);
	public void stationInfo( Parameters p, String msg );
	public void linkUp( Parameters p );
	public void linkDown( Parameters p );
	public void courtesyTone( Parameters p );
	public void activityReminder( Parameters p );
	public void say(Parameters p, String text);
}