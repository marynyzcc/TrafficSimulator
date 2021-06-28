package simulator.model;

import java.util.Iterator;
import java.util.List;

public class MostCrowdedStrategy implements LightSwitchingStrategy {
	
	private int timeSlot;
	
	public MostCrowdedStrategy(int timeSlot) {
		this.timeSlot = timeSlot;
	}

	@Override
	public int chooseNextGreen(List<Road> roads, List<List<Vehicle>> qs, int currGreen, int lastSwitchingTime, int currTime) {
		int lq = 0;
		int aux = 0;
		
		//lista de carreteras vacias -> -1
		if(roads.isEmpty()) return -1;
		else if(currGreen == -1) {	
			Iterator<List<Vehicle>> it = qs.iterator();
			while(it.hasNext()) {
				aux = it.next().size();
				if(aux > lq) lq = aux;
			}
			return lq;
		}
		// devuelve -> currgreen
		else if((currTime-lastSwitchingTime) < this.timeSlot) return currGreen;
		//devuelve -> indice de la siguiente carrera entrante
		else {
			lq = currGreen;
			
			for(int i = (currGreen+1)%roads.size(); i < qs.size(); i++) {
				aux = qs.get(i).size();
				if(aux > lq) lq = aux;
			}
			return lq;
		}
	}

}
