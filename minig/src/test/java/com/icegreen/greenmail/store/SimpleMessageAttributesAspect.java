package com.icegreen.greenmail.store;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class SimpleMessageAttributesAspect {

	@SuppressWarnings("rawtypes")
	@Around(value = "call(* com.icegreen.greenmail.store.SimpleMessageAttributes.getParameters(..))")
	public void replace(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		SimpleMessageAttributes messageAttributes = (SimpleMessageAttributes) thisJoinPoint
				.getTarget();

		StringBuffer buf = (StringBuffer) thisJoinPoint.getArgs()[0];

		Field f = messageAttributes.getClass().getDeclaredField("parameters");
		f.setAccessible(true);
		Set parameters = (Set) f.get(messageAttributes);

		if (parameters == null || parameters.isEmpty()) {
			buf.append("NIL");
		} else {
			buf.append("(");
			Iterator it = parameters.iterator();

			if (it.hasNext()) {
				buf.append((String) it.next());
			}

			while (it.hasNext()) {
				buf.append(" ");
				buf.append((String) it.next());

			}
			buf.append(")");
		}
	}
}
