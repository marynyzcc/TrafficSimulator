package simulator.model;

import org.json.JSONObject;

public abstract class SimulatedObject {

	protected String _id;

	SimulatedObject(String id) {
		if(id == null)
			throw new IllegalArgumentException("Simulated object identifier cannot be null");
		else
			_id = id;
	}

	public String getId() {
		return _id;
	}

	@Override
	public String toString() {
		return _id;
	}

	abstract void advance(int time);

	abstract public JSONObject report();
}
