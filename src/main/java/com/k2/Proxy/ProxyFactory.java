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

public class ProxyFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@SuppressWarnings("rawtypes")
	private Map<Class<?>, Class<? extends AProxy>> proxiesCache = new HashMap<Class<?>, Class<? extends AProxy>>();

	private ProformaLibrary instanceLibrary;

	private ProxyFactory() {}
	
	private static ProxyFactory staticFactory;
	
	public static ProxyFactory dynamicFactory(ProformaLibrary library) {
		ProxyFactory factory = new ProxyFactory();
		factory.instanceLibrary = library;
		return factory;
	}
	
	public static ProxyFactory staticFactory() {
		if (staticFactory == null) staticFactory = new ProxyFactory();
		return staticFactory;
	}
	
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<? extends AProxy> getProxyClass(Class<?> cls) {
		if (AProxy.class.isAssignableFrom(cls)) return (Class<? extends AProxy>) cls;
		Class<? extends AProxy> c = proxiesCache.get(cls);
		if (c != null) return c;
		c = generateProxy(cls);
		proxiesCache.put(cls, c);
		return c;
		
	}
	
	@SuppressWarnings("rawtypes")
	private Class<? extends AProxy> generateProxy(Class<?> cls) {
		
		
		try {
			StringWriter sw = new StringWriter();

			ProxyFactory.staticFactory().outputGenericClassProxy(cls, sw);

			logger.info("Generating proxy class for {} from \n{}\n", cls.getName(), sw.toString());

			return ClassUtil.createClassFromString(AProxy.class, cls.getPackage().getName(), cls.getSimpleName()+"_Proxy", sw.toString());

		} catch (IOException e) {
			throw new ProxyError("Unable to generate proxy class code for class {}", cls.getName());
		}
		
	}
	
	
	private static ProformaLibrary defaultLibrary = new DefaultProxyLibrary();
	
	private Writer outputGenericClassProxy(Class<?> cls, Writer out) throws IOException {
		if (instanceLibrary == null) {
			defaultLibrary.getProforma("classProxy").with(new ClassProformaAdapter(cls)).setIndent("    ").write(out).flush();
		} else {
			instanceLibrary.getProforma("classProxy").with(new ClassProformaAdapter(cls)).setIndent("    ").write(out).flush();			
		}
		
		return out;
	}
	
	

}
