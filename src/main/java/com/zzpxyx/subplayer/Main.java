package com.zzpxyx.subplayer;

import java.util.ArrayList;

import com.zzpxyx.subplayer.core.Controller;
import com.zzpxyx.subplayer.core.Model;
import com.zzpxyx.subplayer.core.Subtitle;
import com.zzpxyx.subplayer.core.View;

public class Main {
	public static void main(String[] args) {
		// List<Subtitle> subtitleList=SrtParser.getSubtitles("/tmp/a.srt");

		ArrayList<Subtitle> subtitleList = new ArrayList<Subtitle>();
		subtitleList.add(new Subtitle(0, 1000, "hello\nhello hello"));
		subtitleList.add(new Subtitle(2000, 3000, "world\nworld world"));

		Model model = new Model(subtitleList);
		View view = new View();
		Controller controller = new Controller(model, view);
		controller.Play();
	}
}
