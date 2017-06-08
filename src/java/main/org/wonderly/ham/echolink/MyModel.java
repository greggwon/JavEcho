package org.wonderly.ham.echolink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.wonderly.ham.echolink.Javecho.EntryComparator;

public class MyModel implements TreeModel {
	private static final Logger log = Logger.getLogger( MyModel.class.getName() );
	volatile List<Entry> data;
	volatile String root;
	NamedList<NamedList<Entry>> nodes = new NamedList<NamedList<Entry>>("Loading...");
	List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
	String name;
	volatile boolean dbg = true;
	NamedList<Entry> stations;
	NamedList<Entry> links;
	NamedList<Entry> repeat;
	NamedList<Entry> conf;
	// NamedList<Entry> msg;
	AlarmManager alarmMgr;
	List<Entry>sysmsg;

	public Entry findStation(String call) {
		for (int i = 0; i < data.size(); ++i) {
			Entry e = data.get(i);
			if (e.getStation().getCall().equals(call))
				return e;
		}
		return null;
	}

	public List<Entry> getContents() {
		return data;
	}

	public String toString() {
		return name;
	}

	public MyModel(List<Entry> v, String name, AlarmManager mgr, List<Entry>sMsgs, boolean debug) {
		this.name = name;
		root = name;
		dbg = debug;
		sysmsg = sMsgs;
		this.alarmMgr = mgr;
		setData(v);
	}

	public void setData(List<Entry> v) {
		data = new ArrayList<Entry>();
		fillData(v);
		updateAll();
	}

	public int getChildCount(Object node) {
		if (dbg)
			Javecho.progress(this + ": getChildCount(" + node + ")");
		if (node == root) {
			if (dbg)
				Javecho.progress(this + ":" + nodes + ": size=" + nodes.size());
			return nodes.size();
		}
		if (node instanceof NamedList == false)
			return 0;
		NamedList v = (NamedList) node;
		if (dbg)
			Javecho.progress(this + ": " + v + ": size=" + v.size());
		return v.size();
	}

	public boolean isLeaf(Object node) {
		if (dbg)
			Javecho.progress(this + ": [" + node + "] is leaf? " + (node instanceof Entry));
		return node instanceof Entry;
	}

	public int getIndexOfChild(Object parent, Object node) {
		if (dbg)
			Javecho.progress(this + ": getIndexOfChild(" + parent + "," + node + ")");
		int idx = nodes.indexOf(parent);
		if (parent == root) {
			if (dbg)
				Javecho.progress(parent + ": index of " + node + " = " + idx);
			return idx;
		}
		if (idx == -1) {
			if (dbg)
				Javecho.progress(parent + " OOOOPPPPSSSS at " + node + ", no child index");
			return -1;
		}
		NamedList<Entry> v = nodes.get(idx);
		if (dbg)
			Javecho.progress(parent + ": index of " + node + " = " + v.indexOf(node));
		return v.indexOf(node);
	}

	public void addTreeModelListener(TreeModelListener lis) {
		if (dbg)
			Javecho.progress(this + ": addTreeModelListener(" + lis + ")");
		listeners.add(lis);
		TreeModelEvent ev = new TreeModelEvent(this, new Object[] { root });
		lis.treeStructureChanged(ev);
		lis.treeNodesChanged(ev);
	}

	public void removeTreeModelListener(TreeModelListener lis) {
		if (dbg)
			Javecho.progress(this + ": removeTreeModelListener(" + lis + ")");
		listeners.remove(lis);
	}

	public void valueForPathChanged(TreePath path, Object value) {
		if (dbg)
			Javecho.progress(this + ": valueForPathChanged(" + path + "," + value + ")");
	}

	public synchronized Object getChild(Object parent, int idx) {
		if (dbg)
			Javecho.progress(this + ": getChild(" + parent + "," + idx + ")");
		if (parent == root) {
			if (dbg)
				Javecho.progress(parent + ": child at " + idx + " = " + nodes.get(idx));
			return nodes.get(idx);
		}
		NamedList v = (NamedList) parent;
		if (dbg)
			Javecho.progress(parent + ": child at " + idx + " = " + v.get(idx));
		return v.get(idx);
	}

	public synchronized Object getRoot() {
		return root;
	}

	void fillData(List<Entry> v) {
		if (stations == null)
			stations = new NamedList<Entry>("Stations");
		else
			stations.clear();
		if (links == null)
			links = new NamedList<Entry>("Links");
		else
			links.clear();
		if (repeat == null)
			repeat = new NamedList<Entry>("Repeaters");
		else
			repeat.clear();
		if (conf == null)
			conf = new NamedList<Entry>("Conferences");
		else
			conf.clear();
		nodes = new NamedList<NamedList<Entry>>("nodes");
		if (name.equals("Favorites") == true || Javecho.pr.isRepeatersInStationList())
			nodes.add(repeat);
		if (name.equals("Favorites") == true || Javecho.pr.isLinksInStationList())
			nodes.add(links);
		if (name.equals("Favorites") == true || Javecho.pr.isUsersInStationList())
			nodes.add(stations);
		if (name.equals("Favorites") == true || Javecho.pr.isConferencesInStationList())
			nodes.add(conf);
		addData(v);
		Comparator<Entry> e = new EntryComparator();
		if (name.equals("Favorites") == true || Javecho.pr.isRepeatersInStationList())
			Collections.sort(repeat, e);
		if (name.equals("Favorites") == true || Javecho.pr.isLinksInStationList())
			Collections.sort(links, e);
		if (name.equals("Favorites") == true || Javecho.pr.isUsersInStationList())
			Collections.sort(stations, e);
		if (name.equals("Favorites") == true || Javecho.pr.isConferencesInStationList())
			Collections.sort(conf, e);
	}

