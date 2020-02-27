package com.letsprog.learning.jndi.lookup.ejb3;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class LookerUp {

	private Properties prop = new Properties();
	private String jndiPrefix;
 
	// Same WAR
	public LookerUp(){
		prop.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
	}
	
	// Client and EJBs are in the same EAR but not the same WAR
	public LookerUp(String address, int httpPort){
 
		String protocol;
		protocol = "http-remoting";
		prop.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
		prop.put(Context.PROVIDER_URL, protocol+"://"+address+":"+httpPort+"/");
		prop.put("jboss.naming.client.ejb.context", true);
		prop.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		jndiPrefix="ejb";
 
	}
	
	public Object findLocalSessionBean(String moduleName, String beanName, String interfaceFullQualifiedName) throws NamingException{
 
		final Context context = new InitialContext(prop);
		Object object = context.lookup("java:global/"+moduleName+"/"+beanName+"!"+interfaceFullQualifiedName);
		context.close();
 
		return object;
	}
 
	public Object findSessionBean(String jndiName) throws NamingException{
 
		final Context context = new InitialContext(prop);
		Object object = context.lookup(jndiName);
		context.close();
 
		return object;
	}
	
	public Object findRemoteSessionBean(SessionBeanType beanType, String earName, String moduleName, String deploymentDistinctName, String beanName, String interfaceFullQualifiedName) throws NamingException{
		 
		String suffix = "";
		if(beanType.equals(SessionBeanType.STATEFUL)){
			suffix = "?stateful";
		}
 
		final Context context = new InitialContext(prop);
		Object object = context.lookup(jndiPrefix+":"+earName+"/"+moduleName+"/"+deploymentDistinctName+"/"+beanName+"!"+interfaceFullQualifiedName+suffix);
		context.close();
 
		return object;
	}
	
	public enum SessionBeanType {
		STATEFUL,STATELESS,SINGLETON
	}
}
