package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.SwingThreadSniperListener;
import auctionsniper.xmpp.XMPPAuction;

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
		
		XMPPConnection connection =
				connectTo(args[ARG_XMPP_HOSTNAME], args[ARG_SNIPER_ID], args[ARG_SNIPER_PSWD]);
		
		main.disconnectWhenUICloses(connection);
		main.addUserRequestListenerFor(connection);
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
	disconnectWhenUICloses(final XMPPConnection connection) {
	
		ui.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e) {
				connection.disconnect();
			}
		});
	}
	
	private void addUserRequestListenerFor(XMPPConnection connection) {
	
		ui.addUserRequestListener(new UserRequestListener() {
			
			@Override
			public void joinAuction(String itemId) {
			
				snipers.addSniper(SniperSnapshot.joining(itemId));
				
				Auction auction = new XMPPAuction(connection, itemId);

				notToBeGarbageCollected.add(auction);
				
				auction.addAuctionEventListener(
					new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers)));		
					
				auction.join();
			}
		});
	}
	
	public static XMPPConnection
	connectTo(String hostname, String username, String password) throws XMPPException {
	
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, XMPPAuction.AUCTION_RESOURCE);
		return connection;
	}
}
