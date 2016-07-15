package auctionsniper.ui;

public enum Column {
	ITEM_ITENTIFIER,
	LAST_PRICE,
	LAST_BID,
	SNIPER_STATE;
	
	public static Column at(int offset) { return values()[offset]; }
}
