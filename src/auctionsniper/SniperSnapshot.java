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
	
	public SniperSnapshot bidding(int newLastPrice, int newLastBid){
		return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING); 
	}
	
	public SniperSnapshot winning(int newLastPrice){
		return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.WINNING); 
	}
	
	public static SniperSnapshot joining(String itemId){
		return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING); 
	}
	
	public SniperSnapshot closed(){
		return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed()); 
	}
}
