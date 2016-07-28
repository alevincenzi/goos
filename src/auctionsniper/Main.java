package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.SwingThreadSniperListener;
import auctionsniper.xmpp.AuctionMessageTranslator;
import auctionsniper.xmpp.XMPPAuction;

public class Main {

	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: Join;";
	public static final String BID_COMMAND_FORMAT  = "SOLVersion: 1.1; Command: Bid; Price: %d;";
	public static final String CLOSE_EVENT_FORMAT  = "SOLVersion: 1.1; Event: CLOSE;";
	public static final String PRICE_EVENT_FORMAT  = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;"; 
	
	private static final String AUCTION_RESOURCE  = "Auction";
	private static final String ITEM_ID_AS_LOGIN  = "auction-%s";
	private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	
	private static final int ARG_XMPP_HOSTNAME = 0; 
	private static final int ARG_SNIPER_ID     = 1; 
	private static final int ARG_SNIPER_PSWD   = 2; 
	
	private final SnipersTableModel snipers = new SnipersTableModel();
	private MainWindow ui;
	
	private ArrayList<Chat> notToBeGarbageCollected = new ArrayList<Chat>();

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
				
				Chat chat = connection.getChatManager()
						.createChat(auctionId(itemId, connection), null);
				

				notToBeGarbageCollected.add(chat);
				
				Auction auction = new XMPPAuction(chat);

				chat.addMessageListener(
						new AuctionMessageTranslator(
							connection.getUser(),
							new AuctionSniper(
								itemId,
								auction,
								new SwingThreadSniperListener(snipers))));		
					
				auction.join();
			}
		});
	}
	
	private static XMPPConnection
	connectTo(String hostname, String username, String password) throws XMPPException {
	
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
		return connection;
	}
	
	private static String
	auctionId(String itemId, XMPPConnection connection) {
	
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}
}
