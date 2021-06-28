package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;


public class RoadMap {
	private List<Junction> junctionList;
	private List<Road> roadList;
	private List<Vehicle> vehicleList;
	private Map<String,Junction> junctionMap;
	private Map<String,Road> roadMap;
	private Map<String,Vehicle> vehicleMap;
	
	
	RoadMap() {
		this.junctionList = new ArrayList<Junction>();
		this.roadList = new ArrayList<Road>();
		this.vehicleList = new ArrayList<Vehicle>();
		this.junctionMap = new HashMap<String,Junction>();
		this.roadMap = new HashMap<String,Road>();
		this.vehicleMap = new HashMap<String,Vehicle>();
	}
	
	void addJunction(Junction j) {
		
		if(junctionList.contains(j)) throw new IllegalArgumentException("Junction cannot be added in RoadMap.");
		junctionList.add(j);
		junctionMap.put(j._id, j);
	}
	
	
	
	void addRoad(Road r){
		if(!roadList.contains(r) && junctionMap.containsValue(r.getSourceJunction()) && junctionMap.containsValue(r.getDestinationJunction())) {
			roadList.add(r);
			roadMap.put(r._id, r);
		}
		else throw new IllegalArgumentException("Road cannot be added in RoadMap");
		
	}
	
	private boolean itineraryIsValid(Vehicle v) {
		boolean isValid = true;
		int i = 0;
		while(i < v.getItinerary().size() - 1  && isValid) {
			Junction src = v.getItinerary().get(i);
			Junction dest = v.getItinerary().get(i + 1);
			Road r = src.roadTo(dest);
			isValid = roadList.contains(r);
			i++;
		}
		
		return isValid;
	}
	
	void addVehicle(Vehicle v){
		if(!vehicleList.contains(v) && itineraryIsValid(v)) {
			vehicleList.add(v);
			vehicleMap.put(v._id, v);
			}
		else throw new IllegalArgumentException("Vehicle cannot be added in RoadMap");
	}
	
	public Junction getJunction(String id) {
		return junctionMap.get(id);
		
	}
	
	public Road getRoad(String id) {
		return roadMap.get(id);
		
	}
	
	public Vehicle getVehicle(String id) {
		return vehicleMap.get(id);
		
	}
	
	public List<Junction>getJunctions(){
		return Collections.unmodifiableList(junctionList);
		
	}
	
	public List<Road>getRoads(){
		return Collections.unmodifiableList(roadList);
		
	}
	
	public List<Vehicle>getVehicles(){
		return Collections.unmodifiableList(vehicleList);
		
	}
	
	void reset() {
		junctionList.clear();
		roadList.clear();
		vehicleList.clear();
		junctionMap.clear();
		roadMap.clear();
		vehicleMap.clear();
	}
	
	public JSONObject report() {
		JSONObject roadMapInfo = new JSONObject();
		JSONArray junctionsReport = new JSONArray();
		JSONArray roadsReport = new JSONArray();
		JSONArray vehiclesReport = new JSONArray();
		
		List<Junction> jl = Collections.unmodifiableList(new ArrayList<>(junctionList));
		List<Road> rl = Collections.unmodifiableList(new ArrayList<>(roadList));
		List<Vehicle> vl = Collections.unmodifiableList(new ArrayList<>(vehicleList));
		
		for(Junction j: jl)
			junctionsReport.put(j.report());
		
		for(Road r: rl)
			roadsReport.put(r.report());
		
		for(Vehicle v: vl)
			vehiclesReport.put(v.report());
		
		roadMapInfo.put("junctions", junctionsReport);
		roadMapInfo.put("roads", roadsReport);
		roadMapInfo.put("vehicles", vehiclesReport);
		
		return roadMapInfo;
	}
}
