package integration.auctionsniper.ui;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

import auctionsniper.BidItem;
import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import auctionsniper.ui.MainWindow;
import endtoend.auctionsniper.AuctionSniperDriver;

public class MainWindowTest {

	private final MainWindow mainWindow
		= new MainWindow(new SniperPortfolio());
	
	private final AuctionSniperDriver driver
		= new AuctionSniperDriver(100);
	
	@Test
	public void 
	makesUserRequestWhenJoinButtonClicked() { 
	
		final ValueMatcherProbe<BidItem> itemProbe = 
	      new ValueMatcherProbe<BidItem>(equalTo(new BidItem("the item id", 789)), "item request");
		
	    mainWindow.addUserRequestListener( 
	    	new UserRequestListener() { 
	    		public void joinAuction(BidItem item) { 
	    			itemProbe.setReceivedValue(item); 
	    		} 
	        }); 
	    
	    driver.startBiddingWithStopPrice("the item id", 789);
	    driver.check(itemProbe); 
	}			
}
