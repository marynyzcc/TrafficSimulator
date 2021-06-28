package simulator.model;

public class CityRoad extends Road {

	CityRoad(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length, Weather weather) {
		super(id, srcJunc, destJunc, maxSpeed, contLimit, length, weather);
	}

	@Override
	void reduceTotalContamination() {
		if(this.weatherConditions == Weather.WINDY || this.weatherConditions == Weather.STORM)
			this.totalContamination -= 10;
		else 
			this.totalContamination -= 2;
		
		if(this.totalContamination < 0) this.totalContamination = 0;
	}

	@Override
	void updateSpeedLimit() {}

	@Override
	int calculateVehicleSpeed(Vehicle v) {
		return (int)(((11.0 - v.getContClass())/11.0) * this.currentSpeedLimit);

	}

}
