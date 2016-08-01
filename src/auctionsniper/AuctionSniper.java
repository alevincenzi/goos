package auctionsniper;

import auctionsniper.util.Announcer;

public class AuctionSniper implements AuctionEventListener{

	private final Announcer<SniperListener> listeners
		= Announcer.to(SniperListener.class);
	  
	private final Auction auction;
	private final BidItem item;

	private SniperSnapshot snapshot;
	  
	public
	AuctionSniper(BidItem item, Auction auction){
	
		this.auction  = auction;
		this.item     = item;
		this.snapshot = SniperSnapshot.joining(item.identifier);
	}
	
	public void
	addSniperListener(SniperListener listener) {
	
		listeners.addListener(listener);
	}
	  
	@Override
	public void
	auctionClosed() {
	
		snapshot = snapshot.closed();
		notifyChange();
	}
	
	@Override
	public void
	auctionFailed() {
	
		snapshot = snapshot.failed(); 
	    notifyChange();
	}

	@Override
	public void
	currentPrice(int price, int increment, PriceSource priceSource) {
		
		switch (priceSource) {
		
		case FromSniper:
			snapshot = snapshot.winning(price);
			break;
		
		case FromOtherBidder:	
			int bid = price + increment;
		     
			if (item.allowsBid(bid)) {
		    
				auction.bid(bid);
		        snapshot = snapshot.bidding(price, bid);
		    } else {
		    
		    	snapshot = snapshot.losing(price);
		    }
			break;
		}

		notifyChange();
	}
	
	public SniperSnapshot
	getSnapshot() {
	
		return snapshot;
	}
	  
	private void
	notifyChange(){
		
		listeners.announce().sniperStateChanged(snapshot);
	}
}