	private void addData(List<Entry> v) {
		alarmMgr.almOnce = false;
		for (int i = 0; i < v.size(); ++i) {
			Entry e = v.get(i);
			addData(e, false);
		}
		alarmMgr.firstAlm = false;
	}

	void updateAll() {
		updatePath(new TreePath(new Object[] { root, stations }));
		updatePath(new TreePath(new Object[] { root, links }));
		updatePath(new TreePath(new Object[] { root, repeat }));
		updatePath(new TreePath(new Object[] { root, conf }));
	}

	void updatePath(TreePath pth) {
		TreeModelEvent ev = new TreeModelEvent(this, pth);
		for (int i = 0; i < listeners.size(); ++i) {
			TreeModelListener lis = (TreeModelListener) listeners.get(i);
			lis.treeStructureChanged(ev);
			lis.treeNodesChanged(ev);
		}
	}

	void deletedPath(TreePath pth) {
		TreeModelEvent ev = new TreeModelEvent(this, pth);
		for (int i = 0; i < listeners.size(); ++i) {
			TreeModelListener lis = (TreeModelListener) listeners.get(i);
			lis.treeNodesRemoved(ev);
		}
	}

	public void addData(Entry e) {
		addData(e, true);
	}

	private void addData(Entry e, boolean update) {
		if (data.contains(e) == true) {
			// if ( pr.isBeeping() ) {
			// Toolkit.getDefaultToolkit().beep();
			// } else
			if (e.getStation().getCall().trim().length() > 0) {
				Javecho.progress(e.getStation().getCall() + " already in list " + "Station Not Added");
			}
			return;
		}
		if (e.getType() != Entry.TYPE_MSG) {
			if (e.getStation().disconnected())
				alarmMgr.checkAlarms(e.getStation());
			else if (e.getStation().connected())
				alarmMgr.checkAlarms(e.getStation());
			else if (e.getStation().wentIdle())
				alarmMgr.checkAlarms(e.getStation());
			else if (e.getStation().wentBusy())
				alarmMgr.checkAlarms(e.getStation());
		}
		boolean add = false;
		if (Javecho.pr.isConferencesInStationList() && e.getType() == Entry.TYPE_CONF) {
			add = true;
		} else if (Javecho.pr.isUsersInStationList() && e.getType() == Entry.TYPE_STATION) {
			add = true;
		} else if (Javecho.pr.isLinksInStationList() && e.getType() == Entry.TYPE_LINK) {
			add = true;
		} else if (Javecho.pr.isRepeatersInStationList() && e.getType() == Entry.TYPE_REPEATER) {
			add = true;
		} else if (e.getType() == Entry.TYPE_MSG) {
			add = true;
		}
		if (name.equals("Favorites") == false && !add)
			return;
		data.add(e);
		if (update)
			Collections.sort(data, new EntryComparator());
		NamedList<Entry> w = null;
		if (e.getType() == Entry.TYPE_LINK)
			w = links;
		else if (e.getType() == Entry.TYPE_REPEATER)
			w = repeat;
		else if (e.getType() == Entry.TYPE_STATION)
			w = stations;
		else if (e.getType() == Entry.TYPE_CONF)
			w = conf;
		else if (e.getType() == Entry.TYPE_MSG) {
			sysmsg.add(e);
			log.fine("add server error message: " + e);
			return;// w = msg;
		}
		if (w == null)
			throw new NullPointerException(e.getType() + ": Entry type unknown");
		w.add(e);
		if (update) {
			Collections.sort(w, new EntryComparator());
			updatePath(new TreePath(new Object[] { root, w }));
		}
	}

	public void removeData(Entry e) {
		removeData(e, true);
	}

	private void removeData(Entry e, boolean update) {
		NamedList<?> w = null;
		if (e.getType() == Entry.TYPE_LINK)
			w = links;
		else if (e.getType() == Entry.TYPE_REPEATER)
			w = repeat;
		else if (e.getType() == Entry.TYPE_STATION)
			w = stations;
		else if (e.getType() == Entry.TYPE_CONF)
			w = conf;
		else if (e.getType() == Entry.TYPE_MSG)
			return;// w = msg;
		if (w == null)
			throw new NullPointerException(e.getType() + ": Entry type unknown");
		w.remove(e);
		if (update)
			deletedPath(new TreePath(new Object[] { root, w, e }));
	}
}

