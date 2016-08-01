package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import auctionsniper.ui.MainWindow;
import auctionsniper.xmpp.XMPPAuctionHouse;

public class Main {

	private static final int ARG_XMPP_HOSTNAME = 0; 
	private static final int ARG_SNIPER_ID     = 1; 
	private static final int ARG_SNIPER_PSWD   = 2; 
	
	private final SniperPortfolio portfolio = new SniperPortfolio();

	private MainWindow ui;
	
	public
	Main() throws Exception {
	
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				ui = new MainWindow(portfolio);
			}
		});
	}
	
	public static void
	main(String ... args) throws Exception {
		
		Main main = new Main();
		
		XMPPAuctionHouse auctionHouse =
				XMPPAuctionHouse.connect(args[ARG_XMPP_HOSTNAME], args[ARG_SNIPER_ID], args[ARG_SNIPER_PSWD]);
		
		main.disconnectWhenUICloses(auctionHouse);
		main.addUserRequestListenerFor(auctionHouse);
	}

	private void
	disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse) {
	
		ui.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e) {
				auctionHouse.disconnect();
			}
		});
	}
	
	private void
	addUserRequestListenerFor(final XMPPAuctionHouse auctionHouse) {
		
		ui.addUserRequestListener(
			new SniperLauncher(auctionHouse, portfolio));
	}
}
