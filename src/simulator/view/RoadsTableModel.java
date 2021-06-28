package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.Road;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;

public class RoadsTableModel extends AbstractTableModel implements TrafficSimObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Road> roadList;
	private String[] columns = { "Id", "Length", "Weather", "Max.Speed", "Speed Limit", "Total CO2", "CO2 Limit" };

	public RoadsTableModel(Controller ctrl) {
		ctrl.addObserver(this);
		roadList = new ArrayList<Road>();
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public int getRowCount() {
		return roadList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Road road = roadList.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return road.getId();
		case 1:
			return road.getLength();
		case 2:
			return road.getWeatherConditions().toString();
		case 3:
			return road.getMaximumSpeed();
		case 4:
			return road.getCurrentSpeed();
		case 5:
			return road.getTotalContamination();
		default:
			return road.getContaminationAlarmLimit();
		}
	}

	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		this.roadList.clear();
		this.roadList.addAll(map.getRoads());
		fireTableDataChanged();
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		this.roadList.clear();
		this.roadList.addAll(map.getRoads());
		fireTableDataChanged();
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		this.roadList.clear();
		this.roadList.addAll(map.getRoads());
		fireTableDataChanged();
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		this.roadList.clear();
		fireTableDataChanged();
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
		fireTableDataChanged();
	}

	@Override
	public void onError(String err) {
	}
}
