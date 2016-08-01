package auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.util.Announcer;

public class XMPPAuction implements Auction {
	
	public static final String JOIN_COMMAND_FORMAT  = "SOLVersion: 1.1; Command: Join;";
	public static final String BID_COMMAND_FORMAT   = "SOLVersion: 1.1; Command: Bid; Price: %d;";
	public static final String CLOSE_EVENT_FORMAT   = "SOLVersion: 1.1; Event: CLOSE;";
	public static final String PRICE_EVENT_FORMAT   = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;"; 
	public static final String MISSING_EVENT_FORMAT = "SOLVersion: 1.1;               CurrentPrice: %d; Increment: %d; Bidder: %s;";
	
	private final Announcer<AuctionEventListener> auctionEventListeners
		= Announcer.to(AuctionEventListener.class);
			
	private final Chat chat;
	
	private final XMPPFailureReporter failureReporter;
	
	public
	XMPPAuction(
		XMPPConnection connection, String auctionJID, LoggingXMPPFailureReporter failureReporter) {
	
		this.failureReporter = failureReporter;
		AuctionMessageTranslator translator = translatorFor(connection);
		chat = connection.getChatManager().createChat(auctionJID, translator);
		addAuctionEventListener(chatDisconnectorFor(translator));
	}

	@Override
	public void
	addAuctionEventListener(AuctionEventListener auctionEventListener) {
	
		auctionEventListeners.addListener(auctionEventListener);
	}

	private AuctionMessageTranslator
	translatorFor(XMPPConnection connection) {
	
		return new AuctionMessageTranslator(
			connection.getUser(), auctionEventListeners.announce(), failureReporter);
	} 
	
	private AuctionEventListener 
	chatDisconnectorFor(final AuctionMessageTranslator translator) { 
	
		return new AuctionEventListener() { 
			
			public void auctionFailed() { 
				chat.removeMessageListener(translator); 
			}
			public void auctionClosed() {
			}
			public void currentPrice(int price, int increment, PriceSource priceSource) {
			}
	    }; 
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