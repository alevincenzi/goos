package auctionsniper.test.unit;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;
import auctionsniper.ui.Main;

@RunWith(JMock.class)
public class AuctionMessageTranslatorTest {

	public static final Chat UNUSED_CHAT = null;
	
	private final Mockery context =
			new Mockery();
	private final AuctionEventListener listener =
			context.mock(AuctionEventListener.class);
	private final AuctionMessageTranslator translator =
			new AuctionMessageTranslator(listener);

	@Test
	public void notifyAuctionCLosedWhenCloseMessageReceived(){
		
		context.checking(new Expectations() {{
			oneOf(listener).auctionClosed();
		}});
		
		Message message = new Message();
		message.setBody(Main.CLOSE_EVENT_FORMAT);
		
		translator.processMessage(UNUSED_CHAT, message);
	}
}
