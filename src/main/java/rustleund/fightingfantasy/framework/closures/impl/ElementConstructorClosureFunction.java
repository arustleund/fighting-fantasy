package rustleund.fightingfantasy.framework.closures.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.closures.Closure;

import com.google.common.base.Function;

public class ElementConstructorClosureFunction implements Function<Element, Closure> {

	private Constructor<? extends Closure> constructor;

	public ElementConstructorClosureFunction(Class<? extends Closure> clazz) {
		try {
			this.constructor = clazz.getConstructor(Element.class);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Closure apply(Element input) {
		try {
			return this.constructor.newInstance(input);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

}
