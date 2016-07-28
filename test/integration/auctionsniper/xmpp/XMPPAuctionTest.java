package integration.auctionsniper.xmpp;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.Main;
import auctionsniper.xmpp.XMPPAuction;
import endtoend.auctionsniper.ApplicationRunner;
import endtoend.auctionsniper.FakeAuctionServer;

public class XMPPAuctionTest {

	private final FakeAuctionServer server
		= new FakeAuctionServer("item-54321"); 
	  
	private XMPPConnection connection;
	  
	@Before
	public void
	openConnection() throws XMPPException {
		
		connection = Main.connectTo(
				FakeAuctionServer.XMPP_HOSTNAME,
				ApplicationRunner.SNIPER_ID,
				ApplicationRunner.SNIPER_PASSWORD);
	}
		
	@After
	public void
	closeConnection() {
	
		if (connection != null) {
			connection.disconnect();
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
	    
	    Auction auction = new XMPPAuction(connection, server.getItemId());
	    auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
	    
	    auction.join(); 
	    server.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID); 
	    server.announceClosed(); 
	    
	    assertTrue("should have been closed", auctionWasClosed.await(2, SECONDS)); 
	 } 
	  
	 private AuctionEventListener 
	 auctionClosedListener(final CountDownLatch auctionWasClosed) { 
	    
		 return new AuctionEventListener() { 
	     
			 public void auctionClosed() { 
				 
				 auctionWasClosed.countDown(); 
			 } 
			 public void currentPrice(int price, int increment, PriceSource priceSource) {
	    	 }
		 }; 
	 }
}
