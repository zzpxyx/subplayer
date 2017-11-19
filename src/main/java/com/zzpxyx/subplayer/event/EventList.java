package com.zzpxyx.subplayer.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

public class EventList implements ListIterator<Event> {
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
		return list.get(nextIndex--);
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

	public void sort() {
		Collections.sort(list);
	}

	public Event peekNext() {
		return list.get(nextIndex);
	}

	public Event peekPrevious() {
		return list.get(nextIndex - 1);
	}
}
