package net.craftstars.general.util;

public enum AccountStatus {
	FROZEN(false),
	INSUFFICIENT(false),
	SUFFICIENT(true),
	BYPASS(true);
	public final boolean canContinue;
	public static double price;
	
	private AccountStatus(boolean b) {
		canContinue = b;
	}
}
