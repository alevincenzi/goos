package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
	void sniperLost();
	void sniperBidding(SniperSnapshot state);
	void sniperWinning();
	void sniperWon();
}
