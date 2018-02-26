package example.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TestMutatorLinkedInvocationHandler<T> extends AbstractTestingLinkedInvocationHandler<T> implements InvocationHandler {
	
	public TestMutatorLinkedInvocationHandler() {}
	public TestMutatorLinkedInvocationHandler(Class<T> handlerForClass) {super(handlerForClass); }

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		if (args.length > 0) {
			if (args[0] instanceof String) {
				setResult(this.getClass(), "Executing method "+method.getName()+" on class "+proxy.getClass().getName()+" Mutating "+(String)args[0]+" to "+(String)args[0]+" Mutated!");
				args[0] = (String)args[0]+" Mutated!";
			} else if (args[0] instanceof Long) {
				setResult(this.getClass(), "Executing method "+method.getName()+" on class "+proxy.getClass().getName()+" Mutating "+(Long)args[0]+" to "+((Long)args[0]+10));
				args[0] = (Long)args[0]+10;
			} else {
				setResult(this.getClass(), "Executing method "+method.getName()+" on class "+proxy.getClass().getName()+" nothing to Mutate");
			}
		} else {
			setResult(this.getClass(), "Executing method "+method.getName()+" on class "+proxy.getClass().getName()+" no arrguments to Mutate");
		}
//		System.out.println(getResult(this.getClass()));
		if (linkedHandler != null) return linkedHandler.invoke(proxy, method, args);
		return null;
	}

}
