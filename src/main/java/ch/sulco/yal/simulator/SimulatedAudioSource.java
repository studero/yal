package ch.sulco.yal.simulator;

import java.util.Arrays;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.AppConfig;
import ch.sulco.yal.dm.RecordingState;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.audio.AudioSource;

public class SimulatedAudioSource extends AudioSource {
	private final static Logger log = LoggerFactory.getLogger(SimulatedAudioSource.class);

	@Inject
	private AppConfig appConfig;

	private int dataSize;

	@Override
	protected long getSampleLength(Sample sample) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected Thread getRecordThread() {
		this.dataSize = (int) (SimulatedAudioSource.this.appConfig.getSampleRate()
				* SimulatedAudioSource.this.appConfig.getSampleSize() / 2 / 1000);
		log.info("dataSize " + this.dataSize);
		return new RecordThread();
	}

	private class RecordThread extends Thread {

		@Override
		public void run() {
			while (SimulatedAudioSource.this.getInputChannel().getRecordingState() == RecordingState.RECORDING) {
				try {
					byte[] buffer = new byte[SimulatedAudioSource.this.dataSize];
					Arrays.fill(buffer, (byte) 0x00);
					SimulatedAudioSource.this.newAudioData(buffer);
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
