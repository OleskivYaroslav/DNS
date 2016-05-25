
package org.xbill.DNS;



public class AFSDBRecord extends U16NameBase {

private static final long serialVersionUID = 3034379930729102437L;

AFSDBRecord() {}

Record
getObject() {
	return new AFSDBRecord();
}


public
AFSDBRecord(Name name, int dclass, long ttl, int subtype, Name host) {
	super(name, Type.AFSDB, dclass, ttl, subtype, "subtype", host, "host");
}

/** Gets the subtype indicating the service provided by the host. */
public int
getSubtype() {
	return getU16Field();
}

/** Gets the host providing service for the domain. */
public Name
getHost() {
	return getNameField();
}

}
