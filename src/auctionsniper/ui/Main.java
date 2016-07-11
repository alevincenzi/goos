package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.SniperState;
import auctionsniper.xmpp.AuctionMessageTranslator;
import auctionsniper.xmpp.XMPPAuction;

public class Main {

	public static final String MAIN_WINDOW_NAME   = "Auction Sniper Main";
	public static final String SNIPER_STATUS_NAME = "sniper status";
	public static final String SNIPERS_TABLE_NAME = "Snipers Table";
	
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
		public static final String STATUS_WINNING = "Winning";
		public static final String STATUS_WON     = "Won";
		
		private final SnipersTableModel snipers = new SnipersTableModel();

		private static final long serialVersionUID = 1L;

		public MainWindow() {
			super("Auction Sniper");
			setName(MAIN_WINDOW_NAME);
			fillContentPane(makeSnipersTable());
			pack();
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
		}
		
		private void fillContentPane(JTable snipersTable){
			final Container contentPane = getContentPane();
			contentPane.setLayout(new BorderLayout());
			
			contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
		}
		
		private JTable makeSnipersTable(){
			final JTable snipersTable = new JTable(snipers);
			snipersTable.setName(SNIPERS_TABLE_NAME);
			return snipersTable;
		}
		
		public void showStatusText(String status) {
			snipers.setStatusText(status);
		}
		
		public void sniperStatusChanged(SniperState state, String statusText){
			snipers.sniperStatusChanged(state, statusText);
		}
	}

	public static class SnipersTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;

		private final static SniperState STARTING_UP = new SniperState("", 0, 0);
		
		private String statusText = MainWindow.STATUS_JOINING;
		private SniperState sniperState = STARTING_UP;
		
		@Override
		public int getRowCount() {
			return 1;
		}

		@Override
		public int getColumnCount() {
			return Column.values().length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			
			switch(Column.at(columnIndex)){
			case ITEM_ITENTIFIER:
				return sniperState.itemId;
			case LAST_BID:
				return sniperState.lastBid;
			case LAST_PRICE:
				return sniperState.lastPrice;
			case SNIPER_STATUS:
				return statusText;
			default:
				throw new IllegalArgumentException("No column at " + columnIndex);
			}
		}

		public void sniperStatusChanged(SniperState newSniperState, String newStatusText){
			sniperState = newSniperState;
			statusText = newStatusText;
			fireTableRowsUpdated(0, 0);
		}
		
		public void setStatusText(String statusText) {
			this.statusText = statusText;
			fireTableRowsUpdated(0, 0);
		}
	}

	public class SniperStateDisplayer implements SniperListener {

		@Override
		public void sniperLost() {
			showStatus(MainWindow.STATUS_LOST);
		}

		@Override
		public void sniperBidding(final SniperState state) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run(){
					ui.sniperStatusChanged(state, MainWindow.STATUS_BIDDING);
				}
			});
		}
		
		@Override
		public void sniperWinning() {
			showStatus(MainWindow.STATUS_WINNING);
		}
		
		@Override
		public void sniperWon() {
			showStatus(MainWindow.STATUS_WON);
		}
		
		private void showStatus(final String status){
			SwingUtilities.invokeLater(new Runnable() {
				public void run(){
					ui.showStatusText(status);
				}
			});			
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
			auctionId(itemId, connection), null);

		notToBeGarbageCollected = chat;
		
		Auction auction = new XMPPAuction(chat);
		
		chat.addMessageListener(
			new AuctionMessageTranslator(
				connection.getUser(),
				new AuctionSniper(
					itemId,
					auction,
					new SniperStateDisplayer())));		
		
		auction.join();
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
}
