package unit.auctionsniper;

import static auctionsniper.SniperState.BIDDING;
import static auctionsniper.SniperState.WINNING;
import static auctionsniper.SniperState.LOST;
import static auctionsniper.SniperState.WON;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.AuctionEventListener.PriceSource;

@RunWith(JMock.class)
public class AuctionSniperTest {

	protected static final String ITEM_ID = "item-id";
	
	private final Mockery context =
			new Mockery();
	
	private final Auction auction =
			context.mock(Auction.class);
	
	private final SniperListener sniperListener =
			context.mock(SniperListener.class);
	
	private final AuctionSniper sniper =
			new AuctionSniper(ITEM_ID, auction);
	
	private final States sniperState =	
			context.states("sniper");
	
	@Before
	public void
	attachListener() {
	
		sniper.addSniperListener(sniperListener);
	}
	  
	@Test
	public void
	reportsLostWhenAuctionClosesImmediately(){
		
		context.checking(new Expectations() {{
			atLeast(1).of(sniperListener).sniperStateChanged(
				new SniperSnapshot(ITEM_ID, 0, 0, LOST));
		}});
		
		sniper.auctionClosed();
	}
	
	@Test
	public void
	reportsLostIfAuctionClosesWhenBidding(){

	    allowingSniperBidding();
	    ignoringAuction();
	    
		context.checking(new Expectations(){{
			ignoring(auction);
			atLeast(1).of(sniperListener).sniperStateChanged(
					new SniperSnapshot(ITEM_ID, 123, 168, LOST)); 
            when(sniperState.is("bidding"));
		}});
		
	    sniper.currentPrice(123, 45, PriceSource.FromOtherBidder); 
	    sniper.auctionClosed(); 
	}
	
	@Test
	public void
	reportsWonIfAuctionClosesWhenWinning(){
	    
		allowingSniperBidding();
	    allowingSniperWinning();
	    ignoringAuction();
	    
		context.checking(new Expectations(){{
			ignoring(auction);
			atLeast(1).of(sniperListener).sniperStateChanged(
					new SniperSnapshot(ITEM_ID, 135, 135, WON));
			when(sniperState.is("winning"));
		}});
		
	    sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
	    sniper.currentPrice(135, 45, PriceSource.FromSniper); 
	    sniper.auctionClosed(); 
	}
	
	@Test
	public void
	bidsHigherAndReportsBiddingWhenNewPriceArrives(){
	    
		final int price = 1001; 
	    final int increment = 25; 
	    final int bid = price + increment;
	    
	    context.checking(new Expectations() {{ 
	      one(auction).bid(bid); 
	      
	      atLeast(1).of(sniperListener).sniperStateChanged(
	    		  new SniperSnapshot(ITEM_ID, price, bid, BIDDING)); 
	    }}); 
	    
	    sniper.currentPrice(price, increment, PriceSource.FromOtherBidder); 
	}
	
	@Test
	public void
	reportsIsWinningWhenCurrentPriceComesFromSniper(){
		
	    allowingSniperBidding();
	    ignoringAuction();
	    
	    context.checking(new Expectations() {{ 
	        atLeast(1).of(sniperListener).sniperStateChanged(
	        		new SniperSnapshot(ITEM_ID, 135, 135, WINNING));
	        when(sniperState.is("bidding"));
	    }}); 
	      
	    sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
	    sniper.currentPrice(135, 45, PriceSource.FromSniper); 	
	}
	
	private void
	ignoringAuction() {
	
		context.checking(new Expectations() {{ 
			ignoring(auction);
		}});
	}
	
	private void
	allowingSniperBidding() {
	
		allowSniperStateChange(BIDDING, "bidding");
	}

	private void
	allowingSniperWinning() {
	
		allowSniperStateChange(WINNING, "winning");
	}

	private void
	allowSniperStateChange(final SniperState newState, final String oldState) {
	
		context.checking(new Expectations() {{ 
			allowing(sniperListener).sniperStateChanged(
					with(aSniperThatIs(newState)));
			then(sniperState.is(oldState));
		 }});
	}
		  
	private Matcher<SniperSnapshot>
	aSniperThatIs(final SniperState state){
		
		return new FeatureMatcher<SniperSnapshot, SniperState>(
				equalTo(state), "sniper that is ", "was")
		{
			@Override
			protected SniperState featureValueOf(SniperSnapshot actual) {
				return actual.state;
			}
		};
	}
}
