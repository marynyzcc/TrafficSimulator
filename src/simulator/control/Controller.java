package simulator.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.factories.Factory;
import simulator.model.Event;
import simulator.model.TrafficSimObserver;
import simulator.model.TrafficSimulator;

public class Controller {
	private TrafficSimulator sim;
	private Factory<Event> eventsFactory;
	
	public Controller(TrafficSimulator sim, Factory<Event> eventsFactory) {
		if(sim == null || eventsFactory == null) throw new IllegalArgumentException("Arguments cannot be null");
		this.sim = sim; 
		this.eventsFactory = eventsFactory;
	}
	
	public void loadEvents(InputStream in) {
		JSONObject jo = new JSONObject(new JSONTokener(in));
		JSONArray ja = jo.getJSONArray("events");
		if(ja == null) throw new IllegalArgumentException("Input does not have a correct form.");
		for(int i = 0; i < ja.length(); i++) {
			sim.addEvent(eventsFactory.createInstance(ja.getJSONObject(i)));
		}
	}
	
	public void run(int n, OutputStream out) throws Exception {
		PrintStream p = new PrintStream(out);
		p.println("{" + "\n" + "  \"states\": [");
		
		for(int i = 0; i < n; i++) {
			sim.advance();
			if(i == n-1)
				p.println(sim.report());
			else
				p.println(sim.report() + ",");
		}
		p.println("]" + "\n" + "}");
	}
	
	public void reset() {
		sim.reset();
	}
	
	public void addObserver(TrafficSimObserver o) {
		sim.addObserver(o);
	}
	
	public void removeObserver(TrafficSimObserver o) {
		sim.removeObserver(o);
	}
	
	public void addEvent(Event e) {
		sim.addEvent(e);
	}
	
	public void run(int n) throws Exception {
		for(int i = 0; i < n; i++) 
			sim.advance();
	}
}
