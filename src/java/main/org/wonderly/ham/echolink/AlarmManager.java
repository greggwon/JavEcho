package org.wonderly.ham.echolink;

import java.io.IOException;
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
		log.log(Level.FINER, "Check for alarm of {0}", dt);
		log.log(Level.FINEST, "alarms for: {0}", almEd.getHistory());
		if (almEd.getHistory().contains(dt.getCall()) == false) {
			log.log(Level.FINER, "No alarm entry for: {0}", dt.getCall());
			return;
		}
		almOnce = true;
		log.log(Level.FINER, "Adding status for: {0}", dt);
		almLog.addEntry(dt);
		try {
			log.log(Level.FINE, "Sounding Alarm for: {0}", dt);
			je.audio.alarm();
			if (almLog.isVisible() == false) {
				almLog.setVisible(true);
			}
		} catch (IOException ex) {
			log.log(Level.WARNING, ex.toString(), ex);
		} catch (RuntimeException ex) {
			log.log(Level.SEVERE, ex.toString(), ex);
		}
	}
}
