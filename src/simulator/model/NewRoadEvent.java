package simulator.model;

public abstract class NewRoadEvent extends Event {
	
	protected String id;
	protected String srcJun; 
	protected String destJunc;
	protected int length; 
	protected int co2Limit;
	protected int maxSpeed;
	protected Weather weather;
	protected Junction srcJunction;
	protected Junction destJunction;

	public NewRoadEvent(int time, String id, String srcJun, String
			destJunc, int length, int co2Limit, int maxSpeed, Weather weather) {
		super(time);
		this.id = id;
		this.srcJun = srcJun;
		this.destJunc = destJunc;
		this.length = length;
		this.co2Limit = co2Limit;
		this.maxSpeed = maxSpeed;
		this.weather = weather;
	}

	@Override
	void execute(RoadMap map) {
		srcJunction = map.getJunction(srcJun);
		destJunction = map.getJunction(destJunc);
		Road r = createRoadObject();
		map.addRoad(r);
	}
	
	abstract protected Road createRoadObject();
	
	@Override
	public String toString() {
		return "New Road '" + this.id + "'"; 
	}
}
