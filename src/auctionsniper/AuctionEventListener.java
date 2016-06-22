package auctionsniper;

public interface AuctionEventListener {

	enum PriceSource {
		FromSniper,
		FromOtherBidder
	};
	
	public void auctionClosed();

	public void currentPrice(int price, int increment, PriceSource priceSource);
}
