package com.zzpxyx.subplayer.core;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import com.zzpxyx.subplayer.event.Event;
import com.zzpxyx.subplayer.event.EventList;

public class Model extends Observable {
	private EventList eventList;
	private long previousEventSystemTimestamp; // Auxiliary timestamp for time axis stabilization.
	private long startTimeMarker = 0; // Start playing from here. The marker for the "playing cursor".
	private Timer scheduler;
	private LinkedList<String> visibleSubtitleList = new LinkedList<>();
	private boolean isPlaying = false;

	public Model(EventList list) {
		eventList = list;
	}

	public void PlayOrPause() {
		if (isPlaying) {
			// We want to pause now.
			Pause();
		} else {
			// We want to play now.
			Play();
		}
	}

	public void Play() {
		if (!isPlaying && eventList.hasNext()) {
			// Treat as if resuming. A new play equals to resuming from time marker 0.
			Event previousEvent = eventList.hasPrevious() ? eventList.peekPrevious() : new Event(Event.Type.End, 0, "");
			Event nextEvent = eventList.peekNext();
			previousEventSystemTimestamp = System.currentTimeMillis() - (startTimeMarker - previousEvent.time);

			// Schedule next event.
			scheduler = new Timer();
			long nextEventDelay = nextEvent.time - startTimeMarker;
			System.out.println(eventList.nextIndex() + " " + nextEvent.time + " " + startTimeMarker);
			scheduler.schedule(new EventHandler(), nextEventDelay);

			isPlaying = true;
		}
	}

	public void Pause() {
		if (isPlaying) {
			// Cancel all scheduled events.
			scheduler.cancel();

			// Save current state.
			Event previousEvent = eventList.hasPrevious() ? eventList.peekPrevious() : new Event(Event.Type.End, 0, "");
			startTimeMarker = System.currentTimeMillis() - previousEventSystemTimestamp + previousEvent.time;

			isPlaying = false;
		}
	}

	public void Next() {
		if (eventList.hasNext()) {
			Event nextEvent = eventList.next();
			JumpToEvent(nextEvent);
		}
	}

	public void Previous() {
		if (eventList.hasPrevious()) {
			Event previousEvent = eventList.previous();
			JumpToEvent(previousEvent);
		}
	}

	private void JumpToEvent(Event targetEvent) {
		boolean wasPlaying = isPlaying;

		// Pause the play.
		Pause();

		// Adjust start point.
		startTimeMarker = targetEvent.time;

		// Update displaying subtitles.
		switch (targetEvent.type) {
		case Start:
			if (!visibleSubtitleList.contains(targetEvent.text)) {
				visibleSubtitleList.add(targetEvent.text);
			}
			break;
		case End:
			visibleSubtitleList.remove(targetEvent.text);
			break;
		}
		setChanged();
		notifyObservers(visibleSubtitleList);

		// Resume playing if necessary.
		if (wasPlaying) {
			Play();
		}
	}

	/**
	 * Handler for events.
	 */
	private class EventHandler extends TimerTask {
		@Override
		public void run() {
			// Get a snapshot of current state.
			Event previousEvent = eventList.hasPrevious() ? eventList.peekPrevious() : new Event(Event.Type.End, 0, "");
			Event currentEvent = eventList.next(); // Note that the cursor in the list moves to next.

			// Calculate offset.
			long currentEventSystemTimestamp = System.currentTimeMillis();
			long elapsedTimeRealWorld = currentEventSystemTimestamp - previousEventSystemTimestamp;
			long elapsedTimeSubtitle = currentEvent.time - previousEvent.time;
			long offset = elapsedTimeRealWorld - elapsedTimeSubtitle;

			// Schedule next event.
			if (eventList.hasNext()) {
				Event nextEvent = eventList.peekNext();
				long nextEventDelay = Math.max(nextEvent.time - currentEvent.time + offset, 0);
				scheduler.schedule(new EventHandler(), nextEventDelay);
				previousEventSystemTimestamp = currentEventSystemTimestamp;
			}

			// Update displaying subtitles.
			switch (currentEvent.type) {
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
