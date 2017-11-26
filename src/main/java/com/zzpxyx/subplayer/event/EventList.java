package com.zzpxyx.subplayer.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

public class EventList implements ListIterator<Event>, Iterable<Event> {
	private ArrayList<Event> list = new ArrayList<>();
	private int nextIndex = 0;

	@Override
	public void add(Event event) {
		list.add(event);
	}

	@Override
	public boolean hasNext() {
		return nextIndex < list.size();
	}

	@Override
	public boolean hasPrevious() {
		return nextIndex > 0;
	}

	@Override
	public Event next() {
		return list.get(nextIndex++);
	}

	@Override
	public Event previous() {
		return list.get(--nextIndex);
	}

	@Override
	public int nextIndex() {
		return nextIndex;
	}

	@Override
	public int previousIndex() {
		return nextIndex - 1;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(Event event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Event> iterator() {
		return this;
	}

	public Event peekNext() {
		return list.get(nextIndex);
	}

	public Event peekPrevious() {
		return list.get(nextIndex - 1);
	}

	public void prepare() {
		Collections.sort(list);
		list.get(0).elapsedSubtitleTime = list.get(0).time;
		for (int i = 1; i < list.size(); i++) {
			list.get(i).elapsedSubtitleTime = list.get(i).time - list.get(i - 1).time;
		}
	}
}
