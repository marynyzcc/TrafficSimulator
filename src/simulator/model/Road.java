package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;


public abstract class Road extends SimulatedObject {

	protected Junction sourceJunction;
	protected Junction destinationJunction;
	protected int length;
	protected int maximumSpeed;
	protected int currentSpeedLimit;
	protected int contaminationAlarmLimit;
	protected Weather weatherConditions;
	protected int totalContamination;
	protected List<Vehicle> vehicles;
	
	
	Road(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length, Weather weather) {
		super(id);
		
		if(maxSpeed <= 0) throw new IllegalArgumentException("Maximum speed must be positive");
		if(contLimit < 0) throw new IllegalArgumentException("Contamination limit must be positive");
		if(length < 0) throw new IllegalArgumentException("Length of the road must be positive");
		if(srcJunc == null) throw new IllegalArgumentException("Source junction cannot be null");
		if(destJunc == null) throw new IllegalArgumentException("Destination juction cannot be null");
		
		setWeather(weather);
		this.sourceJunction = srcJunc;
		this.destinationJunction = destJunc;
		this.sourceJunction.addOutGoingRoad(this);
		this.destinationJunction.addIncommingRoad(this);
		this.maximumSpeed = maxSpeed;
		this.contaminationAlarmLimit = contLimit;
		this.length = length;
		this.currentSpeedLimit = this.maximumSpeed;
		this.totalContamination = 0;
		vehicles = new ArrayList<Vehicle>();
	}
	
	abstract void reduceTotalContamination();
	abstract void updateSpeedLimit();
	abstract int calculateVehicleSpeed(Vehicle v);
	
	
	@Override
	void advance(int time) {
		//Paso 1: disminuir CO2
		reduceTotalContamination();
		
		//Paso 2: modifica el limte de velocidad
		updateSpeedLimit();
		
		//Paso 3: recorre la lista de vehiculos
		for(Vehicle v: vehicles) {
			if(v.getStatus() != VehicleStatus.WAITING)
				v.setSpeed(calculateVehicleSpeed(v));
			v.advance(time);
		}
		
		//Ordenar la lista de vehiculos por su localizacion
		Comparator<Vehicle> cmp = new Comparator<Vehicle>() {
			public int compare(Vehicle v1, Vehicle v2) {
				if(v1.getLocation() < v2.getLocation())
					return 1;
				else if(v2.getLocation() > v2.getLocation())
					return -1;
				else return 0;
			}
		};
		
		Collections.sort(vehicles, cmp);
		
	}

	@Override
	public JSONObject report() {
		JSONObject road = new JSONObject();
		
		road.put("id", this._id);
		road.put("speedlimit", this.currentSpeedLimit);
		road.put("weather", this.weatherConditions);
		road.put("co2", this.totalContamination);
		
		List<Vehicle> lv = Collections.unmodifiableList(new ArrayList<>(vehicles));
		JSONArray vehicle_ids = new JSONArray();
		
		for(Vehicle v: lv)
			vehicle_ids.put(v.getId());
		
		road.put("vehicles", vehicle_ids);
		
		return road;
	}
	
	public void addContamination (int c) {
		if(c < 0) throw new IllegalArgumentException("Cannot add negative units of co2. Must be positive");
		
		this.totalContamination += c;
	}
	
	void enter(Vehicle v) {
		if(v.getLocation() != 0 && v.getSpeed() != 0) throw new IllegalArgumentException ("The vehicle cannot enter on the road, is not in waiting nor pending status");
		
		vehicles.add(v);
	}
	
	void exit(Vehicle v) {
		vehicles.remove(v);
	}
	
	//getters
	public int getLength() {
		return this.length;
	}
	
	public Junction getSourceJunction() {
		return this.sourceJunction;
	}

	public Junction getDestinationJunction() {
		return this.destinationJunction;
	}

	public int getMaximumSpeed() {
		return this.maximumSpeed;
	}

	public int getCurrentSpeed() {
		return this.currentSpeedLimit;
	}

	public int getContaminationAlarmLimit() {
		return this.contaminationAlarmLimit;
	}

	public Weather getWeatherConditions() {
		return this.weatherConditions;
	}

	public int getTotalContamination() {
		return this.totalContamination;
	}

	public List<Vehicle> getVehicles() {
		return this.vehicles;
	}
	
	//setters
	void setWeather(Weather w) {
		if(w == null) throw new IllegalArgumentException("Weather cannot be null");
		
		this.weatherConditions = w;
	}
}
