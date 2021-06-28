package simulator.model;

import java.util.Iterator;
import java.util.List;

import simulator.misc.Pair;

public class NewSetContClassEvent extends Event {

	private List<Pair<String, Integer>> cs;

	public NewSetContClassEvent(int time, List<Pair<String, Integer>> cs) {
		super(time);
		if (cs == null)
			throw new IllegalArgumentException("Cannot set contamination class");
		this.cs = cs;
	}

	@Override
	void execute(RoadMap map) {

		Iterator<Pair<String, Integer>> it = cs.iterator();
		while (it.hasNext()) {
			Pair<String, Integer> pair = it.next();
			Vehicle v = map.getVehicle(pair.getFirst());
			if (v == null)
				throw new IllegalArgumentException("Cannot set contamination class");
			v.setContaminationClass(pair.getSecond());
		}

	}

	@Override
	public String toString() {
		String contClassList = "Change CO2 class: [(" + cs.get(0).getFirst() + "," + cs.get(0).getSecond() + ")]";
		return contClassList;
	}
}
