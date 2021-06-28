package simulator.model;

public class InterCityRoad extends Road {

	InterCityRoad(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length, Weather weather) {
		super(id, srcJunc, destJunc, maxSpeed, contLimit, length, weather);
		
	}

	@Override
	void reduceTotalContamination() {
		int x;
		
		switch(this.weatherConditions) {
			case SUNNY: x = 2; break;
			case CLOUDY: x = 3; break;
			case RAINY: x = 10; break;
			case WINDY: x = 15; break;
			case STORM: x = 20; break;
			default: x = 0; 
		}
		
		this.totalContamination = (int) (((100.0-x)/100.0) * this.totalContamination);
	}

	@Override
	void updateSpeedLimit() {
		if(this.totalContamination > this.contaminationAlarmLimit) 
			this.currentSpeedLimit = (int) (this.maximumSpeed * 0.5);
		else 
			this.currentSpeedLimit = this.maximumSpeed;
	}

	@Override
	int calculateVehicleSpeed(Vehicle v) {
		if(this.weatherConditions == Weather.STORM)
			return (int)(this.currentSpeedLimit*0.8);
		else 
			return this.currentSpeedLimit;
	}

}
