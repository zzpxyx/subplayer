package com.zzpxyx.subplayer.core;

import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class Model extends Observable {
	private List<Subtitle> subtitleList;
	private ListIterator<Subtitle> subtitleListIterator;
	private long lastStartEventSystemTimestamp;
	private Subtitle lastSubtitle;
	private long startTimeMarker = 0;
	private Timer scheduler = new Timer();

	public Model(List<Subtitle> subtitleList) {
		this.subtitleList = subtitleList;
		this.subtitleListIterator = subtitleList.listIterator();
	}

	public void Play() {
		lastStartEventSystemTimestamp=System.currentTimeMillis();
		lastSubtitle=new Subtitle(0, 0, "");
		scheduler.schedule(new StartEventHandler(), 0);
	}

	/**
	 * Handler for the start event.
	 * 
	 * The start event is fired when a subtitle becomes visible.
	 */
	private class StartEventHandler extends TimerTask {
		@Override
		public void run() {
			// Up to this point, the "current" subtitle is still displaying or has been
			// erased.

			if (subtitleListIterator.hasNext()) {
				// Get a snapshot of current state.
				long currentStartEventSystemTimestamp = System.currentTimeMillis();
				Subtitle currentSubtitle = subtitleListIterator.next();

				// Display current subtitle.
				System.out.println(currentSubtitle.text);

				// Calculate offset.
				long elapsedTimeRealWorld = currentStartEventSystemTimestamp - lastStartEventSystemTimestamp;
				long elapsedTimeSubtitle = currentSubtitle.startTime - lastSubtitle.startTime;
				long offset = elapsedTimeRealWorld - elapsedTimeSubtitle;

				// Schedule next start event.
				if (subtitleListIterator.hasNext()) {
					int nextSubtitleIndex = subtitleListIterator.nextIndex();
					Subtitle nextSubtitle = subtitleList.get(nextSubtitleIndex);
					long nextStartEventDelay = Math.max(nextSubtitle.startTime - currentSubtitle.startTime + offset, 0);
					scheduler.schedule(new StartEventHandler(), nextStartEventDelay);
					lastStartEventSystemTimestamp=currentStartEventSystemTimestamp;
					lastSubtitle=currentSubtitle;
				}
			}

		}
	}
}
