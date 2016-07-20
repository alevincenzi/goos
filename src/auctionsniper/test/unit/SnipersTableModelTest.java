package auctionsniper.test.unit;

import static auctionsniper.SniperState.BIDDING;
import static auctionsniper.ui.SnipersTableModel.textFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.SniperSnapshot;
import auctionsniper.ui.Column;
import auctionsniper.ui.SnipersTableModel;

@RunWith(JMock.class) 
public class SnipersTableModelTest { 
	
	private final Mockery context = new Mockery();
	
	private TableModelListener listener =
			context.mock(TableModelListener.class);
	
	private final SnipersTableModel model =
			new SnipersTableModel();
	
	@Before
	public void attachModelListener() {
		model.addTableModelListener(listener);
	}
	
	@Test
	public void hasEnoughColumns() {
		assertThat(model.getColumnCount(), equalTo(Column.values().length));
	}
	
	@Test
	public void setsUpColumnHeadings() {
		for (Column column : Column.values()) {
			assertEquals(column.name,model.getColumnName(column.ordinal()));
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void setsSniperValuesInColumns() {
		
		context.checking(new Expectations() {{
			one(listener).tableChanged(with(aRowChangedEvent()));
		}});

		model.sniperStateChanged(
			new SniperSnapshot("item id", 555, 666, BIDDING));
		
		assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
		assertColumnEquals(Column.LAST_PRICE,      555);
		assertColumnEquals(Column.LAST_BID,        666);
		assertColumnEquals(Column.SNIPER_STATE,    textFor(BIDDING));
	}

	private void assertColumnEquals(Column column, Object expected) {
		
		final int rowIndex = 0;
		final int columnIndex = column.ordinal();
		assertEquals(expected, model.getValueAt(rowIndex,  columnIndex));
	}
	
	private Matcher<TableModelEvent> aRowChangedEvent() {
		return samePropertyValuesAs(new TableModelEvent(model, 0));
	}	
}
