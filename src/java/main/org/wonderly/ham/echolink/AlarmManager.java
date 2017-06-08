package org.wonderly.ham.echolink;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AlarmManager {
	private static final Logger log = Logger.getLogger(Javecho.class.getName());
	boolean almOnce = false;
	boolean firstAlm = true;
	Javecho je;
	AlarmEditor almEd;
	AlarmLog almLog;

	public AlarmManager(Javecho j) {
		je = j;
	}

	void showAlarmEditor() {
		almEd.showFrame();
	}

	void showAlarmLog() {
		almLog.showFrame();
	}

	public void checkAlarms(StationData dt) {
		if (almOnce || firstAlm)
			return;
		log.finer("Check for alarm of " + dt);
		log.finest("alarms for: " + almEd.getHistory());
		if (almEd.getHistory().contains(dt.getCall()) == false) {
			log.finer("No alarm entry for: " + dt.getCall());
			return;
		}
		almOnce = true;
		log.finer("Adding status for: " + dt);
		almLog.addEntry(dt);
		try {
			log.fine("Sounding Alarm for: " + dt);
			je.audio.alarm();
			if (almLog.isVisible() == false) {
				almLog.setVisible(true);
			}
		} catch (Exception ex) {
			log.log(Level.WARNING, ex.toString(), ex);
		}
	}
}
