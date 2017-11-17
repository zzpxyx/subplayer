package com.zzpxyx.subplayer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

public class SubtitleList implements ListIterator<Subtitle> {
	private ArrayList<Subtitle> list = new ArrayList<Subtitle>();
	private int nextIndex = 0;

	@Override
	public void add(Subtitle subtitle) {
		list.add(subtitle);
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
	public Subtitle next() {
		return list.get(nextIndex++);
	}

	@Override
	public Subtitle previous() {
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
	public void set(Subtitle e) {
		throw new UnsupportedOperationException();
	}

	public void sort() {
		Collections.sort(list);
	}

	public Subtitle peekNext() {
		return list.get(nextIndex);
	}

	public Subtitle peekPrevious() {
		return list.get(nextIndex - 1);
	}
}
