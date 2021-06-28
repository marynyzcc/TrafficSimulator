package simulator.launcher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import simulator.control.Controller;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.Factory;
import simulator.factories.MostCrowdedStrategyBuilder;
import simulator.factories.MoveAllStrategyBuilder;
import simulator.factories.MoveFirstStrategyBuilder;
import simulator.factories.NewCityRoadEventBuilder;
import simulator.factories.NewInterCityRoadEventBuilder;
import simulator.factories.NewJunctionEventBuilder;
import simulator.factories.NewVehicleEventBuilder;
import simulator.factories.RoundRobinStrategyBuilder;
import simulator.factories.SetContClassEventBuilder;
import simulator.factories.SetWeatherEventBuilder;
import simulator.model.DequeuingStrategy;
import simulator.model.Event;
import simulator.model.LightSwitchingStrategy;
import simulator.model.TrafficSimulator;
import simulator.view.MainWindow;

public class Main {

	private final static Integer _timeLimitDefaultValue = 10;
	private static String _inFile = null;
	private static String _outFile = null;
	private static Factory<Event> _eventsFactory = null;
	private static int _timeLimitValue;
	private static boolean GUIMode = false;

	private static void parseArgs(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseHelpOption(line, cmdLineOptions);
			parseModeOption(line);
			parseInFileOption(line);
			parseOutFileOption(line);
			parseSetTicksOption(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static void parseModeOption(CommandLine line) throws ParseException {
		if (line.hasOption("m")) {
			String view = line.getOptionValue("m");
			if (view.equalsIgnoreCase("CONSOLE"))
				GUIMode = false;
			else if (view.equalsIgnoreCase("GUI"))
				GUIMode = true;
			else
				throw new ParseException("Missing argument for option m");
		} else
			GUIMode = true;

	}

	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Events input file").build());
		cmdLineOptions.addOption(
				Option.builder("o").longOpt("output").hasArg().desc("Output file, where reports are written.").build());
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message").build());
		cmdLineOptions.addOption(Option.builder("t").longOpt("ticks").hasArg()
				.desc("Ticks to the simulator's main loop (defualt value is 10)").build());
		cmdLineOptions.addOption(
				Option.builder("m").longOpt("mode").hasArg().desc("View to the simulator (default is GUI)").build());
		return cmdLineOptions;
	}

	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parseInFileOption(CommandLine line) throws ParseException {
		if (GUIMode == false) {
			_inFile = line.getOptionValue("i");

			if (_inFile == null)
				throw new ParseException("An events file is missing");
		} else {
			if (line.hasOption("i")) {
				_inFile = line.getOptionValue("i");

				if (_inFile == null)
					throw new ParseException("An events file is missing");
			}
		}
	}

	private static void parseOutFileOption(CommandLine line) throws ParseException {
		if (GUIMode == false)
			_outFile = line.getOptionValue("o");
	}

	private static void parseSetTicksOption(CommandLine line) throws ParseException {
		if (GUIMode == false) {
			String time = line.getOptionValue("t");
			if (time == null)
				_timeLimitValue = _timeLimitDefaultValue;
			else
				_timeLimitValue = Integer.parseInt(time);
		}
	}

	private static void initFactories() {
		// Factoria de estrategias de semaforo:
		ArrayList<Builder<LightSwitchingStrategy>> lsbs = new ArrayList<>();
		lsbs.add(new RoundRobinStrategyBuilder());
		lsbs.add(new MostCrowdedStrategyBuilder());
		Factory<LightSwitchingStrategy> lssFactory = new BuilderBasedFactory<>(lsbs);
		// Fatorias de estrategia de extraccion de cola:
		ArrayList<Builder<DequeuingStrategy>> dqbs = new ArrayList<>();
		dqbs.add(new MoveFirstStrategyBuilder());
		dqbs.add(new MoveAllStrategyBuilder());
		Factory<DequeuingStrategy> dqsFactory = new BuilderBasedFactory<>(dqbs);
		// Factorias de eventos:
		ArrayList<Builder<Event>> ebs = new ArrayList<>();
		ebs.add(new NewJunctionEventBuilder(lssFactory, dqsFactory));
		ebs.add(new NewCityRoadEventBuilder());
		ebs.add(new NewInterCityRoadEventBuilder());
		ebs.add(new NewVehicleEventBuilder());
		ebs.add(new SetWeatherEventBuilder());
		ebs.add(new SetContClassEventBuilder());
		Factory<Event> eventsFactory = new BuilderBasedFactory<>(ebs);

		_eventsFactory = eventsFactory;
	}

	private static void startBatchMode() throws IOException, Exception {
		TrafficSimulator sim = new TrafficSimulator();
		Controller control = new Controller(sim, _eventsFactory);
		InputStream in = new FileInputStream(_inFile);
		control.loadEvents(in);

		OutputStream out;
		if (_outFile == null)
			out = System.out;
		else
			out = new FileOutputStream(_outFile);

		control.run(_timeLimitValue, out);
	}

	private static void startGUIMode() throws IOException, Exception {
		TrafficSimulator sim = new TrafficSimulator();
		Controller control = new Controller(sim, _eventsFactory);
		if (_inFile != null) {
			InputStream in = new FileInputStream(_inFile);
			control.loadEvents(in);
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainWindow(control);
			}
		});
	}

	private static void start(String[] args) throws IOException, Exception {
		initFactories();
		parseArgs(args);
		if (GUIMode == true)
			startGUIMode();
		else
			startBatchMode();
	}

	// example command lines:
	//
	// -m gui -i resources/examples/ex1.json
	// -m console -i resources/examples/ex1.json
	// -m console -i resources/examples/ex1.json -t 300
	// -m console -i resources/examples/ex1.json -o resources/tmp/ex1.out.json
	// --help

	public static void main(String[] args) {
		try {
			start(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
