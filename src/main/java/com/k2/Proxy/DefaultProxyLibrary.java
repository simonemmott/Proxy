package com.k2.Proxy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.k2.Expressions.predicate.PredicateBuilder;
import com.k2.Proforma.Proforma;
import com.k2.Proforma.ProformaLibrary;

/**
 * This class implements the ProformaLibrary interface and as such provides implementations of proformas by name.
 * 
 * The proformas implemented by this library provide an implementation of the AProxy interface for any given class.
 * 
 * @author simon
 *
 */
public class DefaultProxyLibrary implements ProformaLibrary {
	
	/**
	 * The predicate builder used by the proformas in this library
	 */
	private static PredicateBuilder pb = new PredicateBuilder();
	
	/**
	 * The static field provides an abbreviation for the static methods of Proforma to make the proforma defined in this library easier to read.
	 */
	private static Proforma P = new Proforma();
	
	/**
	 * The proxy method proforma generates the java for the proxied public methods of the proxy class
	 */
	@SuppressWarnings("static-access")
	private static Proforma proxyMethodProforma = new Proforma("proxyMethod")
			.addIf(P.p(Boolean.class, "hasParameters"), 
					"private static final Method ", P.p("staticMethodName"), 
					" = ClassUtil.getMethod(", P.p("declaringClassName"), ".class, \"", P.p("methodName"), 
					"\", ", P.p("parameterTypesClause"), 
					");")
			.addIf(pb.not(P.p(Boolean.class, "hasParameters")), 
					"private static final Method ", P.p("staticMethodName"), 
					" = ClassUtil.getMethod(", P.p("declaringClassName"), ".class, \"", P.p("methodName"), "\");")
			.add("@Override")
			.add("public ", P.p("returnsClause"), " ", P.p("methodSignature"), " {")
			.add(P.i(), "try {")
			// Proxy of public method with parameters and returning a value
			.addIf(pb.and(P.p(Boolean.class, "returnsValue"), P.p(Boolean.class, "hasParameters")),
					P.i(), P.i(), "return (", P.p("returnsClause"), ") AProxy.invoke(invocationHandler, this, ", P.p("staticMethodName"), ", ", P.p("parameterNames"), ");")
			// Proxy of public method without parameters and returning a value - GETTER
			.addIf(pb.and(P.p(Boolean.class, "returnsValue"), pb.not(P.p(Boolean.class, "hasParameters"))),
					P.i(), P.i(), "return (", P.p("returnsClause"), ") AProxy.invoke(invocationHandler, this, ", P.p("staticMethodName"), ");")
			// Proxy of public method with parameters and not returning a value - SETTER
			.addIf(pb.and(pb.not(P.p(Boolean.class, "returnsValue")), P.p(Boolean.class, "hasParameters")),
					P.i(), P.i(), "AProxy.invoke(invocationHandler, this, ", P.p("staticMethodName"), ", ", P.p("parameterNames"), ");")
			// Proxy of public method without parameters and not returning a value
			.addIf(pb.and(pb.not(P.p(Boolean.class, "returnsValue")), pb.not(P.p(Boolean.class, "hasParameters"))),
					P.i(), P.i(), "AProxy.invoke(invocationHandler, this, ", P.p("staticMethodName"), ");")
			.add(P.i(), "} catch (Throwable e) {")
			.add(P.i(), P.i(), "throw new ProxyError(\"Unxpected exeception thrown by proxied instance.\", e);")
			.add(P.i(),"}")
			.add("}")
			.add();
	
	/**
	 * The class proxy body proforma generates the java for the body of the proxy class and includes the proxy method proforma for each public 
	 * proxied method.
	 */
	@SuppressWarnings("static-access")
	private static Proforma classProxyBodyProforma = new Proforma("classProxyBody")
			.add("public class ", P.p("className"), "_Proxy extends ",  P.p("className"), " implements AProxy<", P.p("className"), "> {")
			.add()
			.add(P.i(), "private InvocationHandler invocationHandler;")
			.add()
			.add(P.i(), "public void setInvocationHandler(InvocationHandler handler) {")
			.add(P.i(), P.i(), "invocationHandler = handler;")
			.add(P.i(), "}")
			.add()
			.add(P.i(), "@Override")
			.add(P.i(), "public Class<", P.p("className"), "> proxyForClass() {")
			.add(P.i(), P.i(), "return ", P.p("className"), ".class;")
			.add(P.i(), "}")
			.add()
			.add(P.i(), "public ",P.p("className"), "_Proxy() { super(); }")
			.add()
			.add(proxyMethodProforma.with(P.p(Set.class, "publicMethods")))
			.add("}");
	
	/**
	 * The import proforma generates the java for a java dependency
	 */
	@SuppressWarnings("static-access")
	private static Proforma importProforma = new Proforma("import").setEmbedded(false)
			.add(P.p("importClause"));
	
	/**
	 * The class proxy proforma generates the whole of the java source for the proxy class by calling the embedded import proforma 
	 * for each dependency of the proxy class and the class proxy body proforma
	 */
	@SuppressWarnings("static-access")
	private static Proforma classProxyProforma = new Proforma("classProxy").setAutoIncrementIndent(false)
			.add("package ", P.p("packageName"), ";")
			.add()
			.add("import java.lang.reflect.*;")
			.add(importProforma.with(P.p(Set.class, "dependencies")))
			.add(classProxyBodyProforma);
	
	/**
	 * This method allows the static proformas in this library to be registerd with the library so that they can be indexed on proforma name
	 * @param proformas	The proformas implemented by this library
	 * @return	A map of the proforma implemented by this library indexed by proforma name
	 */
	private static Map<String, Proforma> register(Proforma ... proformas ) {
		Map<String, Proforma> ps = new HashMap<String, Proforma>();
		for (Proforma p : proformas) {
			ps.put(p.getName(), p);
		}
		return ps;
	}
	
	/**
	 * The map indexing the proforma of this library by name
	 */
	private static Map<String, Proforma> proformas = register(
			classProxyProforma,
			classProxyBodyProforma,
			proxyMethodProforma,
			importProforma);

	@Override
	public Collection<Proforma> getAllProfomas() {
		return proformas.values();
	}

	@Override
	public Proforma getProforma(String name) {
		return proformas.get(name);
	}

	@Override
	public void include(ProformaLibrary... libraries) {
		for (ProformaLibrary library : libraries) {
			for (Proforma p : library.getAllProfomas()) {
				proformas.put(p.getName(), p);
			}
		}
	}

}
