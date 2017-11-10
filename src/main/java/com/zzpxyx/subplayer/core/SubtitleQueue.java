package com.zzpxyx.subplayer.core;

import java.util.ArrayList;

public class SubtitleQueue {
	private ArrayList<Subtitle> queue=new ArrayList<Subtitle>();
	private int nextIndex=0;
	
	public void add(Subtitle subtitle)
	{
		queue.add(subtitle);
	}
	
	public boolean hasNext()
	{
		return nextIndex<queue.size();
	}
	
	public boolean hasPrevious()
	{
		return nextIndex>0;
	}
	
	public Subtitle next()
	{
		if (hasNext())
		{
			return queue.get(nextIndex++);
		}
		return null;
	}
	
	public Subtitle previous()
	{
		if (hasPrevious())
		{
			return queue.get(nextIndex--);
		}
		return null;
	}
}
