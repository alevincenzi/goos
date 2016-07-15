package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
	void sniperLost();
	void sniperStateChanged(SniperSnapshot state);
	void sniperWon();
}
