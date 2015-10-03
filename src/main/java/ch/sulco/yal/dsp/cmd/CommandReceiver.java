package ch.sulco.yal.dsp.cmd;

public interface CommandReceiver {
	void setCommandListener(CommandListener commandListener);

	public interface CommandListener {
		void onCommand(Command command);
	}
}
