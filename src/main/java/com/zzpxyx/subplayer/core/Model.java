package com.zzpxyx.subplayer.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import com.zzpxyx.subplayer.event.Event;

public class Model extends Observable {
	private ArrayList<Event> eventList; // List has a dummy head.
	private Timer scheduler;
	private LinkedList<String> visibleSubtitleList = new LinkedList<>();
	private boolean isPlaying = false;
	private int currentEventIndex = 0;
	private long currentEventElapsedTime = 0;
	private long currentEventSystemTimestamp; // Auxiliary timestamp for time axis stabilization.
	private long offset = 0;

	public Model(ArrayList<Event> list) {
		eventList = list;
	}

	public void playOrPause() {
		if (isPlaying) {
			// We want to pause now.
			pause();
		} else {
			// We want to play now.
			play();
		}
	}

	public void play() {
		if (!isPlaying && currentEventIndex < eventList.size() - 1) {
			// Treat as if resuming. A new play equals to resuming from the start.

			// Save current state.
			currentEventSystemTimestamp = System.currentTimeMillis() - currentEventElapsedTime;

			// Schedule next event.
			scheduler = new Timer();
			scheduler.schedule(new EventHandler(), Math.max(nextEventDelay() + offset - currentEventElapsedTime, 0));

			isPlaying = true;
		}

	}

	public void pause() {
		if (isPlaying) {
			// Cancel all scheduled events.
			scheduler.cancel();

			// Save current state.
			currentEventElapsedTime = System.currentTimeMillis() - currentEventSystemTimestamp;

			isPlaying = false;
		}
	}

	public void next() {
		if (currentEventIndex < eventList.size() - 1) {
			jumpToEvent(currentEventIndex + 1);
		}
	}

	public void previous() {
		if (currentEventIndex > 0) {
			jumpToEvent(currentEventIndex - 1);
		}
	}

	private void jumpToEvent(int newEventIndex) {
		boolean wasPlaying = isPlaying;

		// Pause the play.
		pause();

		// Adjust start point.
		currentEventIndex = newEventIndex;
		currentEventElapsedTime = 0;

		// Update displaying subtitles.
		Event currentEvent = eventList.get(currentEventIndex);
		switch (currentEvent.type) {
		case Dummy:
			visibleSubtitleList.clear();
			break;
		case Start:
			visibleSubtitleList.add(currentEvent.text);
			break;
		case End:
			visibleSubtitleList.remove(currentEvent.text);
			break;
		}
		setChanged();
		notifyObservers(visibleSubtitleList);

		// Resume playing if necessary.
		if (wasPlaying) {
			play();
		}
	}

	private long nextEventDelay() {
		return eventList.get(currentEventIndex + 1).time - eventList.get(currentEventIndex).time;
	}

	/**
	 * Handler for events.
	 */
	private class EventHandler extends TimerTask {
		@Override
		public void run() {
			// Save states for "current" event. All "current" event will become "previous".
			Event previousEvent = eventList.get(currentEventIndex);
			long previousEventSystemTimestamp = currentEventSystemTimestamp;

			// Move to the next event.
			currentEventIndex++;
			Event currentEvent = eventList.get(currentEventIndex);
			currentEventSystemTimestamp = System.currentTimeMillis();

			// Calculate offset.
			long elapsedTimeScheduled = currentEvent.time - previousEvent.time + offset;
			long elapsedTimeRealWorld = currentEventSystemTimestamp - previousEventSystemTimestamp;
			offset = elapsedTimeScheduled - elapsedTimeRealWorld;

			// Schedule next event.
			if (currentEventIndex < eventList.size() - 1) {
				scheduler.schedule(new EventHandler(), Math.max(nextEventDelay() + offset, 0));
			}

			// Update displaying subtitles.
			switch (currentEvent.type) {
			case Dummy:
				visibleSubtitleList.clear();
				break;
			case Start:
				visibleSubtitleList.add(currentEvent.text);
				break;
			case End:
				visibleSubtitleList.remove(currentEvent.text);
				break;
			}
			setChanged();
			notifyObservers(visibleSubtitleList);
		}
	}
}
