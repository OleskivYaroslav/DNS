
package org.xbill.DNS.spi;

import java.lang.reflect.Proxy;

import sun.net.spi.nameservice.*;



public class DNSJavaNameServiceDescriptor implements NameServiceDescriptor {

private static NameService nameService;

static {
	ClassLoader loader = NameService.class.getClassLoader();
	nameService = (NameService) Proxy.newProxyInstance(loader,
			new Class[] { NameService.class },
			new DNSJavaNameService());
}


public NameService
createNameService() {
	return nameService;
}

public String
getType() {
	return "dns";
}

public String
getProviderName() {
	return "dnsjava"; 
}

}
