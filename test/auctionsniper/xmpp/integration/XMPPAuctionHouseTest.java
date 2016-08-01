package auctionsniper.xmpp.integration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import auctionsniper.ApplicationRunner;
import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.BidItem;
import auctionsniper.FakeAuctionServer;
import auctionsniper.xmpp.XMPPAuctionException;
import auctionsniper.xmpp.XMPPAuctionHouse;

public class XMPPAuctionHouseTest {

	private final FakeAuctionServer server
		= new FakeAuctionServer("item-54321"); 
	  
	private XMPPAuctionHouse auctionHouse;
	  
	@Before
	public void
	openConnection() throws XMPPAuctionException {
		
		auctionHouse = XMPPAuctionHouse.connect(
			FakeAuctionServer.XMPP_HOSTNAME,
			ApplicationRunner.SNIPER_ID,
			ApplicationRunner.SNIPER_PASSWORD);
	}
		
	@After
	public void
	closeConnection() {
	
		if (auctionHouse != null) {
			auctionHouse.disconnect();
		}
	}
	  
	@Before
	public void
	startAuction() throws XMPPException {

		server.startSellingItem();
	}
	  
	@After
	public void stopAuction() {
	
		server.stop();
	}
		  
	@Test
	public void 
	receivesEventsFromAuctionServerAfterJoining() throws Exception { 
	  
		CountDownLatch auctionWasClosed = new CountDownLatch(1); 
	    
	    Auction auction = auctionHouse.auctionFor(new BidItem(server.getItemId(), 567));
	    auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
	    
	    auction.join(); 
	    server.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID); 
	    server.announceClosed(); 
	    
	    assertTrue("should have been closed", auctionWasClosed.await(4, SECONDS)); 
	 } 
	  
	 private AuctionEventListener 
	 auctionClosedListener(final CountDownLatch auctionWasClosed) { 
	    
		 return new AuctionEventListener() { 
	     
			 public void auctionClosed() { 
				 
				 auctionWasClosed.countDown(); 
			 } 
			 public void currentPrice(int price, int increment, PriceSource priceSource) {
	    	 }
			 public void auctionFailed() {
			 }
		 }; 
	 }
}
