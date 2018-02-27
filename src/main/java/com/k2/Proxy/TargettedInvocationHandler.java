package com.k2.Proxy;

/**
 * A targeted invocation handler handles methods invocations for a given target by performing logical operations before and/or after invoking the method on the 
 * target object of the targeted invocation handler.
 * @author simon
 *
 * @param <T>	The type of the object that is the target for this targeted invocation handler.
 */
public interface TargettedInvocationHandler<T> extends TypedInvocationHandler<T> {

	/**
	 * Set the target for this invocation handler
	 * @param target		The target of this invocation handler
	 * @return	This targeted invocation handler for method chaining
	 */
	public TargettedInvocationHandler<T> setTarget(T target);
}
