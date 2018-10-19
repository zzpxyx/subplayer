package com.zzpxyx.subplayer.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import com.zzpxyx.subplayer.event.Event;
import com.zzpxyx.subplayer.event.Update;

public class Model extends Observable {
	private List<Event> eventList = new ArrayList<>(); // List has a dummy head.
	private Update update = new Update();
	private Timer scheduler = new Timer();
	private boolean isPlaying = false;
	private int currentEventIndex = 0;
	private long currentEventElapsedTime = 0;
	private long currentEventSystemTimestamp; // Auxiliary timestamp for time axis stabilization.
	private long offset = 0;
	private int speed = 100; // Percent.

	public Model() {
		eventList.add(new Event(Event.Type.Dummy, 0, ""));
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

	public synchronized void adjustOffset(long time) {
		pauseRunResume(() -> {
			offset += time;
		});
	}

	public synchronized void setSpeed(int newSpeed) {
		pauseRunResume(() -> {
			speed = newSpeed;
		});
	}

	public synchronized void jumpToTime(long eventTime) {
		int eventIndex = Collections.binarySearch(eventList, new Event(Event.Type.Dummy, eventTime, ""));
		if (eventIndex < 0) {
			// Exact event not found. An "insertion point" is returned.
			eventIndex = -eventIndex - 2; // Index of the previous event of the target time.
		}
		jumpToEvent(eventIndex);
	}

	private synchronized void jumpToEvent(int newEventIndex) {
		pauseRunResume(() -> {
			// Adjust start point.
			currentEventIndex = newEventIndex;
			currentEventElapsedTime = 0;
			offset = 0;

			// Update displaying subtitles.
			Event currentEvent = eventList.get(currentEventIndex);
			update.text.clear();
			if (currentEvent.type == Event.Type.Start) {
				// Only show subtitle if jumping to a start event.
				update.text.addAll(Arrays.asList(currentEvent.text.split(System.lineSeparator())));
			}
			update.time = currentEvent.time;
			setChanged();
			notifyObservers(update);
		});
	}

	private synchronized void pauseRunResume(Runnable runnable) {
		boolean wasPlaying = isPlaying;

		// Pause the play.
		pause();

		// Execute the runnable.
		runnable.run();

		// Resume playing if necessary.
		if (wasPlaying) {
			play();
		}
	}

	private synchronized long getNextEventDelay() {
		assert currentEventIndex < eventList.size();
		return Math.round(
				(eventList.get(currentEventIndex + 1).time - eventList.get(currentEventIndex).time) * 100d / speed);
	}

	/**
	 * Handler for events.
	 */
	private class EventHandler extends TimerTask {
		@Override
		public void run() {
			synchronized (Model.this) {
				// The "current" event is becoming the "previous" event.
				long previousEventSystemTimestamp = currentEventSystemTimestamp;
				currentEventSystemTimestamp = System.currentTimeMillis();
				long elapsedTimeRealWorld = currentEventSystemTimestamp - previousEventSystemTimestamp;
				long elapsedTimeScheduled = getNextEventDelay() + offset; // Maybe negative due to forward function.
				offset = elapsedTimeScheduled - elapsedTimeRealWorld; // Calculate the new offset.

				// Move to the next event and schedule it.
				currentEventIndex++;
				if (currentEventIndex < eventList.size() - 1) {
					scheduler.schedule(new EventHandler(), Math.max(getNextEventDelay() + offset, 0));
				}

				// Update displaying subtitles.
				Event currentEvent = eventList.get(currentEventIndex);
				switch (currentEvent.type) {
				case Dummy:
					break;
				case Start:
					update.text.addAll(Arrays.asList(currentEvent.text.split(System.lineSeparator())));
					break;
				case End:
					update.text.removeAll(Arrays.asList(currentEvent.text.split(System.lineSeparator())));
					break;
				}
				update.time = currentEvent.time;
				setChanged();
				notifyObservers(update);
			}
		}
	}
}
