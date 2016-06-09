package auctionsniper.ui;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class Main {

	public static final String MAIN_WINDOW_NAME   = "Auction Sniper Main";
	public static final String SNIPER_STATUS_NAME = "sniper status";
	
	public static final String STATUS_JOINING = "Joining";
	public static final String STATUS_LOST    = "Lost";
	
	private static final String AUCTION_RESOURCE  = "Auction";
	private static final String ITEM_ID_AS_LOGIN  = "auction-%s";
	private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	
	private MainWindow ui;
	
	public static class MainWindow extends JFrame {
		
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
	
	public static void main(
			String xmppHostname,
			String sniperId,
			String sniperPassword,
			String itemId) throws Exception {
		
		Main main = new Main();
		
		XMPPConnection connection = connectTo(xmppHostname, sniperId, sniperPassword);
		
		Chat chat = connection.getChatManager().createChat(
		
				auctionId(itemId, connection),
				new MessageListener() {
					@Override
					public void processMessage(Chat chat, Message message) {
						// nothing yet
					}
				});

		chat.sendMessage(new Message());
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
