package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
	
	private static final long serialVersionUID = 1L;

	private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
	
	private SniperSnapshot snapshot = STARTING_UP;
	
	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public String getColumnName(int column){
		return Column.at(column).name;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return Column.at(columnIndex).valueIn(snapshot);
	}

	private static String[] STATUS_TEXT = { 
		"Joining", "Bidding", "Winning", "Losing", "Lost", "Won", "Failed"
	};
	
	public static String textFor(SniperState state){
		return STATUS_TEXT[state.ordinal()];
	}
	
	public void sniperStateChanged(SniperSnapshot newSniperSnapshot){
		snapshot = newSniperSnapshot;
		fireTableRowsUpdated(0, 0);
	}
}

