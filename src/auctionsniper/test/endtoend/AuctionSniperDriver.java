package auctionsniper.test.endtoend;

import static org.hamcrest.Matchers.equalTo;

import javax.swing.JFrame;

import org.hamcrest.Matcher;

import com.objogate.wl.Prober;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

public class AuctionSniperDriver extends JFrameDriver {

	public AuctionSniperDriver(int timeoutMilliSeconds) {
		
		super(
				new GesturePerformer(),

				JFrameDriver.topLevelFrame(
						named(MainWindow.MAIN_WINDOW_NAME),
						showingOnScreen()),
				
				new AWTEventQueueProber(timeoutMilliSeconds, 100)
		);
	}
	
	public void showSniperStatus(String statusText){
		new JLabelDriver(
				this, named(MAIN.SNIPER_STATUS_NAME).hasText(equalTo(statusText)));
	}

}
