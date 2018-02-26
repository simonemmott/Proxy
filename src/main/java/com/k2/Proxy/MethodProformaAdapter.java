package com.k2.Proxy;

import com.k2.Util.StringUtil;
import com.k2.Util.classes.MethodSignature;

public class MethodProformaAdapter implements Comparable<MethodProformaAdapter>{

	private MethodSignature meth;
	private String charRnd6 = StringUtil.random(6);
	
	public MethodProformaAdapter(MethodSignature meth) {
		this.meth = meth;
	}
	
	public String getStaticMethodName() { return meth.getName()+"_"+charRnd6; }
	
	public String getDeclaringClassName() { return meth.getMethod().getDeclaringClass().getSimpleName(); }

	public String getMethodName() { return meth.getName(); }
	
	public String getReturnsClause() { return meth.getReturnsClause(); }
	
	public String getMethodSignature() { return meth.getMethodSignature(); }
	
	public Boolean getReturnsValue() { return meth.returnsValue(); }
	
	public Boolean getHasParameters() { return meth.hasParameters(); }
	
	public String getParameterNames() { return meth.getParameterNamesClause(); }
	
	public String getParameterTypesClause() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<meth.getParameterTypes().length; i++) {
			sb.append(meth.getParameterTypes()[i].getSimpleName()).append(".class");
			if (i < meth.getParameterTypes().length - 1)
				sb.append(", ");
		}
		return sb.toString();
	}



	@Override
	public int compareTo(MethodProformaAdapter o) {
		return meth.compareTo(o.meth);
	}
		
}
