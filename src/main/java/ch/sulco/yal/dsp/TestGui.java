package ch.sulco.yal.dsp;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.Application;
import ch.sulco.yal.PostConstructModule;
import ch.sulco.yal.YalModule;
import ch.sulco.yal.dsp.audio.onboard.AudioSystemProvider;
import ch.sulco.yal.dsp.audio.onboard.OnboardProcessor;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class TestGui extends JPanel {

	private final static Logger log = LoggerFactory.getLogger(TestGui.class);

	private static final long serialVersionUID = 1L;

	private static JFrame frame;

	@Inject
	private Application application;

	@Inject
	private OnboardProcessor controller;

	@Inject
	private AudioSystemProvider audioSystemProvider;

	JComboBox<String> fileSelector;
	JPanel loopsPanel;

	public TestGui() {

		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;

		this.fileSelector = new JComboBox<String>(new File(TestGui.class.getClassLoader().getResource("sounds").getFile()).list());
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

		Injector injector = Guice.createInjector(new YalModule(), new PostConstructModule());

		TestGui contentPane = injector.getInstance(TestGui.class);

		contentPane.setOpaque(true);

		frame.setTitle("Basic GUI for Looper");
		frame.setContentPane(contentPane);

		frame.setSize(600, 500);
		frame.setVisible(true);

		contentPane.application.start();
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

			final Long id = TestGui.this.addSample(file);
			JLabel label = new JLabel(file);
			constraints.gridx = 0;
			this.add(label, constraints);

			JButton start = new JButton("start");
			start.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					TestGui.this.controller.setSampleMute(id, false);
				}
			});
			constraints.gridx = 1;
			this.add(start, constraints);

			JButton stop = new JButton("stop");
			stop.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					TestGui.this.controller.setSampleMute(id, true);
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

	private Long addSample(String fileName) {
		log.info("Add Sample [fileName=" + fileName + "]");
		try {
			File file = new File(TestGui.class.getClassLoader().getResource(fileName).getFile());
			AudioInputStream ais = this.audioSystemProvider.getAudioInputStream(file);
			byte[] data = new byte[(int) file.length()];
			ais.read(data);
			return this.controller.getLoopStore().addSample(ais.getFormat(), data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
