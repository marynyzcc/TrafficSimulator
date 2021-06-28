package simulator.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;

public class StatusBar extends JPanel implements TrafficSimObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel time;
	private JLabel info;

	public StatusBar(Controller ctrl) {
		ctrl.addObserver(this);
		initGUI();
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		this.time.setText("Time: " + time);
		this.info.setText("");
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		this.time.setText("Time: " + time);
		this.info.setText("");
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		this.time.setText("Time: " + time);
		this.info.setText("Event added (" + e.toString() + ")");
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		this.time.setText("Time: " + time);
		this.info.setText("");
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
	}

	@Override
	public void onError(String err) {
		this.info.setText(err);
	}

	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

		// Time
		this.time = new JLabel("Time: 0");
		this.time.setPreferredSize(new Dimension(150, 20));
		this.add(this.time);

		// Info
		this.info = new JLabel("Welcome!");
		this.add(this.info);
		this.add(Box.createGlue());
	}
}