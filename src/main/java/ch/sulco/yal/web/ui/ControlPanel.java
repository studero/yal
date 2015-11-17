package ch.sulco.yal.web.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

import ch.sulco.yal.web.VaadinServer;

public class ControlPanel extends Panel {
	public ControlPanel() {
		HorizontalLayout controlContent = new HorizontalLayout();
		setContent(controlContent);
		Button playBtn = new Button("Play");
		playBtn.addClickListener(e -> VaadinServer.getInstance().getProcessor().play());
		controlContent.addComponent(playBtn);
		Button pauseBtn = new Button("Pause");
		pauseBtn.addClickListener(e -> VaadinServer.getInstance().getProcessor().pause());
		controlContent.addComponent(pauseBtn);
		Button loopBtn = new Button("Loop");
		loopBtn.addClickListener(e -> VaadinServer.getInstance().getProcessor().loop());
		controlContent.addComponent(loopBtn);
		Button stopBtn = new Button("Stop");
		stopBtn.addClickListener(e -> VaadinServer.getInstance().getProcessor().stop());
		controlContent.addComponent(stopBtn);
	}
}
