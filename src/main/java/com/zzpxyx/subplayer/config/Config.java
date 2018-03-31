package com.zzpxyx.subplayer.config;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
	private static final String CONFIG_FILE_PATH = "config.properties";
	private static final int FONT_SIZE_VALUE = 40;
	private static final int WINDOW_HEIGHT_VALUE = 150;

	// Property names.
	public static final String WINDOW_WIDTH = "WindowWidth";
	public static final String WINDOW_HEIGHT = "WindowHeight";
	public static final String WINDOW_X_POSITION = "WindowXPosition";
	public static final String WINDOW_Y_POSITION = "WindowYPosition";
	public static final String FONT_SIZE = "FontSize";

	public static Properties loadConfig() {
		Properties config = new Properties();

		// Set up default values.
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		int windowWidth = screenWidth * 4 / 5;
		config.setProperty(WINDOW_WIDTH, String.valueOf(windowWidth));
		config.setProperty(WINDOW_HEIGHT, String.valueOf(WINDOW_HEIGHT_VALUE));
		config.setProperty(WINDOW_X_POSITION, String.valueOf(screenWidth / 10));
		config.setProperty(WINDOW_Y_POSITION, String.valueOf(screenHeight - WINDOW_HEIGHT_VALUE));
		config.setProperty(FONT_SIZE, String.valueOf(FONT_SIZE_VALUE));

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
