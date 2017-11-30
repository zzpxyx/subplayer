package com.zzpxyx.subplayer.core;

public class Controller {
	private Model model;
	private View view;

	public Controller(Model model, View view) {
		this.model = model;
		this.view = view;
		this.model.addObserver(this.view);
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
}
