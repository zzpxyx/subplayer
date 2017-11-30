package com.zzpxyx.subplayer.parser;

import java.util.ArrayList;

import com.zzpxyx.subplayer.event.Event;

public class AdaptvieParser {
	public static ArrayList<Event> getEventList(String fileName) {
		String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		switch (fileExtName) {
		case "srt":
			return SrtParser.getEventList(fileName);
		case "ssa":
		case "ass":
			return null;
		default:
			return null;
		}
	}
}
