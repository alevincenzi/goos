package auctionsniper.test.unit;

import static auctionsniper.SniperState.BIDDING;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
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
			new AuctionSniper(ITEM_ID, auction, sniperListener);
	
	private final States sniperState =	
			context.states("sniper");
	
	
	@SuppressWarnings("deprecation")
	@Test
	public void reportLostIfAuctionClosesImmeadiately(){
		
		context.checking(new Expectations() {{
			one(sniperListener).sniperLost();
		}});
		
		sniper.auctionClosed();
	}
	
	public void reportLostIfAuctionClosesWhenBidding(){
		context.checking(new Expectations(){{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(
				with(aSniperThatIs(BIDDING)));
				then(sniperState.is("bidding"));
				
			atLeast(1).of(sniperListener).sniperLost();
				when(sniperState.is("bidding"));
		}});
		
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
	}
	
	private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state){
		return new FeatureMatcher<SniperSnapshot, SniperState>(
				equalTo(state), "sniper that is ", "was")
		{
			@Override
			protected SniperState featureValueOf(SniperSnapshot actual) {
				return actual.state;
			}
		};
	}
	
	@Test
	public void reportWonIfAuctionClosesWhenWinning(){
		context.checking(new Expectations(){{
			ignoring(auction);
			allowing(sniperListener).sniperWinning();
				then(sniperState.is("winning"));
				
			atLeast(1).of(sniperListener).sniperWon();
				when(sniperState.is("winning"));
		}});
		
		sniper.currentPrice(123, 45, PriceSource.FromSniper);
		sniper.auctionClosed();
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void bidsHigherAndReportsBiddingWhenNewPriceArrives(){
		final int price = 1001;
		final int increment = 25;
		final int bid = price + increment;
		
		context.checking(new Expectations(){{
			one(auction).bid(bid);
			atLeast(1).of(sniperListener).sniperStateChanged(
					new SniperSnapshot(ITEM_ID, price, bid, BIDDING));
		}});
		
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
	}
	
	@Test
	public void reportsIsWinningWhenCurrentPriceComesFromSniper(){
		
		context.checking(new Expectations() {{
			atLeast(1).of(sniperListener).sniperWinning();
		}});
		
		sniper.currentPrice(123, 45, PriceSource.FromSniper);	
	}
}
