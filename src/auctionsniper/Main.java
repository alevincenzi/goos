package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.SwingThreadSniperListener;
import auctionsniper.xmpp.XMPPAuctionHouse;

public class Main {

	private static final int ARG_XMPP_HOSTNAME = 0; 
	private static final int ARG_SNIPER_ID     = 1; 
	private static final int ARG_SNIPER_PSWD   = 2; 
	
	private final SnipersTableModel snipers = new SnipersTableModel();
	private MainWindow ui;
	
	private ArrayList<Auction> notToBeGarbageCollected = new ArrayList<Auction>();

	public
	Main() throws Exception {
		
		startUserInterface();
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
	startUserInterface() throws Exception{
	
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				ui = new MainWindow(snipers);
			}
		});
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
	
	private void addUserRequestListenerFor(final XMPPAuctionHouse auctionHouse) {
	
		ui.addUserRequestListener(new UserRequestListener() {
			
			@Override
			public void joinAuction(String itemId) {
			
				snipers.addSniper(SniperSnapshot.joining(itemId));
				
				Auction auction = auctionHouse.auctionFor(itemId);

				notToBeGarbageCollected.add(auction);
				
				auction.addAuctionEventListener(
					new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers)));		
					
				auction.join();
			}
		});
	}
}
