package example.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;


public class TestLoggerLinkedInvocationHandler<T> extends AbstractTestingLinkedInvocationHandler<T> implements InvocationHandler {
	
	public TestLoggerLinkedInvocationHandler() { super(); }
	public TestLoggerLinkedInvocationHandler(Class<T> handlerForClass) { super(handlerForClass); }

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		setResult(this.getClass(), "Executing method "+method.getName()+" on class "+proxy.getClass().getName()+" with args "+Arrays.toString(args));
//		System.out.println(getResult(this.getClass()));

		if (linkedHandler != null) return linkedHandler.invoke(proxy, method, args);
		return null;
	}

}
