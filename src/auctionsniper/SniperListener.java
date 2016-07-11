package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
	void sniperLost();
	void sniperBidding(SniperState state);
	void sniperWinning();
	void sniperWon();
}
