package simulator.model;

import java.util.Iterator;
import java.util.List;

import simulator.misc.Pair;

public class SetWeatherEvent extends Event {
	private List<Pair<String, Weather>> ws;

	public SetWeatherEvent(int time, List<Pair<String, Weather>> ws) {
		super(time);
		if (ws == null)
			throw new IllegalArgumentException("Cannot set weather");
		this.ws = ws;
	}

	@Override
	void execute(RoadMap map) {

		Iterator<Pair<String, Weather>> it = ws.iterator();
		while (it.hasNext()) {
			Pair<String, Weather> pair = it.next();
			Road r = map.getRoad(pair.getFirst());
			if (r == null)
				throw new IllegalArgumentException("Cannot set weather");
			r.setWeather(pair.getSecond());
		}

	}

	@Override
	public String toString() {
		String weatherList = "Change Weather: [(" + ws.get(0).getFirst() + "," + ws.get(0).getSecond() + ")]";
		return weatherList;
	}
}
