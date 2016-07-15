package auctionsniper;

public class SniperSnapshot {
	public final String itemId;
	public final int    lastPrice;
	public final int    lastBid;
	
	public SniperSnapshot(String itemId, int lastPrice, int lastBid){
		this.itemId    = itemId;
		this.lastPrice = lastPrice;
		this.lastBid   = lastBid;
	}
}
