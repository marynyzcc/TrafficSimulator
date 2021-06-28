package simulator.model;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum Weather {
	SUNNY, CLOUDY, RAINY, WINDY, STORM;

	public static Image getImage(Weather weather) throws IOException {
		switch (weather) {
		case SUNNY:
			return ImageIO.read(new File("resources/icons/sun.png"));
		case CLOUDY:
			return ImageIO.read(new File("resources/icons/cloud.png"));
		case RAINY:
			return ImageIO.read(new File("resources/icons/rain.png"));
		case WINDY:
			return ImageIO.read(new File("resources/icons/wind.png"));
		case STORM:
			return ImageIO.read(new File("resources/icons/storm.png"));
		default:
			return null;
		}
	}

	public static Weather parse(String string) {
		for (Weather weather : Weather.values())
			if (weather.name().equalsIgnoreCase(string))
				return weather;
		return null;
	}

}
