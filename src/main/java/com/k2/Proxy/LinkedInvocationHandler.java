package com.k2.Proxy;

public interface LinkedInvocationHandler<T> extends TypedInvocationHandler<T> {

	public LinkedInvocationHandler<T> setLinkedHandler(TypedInvocationHandler<T> linkedHandler);
	
	public void setHandlerForClass(Class<T> handlerForClass);
	
}
