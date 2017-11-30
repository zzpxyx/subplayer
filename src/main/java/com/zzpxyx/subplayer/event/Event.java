package com.zzpxyx.subplayer.event;

public class Event implements Comparable<Event> {
	public enum Type {
		Dummy, Start, End
	}

	public Type type;
	public long time;
	public String text;

	public Event() {
	}

	public Event(Type type, long time, String text) {
		this.type = type;
		this.time = time;
		this.text = text;
	}

	@Override
	public int compareTo(Event anotherEvent) {
		return Long.compare(this.time, anotherEvent.time);
	}

	@Override
	public String toString() {
		return time + " " + type + " " + text;
	}
}
