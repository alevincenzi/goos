package endtoend.auctionsniper;


import static auctionsniper.SniperState.textFor;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;

public class ApplicationRunner {
	
	public static final String SNIPER_ID       = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID  = "sniper@vbox/Auction";
	
	private AuctionSniperDriver driver;

	public void
	startBiddingIn(final FakeAuctionServer... auctions){

	    startSniper(); 
	    for (FakeAuctionServer auction : auctions) {
	      openBiddingFor(auction, Integer.MAX_VALUE);
	    }
	}
			
	public void
	startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
	    startSniper();
	    openBiddingFor(auction, stopPrice);
	}

	private void
	startSniper() {
		
		Thread thread = new Thread("Test application") {
			
			@Override
			public void run(){
				
				try {
					Main.main(FakeAuctionServer.XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		
		thread.setDaemon(true);
		thread.start();
		
		driver = new AuctionSniperDriver(1000);
		driver.hasTitle(MainWindow.APPLICATION_TITLE);
		driver.hasColumnTitles();
	}

	private void
	openBiddingFor(FakeAuctionServer auction, int stopPrice) {
	
		final String itemId = auction.getItemId();
		driver.startBiddingWithStopPrice(itemId, stopPrice);
		driver.showsSniperStatus(itemId, 0, 0, textFor(SniperState.JOINING));
	}
	  
	public void
	stop(){
	
		if (driver != null)
			driver.dispose();
	}
	
	public void
	hasShownSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
		
	    driver.showsSniperStatus(
	    	auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOST));
	}
	
	public void
	hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
	
		driver.showsSniperStatus(
			auction.getItemId(), lastPrice, lastBid, textFor(SniperState.BIDDING));
	}

	public void
	hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
	
		driver.showsSniperStatus(
			auction.getItemId(), winningBid, winningBid, textFor(SniperState.WINNING));
	}
	
	public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
	
		driver.showsSniperStatus(
			auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOSING));
	}
	  
	public void
	hasShownSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
	
		driver.showsSniperStatus(
			auction.getItemId(), lastPrice, lastPrice, textFor(SniperState.WON));
	}
}
