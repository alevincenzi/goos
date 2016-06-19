package auctionsniper.test.unit;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;

@RunWith(JMock.class)
public class AuctionSniperTest {

	private final Mockery context =
			new Mockery();
	
	private final Auction auction =
			context.mock(Auction.class);
	
	private final SniperListener sniperListener =
			context.mock(SniperListener.class);
	
	private final AuctionSniper sniper =
			new AuctionSniper(auction, sniperListener);
	
	@SuppressWarnings("deprecation")
	@Test
	public void reportLostWhenAuctionCloses(){
		
		context.checking(new Expectations() {{
			one(sniperListener).sniperLost();
		}});
		
		sniper.auctionClosed();
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void bidsHigherAndReportsBiddingWhenNewPriceArrives(){
		final int price = 1001;
		final int increment = 25;
		
		context.checking(new Expectations(){{
			one(auction).bid(price + increment);
			atLeast(1).of(sniperListener).sniperBidding();
		}});
		
		sniper.currentPrice(price, increment);
	}
}
