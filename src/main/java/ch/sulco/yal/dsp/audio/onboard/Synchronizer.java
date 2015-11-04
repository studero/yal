package ch.sulco.yal.dsp.audio.onboard;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

@Singleton
public class Synchronizer {

	private final static Logger log = LoggerFactory.getLogger(Synchronizer.class);

	private LinkedList<LoopListener> loopListeners = new LinkedList<LoopListener>();
	private long loopLength = 0;
	private ScheduledExecutorService synchronizeService = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> synchronizeTimer;

	public void setLength(long length) {
		loopLength = length;
		log.info("length set to " + loopLength);
	}

	private void checkLine() {
		if (synchronizeTimer == null) {
			synchronizeEvent();
			log.info("Synchronization loop started");
		}
		log.info("Synchronization loop event set up " + synchronizeTimer.getDelay(TimeUnit.MICROSECONDS));
	}

	private void startTimer(long time) {
		log.info("Start timer with " + time + "/" + loopLength);
		synchronizeTimer = synchronizeService.schedule(new Runnable() {
			public void run() {
				synchronizeEvent();
			}
		}, time, TimeUnit.MICROSECONDS);
	}

	private void synchronizeEvent() {
		log.info("Synchronization event");
		if (loopListeners.isEmpty()) {
			synchronizeTimer.cancel(false);
			synchronizeTimer = null;
			log.info("Synchronization loop ended");
		} else {
			Stopwatch stopwatch = Stopwatch.createStarted();
			int count = 0;
			long halfLoopLength = getLoopLength() / 2;
			long position[] = { halfLoopLength, 0, -halfLoopLength };
			for (LoopListener loopListener : loopListeners) {
				long loopPosition[] = loopListener.loopStarted(false);
				if (loopPosition != null) {
					count++;
					if (loopPosition[0] < position[0]) {
						position[0] = loopPosition[0];
					}
					position[1] += loopPosition[1];
					if (loopPosition[2] > position[2]) {
						position[2] = loopPosition[2];
					}
				}
			}
			if (count > 0) {
				position[1] /= count;
			}
			long calculateTime = stopwatch.elapsed(TimeUnit.MICROSECONDS);
			long newLenght = loopLength - position[1] / 2 - calculateTime - 1500;
			log.info("Synchronization position calculated in " + calculateTime + " [min=" + position[0] + ", avg=" + position[1] + ", max="
					+ position[2] + "]");
			startTimer(newLenght);
		}
	}

	public void addLoopListerner(LoopListener loopListerer) {
		if (!this.loopListeners.contains(loopListerer)) {
			this.loopListeners.add(loopListerer);
			log.info("Synchronization listener added, now has " + loopListeners.size());
		}
		if (loopLength == 0) {
			loopListerer.loopStarted(true);
		} else {
			this.checkLine();
		}
	}

	public void removeLoopListerner(LoopListener loopListerer) {
		this.loopListeners.remove(loopListerer);
		log.info("Synchronization listener removed, now has " + loopListeners.size());
	}

	public long getCurrentPosition() {
		if (synchronizeTimer != null) {
			return loopLength - synchronizeTimer.getDelay(TimeUnit.MICROSECONDS);
		}
		return 0;
	}

	public long getLoopLength() {
		return loopLength;
	}
}
