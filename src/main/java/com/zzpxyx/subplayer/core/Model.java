package com.zzpxyx.subplayer.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import com.zzpxyx.subplayer.event.Event;

public class Model extends Observable {
	private List<Event> eventList = new LinkedList<>(); // List has a dummy head.
	private List<String> visibleSubtitleList = new LinkedList<>();
	private Timer scheduler = new Timer();
	private boolean isPlaying = false;
	private int currentEventIndex = 0;
	private long currentEventElapsedTime = 0;
	private long currentEventSystemTimestamp; // Auxiliary timestamp for time axis stabilization.
	private long offset = 0;

	public Model() {
		eventList.add(new Event(Event.Type.Dummy, 0, ""));
	}

	public synchronized void playOrPause() {
		if (isPlaying) {
			// We want to pause now.
			pause();
		} else {
			// We want to play now.
			play();
		}
	}

	public synchronized void play() {
		if (!isPlaying && currentEventIndex < eventList.size() - 1) {
			// Treat as if resuming. A new play equals to resuming from the start.

			// Save current state.
			currentEventSystemTimestamp = System.currentTimeMillis() - currentEventElapsedTime;

			// Schedule next event.
			scheduler = new Timer();
			scheduler.schedule(new EventHandler(), Math.max(getNextEventDelay() + offset - currentEventElapsedTime, 0));
		}
		isPlaying = true;
	}

	public synchronized void pause() {
		if (isPlaying) {
			// Cancel all scheduled events.
			scheduler.cancel();

			// Save current state.
			currentEventElapsedTime = System.currentTimeMillis() - currentEventSystemTimestamp;
		}
		isPlaying = false;
	}

	public synchronized void next() {
		int newStartEventIndex = currentEventIndex;

		while (newStartEventIndex < eventList.size() - 1) {
			newStartEventIndex++;
			if (eventList.get(newStartEventIndex).type == Event.Type.Start) {
				break;
			}
		}
		jumpToEvent(newStartEventIndex); // Now either a start event is found, or jump to the end of the event list.
	}

	public synchronized void previous() {
		int newStartEventIndex = currentEventIndex;

		while (newStartEventIndex > 0) {
			newStartEventIndex--;
			if (eventList.get(newStartEventIndex).type == Event.Type.Start) {
				break;
			}
		}
		jumpToEvent(newStartEventIndex); // Now either a start event is found, or jump to the start of the event list.
	}

	public synchronized void forward() {
		adjustOffset(-50);
	}

	public synchronized void backward() {
		adjustOffset(50);
	}

	public synchronized void stop() {
		// Pause the play.
		pause();

		// Jump back to the starting point.
		jumpToEvent(0);
	}

	public synchronized void setEventList(List<Event> list) {
		// Stop the play.
		stop();

		// Change the list.
		eventList = list;
	}

	private synchronized void jumpToEvent(int newEventIndex) {
		boolean wasPlaying = isPlaying;

		// Pause the play.
		pause();

		// Adjust start point.
		currentEventIndex = newEventIndex;
		currentEventElapsedTime = 0;
		offset = 0;

		// Update displaying subtitles.
		Event currentEvent = eventList.get(currentEventIndex);
		visibleSubtitleList.clear();
		if (currentEvent.type == Event.Type.Start) {
			// Only show subtitle if jumping to a start event.
			visibleSubtitleList.add(currentEvent.text);
		}
		setChanged();
		notifyObservers(visibleSubtitleList);

		// Resume playing if necessary.
		if (wasPlaying) {
			play();
		}
	}

	private synchronized void adjustOffset(long time) {
		boolean wasPlaying = isPlaying;

		// Pause the play.
		pause();

		// Adjust the offset.
		offset += time;

		// Resume playing if necessary.
		if (wasPlaying) {
			play();
		}
	}

	private synchronized long getNextEventDelay() {
		return eventList.get(currentEventIndex + 1).time - eventList.get(currentEventIndex).time;
	}

	/**
	 * Handler for events.
	 */
	private class EventHandler extends TimerTask {
		@Override
		public void run() {
			synchronized (Model.this) {
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
					scheduler.schedule(new EventHandler(), Math.max(getNextEventDelay() + offset, 0));
				}

				// Update displaying subtitles.
				switch (currentEvent.type) {
				case Dummy:
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
}
