package example.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;


public class TestLoggerTargettedInvocationHandler<T> extends AbstractTestingTargettedInvocationHandler<T> implements InvocationHandler {
	
	public TestLoggerTargettedInvocationHandler() { super(); }
	public TestLoggerTargettedInvocationHandler(T target) { super(target); }

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		setResult(this.getClass(), "Executing method "+method.getName()+" on class "+proxy.getClass().getName()+" with args "+Arrays.toString(args));
//		System.out.println(getResult(this.getClass()));

		return method.invoke(target, args);
	}

}
