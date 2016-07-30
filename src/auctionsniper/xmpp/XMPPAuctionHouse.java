package auctionsniper.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;

public class XMPPAuctionHouse implements AuctionHouse {

	private static final String AUCTION_RESOURCE  = "Auction";
	private static final String ITEM_ID_AS_LOGIN  = "auction-%s";
	private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	
	private final XMPPConnection connection;
	  
	public
	XMPPAuctionHouse(XMPPConnection connection) throws XMPPAuctionException {
	
		this.connection = connection;
	}
	  
	@Override
	public Auction auctionFor(String itemId) {

		return new XMPPAuction(connection, auctionId(itemId, connection));
	}

	private static String
	auctionId(String itemId, XMPPConnection connection){
		
		return String.format(AUCTION_ID_FORMAT,  itemId, connection.getServiceName());
	}
	
	public void disconnect() {
	
		connection.disconnect();
	}
	  
	public static XMPPAuctionHouse
	connect(String hostname, String username, String password) throws XMPPAuctionException {
	
		XMPPConnection connection = new XMPPConnection(hostname); 
		    
		try {
			connection.connect(); 
		    connection.login(username, password, AUCTION_RESOURCE); 
		    
		    return new XMPPAuctionHouse(connection);
		
		} catch (XMPPException xmppe) {
			throw new XMPPAuctionException("Could not connect to auction: " + connection, xmppe);
		}
	}
}
