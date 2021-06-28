package simulator.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.Road;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;
import simulator.model.Vehicle;
import simulator.model.VehicleStatus;
import simulator.model.Weather;

public class MapByRoadComponent extends JComponent implements TrafficSimObserver {

	private static final long serialVersionUID = 1L;

	private static final int _JRADIUS = 10;

	private static final Color _BG_COLOR = Color.WHITE;
	private static final Color _JUNCTION_COLOR = Color.BLUE;
	private static final Color _JUNCTION_LABEL_COLOR = new Color(200, 100, 0);
	private static final Color _GREEN_LIGHT_COLOR = Color.GREEN;
	private static final Color _RED_LIGHT_COLOR = Color.RED;
	private static final Color _ROAD_LABEL_COLOR = Color.BLACK;
	private static final Color _ROAD_COLOR = Color.BLACK;

	private RoadMap _map;
	private List<Image> contClassImageList;
	private Image _car;

	public MapByRoadComponent(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
		setPreferredSize(new Dimension(300, 200));
	}

	public void initGUI() {
		contClassImageList = new ArrayList<Image>();

		for (int i = 0; i < 6; i++)
			contClassImageList.add(loadImage("cont_" + i + ".png"));
		_car = loadImage("car.png");
	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// clear with a background color
		g.setColor(_BG_COLOR);
		g.clearRect(0, 0, getWidth(), getHeight());

		if (_map == null || _map.getJunctions().size() == 0) {
			g.setColor(Color.red);
			g.drawString("No map yet!", getWidth() / 2 - 50, getHeight() / 2);
		} else
			try {
				drawMap(g);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private void drawMap(Graphics g) throws IOException {

		List<Road> roads = Collections.unmodifiableList(new ArrayList<>(_map.getRoads()));
		for (int i = 0; i < _map.getRoads().size(); i++) {
			int x1 = 50;
			int x2 = getWidth() - 100;
			Road road = roads.get(i);
			int y = (i + 1) * 50;

			// Draw the road line, the road goes from (x1,y) to (x2,y)
			g.setColor(_ROAD_COLOR);
			g.drawLine(x1, y, x2, y);

			// Draw a circle and the identifier of source's junction at (x1,y)
			g.setColor(_JUNCTION_COLOR);
			g.fillOval(x1 - _JRADIUS / 2, y - _JRADIUS / 2, _JRADIUS, _JRADIUS);
			g.setColor(_JUNCTION_LABEL_COLOR);
			g.drawString(road.getSourceJunction().getId(), x1, y - 8);

			// Draw a circle and the identifier of destination's junction at (x2,y)
			Color destinationJunctionColor = _RED_LIGHT_COLOR;
			int idx = road.getDestinationJunction().getGreenLightIndex();

			if (idx != -1 && road.equals(road.getDestinationJunction().getincomingRoadList().get(idx)))
				destinationJunctionColor = _GREEN_LIGHT_COLOR;

			g.setColor(destinationJunctionColor);
			g.fillOval(x2 - _JRADIUS / 2, y - _JRADIUS / 2, _JRADIUS, _JRADIUS);
			g.setColor(_JUNCTION_LABEL_COLOR);
			g.drawString(road.getDestinationJunction().getId(), x2, y - 8);

			// Draw the vehicles
			drawVehicles(g);

			// Draw the road's label
			g.setColor(_ROAD_LABEL_COLOR);
			g.drawString(road.getId(), x1 - 25, y);

			// Draw weather of the road
			g.drawImage(Weather.getImage(road.getWeatherConditions()), x2 + 8, y - 15, 32, 32, this);

			// Draw contamination class of the road
			int contClass = (int) Math.floor(
					Math.min((double) road.getTotalContamination() / (1.0 + (double) road.getContaminationAlarmLimit()),
							(1.0 / 0.19)));
			g.drawImage(contClassImageList.get(contClass), x2 + 44, y - 15, 32, 32, this);
		}
	}

	private void drawVehicles(Graphics g) {
		for (Vehicle v : _map.getVehicles()) {
			if (v.getStatus() != VehicleStatus.ARRIVED) {
				Road r = v.getRoad();
				int x1 = 50;
				int x2 = getWidth() - 100;
				int x = x1 + (int) ((x2 - x1) * (double) v.getLocation() / (double) r.getLength());
				int i = _map.getRoads().indexOf(r);
				int y = (i + 1) * 50;

				// Choose a color for the vehicle's label and background, depending on its
				// contamination class
				int vLabelColor = (int) (25.0 * (10.0 - (double) v.getContClass()));
				g.setColor(new Color(0, vLabelColor, 0));

				// draw an image of a car (with circle as background) and it identifier
				g.fillOval(x, y - 10, 16, 16);
				g.drawImage(_car, x, y - 10, 16, 16, this);
				g.drawString(v.getId(), x + 2, y - 12);
			}
		}
	}

	// loads an image from a file
	private Image loadImage(String img) {
		Image i = null;
		try {
			return ImageIO.read(new File("resources/icons/" + img));
		} catch (IOException e) {
		}
		return i;
	}

	public void update(RoadMap map) {
		_map = map;
		repaint();
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {

	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		update(map);
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		update(map);
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		update(map);
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
		update(map);
	}

	@Override
	public void onError(String err) {

	}
}
