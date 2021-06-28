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

public class ChangeCO2ClassDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private int _status;
	private JComboBox<String> _vehicles;
	private DefaultComboBoxModel<String> _vehiclesModel;
	private JComboBox<Integer> _co2Class;
	private DefaultComboBoxModel<Integer> _co2ClassModel;
	private JSpinner ticks;

	public ChangeCO2ClassDialog(Frame parent) {
		super(parent, true);
		initGUI();
	}

	private void initGUI() {

		_status = 0;

		setTitle("Change CO2 Class");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		String text = "<html><p>Schedule an event to change the CO2 class of a vehicle after a given number</p><p>of simulation ticks frow now.</p></html>";
		JLabel helpMsg = new JLabel(text);
		mainPanel.add(helpMsg);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		JPanel viewsPanel = new JPanel();
		viewsPanel.setAlignmentX(LEFT_ALIGNMENT);
		mainPanel.add(viewsPanel);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setAlignmentX(LEFT_ALIGNMENT);
		mainPanel.add(buttonsPanel);

		JLabel vLabel = new JLabel("Vehicle: ");
		viewsPanel.add(vLabel);

		// Vehicles ComboBox
		_vehiclesModel = new DefaultComboBoxModel<>();
		_vehicles = new JComboBox<>(_vehiclesModel);
		_vehicles.setPreferredSize(new Dimension(80, 25));
		viewsPanel.add(_vehicles);

		// CO2Class ComboBox
		JLabel co2ClassLabel = new JLabel(" CO2 Class: ");
		viewsPanel.add(co2ClassLabel);
		_co2ClassModel = new DefaultComboBoxModel<>();
		for (int i = 0; i < 11; i++)
			_co2ClassModel.addElement(i);
		_co2Class = new JComboBox<>(_co2ClassModel);
		_co2Class.setPreferredSize(new Dimension(80, 25));
		viewsPanel.add(_co2Class);

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
				ChangeCO2ClassDialog.this.setVisible(false);
			}
		});
		buttonsPanel.add(cancelButton);

		// Boton OK
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_vehiclesModel.getSelectedItem() != null && _co2ClassModel.getSelectedItem() != null) {
					_status = 1;
					ChangeCO2ClassDialog.this.setVisible(false);
				}
			}
		});
		buttonsPanel.add(okButton);

		setPreferredSize(new Dimension(450, 180));
		pack();
		setResizable(false);
		setVisible(false);
	}

	public int open(List<String> vehicles) {

		_vehiclesModel.removeAllElements();
		for (String v : vehicles)
			_vehiclesModel.addElement(v);

		setLocationRelativeTo(getParent());

		setVisible(true);
		return this._status;
	}

	String getVehicle() {
		return (String) _vehiclesModel.getSelectedItem();
	}

	int getCO2Class() {
		return (Integer) _co2Class.getSelectedItem();
	}

	int getTicks() {
		return (Integer) ticks.getValue();
	}
}
