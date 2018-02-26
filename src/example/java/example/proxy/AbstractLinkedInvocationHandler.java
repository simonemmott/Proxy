package example.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.k2.Proxy.AProxy;
import com.k2.Proxy.LinkedInvocationHandler;
import com.k2.Proxy.TargettedInvocationHandler;
import com.k2.Proxy.TypedInvocationHandler;

public abstract class AbstractLinkedInvocationHandler<T> implements LinkedInvocationHandler<T> {

	protected TypedInvocationHandler<T> linkedHandler;
	private Class<T> handlerForClass;

	public AbstractLinkedInvocationHandler(Class<T> handlerForClass) {
		this.handlerForClass = handlerForClass;
	}

	public AbstractLinkedInvocationHandler() {}

	@Override
	public LinkedInvocationHandler<T> setLinkedHandler(TypedInvocationHandler<T> linkedHandler) {
		this.linkedHandler = linkedHandler;
		return this;
	}
	
	@Override
	public void setHandlerForClass(Class<T> handlerForClass) {
		this.handlerForClass = handlerForClass;
	}
	
	@Override
	public Class<T> handlerForClass() {
		return handlerForClass;
	}


}
