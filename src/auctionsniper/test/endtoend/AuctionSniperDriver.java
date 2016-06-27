package auctionsniper.test.endtoend;

import static org.hamcrest.Matchers.equalTo;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import static auctionsniper.ui.Main.*;

public class AuctionSniperDriver extends JFrameDriver {

	@SuppressWarnings("unchecked")
	public AuctionSniperDriver(int timeoutMilliSeconds) {
		
		super(
				new GesturePerformer(),

				JFrameDriver.topLevelFrame(
						named(MAIN_WINDOW_NAME),
						showingOnScreen()),
				
				new AWTEventQueueProber(timeoutMilliSeconds, 100)
		);
	}
	
	@SuppressWarnings("unchecked")
	public void showsSniperStatus(String statusText){
		new JTableDriver(this).hasCell(withLabelText(equalTo(statusText)));
	}

}
