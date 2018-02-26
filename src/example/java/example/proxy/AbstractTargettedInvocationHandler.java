package example.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.k2.Proxy.AProxy;
import com.k2.Proxy.TargettedInvocationHandler;

public abstract class AbstractTargettedInvocationHandler<T> implements TargettedInvocationHandler<T> {

	protected T target;

	public AbstractTargettedInvocationHandler(T target) {
		this.target = target;
	}

	public AbstractTargettedInvocationHandler() {}

	@Override
	public TargettedInvocationHandler<T> setTarget(T target) {
		this.target = target;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<T> handlerForClass() {
		if (target instanceof AProxy) return ((AProxy<T>)target).proxyForClass();
		return (Class<T>) target.getClass();
	}


}
