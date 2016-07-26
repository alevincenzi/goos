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
		
		Thread thread = new Thread("Test application") {
			
			@Override
			public void run(){
				
				try {
					Main.main(arguments(auctions));
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

		for (FakeAuctionServer auction : auctions) {
			driver.showsSniperStatus(
				auction.getItemId(), 0, 0, textFor(SniperState.JOINING));
		}
	}
	
	protected static String[]
	arguments(FakeAuctionServer... auctions) {
		
		String[] arguments = new String[auctions.length  + 3];
		
		arguments[0] = FakeAuctionServer.XMPP_HOSTNAME;
		arguments[1] = SNIPER_ID;
		arguments[2] = SNIPER_PASSWORD;
		
		for (int i = 0; i < auctions.length; i++) {
			arguments[i + 3] = auctions[i].getItemId();
		}
		
		return arguments;
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
	
	public void
	hasShownSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
	
		driver.showsSniperStatus(
			auction.getItemId(), lastPrice, lastPrice, textFor(SniperState.WON));
	}
	
	
	public void
	stop(){
	
		if (driver != null)
			driver.dispose();
	}
}
