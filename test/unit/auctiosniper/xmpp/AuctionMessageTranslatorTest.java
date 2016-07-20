package unit.auctiosniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.AuctionEventListener;
import auctionsniper.Main;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.xmpp.AuctionMessageTranslator;
import endtoend.auctionsniper.ApplicationRunner;

@RunWith(JMock.class)
public class AuctionMessageTranslatorTest {

	public static final Chat UNUSED_CHAT = null;
	
	private final Mockery context =
			new Mockery();
	private final AuctionEventListener listener =
			context.mock(AuctionEventListener.class);
	private final AuctionMessageTranslator translator =
			new AuctionMessageTranslator(ApplicationRunner.SNIPER_ID, listener);

	@Test
	public void notifyAuctionClosedWhenCloseMessageReceived(){
		
		context.checking(new Expectations() {{
			oneOf(listener).auctionClosed();
		}});
		
		Message message = new Message();
		message.setBody(Main.CLOSE_EVENT_FORMAT);
		
		translator.processMessage(UNUSED_CHAT, message);
	}
	
	 @Test
	 public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
		 
		 context.checking(new Expectations() {{
			 exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
		 }});
		 
		 Message message = new Message();
		 message.setBody(String.format(Main.PRICE_EVENT_FORMAT, 192, 7, "Somebody else"));
			
		 translator.processMessage(UNUSED_CHAT, message);
	 }
	 
	 @Test
	 public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
		 
		 context.checking(new Expectations() {{
			 exactly(1).of(listener).currentPrice(234, 5, PriceSource.FromSniper);
		 }});
		 
		 Message message = new Message();
		 message.setBody(String.format(Main.PRICE_EVENT_FORMAT, 234, 5, ApplicationRunner.SNIPER_ID));
			
		 translator.processMessage(UNUSED_CHAT, message);
	 }
}
