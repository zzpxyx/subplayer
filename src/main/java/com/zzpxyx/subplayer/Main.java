package com.zzpxyx.subplayer;

import com.zzpxyx.subplayer.core.Controller;
import com.zzpxyx.subplayer.core.Model;
import com.zzpxyx.subplayer.core.SubtitleList;
import com.zzpxyx.subplayer.core.View;
import com.zzpxyx.subplayer.parser.SrtParser;

public class Main {
	public static void main(String[] args) {
		SubtitleList subtitleList = SrtParser.getSubtitles("/tmp/a.srt");
		Model model = new Model(subtitleList);
		View view = new View();
		Controller controller = new Controller(model, view);
		view.addController(controller);
	}
}
