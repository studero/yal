package ch.sulco.yal.web;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.DataStore.DataEvent;
import ch.sulco.yal.dsp.DataStore.DataEventListener;
import ch.sulco.yal.dsp.LoopActivator;
import ch.sulco.yal.dsp.audio.Processor;

@Singleton
public class VaadinServer implements DataEventListener {

	@Inject
	private DataStore dataStore;

	@Inject
	private LoopActivator loopActivator;

	@Inject
	private Processor processor;

	private static VaadinServer instance;

	public static VaadinServer getInstance() {
		return instance;
	}

	public VaadinServer() {
		instance = this;
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Tomcat tc = new Tomcat();
				tc.setPort(1234);
				tc.setBaseDir("www");
				try {
					tc.addWebapp("/", new File("www").getAbsolutePath());
					tc.init();
					tc.start();
					tc.getServer().await();
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LifecycleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	@PostConstruct
	public void setup() {
		this.dataStore.addListener(this);
	}

	public DataStore getDataStore() {
		return dataStore;
	}

	public LoopActivator getLoopActivator() {
		return loopActivator;
	}

	public Processor getProcessor() {
		return processor;
	}

	@Override
	public void onDataEvent(DataEvent event) {

	}
}
