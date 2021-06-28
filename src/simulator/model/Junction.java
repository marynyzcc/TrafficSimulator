package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Junction extends SimulatedObject {

	private List<Road> incomingRoad;
	private Map<Junction, Road> outcoming;
	private List<List<Vehicle>> qs;
	private Map<Road, List<Vehicle>> qsMap;
	private int currGreen;
	private int lastSwitchingTime;
	private LightSwitchingStrategy lsStrategy;
	private DequeuingStrategy dqStrategy;
	private int x;
	private int y;

	Junction(String id, LightSwitchingStrategy lsStrategy, DequeuingStrategy dqStrategy, int xCoor, int yCoor) {
		super(id);
		if (lsStrategy == null)
			throw new IllegalArgumentException("Light switching strategy cannot be null");
		if (dqStrategy == null)
			throw new IllegalArgumentException("Dequeuing strategy cannot be null");
		if (xCoor < 0 || yCoor < 0)
			throw new IllegalArgumentException("Coordinates must be positive");

		this.lsStrategy = lsStrategy;
		this.dqStrategy = dqStrategy;
		this.x = xCoor;
		this.y = yCoor;
		this.currGreen = -1;
		this.lastSwitchingTime = -1;
		this.incomingRoad = new ArrayList<Road>();
		this.outcoming = new HashMap<Junction, Road>();
		this.qs = new ArrayList<List<Vehicle>>();
		this.qsMap = new HashMap<Road, List<Vehicle>>();

	}

	@Override
	void advance(int time) {
		// Paso 1:
		// hay que llamar a la primera lista de la lista qs
		if (currGreen != -1) {
			List<Vehicle> list = dqStrategy.dequeue(qs.get(currGreen));
			if (!list.isEmpty()) {
				Iterator<Vehicle> it = list.iterator();
				while (it.hasNext()) {
					it.next().moveToNextRoad();
				}
				qs.get(currGreen).clear(); // qs apunta a la misma lista que qsMap
			}
		}
		// Paso 2:
		// calcula el indice de la siguiente carrera a poner el semaforo en verde
		int i = lsStrategy.chooseNextGreen(incomingRoad, qs, currGreen, lastSwitchingTime, time);
		if (i != currGreen) {
			currGreen = i;
			lastSwitchingTime = time;
		}
	}

	@Override
	public JSONObject report() {
		JSONObject junction = new JSONObject();
		junction.put("id", this._id);

		String idRoad;
		if (this.currGreen == -1)
			idRoad = "none";
		else
			idRoad = incomingRoad.get(this.currGreen).getId();

		junction.put("green", idRoad);

		List<Road> ir = Collections.unmodifiableList(new ArrayList<>(incomingRoad));
		JSONArray incomingRoadsQueueList = new JSONArray();

		for (Road r : ir) {
			JSONObject roadInfo = new JSONObject();
			JSONArray vehicles_id = new JSONArray();

			roadInfo.put("road", r.getId());

			for (Vehicle v : r.getVehicles())
				if (v.getStatus() == VehicleStatus.WAITING)
					vehicles_id.put(v.getId());

			roadInfo.put("vehicles", vehicles_id);
			incomingRoadsQueueList.put(roadInfo);
		}

		junction.put("queues", incomingRoadsQueueList);

		return junction;
	}

	public String getQueues() {
		String queues = "";

		if (!incomingRoad.isEmpty()) {
			for (int i = 0; i < qs.size(); i++) {
				queues += this.incomingRoad.get(i).getId() + ":[";

				List<Vehicle> lv = qs.get(i);

				for (int j = 0; j < lv.size(); j++) {
					queues += lv.get(j).getId();

					if (j != lv.size() - 1)
						queues += ",";
				}
				queues += "] ";
			}
		}

		return queues;
	}

	void enter(Vehicle v) {
		Road r = v.getRoad();
		qsMap.get(r).add(v); 
	}

	void addIncommingRoad(Road r) {
		if (r.getDestinationJunction() == this) {
			this.incomingRoad.add(r);
			List<Vehicle> list = new LinkedList<Vehicle>(); // la lista de vehiculos esta vacia en esa r
			qs.add(list);
			qsMap.put(r, list);
		} else
			throw new IllegalArgumentException("This Road connot be added in incoming roads of Junction.");

	}

	void addOutGoingRoad(Road r) {
		if (r.getSourceJunction() == this && !this.outcoming.containsKey(this)) {
			this.outcoming.put(r.getDestinationJunction(), r);
		} else
			throw new IllegalArgumentException("This Road cannot be added in outcoming roads of Junction.");
	}

	Road roadTo(Junction j) {
		return this.outcoming.get(j);
	}

	// getters
	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getGreenLightIndex() {
		return this.currGreen;
	}
	
	public List<Road> getincomingRoadList() {
		return this.incomingRoad;
	}

	public String getRoadWithGreenLight() {
		if (this.currGreen == -1)
			return "NONE";
		else
			return this.incomingRoad.get(this.currGreen).getId();
	}

}
