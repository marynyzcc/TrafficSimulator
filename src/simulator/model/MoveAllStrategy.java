package simulator.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MoveAllStrategy implements DequeuingStrategy{
	
	public MoveAllStrategy() {}
	
	@Override
	public List<Vehicle> dequeue(List<Vehicle> q) {
		List<Vehicle> qAux = new ArrayList<Vehicle>();
		if(!q.isEmpty()) {
			Iterator<Vehicle> it = q.iterator();
			while(it.hasNext()) {
				qAux.add(it.next());
			}
		}

		return qAux;
	}

}
