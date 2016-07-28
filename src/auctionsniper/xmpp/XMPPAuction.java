package auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.util.Announcer;

public class XMPPAuction implements Auction {
	
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: Join;";
	public static final String BID_COMMAND_FORMAT  = "SOLVersion: 1.1; Command: Bid; Price: %d;";
	public static final String CLOSE_EVENT_FORMAT  = "SOLVersion: 1.1; Event: CLOSE;";
	public static final String PRICE_EVENT_FORMAT  = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;"; 
	
	private final Announcer<AuctionEventListener> auctionEventListeners
		= Announcer.to(AuctionEventListener.class);
			
	private final Chat chat;
	
	public
	XMPPAuction(XMPPConnection connection, String itemId) {
	
		chat = connection.getChatManager().createChat(
				auctionId(itemId, connection),
				new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce()));
	}

	private static String
	auctionId(String itemId, XMPPConnection connection){
		
		return String.format(XMPPAuctionHouse.AUCTION_ID_FORMAT,  itemId, connection.getServiceName());
	}
	
	@Override
	public void
	addAuctionEventListener(AuctionEventListener auctionEventListener) {
	
		auctionEventListeners.addListener(auctionEventListener);
	}

	@Override
	public void
	bid(int amount) {
	
		sendMessage(String.format(BID_COMMAND_FORMAT, amount));
	}

	@Override
	public void
	join() {
	
		sendMessage(JOIN_COMMAND_FORMAT);
	}
	
	private void
	sendMessage(final String message) {
	
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
};