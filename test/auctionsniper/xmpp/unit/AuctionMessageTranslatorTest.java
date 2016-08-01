package auctionsniper.xmpp.unit;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.ApplicationRunner;
import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.xmpp.AuctionMessageTranslator;
import auctionsniper.xmpp.XMPPAuction;
import auctionsniper.xmpp.XMPPFailureReporter;

@RunWith(JMock.class)
public class AuctionMessageTranslatorTest {

	public static final Chat UNUSED_CHAT = null;
	
	private final Mockery context
		= new Mockery();
	
	private final XMPPFailureReporter failureReporter
		= context.mock(XMPPFailureReporter.class);
	  
	private final AuctionEventListener listener
		= context.mock(AuctionEventListener.class);
	
	private final AuctionMessageTranslator translator
		= new AuctionMessageTranslator(ApplicationRunner.SNIPER_ID, listener, failureReporter);

	@Test
	public void
	notifyAuctionClosedWhenCloseMessageReceived(){
		
		context.checking(new Expectations() {{
			exactly(1).of(listener).auctionClosed();
		}});
		
		Message message = new Message();
		message.setBody(XMPPAuction.CLOSE_EVENT_FORMAT);
		
		translator.processMessage(UNUSED_CHAT, message);
	}
	
	@Test
	public void
	notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
	 
		context.checking(new Expectations() {{
			exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
		}});
		 
		Message message = new Message();
		message.setBody(String.format(XMPPAuction.PRICE_EVENT_FORMAT, 192, 7, "Somebody else"));
			
		translator.processMessage(UNUSED_CHAT, message);
	 }
	 
	 @Test
	 public void
	 notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
		 
		context.checking(new Expectations() {{
			exactly(1).of(listener).currentPrice(234, 5, PriceSource.FromSniper);
		}});
		 
		Message message = new Message();
		message.setBody(String.format(XMPPAuction.PRICE_EVENT_FORMAT, 234, 5, ApplicationRunner.SNIPER_ID));
			
		translator.processMessage(UNUSED_CHAT, message);
	 }
	 
	 @Test
	 public void 
	 notifiesAuctionFailedWhenBadMessageReceived() { 
	 
		 String badMessage = "a bad message";
		 expectFailureWithMessage(badMessage);

		 translator.processMessage(UNUSED_CHAT, message(badMessage));  
	 }
	 
	 @Test
	 public void 
	 notifiesAuctionFailedWhenEventTypeMissing() { 
	 
		 context.checking(new Expectations() {{
			 exactly(1).of(listener).auctionFailed();
		 }});

		Message message = new Message();
		message.setBody(String.format(XMPPAuction.MISSING_EVENT_FORMAT, 234, 5, ApplicationRunner.SNIPER_ID));
		 
		translator.processMessage(UNUSED_CHAT, message); 
	 }
	 
	 private Message
	 message(String body) { 
	
		 Message message = new Message(); 
		 message.setBody(body); 
		 return message; 
	} 
		  
	private void
	expectFailureWithMessage(final String badMessage) { 
	
		context.checking(new Expectations() {{  
		
			oneOf(listener).auctionFailed(); 
		    oneOf(failureReporter).cannotTranslateMessage(
		    		with(ApplicationRunner.SNIPER_ID), with(badMessage), with(any(Exception.class))); 
		}}); 
	} 
}
