

package org.xbill.DNS.utils;

import java.util.Arrays;
import java.security.*;



public class HMAC {

private MessageDigest digest;
private int blockLength;

private byte [] ipad, opad;

private static final byte IPAD = 0x36;
private static final byte OPAD = 0x5c;

private void
init(byte [] key) {
	int i;

	if (key.length > blockLength) {
		key = digest.digest(key);
		digest.reset();
	}
	ipad = new byte[blockLength];
	opad = new byte[blockLength];
	for (i = 0; i < key.length; i++) {
		ipad[i] = (byte) (key[i] ^ IPAD);
		opad[i] = (byte) (key[i] ^ OPAD);
	}
	for (; i < blockLength; i++) {
		ipad[i] = IPAD;
		opad[i] = OPAD;
	}
	digest.update(ipad);
}


public
HMAC(MessageDigest digest, int blockLength, byte [] key) {
	digest.reset();
	this.digest = digest;
  	this.blockLength = blockLength;
	init(key);
}


public
HMAC(String digestName, int blockLength, byte [] key) {
	try {
		digest = MessageDigest.getInstance(digestName);
	} catch (NoSuchAlgorithmException e) {
		throw new IllegalArgumentException("unknown digest algorithm "
						   + digestName);
	}
	this.blockLength = blockLength;
	init(key);
}


public
HMAC(MessageDigest digest, byte [] key) {
	this(digest, 64, key);
}


public
HMAC(String digestName, byte [] key) {
	this(digestName, 64, key);
}

public void
update(byte [] b, int offset, int length) {
	digest.update(b, offset, length);
}


public void
update(byte [] b) {
	digest.update(b);
}


public byte []
sign() {
	byte [] output = digest.digest();
	digest.reset();
	digest.update(opad);
	return digest.digest(output);
}


public boolean
verify(byte [] signature) {
	return verify(signature, false);
}


public boolean
verify(byte [] signature, boolean truncation_ok) {
	byte [] expected = sign();
	if (truncation_ok && signature.length < expected.length) {
		byte [] truncated = new byte[signature.length];
		System.arraycopy(expected, 0, truncated, 0, truncated.length);
		expected = truncated;
	}
	return Arrays.equals(signature, expected);
}


public void
clear() {
	digest.reset();
	digest.update(ipad);
}

public int
digestLength() {
	return digest.getDigestLength();
}

}
