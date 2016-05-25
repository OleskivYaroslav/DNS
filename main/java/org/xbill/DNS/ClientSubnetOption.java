
package org.xbill.DNS;

import java.net.*;
import java.util.regex.*;


public class ClientSubnetOption extends EDNSOption {

private static final long serialVersionUID = -3868158449890266347L;

private int family;
private int sourceNetmask;
private int scopeNetmask;
private InetAddress address;

ClientSubnetOption() {
	super(EDNSOption.Code.CLIENT_SUBNET);
}

private static int
checkMaskLength(String field, int family, int val) {
	int max = Address.addressLength(family) * 8;
	if (val < 0 || val > max)
		throw new IllegalArgumentException("\"" + field + "\" " + val +
						   " must be in the range " +
						   "[0.." + max + "]");
	return val;
}


public 
ClientSubnetOption(int sourceNetmask, int scopeNetmask, InetAddress address) {
	super(EDNSOption.Code.CLIENT_SUBNET);

	this.family = Address.familyOf(address);
	this.sourceNetmask = checkMaskLength("source netmask", this.family,
					     sourceNetmask);
	this.scopeNetmask = checkMaskLength("scope netmask", this.family,
					     scopeNetmask);
	this.address = Address.truncate(address, sourceNetmask);

	if (!address.equals(this.address))
		throw new IllegalArgumentException("source netmask is not " +
						   "valid for address");
}


public 
ClientSubnetOption(int sourceNetmask, InetAddress address) {
	this(sourceNetmask, 0, address);
}


public int 
getFamily() {
	return family;
}

/** Returns the source netmask. */
public int 
getSourceNetmask() {
	return sourceNetmask;
}

/** Returns the scope netmask. */
public int 
getScopeNetmask() {
	return scopeNetmask;
}

/** Returns the IP address of the client. */
public InetAddress 
getAddress() {
	return address;
}

void 
optionFromWire(DNSInput in) throws WireParseException {
	family = in.readU16();
	if (family != Address.IPv4 && family != Address.IPv6)
		throw new WireParseException("unknown address family");
	sourceNetmask = in.readU8();
	if (sourceNetmask > Address.addressLength(family) * 8)
		throw new WireParseException("invalid source netmask");
	scopeNetmask = in.readU8();
	if (scopeNetmask > Address.addressLength(family) * 8)
		throw new WireParseException("invalid scope netmask");

	// Read the truncated address
	byte [] addr = in.readByteArray();
	if (addr.length != (sourceNetmask + 7) / 8)
		throw new WireParseException("invalid address");

	// Convert it to a full length address.
	byte [] fulladdr = new byte[Address.addressLength(family)];
	System.arraycopy(addr, 0, fulladdr, 0, addr.length);

	try {
		address = InetAddress.getByAddress(fulladdr);
	} catch (UnknownHostException e) {
		throw new WireParseException("invalid address", e);
	}

	InetAddress tmp = Address.truncate(address, sourceNetmask);
	if (!tmp.equals(address))
		throw new WireParseException("invalid padding");
}

void 
optionToWire(DNSOutput out) {
	out.writeU16(family);
	out.writeU8(sourceNetmask);
	out.writeU8(scopeNetmask);
	out.writeByteArray(address.getAddress(), 0, (sourceNetmask + 7) / 8);
}

String 
optionToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(address.getHostAddress());
	sb.append("/");
	sb.append(sourceNetmask);
	sb.append(", scope netmask ");
	sb.append(scopeNetmask);
	return sb.toString();
}

}
