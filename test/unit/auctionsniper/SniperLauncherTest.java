package unit.auctionsniper;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.Matcher;
import org.hamcrest.FeatureMatcher;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.AuctionSniper;
import auctionsniper.BidItem;
import auctionsniper.SniperCollector;
import auctionsniper.SniperLauncher;

import org.jmock.integration.junit4.JMock;

@RunWith(JMock.class)
public class SniperLauncherTest {
  
	private final Mockery context
  		= new Mockery();
  
	private final States auctionState
		= context.states("auction state").startsAs("not joined");
  
	private final Auction auction
		= context.mock(Auction.class);
  
	private final AuctionHouse auctionHouse
		= context.mock(AuctionHouse.class);
  
	private final SniperCollector sniperCollector
		= context.mock(SniperCollector.class);
  
	private final SniperLauncher launcher
		= new SniperLauncher(auctionHouse, sniperCollector);
  
  
	@Test
	public void
	addsNewSniperToCollectorAndThenJoinsAuction() {
    
		final BidItem item = new BidItem("item 123", 456);

		context.checking(new Expectations() {{
      
			allowing(auctionHouse).auctionFor(item);
				will(returnValue(auction));
      
			oneOf(auction).addAuctionEventListener(
				with(sniperForItem(item)));
				when(auctionState.is("not joined"));
      
			oneOf(sniperCollector).addSniper(with(sniperForItem(item)));
				when(auctionState.is("not joined"));
      
			one(auction).join(); then(auctionState.is("joined"));
		}});
    
		launcher.joinAuction(item);
	}

	protected Matcher<AuctionSniper>
	sniperForItem(BidItem item) {
    
		return new FeatureMatcher<AuctionSniper, String>(
		equalTo(item.identifier), "sniper with item id", "item") {
      
			@Override
			protected String
			featureValueOf(AuctionSniper actual) {
				
				return actual.getSnapshot().itemId;
			}
		};
	}
}
