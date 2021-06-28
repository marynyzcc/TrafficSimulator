package simulator.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewVehicleEvent extends Event {
	
	private String id;
	private int maxSpeed;
	private int contClass;
	private List<String> itinerary;

	public NewVehicleEvent(int time, String id, int maxSpeed, int
			contClass, List<String> itinerary) {
		super(time);
		this.id = id;
		this.maxSpeed = maxSpeed;
		this.contClass = contClass;
		this.itinerary = itinerary;
	}

	@Override
	void execute(RoadMap map) {
		List<Junction> itin = new ArrayList<Junction>();
		Iterator<String> it = itinerary.iterator();
		while(it.hasNext()) {
			Junction j = map.getJunction(it.next());
			itin.add(j);
		}
		Vehicle v = new Vehicle(id, maxSpeed, contClass, itin);
		map.addVehicle(v);
		
		map.getVehicle(v.getId()).moveToNextRoad();
	}
	
	@Override
	public String toString() {
		return "New Vehicle '" + this.id + "'"; 
	}
}
