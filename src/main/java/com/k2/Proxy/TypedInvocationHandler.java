package com.k2.Proxy;

import java.lang.reflect.InvocationHandler;

/**
 * A typed invocation handler handles method invocations on proxies for a given proxied type
 * @author simon
 *
 * @param <T>	The class of the proxied object
 */
public interface TypedInvocationHandler<T> extends InvocationHandler {
	
	/**
	 * Get the class that this handler will handle public method invocations
	 * @return	The class that this handler will handle public method invocations
	 */
	public Class<T> handlerForClass();

}
