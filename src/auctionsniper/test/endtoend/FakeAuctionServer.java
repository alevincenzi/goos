package auctionsniper.test.endtoend;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class FakeAuctionServer {

	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String XMPP_HOSTNAME    = "vbox";
	public static final String AUCTION_PASSWORD = "auction";
	
	private final SingleMessageListener messageListener = new SingleMessageListener();
	private final String itemId;
	private final XMPPConnection connection;
	private Chat currentChat;
	
	public FakeAuctionServer(String itemId){
		
		this.itemId = itemId;
		this.connection = new XMPPConnection(XMPP_HOSTNAME);
	}
	
	public void startSellingItem() throws XMPPException {
		
		connection.connect();
		connection.login(
			String.format(ITEM_ID_AS_LOGIN, itemId),
			AUCTION_PASSWORD,
			AUCTION_RESOURCE);
		
		connection.getChatManager().addChatListener(
			new ChatManagerListener(){
				public void chatCreated(Chat chat, boolean createdLocally) {
					currentChat = chat;
					chat.addMessageListener(messageListener);
				}
			});
	}
	
	
	public void reportPrice(int price, int increment, String bidder) throws XMPPException {
		
	}
	
	public void hasReceivedBid(int bid, String bidder) throws InterruptedException {
		
	}
	
	public String getItemId(){
		return itemId;
	}
	
	public void hasReceivedJoinRequestFromSniper() throws InterruptedException{
		messageListener.receivesAMessage();
	}
	
	public void announceClosed() throws XMPPException{
		currentChat.sendMessage(new Message());
	}
	
	public void stop(){
		connection.disconnect();
	}
}

class SingleMessageListener implements MessageListener {

	private final ArrayBlockingQueue<Message> messages =
			new ArrayBlockingQueue<Message>(1);
	
	@Override
	public void processMessage(Chat chat, Message message) {
		messages.add(message);
	}

	public void receivesAMessage() throws InterruptedException {
		assertThat(
			"Message", messages.poll(5, TimeUnit.SECONDS), is(notNullValue()));
	}
}
