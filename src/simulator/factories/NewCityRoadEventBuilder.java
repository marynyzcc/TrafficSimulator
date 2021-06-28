package simulator.factories;


import simulator.model.NewCityRoadEvent;
import simulator.model.NewRoadEvent;

public class NewCityRoadEventBuilder extends NewRoadEventBuilder {

	public NewCityRoadEventBuilder() {
		super("new_city_road");
	}
	
	@Override
	protected NewRoadEvent createTheInstanceOfNewRoadEvent() {
		return new NewCityRoadEvent(time, id, srcJunction, destJunction, length, co2limit, maxSpeed, weather);
	}

}
