package com.zzpxyx.subplayer.core;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class Model extends Observable {
	private List<Subtitle> subtitleList;
	private ListIterator<Subtitle> subtitleListIterator;
	private long lastStartEventSystemTimestamp;
	private long lastSubtitleStartTime = 0;
	private long startTimeMarker = 0; // Start playing from here. The marker for the "playing cursor".
	private Timer scheduler = new Timer();
	private LinkedList<Subtitle> visibleSubtitleList = new LinkedList<>();
	private boolean isPlaying = false;

	public Model(List<Subtitle> subtitleList) {
		this.subtitleList = subtitleList;
		this.subtitleListIterator = subtitleList.listIterator();
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
		if (!isPlaying) {
			// Treat as if resuming. A new play equals to resuming from time marker 0.
			lastStartEventSystemTimestamp = System.currentTimeMillis() - (startTimeMarker - lastSubtitleStartTime);
			scheduler = new Timer();

			// Schedule the end events for the currently visible subtitles.
			visibleSubtitleList.forEach(s -> scheduler.schedule(new EndEventHandler(s), s.endTime - startTimeMarker));

			// Schedule the next start event.
			if (subtitleListIterator.hasNext()) {
				int nextSubtitleIndex = subtitleListIterator.nextIndex();
				Subtitle nextSubtitle = subtitleList.get(nextSubtitleIndex);
				long nextStartEventDelay = nextSubtitle.startTime - startTimeMarker;
				scheduler.schedule(new StartEventHandler(), nextStartEventDelay);
			}

			isPlaying = true;
		}
	}

	public void Pause() {
		if (isPlaying) {
			// Cancel all scheduled events.
			scheduler.cancel();

			// Save current state.
			startTimeMarker = System.currentTimeMillis() - lastStartEventSystemTimestamp + lastSubtitleStartTime;

			isPlaying = false;
		}
	}

	public void Next() {
		if (subtitleListIterator.hasNext()) {
			boolean wasPlaying = isPlaying;

			// Pause the play.
			Pause();

			// Adjust start point.
			Subtitle nextSubtitle = subtitleListIterator.next();
			lastSubtitleStartTime = nextSubtitle.startTime;
			startTimeMarker = nextSubtitle.startTime;

			// Display next subtitle.
			visibleSubtitleList.clear();
			visibleSubtitleList.add(nextSubtitle);
			setChanged();
			notifyObservers(visibleSubtitleList);

			// Resume playing if necessary.
			if (wasPlaying) {
				Play();
			}
		}
	}

	/**
	 * Handler for the start event.
	 * 
	 * The start event is fired when a subtitle becomes visible.
	 */
	private class StartEventHandler extends TimerTask {
		@Override
		public void run() {
			// At this point, the "last" subtitle is still displaying or has been erased.

			if (subtitleListIterator.hasNext()) {
				// Get a snapshot of current state.
				long currentStartEventSystemTimestamp = System.currentTimeMillis();
				Subtitle currentSubtitle = subtitleListIterator.next();

				// Display current subtitle.
				visibleSubtitleList.add(currentSubtitle);
				setChanged();
				notifyObservers(visibleSubtitleList);

				// Calculate duration and offset.
				long duration = currentSubtitle.endTime - currentSubtitle.startTime;
				long elapsedTimeRealWorld = currentStartEventSystemTimestamp - lastStartEventSystemTimestamp;
				long elapsedTimeSubtitle = currentSubtitle.startTime - lastSubtitleStartTime;
				long offset = elapsedTimeRealWorld - elapsedTimeSubtitle;

				// Schedule the end event.
				scheduler.schedule(new EndEventHandler(currentSubtitle), duration);

				// Schedule the next start event.
				if (subtitleListIterator.hasNext()) {
					int nextSubtitleIndex = subtitleListIterator.nextIndex();
					Subtitle nextSubtitle = subtitleList.get(nextSubtitleIndex);
					long nextStartEventDelay = Math.max(nextSubtitle.startTime - currentSubtitle.startTime + offset, 0);
					scheduler.schedule(new StartEventHandler(), nextStartEventDelay);
					lastStartEventSystemTimestamp = currentStartEventSystemTimestamp;
					lastSubtitleStartTime = currentSubtitle.startTime;
				}
			}

		}
	}

	/**
	 * Handler for the end event.
	 * 
	 * The end event is fired when a subtitle becomes invisible.
	 */
	private class EndEventHandler extends TimerTask {
		private Subtitle subtitleToErase;

		public EndEventHandler(Subtitle subtitleToErase) {
			super();
			this.subtitleToErase = subtitleToErase;
		}

		@Override
		public void run() {
			// At this point, there may be more than one subtitle displaying.

			// Erase the given subtitle.
			visibleSubtitleList.remove(subtitleToErase);
			setChanged();
			notifyObservers(visibleSubtitleList);
		}
	}
}
