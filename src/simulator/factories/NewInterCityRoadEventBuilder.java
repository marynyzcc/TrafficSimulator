package simulator.factories;

import simulator.model.NewInterCityRoadEvent;
import simulator.model.NewRoadEvent;

public class NewInterCityRoadEventBuilder extends NewRoadEventBuilder {
	
	public NewInterCityRoadEventBuilder() {
		super("new_inter_city_road");
	}
	
	@Override
	protected NewRoadEvent createTheInstanceOfNewRoadEvent() {
		return new NewInterCityRoadEvent(time, id, srcJunction, destJunction, length, co2limit, maxSpeed, weather);
	}

}
