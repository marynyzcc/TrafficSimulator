package simulator.factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Pair;
import simulator.model.Event;
import simulator.model.NewSetContClassEvent;

public class SetContClassEventBuilder extends Builder<Event> {

	public SetContClassEventBuilder() {
		super("set_cont_class");
	}

	@Override
	protected Event createTheInstance(JSONObject data) {
		int time = data.getInt("time");
		JSONArray info = data.getJSONArray("info");
		List<Pair<String,Integer>> cs = new ArrayList<Pair<String,Integer>>();
		
		for(int i = 0; i< info.length(); i++) {
			String v = info.getJSONObject(i).getString("vehicle");
			Integer c = info.getJSONObject(i).getInt("class");
			Pair<String,Integer> p = new Pair<String, Integer>(v,c);
			cs.add(p);
		}
		return new NewSetContClassEvent(time, cs);
	}

}
