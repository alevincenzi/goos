package auctionsniper;

public class SniperSnapshot {
	public final String      itemId;
	public final int         lastPrice;
	public final int         lastBid;
	public final SniperState state;
	
	public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state){
		this.itemId    = itemId;
		this.lastPrice = lastPrice;
		this.lastBid   = lastBid;
		this.state     = state;
	}
}
