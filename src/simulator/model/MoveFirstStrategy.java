package simulator.model;

import java.util.ArrayList;
import java.util.List;

public class MoveFirstStrategy implements DequeuingStrategy {
	
	public MoveFirstStrategy() {}
	
	@Override
	public List<Vehicle> dequeue(List<Vehicle> q) {
		List<Vehicle> qAux = new ArrayList<Vehicle>();
		
		if(!q.isEmpty())
			qAux.add(q.get(0));
	
		return qAux;
	}
	
	
}
