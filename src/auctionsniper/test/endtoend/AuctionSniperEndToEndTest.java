package auctionsniper.test.endtoend;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

public class AuctionSniperEndToEndTest {

	private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
	private final ApplicationRunner application = new ApplicationRunner();
	
	@Test
	public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
		
		auction.startSellingItem();
		
		application.startBiddingIn(auction);
		
		auction.hasReceivedJoinRequestFromSniper();
		auction.announceClosed();
		
		application.showsSniperHasLostAuction();
	}
	
	@After
	public void stopAcution() {
		auction.stop();
	}
	
	@After
	public void stopApplication() {
		application.stop();
	}

}
