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

public class ClassProformaAdapter {

	private Class<?> cls;
	
	public ClassProformaAdapter(Class<?> cls) {
		this.cls = cls;
	}
	
	public String getClassName() { return cls.getSimpleName(); }

	public String getPackageName() { return cls.getPackage().getName(); }
	
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
