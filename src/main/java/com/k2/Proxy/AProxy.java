package com.k2.Proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public interface AProxy<T> {
	public void setInvocationHandler(InvocationHandler handler);
	
	public static Object invoke(InvocationHandler invocationHandler, Object onObject, Method method, Object ... args) throws Throwable {
		return invocationHandler.invoke(onObject, method, args);
	}

	public Class<T> proxyForClass();
}
