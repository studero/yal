package ch.sulco.yal.web.ui;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dsp.DataStore.DataEvent;
import ch.sulco.yal.dsp.DataStore.DataEventListener;
import ch.sulco.yal.web.VaadinServer;

@Title("YAL")
@Push(value = PushMode.AUTOMATIC)
public class YalUI extends UI implements DataEventListener {
	private final static Logger log = LoggerFactory.getLogger(YalUI.class);

	private final List<LoopPanel> loopPnls = new ArrayList<>();

	private ControlPanel controlPnl;
	private Label eventLbl;

	@Override
	protected void init(VaadinRequest request) {
		VaadinServer.getInstance().getDataStore().addListener(this);

		VerticalLayout content = new VerticalLayout();
		content.setMargin(true);
		setContent(content);

		controlPnl = new ControlPanel();
		content.addComponent(controlPnl);

		eventLbl = new Label();
		content.addComponent(eventLbl);

		for (Loop loop : VaadinServer.getInstance().getDataStore().getLoops()) {
			LoopPanel loopPnl = new LoopPanel(loop);
			loopPnls.add(loopPnl);
			content.addComponent(loopPnl);
		}
	}

	@Override
	public void onDataEvent(DataEvent event) {
		log.info("event " + event);
		eventLbl.setCaption(eventLbl.getCaption() + event.getEventType());
	}
}
