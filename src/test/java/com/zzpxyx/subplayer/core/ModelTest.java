package com.zzpxyx.subplayer.core;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.zzpxyx.subplayer.event.Event;
import com.zzpxyx.subplayer.parser.SrtParser;

class ModelTest implements Observer {
	private static final String RESOURCE_PATH = "src/test/resources/";
	private static final long ERROR_RANGE = 5;

	private CountDownLatch latch;
	private ArrayList<Event> actualList = new ArrayList<>();
	private long startTimestamp;

	@Test
	void testPlayPauseStop() {
		List<Event> eventList = SrtParser.getEventList(RESOURCE_PATH + "PlayPauseStop.srt");
		List<Event> expectedList = parseOutputFile(RESOURCE_PATH + "PlayPauseStop.out");
		actualList.clear();
		latch = new CountDownLatch(7);
		Model model = new Model(eventList);
		model.addObserver(this);
		startTimestamp = System.currentTimeMillis();
		try {
			model.play();
			Thread.sleep(50);
			model.play();
			Thread.sleep(100);
			model.pause();
			Thread.sleep(100);
			model.pause();
			Thread.sleep(100);
			model.playOrPause();
			Thread.sleep(100);
			model.stop();
			Thread.sleep(50);
			model.playOrPause();
			Thread.sleep(50);
			model.playOrPause();
			Thread.sleep(50);
			model.playOrPause();
			latch.await();
		} catch (InterruptedException e) {
			// Test outcome is back or test went wrong. No need to do anything here.
		}
		assertTrue(compareEventList(expectedList, actualList));
	}

	@Override
	public void update(Observable model, Object arg) {
		if (arg instanceof List<?>) {
			long time = System.currentTimeMillis() - startTimestamp;
			List<?> visibleSubtitleList = (List<?>) arg;
			String text = visibleSubtitleList.stream().map(o -> o.toString()).collect(Collectors.joining("|"));
			actualList.add(new Event(Event.Type.Dummy, time, text));
			latch.countDown();
		}
	}

	private List<Event> parseOutputFile(String fileName) {
		List<Event> textList = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			textList = stream.map(s -> parseEvent(s)).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return textList;
	}

	private Event parseEvent(String line) {
		String[] fields = line.split("\\^", -1);
		long time = Long.parseLong(fields[0]);
		String text = fields[1];
		return new Event(Event.Type.Dummy, time, text);
	}

	private boolean compareEventList(List<Event> list1, List<Event> list2) {
		if (list1.size() != list2.size()) {
			return false;
		}
		Iterator<Event> iterator1 = list1.iterator();
		Iterator<Event> iterator2 = list2.iterator();
		while (iterator1.hasNext()) {
			Event event1 = iterator1.next();
			Event event2 = iterator2.next();
			if (Math.abs(event1.time - event2.time) > ERROR_RANGE || !event1.text.equals(event2.text)) {
				return false;
			}
		}
		return true;
	}
}