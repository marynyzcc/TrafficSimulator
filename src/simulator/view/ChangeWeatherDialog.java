package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class ChangeWeatherDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private int _status;
	private JComboBox<String> _roads;
	private DefaultComboBoxModel<String> _roadsModel;
	private JComboBox<String> _weathers;
	private DefaultComboBoxModel<String> _weathersModel;
	private JSpinner ticks;

	public ChangeWeatherDialog(Frame parent) {
		super(parent, true);
		initGUI();
	}

	private void initGUI() {

		_status = 0;

		setTitle("Change Road Weather");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		String text = "<html><p>Schedule an event to change the weather of a road after a given number</p><p>of simulation ticks from now.</p></html>";
		JLabel helpMsg = new JLabel(text);
		mainPanel.add(helpMsg);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		JPanel viewsPanel = new JPanel();
		viewsPanel.setAlignmentX(LEFT_ALIGNMENT);
		mainPanel.add(viewsPanel);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setAlignmentX(LEFT_ALIGNMENT);
		mainPanel.add(buttonsPanel);

		JLabel rLabel = new JLabel("Road: ");
		viewsPanel.add(rLabel);

		// Roads ComboBox
		_roadsModel = new DefaultComboBoxModel<>();
		_roads = new JComboBox<>(_roadsModel);
		_roads.setPreferredSize(new Dimension(80, 25));
		viewsPanel.add(_roads);

		// Weather ComboBox
		JLabel weather = new JLabel(" Weather: ");
		viewsPanel.add(weather);
		_weathersModel = new DefaultComboBoxModel<>();
		_weathers = new JComboBox<>(_weathersModel);
		_weathers.setPreferredSize(new Dimension(80, 25));
		viewsPanel.add(_weathers);

		// Ticks Spinner
		JLabel tLabel = new JLabel(" Ticks: ");
		viewsPanel.add(tLabel);
		ticks = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
		ticks.setPreferredSize(new Dimension(80, 25));
		viewsPanel.add(ticks);

		// Boton Cancel
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_status = 0;
				ChangeWeatherDialog.this.setVisible(false);
			}
		});
		buttonsPanel.add(cancelButton);

		// Boton OK
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_roadsModel.getSelectedItem() != null && _weathersModel.getSelectedItem() != null) {
					_status = 1;
					ChangeWeatherDialog.this.setVisible(false);
				}
			}
		});
		buttonsPanel.add(okButton);

		setPreferredSize(new Dimension(450, 180));
		pack();
		setResizable(false);
		setVisible(false);
	}

	public int open(List<String> roads, List<String> weathers) {

		_roadsModel.removeAllElements();
		for (String r : roads)
			_roadsModel.addElement(r);

		_weathersModel.removeAllElements();
		for (String w : weathers)
			_weathersModel.addElement(w);

		setLocationRelativeTo(getParent());

		setVisible(true);
		return this._status;
	}

	String getRoad() {
		return (String) _roadsModel.getSelectedItem();
	}

	String getWeather() {
		return (String) _weathersModel.getSelectedItem();
	}

	int getTicks() {
		return (Integer) ticks.getValue();
	}
}
