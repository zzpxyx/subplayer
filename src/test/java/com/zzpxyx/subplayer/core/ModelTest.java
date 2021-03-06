package com.zzpxyx.subplayer.core;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.zzpxyx.subplayer.event.Event;
import com.zzpxyx.subplayer.event.Update;
import com.zzpxyx.subplayer.parser.SrtParser;

class ModelTest implements Observer {
	private static final String RESOURCE_PATH = "src/test/resources/";
	private static final long TIMEOUT_MARGIN = 1000;
	private static final int SAMPLE_ROUNDS = 10;

	private List<Event> eventList;
	private List<Event> expectedList;
	private List<Event> actualList;
	private CountDownLatch latch;
	private Model model;
	private long startTimestamp;
	private static long eventTimeErrorRange;

	@BeforeAll
	static void initAll() throws InterruptedException {
		long timestamp = System.currentTimeMillis();
		for (int round = 0; round < SAMPLE_ROUNDS; round++) {
			Thread.yield();
			Thread.sleep(100);
		}
		eventTimeErrorRange = Math.abs(System.currentTimeMillis() - timestamp - 100 * SAMPLE_ROUNDS) + 5;
		System.out.println("Event time error range: " + eventTimeErrorRange);
	}

	@Test
	void testPlayPauseStop() {
		runTest("Test.srt", "PlayPauseStop.out", () -> {
			startTimestamp = System.currentTimeMillis(); // Too slow when putting this within runTest().
			try {
				model.play();
				Thread.sleep(50);
				model.play();
				Thread.sleep(100);
				model.pause();
				Thread.sleep(100);
				model.pause();
				Thread.sleep(100);
				model.play();
				Thread.sleep(100);
				model.stop();
				Thread.sleep(50);
				model.play();
				Thread.sleep(50);
				model.pause();
				Thread.sleep(50);
				model.play();
				latch.await();
			} catch (InterruptedException e) {
				// Test outcome is back or test went wrong. No need to do anything here.
			}
		});
	}

	@Test
	void testNextPrevious() {
		runTest("Test.srt", "NextPrevious.out", () -> {
			startTimestamp = System.currentTimeMillis();
			try {
				model.next();
				Thread.sleep(50);
				model.previous();
				Thread.sleep(50);
				model.play();
				Thread.sleep(50);
				model.next();
				Thread.sleep(150);
				model.previous();
				Thread.sleep(250);
				model.next();
				model.next();
				model.previous();
				latch.await();
			} catch (InterruptedException e) {
				// Test outcome is back or test went wrong. No need to do anything here.
			}
		});
	}

	@Test
	void testForwardBackward() {
		runTest("Test.srt", "ForwardBackward.out", () -> {
			startTimestamp = System.currentTimeMillis();
			try {
				model.adjustOffset(-50);
				Thread.sleep(50);
				model.adjustOffset(50);
				Thread.sleep(50);
				model.adjustOffset(-50);
				Thread.sleep(50);
				model.play();
				model.adjustOffset(-50);
				model.adjustOffset(-50);
				Thread.sleep(100);
				model.adjustOffset(100);
				latch.await();
			} catch (InterruptedException e) {
				// Test outcome is back or test went wrong. No need to do anything here.
			}
		});
	}

	@Test
	void testSetEventList() {
		runTest("Test.srt", "SetEventList.out", () -> {
			startTimestamp = System.currentTimeMillis();
			try {
				model.play();
				Thread.sleep(250);
				model.setEventList(eventList);
				model.play();
				latch.await();
			} catch (InterruptedException e) {
				// Test outcome is back or test went wrong. No need to do anything here.
			}
		});
	}

	@Test
	void testIncreaseDecreaseSpeed() {
		runTest("Test.srt", "IncreaseDecreaseSpeed.out", () -> {
			startTimestamp = System.currentTimeMillis();
			try {
				model.setSpeed(90);
				model.play();
				Thread.sleep(150);
				model.setSpeed(200);
				latch.await();
			} catch (InterruptedException e) {
				// Test outcome is back or test went wrong. No need to do anything here.
			}
		});
	}

	@Test
	void testJumpToTime() {
		runTest("Test.srt", "JumpToTime.out", () -> {
			startTimestamp = System.currentTimeMillis();
			try {
				model.jumpToTime(100);
				model.play();
				Thread.sleep(50);
				model.jumpToTime(150);
				latch.await();
			} catch (InterruptedException e) {
				// Test outcome is back or test went wrong. No need to do anything here.
			}
		});
	}

	@Override
	public void update(Observable model, Object arg) {
		if (arg instanceof Update) {
			long time = System.currentTimeMillis() - startTimestamp;
			String text = String.join("|", ((Update) arg).text);
			actualList.add(new Event(Event.Type.Dummy, time, text));
			latch.countDown();
		}
	}

	private void runTest(String dataFileName, String outputFileName, Executable test) {
		try {
			eventList = SrtParser.getEventList(RESOURCE_PATH + dataFileName, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		expectedList = parseOutputFile(RESOURCE_PATH + outputFileName);
		actualList = new LinkedList<>();
		latch = new CountDownLatch(expectedList.size());
		model = new Model();
		model.setEventList(eventList);
		model.addObserver(this);
		long timeout = expectedList.get(expectedList.size() - 1).time + TIMEOUT_MARGIN;
		assertTimeoutPreemptively(Duration.ofMillis(timeout), test);
		assertTrue(compareEventList(expectedList, actualList));
	}

	private List<Event> parseOutputFile(String fileName) {
		List<Event> textList = new LinkedList<>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			// Each line is time^text, where time is the time mark, not interval.
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
		if (list1 == null || list2 == null || list1.size() != list2.size()) {
			return false;
		}
		Iterator<Event> iterator1 = list1.iterator();
		Iterator<Event> iterator2 = list2.iterator();
		while (iterator1.hasNext()) {
			Event event1 = iterator1.next();
			Event event2 = iterator2.next();
			if (Math.abs(event1.time - event2.time) > eventTimeErrorRange || !event1.text.equals(event2.text)) {
				return false;
			}
		}
		return true;
	}
}
