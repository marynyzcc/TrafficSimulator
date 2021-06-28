package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.Junction;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;

public class JunctionsTableModel extends AbstractTableModel implements TrafficSimObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Junction> junctionList;
	private String[] columns = { "Id", "Green", "Queues" };

	public JunctionsTableModel(Controller ctrl) {
		ctrl.addObserver(this);
		junctionList = new ArrayList<Junction>();
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public int getRowCount() {
		return junctionList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Junction junction = junctionList.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return junction.getId();
		case 1:
			return junction.getRoadWithGreenLight();
		default:
			return junction.getQueues();
		}
	}

	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		this.junctionList.clear();
		this.junctionList.addAll(map.getJunctions());
		fireTableDataChanged();
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		this.junctionList.clear();
		this.junctionList.addAll(map.getJunctions());
		fireTableDataChanged();
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		this.junctionList.clear();
		this.junctionList.addAll(map.getJunctions());
		fireTableDataChanged();
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		this.junctionList.clear();
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
