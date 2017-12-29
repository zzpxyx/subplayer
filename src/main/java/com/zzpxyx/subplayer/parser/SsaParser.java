package com.zzpxyx.subplayer.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.zzpxyx.subplayer.event.Event;

public class SsaParser {
	public static List<Event> getEventList(String fileName) {
		ArrayList<Event> list = new ArrayList<>();
		list.add(new Event(Event.Type.Dummy, 0, "")); // Add a dummy head.
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			stream.filter(line -> line.startsWith("Dialogue:")).forEach(line -> {
				String[] fields = line.split(",", 10);
				String text = fields[9].trim();
				text = text.replaceAll("\\{.*?\\\\p\\d+?.*?\\}.*?\\{.*?\\\\p\\d+?.*?\\}", ""); // Remove draw commands.
				text = text.replaceAll("\\{.*?\\}", ""); // Remove styles.
				text = text.replaceAll("\\\\N|\\\\n", System.lineSeparator());
				if (!text.isEmpty()) {
					for (int i = 1; i < 3; i++) {
						fields[i] = "PT" + fields[i].trim();
						fields[i] = fields[i].replaceFirst(":", "H");
						fields[i] = fields[i].replaceFirst(":", "M");
						fields[i] += "S";
					}
					long startTime = Duration.parse(fields[1]).toMillis();
					long endTime = Duration.parse(fields[2]).toMillis();
					list.add(new Event(Event.Type.Start, startTime, text));
					list.add(new Event(Event.Type.End, endTime, text));
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.sort(list);
		return list;
	}
}
