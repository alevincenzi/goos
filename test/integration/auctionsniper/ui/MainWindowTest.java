package integration.auctionsniper.ui;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

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
	
		final ValueMatcherProbe<String> buttonProbe = 
	      new ValueMatcherProbe<String>(equalTo("the item id"), "join request");
		
	    mainWindow.addUserRequestListener( 
	    	new UserRequestListener() { 
	    		public void joinAuction(String itemId) { 
	    			buttonProbe.setReceivedValue(itemId); 
	    		} 
	        }); 
	    
	    driver.startBiddingFor("the item id");
	    driver.check(buttonProbe); 
	}			
}
