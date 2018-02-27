package com.k2.Proxy;

/**
 * This interface defines the methods required for an implementation of InvocationHandler where the handler passes the invocation
 * to the next handler in a linked list of invocation handlers to allow multiple handlers to be invoked in a defined order when the
 * public method of a proxy is called.
 * 
 * @author simon
 *
 * @param <T> The class of the ultimate object being proxied by the linked invocation handlers
 */
public interface LinkedInvocationHandler<T> extends TypedInvocationHandler<T> {

	/**
	 * Set the handler that this handler will pass control to in lieu of invoking the invoked method on the proxy target.
	 * @param linkedHandler	The next handler in the linked list oof handlers
	 * @return	This invocation handler for method chaining
	 */
	public LinkedInvocationHandler<T> setLinkedHandler(TypedInvocationHandler<T> linkedHandler);
	
	/**
	 * Set the class that this handler is being used to handle method invocations
	 * @param handlerForClass	The class that this invocation handler is being implemented to handle.
	 */
	public void setHandlerForClass(Class<T> handlerForClass);
	
}
