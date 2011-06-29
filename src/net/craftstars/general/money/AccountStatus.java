package net.craftstars.general.money;

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
