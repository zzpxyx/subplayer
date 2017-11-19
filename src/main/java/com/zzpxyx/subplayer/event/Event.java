package com.zzpxyx.subplayer.event;

public class Event implements Comparable<Event> {
	public enum Type {
		Start, End
	}

	public Type type;
	public long time;
	public String text;
	public long elapsedSubtitleTime; // Elapsed subtitle time since previous event. Not real world time.

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
}
