package com.k2.Proxy;

public interface TargettedInvocationHandler<T> extends TypedInvocationHandler<T> {

	public TargettedInvocationHandler<T> setTarget(T target);
}
