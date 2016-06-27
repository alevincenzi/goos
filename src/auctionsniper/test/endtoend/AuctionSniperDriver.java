package auctionsniper.test.endtoend;

import static org.hamcrest.Matchers.equalTo;
import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static java.lang.String.valueOf;

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
	
	@SuppressWarnings("unchecked")
	public void showsSniperStatus(
		String itemId,
		int    lastPrice,
		int    lastBid,
		String statusText){
		
		JTableDriver table = new JTableDriver(this);
		
		table.hasRow(matching(
			withLabelText(itemId), withLabelText(valueOf(lastPrice)),
			withLabelText(valueOf(lastBid)), withLabelText(statusText)));
	}

}
