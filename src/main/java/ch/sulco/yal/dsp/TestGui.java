package ch.sulco.yal.dsp;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.sulco.yal.dsp.audio.onboard.AudioSystemProvider;
import ch.sulco.yal.dsp.audio.onboard.LoopStore;
import ch.sulco.yal.dsp.audio.onboard.OnboardProcessor;
import ch.sulco.yal.dsp.audio.onboard.Player;
import ch.sulco.yal.dsp.audio.onboard.Recorder;

public class TestGui extends JPanel {
	private static final long serialVersionUID = 1L;

	private static JFrame frame;
	private OnboardProcessor controller;
	JComboBox<String> fileSelector;
	JPanel loopsPanel;

	public TestGui() {
		AppConfig appConfig = new AppConfig();
		Player player = new Player();
		LoopStore loopStore = new LoopStore(appConfig, new AudioSystemProvider());
		this.controller = new OnboardProcessor(player, loopStore, new Recorder(appConfig, player, loopStore));

		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;

		this.fileSelector = new JComboBox<String>(new File(TestGui.class.getClassLoader().getResource("sounds")
				.getFile()).list());
		this.add(this.fileSelector, constraints);

		JButton add = new JButton("add");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TestGui.this.loopsPanel.add(new LoopPanel((String) TestGui.this.fileSelector.getSelectedItem()));
				TestGui.this.loopsPanel.updateUI();
			}
		});
		constraints.gridx = 2;
		constraints.gridwidth = 1;
		this.add(add, constraints);

		this.loopsPanel = new JPanel();
		this.loopsPanel.setPreferredSize(new Dimension(500, 400));
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 3;
		this.add(this.loopsPanel, constraints);
	}

	public static void main(String[] args) {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		TestGui contentPane = new TestGui();

		contentPane.setOpaque(true);

		frame.setTitle("Basic GUI for Looper");
		frame.setContentPane(contentPane);

		frame.setSize(600, 500);
		frame.setVisible(true);
	}

	private class LoopPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public LoopPanel(String file) {
			file = "sounds/" + file;
			this.setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.weightx = 1.0;
			constraints.weighty = 1.0;
			constraints.gridy = 0;

			final int id = TestGui.this.controller.getLoopStore().addSample(file);
			JLabel label = new JLabel(file);
			constraints.gridx = 0;
			this.add(label, constraints);

			JButton start = new JButton("start");
			start.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					TestGui.this.controller.getPlayer().startSample(
							TestGui.this.controller.getLoopStore().getSample(id));
				}
			});
			constraints.gridx = 1;
			this.add(start, constraints);

			JButton stop = new JButton("stop");
			stop.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					TestGui.this.controller.getPlayer()
							.stopSample(TestGui.this.controller.getLoopStore().getSample(id));
				}
			});
			constraints.gridx = 2;
			this.add(stop, constraints);

			JButton remove = new JButton("remove");
			remove.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					TestGui.this.controller.getLoopStore().removeSample(id);
					TestGui.this.loopsPanel.remove(LoopPanel.this.getPanel());
					TestGui.this.loopsPanel.updateUI();
				}
			});
			constraints.gridx = 3;
			this.add(remove, constraints);
		}

		private JPanel getPanel() {
			return this;
		}
	}
}
