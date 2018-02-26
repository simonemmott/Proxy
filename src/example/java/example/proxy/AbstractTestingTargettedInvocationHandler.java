package example.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.k2.Proxy.TargettedInvocationHandler;

public abstract class AbstractTestingTargettedInvocationHandler<T> extends AbstractTargettedInvocationHandler<T> {

	private static Map<Class<? extends InvocationHandler>, Object> results = new HashMap<Class<? extends InvocationHandler>, Object>();
	
	public AbstractTestingTargettedInvocationHandler() {
		super();
	}
	public AbstractTestingTargettedInvocationHandler(T target) {
		super(target);
	}

	protected void setResult(Class<? extends InvocationHandler> handlerClass, Object result) {
		results.put(handlerClass, result);
	}
	
	public static Object getResult(Class<? extends InvocationHandler> handlerClass) { return results.get(handlerClass); }
	
	public Object getResult() { return results.get(this.getClass()); }


}
