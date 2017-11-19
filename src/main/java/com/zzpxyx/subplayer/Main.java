package com.zzpxyx.subplayer;

import com.zzpxyx.subplayer.core.Controller;
import com.zzpxyx.subplayer.core.Model;
import com.zzpxyx.subplayer.core.View;
import com.zzpxyx.subplayer.event.EventList;
import com.zzpxyx.subplayer.parser.SrtParser;

public class Main {
	public static void main(String[] args) {
		EventList eventList = SrtParser.getEventList("/tmp/a.srt");
		Model model = new Model(eventList);
		View view = new View();
		Controller controller = new Controller(model, view);
		view.addController(controller);
	}
}
