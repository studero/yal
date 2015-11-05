package ch.sulco.yal.dsp.audio.onboard;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.LoopActivator;

@Singleton
public class Synchronizer {
	private final static Logger log = LoggerFactory.getLogger(Synchronizer.class);

	@Inject
	private DataStore dataStore;

	@Inject
	private LoopActivator loopActivator;

	private LinkedList<LoopListener> loopListeners = new LinkedList<>();
	private LinkedList<SyncAdjuster> syncAdjusters = new LinkedList<>();

	private long loopLength = 0;
	private boolean firstLoop;
	private ScheduledExecutorService synchronizeService = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> synchronizeTimer;

	public void setLength(long length) {
		loopLength = length;
		synchronizeTimer = null;
		firstLoop = true;
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
		triggerLoopStarted(firstLoop);
		firstLoop = false;
	}

	public void stopLoop() {
		log.info("stop loop");
		if (synchronizeTimer != null) {
			synchronizeTimer.cancel(false);
			synchronizeTimer = null;
			log.info("Synchronization loop ended");
		}
		for (LoopListener loopListener : loopListeners) {
			loopListener.loopStopped();
		}
	}

	private void synchronizeEvent() {
		log.info("Synchronization event");
		if (dataStore.getNextLoopId() != null && dataStore.getNextLoopId() != dataStore.getCurrentLoop().getId()) {
			loopActivator.setCurrentLoopId(dataStore.getNextLoopId());
		} else {
			Stopwatch stopwatch = Stopwatch.createStarted();
			int count = 0;
			long halfLoopLength = getLoopLength() / 2;
			SyncAdjustment loopSyncAdjustment = new SyncAdjustment(halfLoopLength, -halfLoopLength, 0L);
			for (SyncAdjuster syncAdjuster : syncAdjusters) {
				SyncAdjustment syncAdjustment = syncAdjuster.getSyncAdjustment();
				if (syncAdjustment != null) {
					count++;
					if (syncAdjustment.getLowestSamplePosition() < loopSyncAdjustment.getLowestSamplePosition()) {
						loopSyncAdjustment.setLowestSamplePosition(syncAdjustment.getLowestSamplePosition());
					}
					loopSyncAdjustment.setAverageSamplePosition(
							loopSyncAdjustment.getAverageSamplePosition() + syncAdjustment.getAverageSamplePosition());
					if (syncAdjustment.getHighestSamplePosition() > loopSyncAdjustment.getHighestSamplePosition()) {
						loopSyncAdjustment.setHighestSamplePosition(syncAdjustment.getHighestSamplePosition());
					}
				}
			}
			if (count > 0) {
				loopSyncAdjustment.setAverageSamplePosition(loopSyncAdjustment.getAverageSamplePosition() / count);
			}
			long calculateTime = stopwatch.elapsed(TimeUnit.MICROSECONDS);
			long newLenght = loopLength - loopSyncAdjustment.getAverageSamplePosition() / 2 - calculateTime - 1500;
			log.info("Synchronization position calculated in " + calculateTime + " [min=" + loopSyncAdjustment.getLowestSamplePosition()
					+ ", avg=" + loopSyncAdjustment.getAverageSamplePosition() + ", max="
					+ loopSyncAdjustment.getHighestSamplePosition() + "]");
			startTimer(newLenght);
		}
	}

	public void addLoopListerner(LoopListener loopListerer) {
		if (!this.loopListeners.contains(loopListerer)) {
			this.loopListeners.add(loopListerer);
			log.info("Synchronization listener added, now has " + loopListeners.size());
		}
	}

	public void addSyncAdjuster(SyncAdjuster syncAdjuster) {
		if (!this.syncAdjusters.contains(syncAdjuster)) {
			this.syncAdjusters.add(syncAdjuster);
			log.info("Synchronization adjuster added, now has " + syncAdjusters.size());
		}
	}

	public void startLoop() {
		log.info("start loop [length=" + loopLength + "]");
		this.checkLine();
	}

	private void triggerLoopStarted(boolean firstLoop) {
		for (LoopListener loopListener : loopListeners) {
			loopListener.loopStarted(firstLoop);
		}
	}

	public void removeLoopListerner(LoopListener loopListerer) {
		this.loopListeners.remove(loopListerer);
		log.info("Synchronization listener removed, now has " + loopListeners.size());
	}

	public void removeSyncAdjuster(SyncAdjuster syncAdjuster) {
		this.syncAdjusters.remove(syncAdjuster);
		log.info("Synchronization adjuster removed, now has " + syncAdjusters.size());
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
