package com.zzpxyx.subplayer.core;

public class Subtitle implements Comparable<Subtitle> {
	public long startTime;
	public long endTime;
	public String text;

	public Subtitle() {
	}

	public Subtitle(long startTime, long endTime, String text) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.text = text;
	}

	@Override
	public int compareTo(Subtitle o) {
		if (this.startTime < o.startTime) {
			return -1;
		} else if (this.startTime > o.startTime) {
			return 1;
		}
		return 0;
	}
}
