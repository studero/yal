package ch.sulco.yal.dsp.audio;

import java.util.Set;

import ch.sulco.yal.dm.RecordingState;

/**
 * provides method for audio processing.
 */
public interface Processor {
	/**
	 * @return an array of sample ids.
	 */
	Set<Long> getSampleIds();

	/**
	 * @param sampleId
	 *            the id of the sample.
	 * @return sample data as byte array for provided sample id.
	 */
	byte[] getData(int sampleId);

	/**
	 * @param data
	 *            the sample data as byte array to be loaded.
	 * @return the newly created sample id.
	 */
	Long putData(byte[] data);

	/**
	 * start playing current audio setup.
	 */
	void play();

	/**
	 * pause current audio setup.
	 */
	void pause();

	/**
	 * create loop.
	 */
	void loop();

	/**
	 * set provided channel id to provided recording state.
	 * 
	 * @param channelId
	 *            the id of the channel.
	 * @param recording
	 *            true if channel should be recording.
	 */
	void setChannelRecording(Long channelId, boolean recording);

	RecordingState getChannelRecordingState(Long channelId);

	/**
	 * set provided sample id to provided mute state.
	 * 
	 * @param sampleId
	 *            the id of the sample.
	 * @param mute
	 *            true if the sample should be muted.
	 */
	void setSampleMute(Long sampleId, boolean mute);

	boolean isSampleMute(Long sampleId);

	/**
	 * set provided sample id to provided volume.
	 * 
	 * @param sampleId
	 *            the id of the sample.
	 * @param volume
	 *            the volume the sample should be set to.
	 */
	void setSampleVolume(Long sampleId, float volume);

	float getSampleVolume(Long sampleId);

	/**
	 * remove sample identified by provided sample id.
	 * 
	 * @param sampleId
	 *            the id of the sample.
	 */
	void removeSample(Long sampleId);

	Long getLoopLength();

	boolean getChannelMonitoring(Long channelId);

	void setChannelMonitoring(Long channelId, boolean monitoring);
}
