package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;
import simulator.model.Vehicle;
import simulator.model.VehicleStatus;

public class VehiclesTableModel extends AbstractTableModel implements TrafficSimObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Vehicle> vehicleList;
	private String[] columns = { "Id", "Location", "Itinerary", "CO2 Class", "Max.Speed", "Speed", "Total CO2",
			"Distance" };

	public VehiclesTableModel(Controller ctrl) {
		ctrl.addObserver(this);
		vehicleList = new ArrayList<Vehicle>();
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public int getRowCount() {
		return vehicleList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Vehicle vehicle = vehicleList.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return vehicle.getId();
		case 1:
			if (vehicle.getStatus() == VehicleStatus.WAITING) {
				return "Waiting:" + vehicle.getWaitingInJunction();
			} else if (vehicle.getStatus() == VehicleStatus.ARRIVED) {
				return "Arrived";
			} else if (vehicle.getStatus() == VehicleStatus.PENDING) {
				return "Pending";
			} else {
				return vehicle.getRoad().getId() + ":" + vehicle.getLocation();
			}
		case 2:
			return vehicle.getItinerary();
		case 3:
			return vehicle.getContClass();
		case 4:
			return vehicle.getMaxSpeed();
		case 5:
			return vehicle.getSpeed();
		case 6:
			return vehicle.getTotalContamination();
		default:
			return vehicle.getTotalTravelledDistance();
		}
	}

	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		this.vehicleList.clear();
		this.vehicleList.addAll(map.getVehicles());
		fireTableDataChanged();
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		this.vehicleList.clear();
		this.vehicleList.addAll(map.getVehicles());
		fireTableDataChanged();

	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		this.vehicleList.clear();
		this.vehicleList.addAll(map.getVehicles());
		fireTableDataChanged();

	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		this.vehicleList.clear();
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
