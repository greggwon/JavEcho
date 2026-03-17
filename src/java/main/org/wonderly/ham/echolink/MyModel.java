package org.wonderly.ham.echolink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
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
	volatile Javecho je;
	NamedList<NamedList<Entry>> nodes = new NamedList<>("Loading...");
	List<TreeModelListener> listeners = new ArrayList<>();
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

	@Override
	public String toString() {
		return name;
	}

	public MyModel(Javecho je, List<Entry> v, String name, AlarmManager mgr, List<Entry>sMsgs, boolean debug) {
		this.name = name;
		root = name;
		this.je = je;
		dbg = debug;
		sysmsg = sMsgs;
		this.alarmMgr = mgr;
		setData(v);
	}

	public final void setData(List<Entry> v) {
		data = new ArrayList<>();
		fillData(v);
		updateAll();
	}

	@Override
	public int getChildCount(Object node) {
		if (dbg)
			je.progress(this + ": getChildCount(" + node + ")");
		if (node == root) {
			if (dbg)
				je.progress(this + ":" + nodes + ": size=" + nodes.size());
			return nodes.size();
		}
		if (node instanceof NamedList == false)
			return 0;
		NamedList v = (NamedList) node;
		if (dbg)
			je.progress(this + ": " + v + ": size=" + v.size());
		return v.size();
	}

	@Override
	public boolean isLeaf(Object node) {
		if (dbg)
			je.progress(this + ": [" + node + "] is leaf? " + (node instanceof Entry));
		return node instanceof Entry;
	}

	@Override
	public int getIndexOfChild(Object parent, Object node) {
		if (dbg)
			je.progress(this + ": getIndexOfChild(" + parent + "," + node + ")");
		int idx = nodes.indexOf(parent);
		if (parent == root) {
			if (dbg)
				je.progress(parent + ": index of " + node + " = " + idx);
			return idx;
		}
		if (idx == -1) {
			if (dbg)
				je.progress(parent + " OOOOPPPPSSSS at " + node + ", no child index");
			return -1;
		}
		NamedList<Entry> v = nodes.get(idx);
		if (dbg)
			je.progress(parent + ": index of " + node + " = " + v.indexOf(node));
		return v.indexOf(node);
	}

	@Override
	public void addTreeModelListener(TreeModelListener lis) {
		if (dbg)
			je.progress(this + ": addTreeModelListener(" + lis + ")");
		listeners.add(lis);
		TreeModelEvent ev = new TreeModelEvent(this, new Object[] { root });
		lis.treeStructureChanged(ev);
		lis.treeNodesChanged(ev);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener lis) {
		if (dbg)
			je.progress(this + ": removeTreeModelListener(" + lis + ")");
		listeners.remove(lis);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object value) {
		if (dbg)
			je.progress(this + ": valueForPathChanged(" + path + "," + value + ")");
	}

	@Override
	public synchronized Object getChild(Object parent, int idx) {
		if (dbg)
			je.progress(this + ": getChild(" + parent + "," + idx + ")");
		if (parent == root) {
			if (dbg)
				je.progress(parent + ": child at " + idx + " = " + nodes.get(idx));
			return nodes.get(idx);
		}
		NamedList v = (NamedList) parent;
		if (dbg)
			je.progress(parent + ": child at " + idx + " = " + v.get(idx));
		return v.get(idx);
	}

	@Override
	public synchronized Object getRoot() {
		return root;
	}

	void fillData(List<Entry> v) {
		if (stations == null)
			stations = new NamedList<>("Stations");
		else
			stations.clear();
		if (links == null)
			links = new NamedList<>("Links");
		else
			links.clear();
		if (repeat == null)
			repeat = new NamedList<>("Repeaters");
		else
			repeat.clear();
		if (conf == null)
			conf = new NamedList<>("Conferences");
		else
			conf.clear();
		nodes = new NamedList<>("nodes");
		if (name.equals("Favorites") == true || je.getParms().isRepeatersInStationList())
			nodes.add(repeat);
		if (name.equals("Favorites") == true || je.getParms().isLinksInStationList())
			nodes.add(links);
		if (name.equals("Favorites") == true || je.getParms().isUsersInStationList())
			nodes.add(stations);
		if (name.equals("Favorites") == true || je.getParms().isConferencesInStationList())
			nodes.add(conf);
		addData(v);
		Comparator<Entry> e = new EntryComparator();
		if (name.equals("Favorites") == true || je.getParms().isRepeatersInStationList())
			Collections.sort(repeat, e);
		if (name.equals("Favorites") == true || je.getParms().isLinksInStationList())
			Collections.sort(links, e);
		if (name.equals("Favorites") == true || je.getParms().isUsersInStationList())
			Collections.sort(stations, e);
		if (name.equals("Favorites") == true || je.getParms().isConferencesInStationList())
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
				je.progress(e.getStation().getCall() + " already in list " + "Station Not Added");
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
		if (je.getParms().isConferencesInStationList() && e.getType() == Entry.TYPE_CONF) {
			add = true;
		} else if (je.getParms().isUsersInStationList() && e.getType() == Entry.TYPE_STATION) {
			add = true;
		} else if (je.getParms().isLinksInStationList() && e.getType() == Entry.TYPE_LINK) {
			add = true;
		} else if (je.getParms().isRepeatersInStationList() && e.getType() == Entry.TYPE_REPEATER) {
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
		switch (e.getType()) {
			case Entry.TYPE_LINK:
				w = links;
				break;
			case Entry.TYPE_REPEATER:
				w = repeat;
				break;
			case Entry.TYPE_STATION:
				w = stations;
				break;
			case Entry.TYPE_CONF:
				w = conf;
				break;
			case Entry.TYPE_MSG:
				sysmsg.add(e);
				log.log(Level.FINE, "add server error message: {0}", e);
				return;// w = msg;
			default:
				break;
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
		switch (e.getType()) {
			case Entry.TYPE_LINK:
				w = links;
				break;
			case Entry.TYPE_REPEATER:
				w = repeat;
				break;
			case Entry.TYPE_STATION:
				w = stations;
				break;
			case Entry.TYPE_CONF:
				w = conf;
				break;
			case Entry.TYPE_MSG:
				return;// w = msg;
			default:
				break;
		}
		if (w == null)
			throw new NullPointerException(e.getType() + ": Entry type unknown");
		w.remove(e);
		if (update)
			deletedPath(new TreePath(new Object[] { root, w, e }));
	}
}

