package com.k2.Proxy;

import java.util.Set;
import java.util.TreeSet;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.k2.Util.classes.ClassUtil;
import com.k2.Util.classes.Dependencies;
import com.k2.Util.classes.Dependency;
import com.k2.Util.classes.MethodSignature;

/**
 * The class proforma adapter adapts an instance of the java Class object into an instance that provides values for the class proforma used
 * to generate jave source code for the proxy for the given class.
 * 
 * @author simon
 *
 */
public class ClassProformaAdapter {

	private Class<?> cls;
	
	/**
	 * Create an adapter to adapt the given class to provide the values required by the proxy class proforma
	 * @param cls	The class being proxied
	 */
	public ClassProformaAdapter(Class<?> cls) {
		this.cls = cls;
	}
	
	/**
	 * @return The simple name of the proxied class
	 */
	public String getClassName() { return cls.getSimpleName(); }

	/**
	 * @return The package name of the proxied class
	 */
	public String getPackageName() { return cls.getPackage().getName(); }
	
	/**
	 * Generate and return the set of class dependencies required by the proxy class of the class being proxied.
	 * @return	The set of dependencies required by the proxy class
	 */
	public Set<Dependency> getDependencies() {
		Dependencies dependencies = new Dependencies();
			dependencies.add(
					InvocationHandler.class,
					Method.class,
					AProxy.class,
					ProxyError.class,
					ClassUtil.class,
					cls);
		return dependencies.getDependencies();
	}
	
	/**
	 * Generate and return the set of adapted public methods that must be implemented by the proxy class
	 * @return	The set of public methods on the class being proxied converted into values for the method proforma
	 * used to generate java source for the proxy class
	 */
	public Set<MethodProformaAdapter> getPublicMethods() {
		Set<MethodProformaAdapter> mSet = new TreeSet<MethodProformaAdapter>();
		for (Method m : ClassUtil.getAllMethods(cls)) {
			if (m.getModifiers() == Modifier.PUBLIC) {
				mSet.add(new MethodProformaAdapter(MethodSignature.forMethod(m)));
			}
		}
		return mSet;
	}
	
}
