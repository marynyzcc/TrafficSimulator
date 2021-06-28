package simulator.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import simulator.misc.SortedArrayList;

public class TrafficSimulator implements Observable<TrafficSimObserver> {
	private RoadMap rMap;
	private List<Event> eventList;
	private int time;
	private List<TrafficSimObserver> observerList;

	public TrafficSimulator() {
		this.rMap = new RoadMap();
		this.eventList = new SortedArrayList<Event>();
		this.time = 0;
		observerList = new ArrayList<TrafficSimObserver>();
	}

	public void addEvent(Event e) {
		eventList.add(e);
		notifyOnEventAdded(this.rMap, this.eventList, e, this.time);
	}

	public void advance() throws Exception {
		// 1) incrementa time
		try {
			this.time += 1;
			notifyOnAdvanceStart(this.rMap, this.eventList, this.time);

			// 2) ejecuta los eventos correspondientes
			int i = 0;
			while (i < eventList.size()) {
				Event e = eventList.get(i);
				if (e.getTime() == this.time) {
					eventList.remove(i);
					e.execute(rMap);
				} else
					i++;
			}

			// 3) avanzan los cruces
			Iterator<Junction> itJunc = rMap.getJunctions().iterator();
			while (itJunc.hasNext()) {
				itJunc.next().advance(time);
			}

			// 4) avanzan las carreteras
			Iterator<Road> itRoads = rMap.getRoads().iterator();
			while (itRoads.hasNext()) {
				itRoads.next().advance(time);
			}

			notifyOnAdvanceEnd(this.rMap, this.eventList, this.time);

		} catch (Exception e) {
			notifyOnError(e.getMessage());
			throw new Exception(e.getMessage());
		}
	}

	public void reset() {
		rMap.reset();
		eventList.clear();
		this.time = 0;
		notifyOnReset(this.rMap, this.eventList, this.time);
	}

	public JSONObject report() {
		JSONObject simulatorState = new JSONObject();

		simulatorState.put("time", this.time);
		simulatorState.put("state", this.rMap.report());

		return simulatorState;
	}

	@Override
	public void addObserver(TrafficSimObserver o) {
		if (!observerList.contains(o)) {
			observerList.add(o);
			o.onRegister(this.rMap, this.eventList, this.time);
		}
	}

	@Override
	public void removeObserver(TrafficSimObserver o) {
		if (observerList.contains(o))
			observerList.remove(o);
	}

	private void notifyOnAdvanceStart(RoadMap rM, List<Event> eL, int t) {
		for (TrafficSimObserver o : observerList)
			o.onAdvanceStart(rM, eL, t);
	}

	private void notifyOnAdvanceEnd(RoadMap rM, List<Event> eL, int t) {
		for (TrafficSimObserver o : observerList)
			o.onAdvanceEnd(rM, eL, t);
	}

	private void notifyOnEventAdded(RoadMap rM, List<Event> eL, Event e, int t) {
		for (TrafficSimObserver o : observerList)
			o.onEventAdded(rM, eL, e, t);
	}

	private void notifyOnReset(RoadMap rM, List<Event> eL, int t) {
		for (TrafficSimObserver o : observerList)
			o.onReset(rM, eL, t);
	}

	private void notifyOnError(String err) {
		for (TrafficSimObserver o : observerList)
			o.onError(err);
	}

}