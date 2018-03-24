package com.zzpxyx.subplayer.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
	private static final String CONFIG_FILE_PATH = "config.properties";

	public static final String WINDOW_WIDTH = "WindowWidth";
	public static final String WINDOW_HEIGHT = "WindowHeight";
	public static final String WINDOW_X_POSITION = "WindowXPosition";
	public static final String WINDOW_Y_POSITION = "WindowYPosition";

	public static Properties loadConfig() {
		Properties config = new Properties();

		// Set up default values.
		config.setProperty(WINDOW_WIDTH, "1500");
		config.setProperty(WINDOW_HEIGHT, "200");
		config.setProperty(WINDOW_X_POSITION, "200");
		config.setProperty(WINDOW_Y_POSITION, "900");

		// Override default values with user-specified values.
		Path configPath = Paths.get(CONFIG_FILE_PATH);
		if (Files.exists(configPath)) {
			try (BufferedReader reader = Files.newBufferedReader(configPath)) {
				config.load(reader);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return config;
	}
}
