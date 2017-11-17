package com.zzpxyx.subplayer.core;

public class Controller {
	private Model model;
	private View view;

	public Controller(Model model, View view) {
		this.model = model;
		this.view = view;
		this.model.addObserver(this.view);
	}

	public void PlayOrPause() {
		model.PlayOrPause();
	}

	public void Next() {
		model.Next();
	}
}
