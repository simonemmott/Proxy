package com.k2.Proxy;

import com.k2.Util.StringUtil;
import com.k2.Util.classes.MethodSignature;

/**
 * The method proforma adapter adapts an instance of MethodSignature to provide values for the method proforma used to generate
 * the java code for the proxied public method of the proxyied class
 * @author simon
 *
 */
public class MethodProformaAdapter implements Comparable<MethodProformaAdapter>{

	/**
	 * The method signature of the method being proxied.
	 */
	private MethodSignature meth;
	/**
	 * A random 6 character string appended to the method name to make the static pointer to the public method on the proxied class
	 * unique
	 */
	private String charRnd6 = StringUtil.random(6);
	
	/**
	 * Create a proforma adapter for the given method signature
	 * @param meth	The MethodSignatoure of the method being proxied
	 */
	public MethodProformaAdapter(MethodSignature meth) {
		this.meth = meth;
	}
	
	/**
	 * @return The name of the method appended with a random string to make the static pointer to overloaded methods unique
	 */
	public String getStaticMethodName() { return meth.getName()+"_"+charRnd6; }
	
	/**
	 * @return	The simple name of the class implementing the method to be proxied. This may not be the proxied class but may be one
	 * of the super classes of the proxied class for method defined on the super classes of the proxied class but not overriden by the 
	 * proxied class
	 */
	public String getDeclaringClassName() { return meth.getMethod().getDeclaringClass().getSimpleName(); }

	/**
	 * 
	 * @return	The name of the proxied method
	 */
	public String getMethodName() { return meth.getName(); }
	
	/**
	 * 
	 * @return The returns clause of the prxied method. i.e. 'void' for methods that don't return a value and the simple name of the returned 
	 * class for methods that do return a value
	 */
	public String getReturnsClause() { return meth.getReturnsClause(); }
	
	/**
	 * 
	 * @return	The java source of the method being proxied. This includes the method name, parenthesis, and arguments but not the returns clause
	 */
	public String getMethodSignature() { return meth.getMethodSignature(); }
	
	/**
	 * 
	 * @return	True if the method return a value
	 */
	public Boolean getReturnsValue() { return meth.returnsValue(); }
	
	/**
	 * 
	 * @return	True if the method requires parameters
	 */
	public Boolean getHasParameters() { return meth.hasParameters(); }
	
	public String getParameterNames() { return meth.getParameterNamesClause(); }
	
	/**
	 * 
	 * @return	A comma concatenated string of the simple names of the classes of the parameters in parameter order an with each class simple name
	 * post fixed with the string '.class'
	 */
	public String getParameterTypesClause() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<meth.getParameterTypes().length; i++) {
			sb.append(meth.getParameterTypes()[i].getSimpleName()).append(".class");
			if (i < meth.getParameterTypes().length - 1)
				sb.append(", ");
		}
		return sb.toString();
	}


	/**
	 * Method proforma adapters are comparable using the comparability of method signatures
	 */
	@Override
	public int compareTo(MethodProformaAdapter o) {
		return meth.compareTo(o.meth);
	}
		
}
