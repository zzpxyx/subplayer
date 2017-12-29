package com.zzpxyx.subplayer.parser;

import java.util.ArrayList;
import java.util.List;

import com.zzpxyx.subplayer.event.Event;

public class AdaptvieParser {
	public static List<Event> getEventList(String fileName) {
		String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		switch (fileExtName) {
		case "srt":
			return SrtParser.getEventList(fileName);
		case "ssa":
		case "ass":
			return SsaParser.getEventList(fileName);
		default:
			ArrayList<Event> list = new ArrayList<>();
			list.add(new Event(Event.Type.Dummy, 0, "")); // Add a dummy head.
			return list;
		}
	}
}
