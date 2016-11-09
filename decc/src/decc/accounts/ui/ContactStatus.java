package decc.accounts.ui;

/* Public key verification status
 */
public enum ContactStatus {
	/** The public is not verified by the DHT
	 */
	UNVERIFIED,
	/**
	 * The public key is under DHT verification
	 */
	VERIFICATION,
	/**
	 * The public key have been verified by the DHT
	 */
	VERIFIED,
	/**
	 * The public key is different of the public key of the DHT
	 */
	INVALID,
	/**
	 * No DHT verification needed, this key is safe
	 * To use the an account only
	 */
	TRUSTED
}
