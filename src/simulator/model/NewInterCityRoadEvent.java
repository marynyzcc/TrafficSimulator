package simulator.model;

public class NewInterCityRoadEvent extends NewRoadEvent {

	public NewInterCityRoadEvent(int time, String id, String srcJun, String destJunc, int length, int co2Limit,
			int maxSpeed, Weather weather) {
		super(time, id, srcJun, destJunc, length, co2Limit, maxSpeed, weather);
	}

	@Override
	protected Road createRoadObject() {
		return new InterCityRoad(id, srcJunction , destJunction, maxSpeed, co2Limit, length, weather);
	}
	
	@Override
	public String toString() {
		return "New Inter City Road '" + this.id + "'"; 
	}
}
