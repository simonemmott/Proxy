package com.k2.Proxy;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Proforma.ProformaLibrary;
import com.k2.Util.Identity.IdentityUtil;
import com.k2.Util.classes.ClassUtil;

/**
 * The proxy factory provides factory methods to get proxy classes and/or proxies of classes.
 * 
 * @author simon
 *
 */
public class ProxyFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * The proxied cache retains the classes generated as proxies of a given class to prevent the overhead of repeatedly generating the same proxy class dynamically
	 */
	@SuppressWarnings("rawtypes")
	private Map<Class<?>, Class<? extends AProxy>> proxiesCache = new HashMap<Class<?>, Class<? extends AProxy>>();

	/**
	 * An instance of the proxy factory can use a specific library to generate its proxy classes
	 */
	private ProformaLibrary instanceLibrary;

	/**
	 * Create an instance of the proxy factory that will use the default profroma library
	 */
	private ProxyFactory() {}
	
	/**
	 * The static field referencing the instance of the proxy factory that is returned by call to the staticFactory() method
	 */
	private static ProxyFactory staticFactory;
	
	/**
	 * Create an instance of the ProxyFactory that will generate its proxies from the given proforma library
	 * @param library	The proforma library that will be used to provide proformas to generate the proxy classes java source. 
	 * @return	An instance of the ProxyFactory linked to the given proforma library
	 */
	public static ProxyFactory dynamicFactory(ProformaLibrary library) {
		ProxyFactory factory = new ProxyFactory();
		factory.instanceLibrary = library;
		return factory;
	}
	
	/**
	 * Get the static proxy factory that uses the default proforma library
	 * @return	The static proxy factory
	 */
	public static ProxyFactory staticFactory() {
		if (staticFactory == null) staticFactory = new ProxyFactory();
		return staticFactory;
	}
	
	/**
	 * Get a instance implementing the AProxy interface that extends the class of the given target and linked to the given array of invocation handlers
	 * @param target		The object to be proxied
	 * @param handlers	The invocation handlers to handle invocations of the public methods of the proxied target.
	 * Each handler class must be an implementation of the TypedInvocationHandler and the first handler must be an implementation of the TargettedInvocationHandler 
	 * @return		The proxy to the proxied target that will call the inocation handlers in reverse order until the method on the target is invoked.
	 * @param <T> The type of the object being proxied
	 */
	@SuppressWarnings("unchecked")
	public <T> T getProxy(T target, Class<?> ... handlers) {
		
		if (handlers.length == 0) return target;
		if (!TargettedInvocationHandler.class.isAssignableFrom(handlers[0])) {
			throw new ProxyError("The first handler for {} must implement the interface TargettedInvocationHandler", IdentityUtil.getIdentity(target, target.getClass().getSimpleName()));
		}
		
		T currentTarget = target;
		TypedInvocationHandler<T> previousHandler = null;

		for (int i=0; i<handlers.length; i++) {
			Class<?> handlerClass = handlers[i];
			
			if (TargettedInvocationHandler.class.isAssignableFrom(handlerClass)) {
				TargettedInvocationHandler<T> handler;
				try {
					handler = (TargettedInvocationHandler<T>) handlerClass.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					logger.error("No zero arg consructor for targetted invocation handler class {}, proxy not created", handlerClass.getName());
					continue;
				}
				handler.setTarget(currentTarget);
				
				currentTarget = (T) getProxy(target.getClass(), handler);
				previousHandler = handler;
				continue;
			}
			
			if (LinkedInvocationHandler.class.isAssignableFrom(handlerClass)) {
				LinkedInvocationHandler<T> handler;
				try {
					handler = (LinkedInvocationHandler<T>) handlerClass.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					logger.error("No zero arg consructor for handler class {}, proxy not created", handlerClass.getName());
					continue;
				}
				handler.setLinkedHandler(previousHandler);
				
				((AProxy<T>)currentTarget).setInvocationHandler(handler);
				
				previousHandler = handler;
				continue;
				
			}
			
			logger.error("Unexpected handler class {}, Expected implementation of TargettedInvocationHandler or LinkedInvocationHandler, proxy not created", handlerClass.getName());
			
		}
		
		return currentTarget;
		
	}
	
	/**
	 * Create an instance of the proxy class for the given class and set its invocation handler to the given invocation handler
	 * @param cls		The class for which a proxy is required
	 * @param handler	The handler that will handle method invocation on the proxy class
	 * @return			A proxy that exends the given class and whos public methods are handled by the given invocation handler.
	 * @param <T> 		The type of the object being proxied
	 */
	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> cls, InvocationHandler handler) {
		try {
			Class<? extends AProxy<T>> c = (Class<? extends AProxy<T>>) getProxyClass(cls);
			AProxy<T> proxy = c.newInstance();
			proxy.setInvocationHandler(handler);
			return (T) proxy;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ProxyError("No zero arg constructor available for class {}", cls.getName());
		} finally {
			
		}
		
	}
	
	/**
	 * Get the proxy class for the given class
	 * If the proxy class has already been proxied then the previously generated proxy class will be returned
	 * @param cls	The class to be proxied
	 * @return		The proxy class of the given class
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<? extends AProxy> getProxyClass(Class<?> cls) {
		if (AProxy.class.isAssignableFrom(cls)) return (Class<? extends AProxy>) cls;
		Class<? extends AProxy> c = proxiesCache.get(cls);
		if (c != null) return c;
		c = generateProxy(cls);
		proxiesCache.put(cls, c);
		return c;
		
	}
	
	/**
	 * Generate the proxy class for the given class.
	 * @param cls	The class to be proxied
	 * @return		The generated proxy class
	 */
	@SuppressWarnings("rawtypes")
	private Class<? extends AProxy> generateProxy(Class<?> cls) {
		
		
		try {
			StringWriter sw = new StringWriter();

			if (instanceLibrary == null) {
				defaultLibrary.getProforma("classProxy").with(new ClassProformaAdapter(cls)).setIndent("    ").write(sw).flush();
			} else {
				instanceLibrary.getProforma("classProxy").with(new ClassProformaAdapter(cls)).setIndent("    ").write(sw).flush();			
			}


			logger.info("Generating proxy class for {} from \n{}\n", cls.getName(), sw.toString());

			return ClassUtil.createClassFromString(AProxy.class, cls.getPackage().getName(), cls.getSimpleName()+"_Proxy", sw.toString());

		} catch (IOException e) {
			throw new ProxyError("Unable to generate proxy class code for class {}", cls.getName());
		}
		
	}
	
	/**
	 * The default static proforma library used to generate proxy classes
	 */
	private static ProformaLibrary defaultLibrary = new DefaultProxyLibrary();
	
}
