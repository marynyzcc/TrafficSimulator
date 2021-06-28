package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import simulator.control.Controller;
import simulator.misc.Pair;
import simulator.model.Event;
import simulator.model.NewSetContClassEvent;
import simulator.model.Road;
import simulator.model.RoadMap;
import simulator.model.SetWeatherEvent;
import simulator.model.TrafficSimObserver;
import simulator.model.Vehicle;
import simulator.model.Weather;

public class ControlPanel extends JPanel implements TrafficSimObserver {

	private static final long serialVersionUID = 1L;
	private Controller _ctrl;
	private JToolBar toolBar;
	private RoadMap rMap;
	private int _time;
	private JFileChooser fileChooser;
	private boolean _stopped;
	private int _ticks;

	public ControlPanel(Controller ctrl) {
		this._ctrl = ctrl;
		_ctrl.addObserver(this);
		initGUI();
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		this.rMap = map;
		this._time = time;
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		this.rMap = map;
		this._time = time;
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
		this.rMap = map;
		this._time = time;
	}

	@Override
	public void onError(String err) {
	}

	private void initGUI() {
		JComponent.setDefaultLocale(Locale.ENGLISH);

		setLayout(new BorderLayout());
		this.toolBar = new JToolBar();

		this.fileChooser = new JFileChooser();
		this.fileChooser.setDialogTitle("Open");
		this.fileChooser.setCurrentDirectory(new File("./resources/examples"));
		this.fileChooser.setMultiSelectionEnabled(false);
		this.fileChooser.setFileFilter(new FileNameExtensionFilter("All Files", "json"));

		// Boton LoadEventsFile
		JButton loadEventsFile = new JButton();
		loadEventsFile.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("resources/icons/open.png")));
		loadEventsFile.setToolTipText("Load an events file");
		loadEventsFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnValue = fileChooser.showOpenDialog(getParent());
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					try {
						File file = fileChooser.getSelectedFile();
						if (file == null)
							throw new Exception("The file doesn't exit!");
						InputStream in = new FileInputStream(file);
						_ctrl.reset();
						_ctrl.loadEvents(in);
					} catch (Exception ex) {
						showErrorDialog(ex.getMessage());
					}
				}
			}
		});

		// Boton changeCO2Class
		JButton changeCO2Class = new JButton();
		changeCO2Class.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("resources/icons/co2class.png")));
		changeCO2Class.setToolTipText("Change CO2 Class");
		changeCO2Class.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ChangeCO2Class();
			}
		});

		// Boton changeRoadWeather
		JButton changeRoadWeather = new JButton();
		changeRoadWeather
				.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("resources/icons/weather.png")));
		changeRoadWeather.setToolTipText("Change Road Weather");
		changeRoadWeather.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ChangeRoadWeather();
			}
		});

		// Boton Exit
		JButton exitButton = new JButton();
		exitButton.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("resources/icons/exit.png")));
		exitButton.setToolTipText("Exit");
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showOptionDialog(new Frame(), "Are you sure you want to quit?", "Quit",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);

				if (res == 0)
					System.exit(0);
			}
		});

		// Boton Run
		JButton runButton = new JButton();
		runButton.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("resources/icons/run.png")));
		runButton.setToolTipText("Run");
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_stopped = false;
				enableToolBar(false); // desactivar botones
				run_sim(_ticks);
			}

			private void run_sim(int n) {
				if (n > 0 && !_stopped) {
					try {
						_ctrl.run(1);
					} catch (Exception e) {
						showErrorDialog(e.getMessage());
						_stopped = true;
						return;
					}
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							run_sim(n - 1);
						}
					});
				} else {
					enableToolBar(true);
					_stopped = true;
				}
			}

			private void enableToolBar(boolean b) {
				// deshabilitar todos los botones excepto el Stop
				loadEventsFile.setEnabled(b);
				changeCO2Class.setEnabled(b);
				changeRoadWeather.setEnabled(b);
				runButton.setEnabled(b);
				exitButton.setEnabled(b);
			}
		});

		// Boton Stop
		JButton stopButton = new JButton();
		stopButton.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("resources/icons/stop.png")));
		stopButton.setToolTipText("Stop");
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}

			private void stop() {
				_stopped = true;
			}
		});

		// Ticks Spinner
		JLabel ticksLabel = new JLabel("Ticks: ");
		JSpinner ticks = new JSpinner(new SpinnerNumberModel(10, 1, 999, 1));
		ticks.setMinimumSize(new Dimension(800, 40));
		ticks.setMaximumSize(new Dimension(800, 40));
		_ticks = (int) ticks.getValue();
		ticks.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				_ticks = (int) ticks.getValue();
			}
		});

		// Add the buttons in the toolBar
		toolBar.add(loadEventsFile);
		toolBar.addSeparator();
		toolBar.add(changeCO2Class);
		toolBar.add(changeRoadWeather);
		toolBar.addSeparator();
		toolBar.add(runButton);
		toolBar.add(stopButton);
		toolBar.add(ticksLabel);
		toolBar.add(ticks);
		toolBar.add(Box.createGlue());
		toolBar.add(exitButton);

		this.add(toolBar, BorderLayout.PAGE_START);
	}

	protected void ChangeCO2Class() {
		ChangeCO2ClassDialog dialog = new ChangeCO2ClassDialog((Frame) SwingUtilities.getWindowAncestor(this));
		List<Vehicle> v_aux = rMap.getVehicles();
		List<String> vehicles = new ArrayList<String>();

		for (Vehicle v : v_aux)
			vehicles.add(v.getId());

		int status = dialog.open(vehicles);

		if (status == 1) {
			List<Pair<String, Integer>> setContClass = new ArrayList<Pair<String, Integer>>();
			Pair<String, Integer> vc = new Pair<String, Integer>(dialog.getVehicle(), dialog.getCO2Class());
			setContClass.add(vc);
			try {
				int timeEvent = _time + dialog.getTicks();
				_ctrl.addEvent(new NewSetContClassEvent(timeEvent, setContClass));
			} catch (Exception ex) {
				showErrorDialog(ex.getMessage());
			}
		}
	}

	protected void ChangeRoadWeather() {
		ChangeWeatherDialog dialog = new ChangeWeatherDialog((Frame) SwingUtilities.getWindowAncestor(this));
		List<Road> r_aux = rMap.getRoads();
		List<String> roads = new ArrayList<String>();
		List<String> weathers = new ArrayList<String>();

		for (Road r : r_aux)
			roads.add(r.getId());

		for (Weather w : Weather.values())
			weathers.add(w.toString());

		int status = dialog.open(roads, weathers);

		if (status == 1) {
			List<Pair<String, Weather>> setWeather = new ArrayList<Pair<String, Weather>>();
			Weather weather = Weather.parse(dialog.getWeather());
			Pair<String, Weather> rw = new Pair<String, Weather>(dialog.getRoad(), weather);
			setWeather.add(rw);
			try {
				int timeEvent = _time + dialog.getTicks();
				_ctrl.addEvent(new SetWeatherEvent(timeEvent, setWeather));
			} catch (Exception ex) {
				showErrorDialog(ex.getMessage());
			}
		}
	}

	private void showErrorDialog(String err) {
		JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
	}
}