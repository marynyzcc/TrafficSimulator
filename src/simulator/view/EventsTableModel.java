package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;

public class EventsTableModel extends AbstractTableModel implements TrafficSimObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Event> eventList;
	private String[] columns = { "Time", "Description" };

	public EventsTableModel(Controller ctrl) {
		ctrl.addObserver(this);
		eventList = new ArrayList<Event>();
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public int getRowCount() {
		return eventList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Event event = eventList.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return event.getTime();
		default:
			return event.toString();
		}
	}

	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		this.eventList.clear();
		this.eventList.addAll(events);
		fireTableDataChanged();
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		this.eventList.clear();
		this.eventList.addAll(events);
		fireTableDataChanged();
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		this.eventList.clear();
		this.eventList.addAll(events);
		fireTableDataChanged();
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		this.eventList.clear();
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
