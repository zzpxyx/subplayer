package com.zzpxyx.subplayer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

public class SubtitleList implements ListIterator<Subtitle>{
	private ArrayList<Subtitle> list=new ArrayList<Subtitle>();
	private int nextIndex=0;
	
	@Override
	public void add(Subtitle subtitle)
	{
		list.add(subtitle);
	}
	
	public void sort()
	{
		Collections.sort(list);
	}
	
	@Override
	public boolean hasNext()
	{
		return nextIndex<list.size();
	}
	
	@Override
	public boolean hasPrevious()
	{
		return nextIndex>0;
	}
	
	@Override
	public Subtitle next()
	{
		if (hasNext())
		{
			return list.get(nextIndex++);
		}
		return null;
	}
	
	@Override
	public Subtitle previous()
	{
		if (hasPrevious())
		{
			return list.get(nextIndex--);
		}
		return null;
	}

	@Override
	public int nextIndex() {
		return nextIndex;
	}

	@Override
	public int previousIndex() {
		return nextIndex-1;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(Subtitle e) {
		throw new UnsupportedOperationException();
	}
}
