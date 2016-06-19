package auctionsniper.ui;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.AuctionMessageTranslator;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;

public class Main implements SniperListener {

	public static final String MAIN_WINDOW_NAME   = "Auction Sniper Main";
	public static final String SNIPER_STATUS_NAME = "sniper status";
	
	public static final String JOIN_COMMAND_FORMAT = "";
	public static final String BID_COMMAND_FORMAT  = "SOLVersion 1.1; Command: Bid; Price: %d;";

	public static final String CLOSE_EVENT_FORMAT  = "SOLVersion: 1.1; Event: CLOSE;";
	public static final String PRICE_EVENT_FORMAT  = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;"; 
	
	private static final String AUCTION_RESOURCE  = "Auction";
	private static final String ITEM_ID_AS_LOGIN  = "auction-%s";
	private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	
	private static final int ARG_XMPP_HOSTNAME = 0; 
	private static final int ARG_SNIPER_ID     = 1; 
	private static final int ARG_SNIPER_PSWD   = 2; 
	private static final int ARG_ITEM_ID       = 3; 
	
	private MainWindow ui;
	
	@SuppressWarnings("unused")
	private Chat notToBeGarbageCollected;

	public static class MainWindow extends JFrame {
		
		public static final String STATUS_JOINING = "Joining";
		public static final String STATUS_LOST    = "Lost";
		public static final String STATUS_BIDDING = "Bidding";
		
		private final JLabel sniperStatus = createLabel(STATUS_JOINING);

		private static final long serialVersionUID = 1L;

		public MainWindow() {
			super("Auction Sniper");
			setName(MAIN_WINDOW_NAME);
			add(sniperStatus);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
		}
		
		private static JLabel createLabel(String initialText){
			JLabel result = new JLabel(initialText);
			result.setName(SNIPER_STATUS_NAME);
			result.setBorder(new LineBorder(Color.BLACK));
			return result;			
		}
		
		public void showStatus(String status) {
			sniperStatus.setText(status);
		}
	}

	
	public Main() throws Exception {
		startUserInterface();
	}
	
	private void startUserInterface() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				ui = new MainWindow();
			}
		});
	}
	
	public static void main(String ... args) throws Exception {
		
		Main main = new Main();
		
		main.joinAuction(connectTo(
			args[ARG_XMPP_HOSTNAME],
			args[ARG_SNIPER_ID],
			args[ARG_SNIPER_PSWD]),
			args[ARG_ITEM_ID]);
	}
	
	private void
	joinAuction(final XMPPConnection connection, String itemId) throws XMPPException {

		disconnectWhenUICloses(connection);

		Chat chat = connection.getChatManager().createChat(
			auctionId(itemId, connection),
			new AuctionMessageTranslator(new AuctionSniper(this)));

		notToBeGarbageCollected = chat;
		
		chat.sendMessage(JOIN_COMMAND_FORMAT);
	}
	
	private void disconnectWhenUICloses(final XMPPConnection connection) {
		ui.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e) {
				connection.disconnect();
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
	
	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}

	@Override
	public void sniperLost() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				ui.showStatus(MainWindow.STATUS_LOST);
			}
		});
	}
}
