package fr.aliasource.webmail.client.rpc;

/**
 * This exception is thrown to make an ajax call fail when we when want the
 * frontend to not do anything.
 * 
 * @author tom
 * 
 */
public class UseCachedData extends RuntimeException {

	private static final long serialVersionUID = 5568601060462425522L;

	public static final String MSG = "304";

	public UseCachedData() {
		super(MSG);
	}

}
