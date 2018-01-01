package com.zzpxyx.subplayer.core;

import java.util.List;

import com.zzpxyx.subplayer.event.Event;

public class Controller {
	private Model model;
	private View view;

	public Controller(Model model, View view) {
		this.model = model;
		this.view = view;
		this.model.addObserver(this.view);
	}

	public void playOrPause(boolean isPlaying) {
		if (isPlaying) {
			model.play();
		} else {
			model.pause();
		}
	}

	public void next() {
		model.next();
	}

	public void previous() {
		model.previous();
	}

	public void stop() {
		model.stop();
	}

	public void setEventList(List<Event> eventList) {
		model.setEventList(eventList);
	}

	public void forward() {
		model.adjustOffset(-50);
	}

	public void backward() {
		model.adjustOffset(50);
	}

	public void increaseSpeed() {
		model.adjustSpeed(-0.02);
	}

	public void decreaseSpeed() {
		model.adjustSpeed(0.02);
	}
}
