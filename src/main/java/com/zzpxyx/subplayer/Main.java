package com.zzpxyx.subplayer;

import com.zzpxyx.subplayer.config.Config;
import com.zzpxyx.subplayer.core.Controller;
import com.zzpxyx.subplayer.core.Model;
import com.zzpxyx.subplayer.core.View;

public class Main {
	public static void main(String[] args) {
		Model model = new Model();
		View view = new View(Config.loadConfig());
		new Controller(model, view);
	}
}
