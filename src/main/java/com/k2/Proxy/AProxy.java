package com.k2.Proxy;

/**
 */
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * The AProxy interface is implemented by Proxies of classes and provides a mechanism through which an invocation handler is set on the proxy
 * to handle invocations of the proxies public methods.
 * @author simon
 *
 * @param <T>	The class for which the class implementing this interface is a proxy to an instance.
 */
public interface AProxy<T> {
	
	/**
	 * This method sets the invocation handler for this proxy instance.
	 * @param handler	The invocation handler that will handle all requests to the public methods of the instance being proxied
	 */
	public void setInvocationHandler(InvocationHandler handler);

	/**
	 * This static method is invoked by the generated implementation of AProxy to invoke the invocation handler for a call to the proxied method
	 * @param invocationHandler	The invocation handler to call to handle the method invocataion
	 * @param onObject	The proxy whos public method is being invoked
	 * @param method		The method that is being invoked
	 * @param args		The arguemnts passes to the method being invoked
	 * @return			The value returned from the invocation handler when handling the method invocation
	 * @throws Throwable		If the method handler encounters a throwable error
	 */
	public static Object invoke(InvocationHandler invocationHandler, Object onObject, Method method, Object ... args) throws Throwable {
		return invocationHandler.invoke(onObject, method, args);
	}

	/**
	 * This method returns the class of the instance being proxied by this proxy
	 * @return	The class of the instance that is being represented by this proxy
	 */
	public Class<T> proxyForClass();
}
