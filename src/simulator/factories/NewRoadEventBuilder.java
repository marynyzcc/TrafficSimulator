package simulator.factories;

import org.json.JSONObject;

import simulator.model.Event;
import simulator.model.NewRoadEvent;
import simulator.model.Weather;

public abstract class NewRoadEventBuilder extends Builder<Event> {
	protected int time;
	protected String id;
	protected String srcJunction;
	protected String destJunction;
	protected int length;
	protected int co2limit;
	protected int maxSpeed;
	protected Weather weather;
	
	
	public NewRoadEventBuilder(String type) {
		super(type);
	}
	
	
	protected Event createTheInstance(JSONObject data) {
		this.time = data.getInt("time");
		this.id = data.getString("id");
		this.srcJunction = data.getString("src");
		this.destJunction = data.getString("dest");
		this.length = data.getInt("length");
		this.co2limit = data.getInt("co2limit");
		this.maxSpeed = data.getInt("maxspeed");
		this.weather = Weather.valueOf(data.getString("weather"));
		
		
		return createTheInstanceOfNewRoadEvent();
	}
	
	abstract protected NewRoadEvent createTheInstanceOfNewRoadEvent();
	
	
}
