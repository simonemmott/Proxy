package com.k2.Proxy;

import java.lang.reflect.InvocationHandler;

public interface TypedInvocationHandler<T> extends InvocationHandler {
	
	public Class<T> handlerForClass();

}
