package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

public class Vehicle extends SimulatedObject {

	private List<Junction> itineraryCopy;
	private int indexLastJunctionVisited;
	private int indexCurrentJunctionWaiting;
	private int maximumSpeed;
	private int currentSpeed;
	private VehicleStatus status;
	private Road road;
	private int location;
	private int contaminationClass;
	private int totalContamination;
	private int totalTravelledDistance;

	Vehicle(String id, int maxSpeed, int contClass, List<Junction> itinerary) {
		super(id);

		if (maxSpeed < 0)
			throw new IllegalArgumentException("Maximum speed of a vehicle cannot be negative");
		if (itinerary.size() < 2)
			throw new IllegalArgumentException("The length of the itinerary must be at least 2");

		setContaminationClass(contClass);
		this.maximumSpeed = maxSpeed;
		this.itineraryCopy = Collections.unmodifiableList(new ArrayList<>(itinerary));
		this.currentSpeed = 0;
		this.status = VehicleStatus.PENDING;
		this.road = null;
		this.location = 0;
		this.totalContamination = 0;
		this.totalTravelledDistance = 0;
		this.indexLastJunctionVisited = -1;
		this.indexCurrentJunctionWaiting = 0;
	}

	@Override
	void advance(int time) {

		if (this.status == VehicleStatus.TRAVELING) {
			int previousLocation = this.location;
			int x = this.location + this.currentSpeed;

			if (x > this.road.getLength())
				this.location = this.road.getLength();
			else
				this.location = x;

			this.totalTravelledDistance += (this.location - previousLocation);

			int c = (this.location - previousLocation) * this.contaminationClass;

			this.totalContamination += c;
			this.road.addContamination(c);

			if (this.location == this.road.getLength()) {
				this.indexCurrentJunctionWaiting = this.indexLastJunctionVisited + 1;
				this.itineraryCopy.get(this.indexCurrentJunctionWaiting).enter(this);
				this.status = VehicleStatus.WAITING;
				this.currentSpeed = 0;
			}
		}
	}

	@Override
	public JSONObject report() {
		JSONObject vehicle = new JSONObject();

		vehicle.put("id", this._id);
		vehicle.put("speed", this.currentSpeed);
		vehicle.put("distance", this.totalTravelledDistance);
		vehicle.put("co2", this.totalContamination);
		vehicle.put("class", this.contaminationClass);
		vehicle.put("status", this.status);

		if (this.status != VehicleStatus.PENDING && this.status != VehicleStatus.ARRIVED) {
			vehicle.put("road", this.road._id);
			vehicle.put("location", this.location);
		}

		return vehicle;
	}

	void moveToNextRoad() {
		if (status != VehicleStatus.PENDING && status != VehicleStatus.WAITING)
			throw new IllegalArgumentException(
					"Vehicle must be in pending or waiting status in order to move to next road");

		if (status == VehicleStatus.WAITING)
			this.road.exit(this);

		this.indexLastJunctionVisited++;

		if (this.indexCurrentJunctionWaiting + 1 < this.itineraryCopy.size()) {
			Junction nextJunction = this.itineraryCopy.get(this.indexCurrentJunctionWaiting + 1);
			this.road = this.itineraryCopy.get(this.indexCurrentJunctionWaiting).roadTo(nextJunction);
			this.location = 0;
			this.road.enter(this);
			status = VehicleStatus.TRAVELING;
		} else
			status = VehicleStatus.ARRIVED;
	}

	// getters
	public int getLocation() {
		return this.location;
	}

	public int getSpeed() {
		return this.currentSpeed;
	}

	public int getMaxSpeed() {
		return this.maximumSpeed;
	}

	public int getContClass() {
		return this.contaminationClass;
	}

	public VehicleStatus getStatus() {
		return this.status;
	}

	public List<Junction> getItinerary() {
		return this.itineraryCopy;
	}

	public Road getRoad() {
		return this.road;
	}

	public int getTotalContamination() {
		return this.totalContamination;
	}

	public String getWaitingInJunction() {
		return this.itineraryCopy.get(this.indexCurrentJunctionWaiting).getId();
	}

	public int getTotalTravelledDistance() {
		return this.totalTravelledDistance;
	}

	// setters
	void setSpeed(int s) {
		if (s <= 0)
			throw new IllegalArgumentException("Speed must be positive");

		if (s > this.maximumSpeed)
			this.currentSpeed = this.maximumSpeed;
		else
			this.currentSpeed = s;
	}

	void setContaminationClass(int c) {
		if (c < 0 || c > 10)
			throw new IllegalArgumentException("Contamination class mast be a number between 0 and 10");

		this.contaminationClass = c;
	}

}