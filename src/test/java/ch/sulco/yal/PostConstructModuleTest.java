package ch.sulco.yal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class PostConstructModuleTest extends AbstractModule {

	@Inject
	private Injector injector;

	public static class A {

		private String name = "foo";

		@PostConstruct
		public void init() {
			this.name = "bar";
		}

	}

	@Before
	public void setUp() {
		Guice.createInjector(this).injectMembers(this);
	}

	@Override
	protected void configure() {
		this.install(new PostConstructModule());
	}

	@Test
	public void shouldPostConstructNameBar() {
		assertThat(this.injector.getInstance(A.class).name, is("bar"));
	}
}
