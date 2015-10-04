package ch.sulco.yal;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.PostConstruct;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class PostConstructModule implements Module, TypeListener {

	@Override
	public void configure(final Binder binder) {
		binder.bindListener(Matchers.any(), this);
	}

	@Override
	public <I> void hear(final TypeLiteral<I> type, final TypeEncounter<I> encounter) {
		encounter.register(new InjectionListener<I>() {

			@Override
			public void afterInjection(final I injectee) {
				List<Method> methods = FluentIterable.of(injectee.getClass().getMethods()).filter(new Predicate<Method>() {
					@Override
					public boolean apply(Method input) {
						return input.isAnnotationPresent(PostConstruct.class);
					}
				}).toList();
				for (Method method : methods) {
					try {
						method.invoke(injectee);
					} catch (final Exception e) {
						throw new RuntimeException(String.format("@PostConstruct %s", method), e);
					}
				}
			}
		});
	}

}