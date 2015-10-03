package ch.sulco.yal.dsp.audio;

import java.util.Set;

/**
 * provides method for audio processing.
 */
public interface Processor {
	/**
	 * @return an array of sample ids.
	 */
	Set<Integer> getSampleIds();

	/**
	 * @return an array of channel ids.
	 */
	Set<Integer> getChannelIds();

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
	int putData(byte[] data);

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
	void setChannelRecording(int channelId, boolean recording);
	
	RecordingState getChannelRecordingState(int channelId);

	/**
	 * set provided sample id to provided mute state.
	 * 
	 * @param sampleId
	 *            the id of the sample.
	 * @param mute
	 *            true if the sample should be muted.
	 */
	void setSampleMute(int sampleId, boolean mute);
	
	boolean isSampleMute(int sampleId);

	/**
	 * set provided sample id to provided volume.
	 * 
	 * @param sampleId
	 *            the id of the sample.
	 * @param volume
	 *            the volume the sample should be set to.
	 */
	void setSampleVolume(int sampleId, float volume);
	
	float getSampleVolume(int sampleId);

	/**
	 * remove sample identified by provided sample id.
	 * 
	 * @param sampleId
	 *            the id of the sample.
	 */
	void removeSample(int sampleId);
	
	Long getLoopLength();
}
