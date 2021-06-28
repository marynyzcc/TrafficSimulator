package simulator.factories;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Pair;
import simulator.model.Event;
import simulator.model.SetWeatherEvent;
import simulator.model.Weather;


public class SetWeatherEventBuilder extends Builder<Event> {

	public SetWeatherEventBuilder() {
		super("set_weather");
	}

	@Override
	protected Event createTheInstance(JSONObject data) {
		int time = data.getInt("time");
		JSONArray info = data.getJSONArray("info");
		List<Pair<String,Weather>> ws = new ArrayList<Pair<String,Weather>>();
		
		for(int i=0; i< info.length(); i++) {
			String r = info.getJSONObject(i).getString("road");
			Weather w = Weather.valueOf(info.getJSONObject(i).getString("weather"));
			Pair<String,Weather> p = new Pair<String, Weather>(r,w);
			ws.add(p);
		}
		
		return new SetWeatherEvent(time, ws);
	}

}
