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
		this.view.addController(this);
	}

	public void setEventList(List<Event> eventList) {
		model.setEventList(eventList);
	}

	public void playOrPause(boolean isPlaying) {
		if (isPlaying) {
			model.play();
		} else {
			model.pause();
		}
	}

	public void stop() {
		model.stop();
	}

	public void backward() {
		model.adjustOffset(50);
	}

	public void forward() {
		model.adjustOffset(-50);
	}

	public void previous() {
		model.previous();
	}

	public void next() {
		model.next();
	}

	public void setSpeed(int newSpeed) {
		model.setSpeed(newSpeed);
	}

	public void jumpToEvent(int newEventIndex) {
		model.jumpToEvent(newEventIndex);
	}

	public void jumpToTime(long eventTime) {
		model.jumpToTime(eventTime);
	}
}
