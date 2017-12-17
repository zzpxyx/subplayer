package com.zzpxyx.subplayer.core;

import com.zzpxyx.subplayer.parser.SrtParser;

public class Controller {
	private Model model;
	private View view;

	public Controller(Model model, View view) {
		this.model = model;
		this.view = view;
		this.model.addObserver(this.view);

		// TODO: Make this an item in settings.
		setSubtitleFile("/tmp/a.srt");
	}

	public void playOrPause() {
		model.playOrPause();
	}

	public void next() {
		model.next();
	}

	public void previous() {
		model.previous();
	}

	public void forward() {
		model.forward();
	}

	public void backward() {
		model.backward();
	}

	public void stop() {
		model.stop();
	}

	public void setSubtitleFile(String fileName) {
		model.setEventList(SrtParser.getEventList(fileName));
	}
}
