package com.zzpxyx.subplayer.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.zzpxyx.subplayer.core.Subtitle;

public class SrtParser {
	// States for the state machine.
	private enum WaitFor {
		SectionBegin, Time, Text, SectionEnd
	}

	public static List<Subtitle> getSubtitles(String fileName) {
		List<Subtitle> list = new LinkedList<Subtitle>();
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
			String line;
			String text = "";
			long startTime = 0;
			long endTime = 0;
			WaitFor waitingFor = WaitFor.SectionBegin;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				switch (waitingFor) {
				case SectionBegin:
					if (!line.isEmpty()) {
						waitingFor = WaitFor.Time;
					}
					break;
				case Time:
					if (!line.isEmpty()) {
						// The time line is like "00:00:01,234 --> 00:00:02,345".
						String[] times = line.split("-->");
						for (int i = 0; i < 2; i++) {
							times[i] = "PT" + times[i].trim();
							times[i] = times[i].replaceFirst(":", "H");
							times[i] = times[i].replaceFirst(":", "M");
							times[i] = times[i].replaceFirst(",", ".");
							times[i] += "S";
						}
						startTime = Duration.parse(times[0]).toMillis();
						endTime = Duration.parse(times[1]).toMillis();
						waitingFor = WaitFor.Text;
					} else {
						waitingFor = WaitFor.SectionBegin;
					}
					break;
				case Text:
					if (!line.isEmpty()) {
						text += line;
						waitingFor = WaitFor.SectionEnd;
					} else {
						waitingFor = WaitFor.SectionBegin;
					}
					break;
				case SectionEnd:
					if (!line.isEmpty()) {
						text += System.lineSeparator() + line;
					} else {
						list.add(new Subtitle(startTime, endTime, text));
						text = "";
						waitingFor = WaitFor.SectionBegin;
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.sort(list);
		return list;
	}
}
