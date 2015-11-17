package ch.sulco.yal.web.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.web.VaadinServer;

public class LoopPanel extends Panel {
	public LoopPanel(Loop loop) {
		VerticalLayout content = new VerticalLayout();
		setContent(content);
		content.addComponent(new Label(loop.getId() + " - " + loop.getName()));
		Button activateBtn = new Button("activate");
		activateBtn.addClickListener(c -> {
			VaadinServer.getInstance().getDataStore().setNextLoopId(loop.getId());
			if (VaadinServer.getInstance().getDataStore().getCurrentLoop() == null)
				VaadinServer.getInstance().getLoopActivator().setCurrentLoopId(loop.getId());
		});
		content.addComponent(activateBtn);
	}
}
