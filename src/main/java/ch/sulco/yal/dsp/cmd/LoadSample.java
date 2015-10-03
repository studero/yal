package ch.sulco.yal.dsp.cmd;

public class LoadSample implements Command {
	private byte[] data;

	public byte[] getData() {
		return this.data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
