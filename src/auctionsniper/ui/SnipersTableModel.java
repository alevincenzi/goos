package auctionsniper.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.SniperPortfolio.PortfolioListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.util.Defect;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();
	
	@Override
	public int
	getRowCount() {
	
		return snapshots.size();
	}

	@Override
	public int
	getColumnCount() {
	
		return Column.values().length;
	}

	@Override
	public String
	getColumnName(int column){
	
		return Column.at(column).name;
	}
	
	@Override
	public Object
	getValueAt(int rowIndex, int columnIndex) {
	
		return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
	}

	@Override
	public void sniperAdded(AuctionSniper sniper) {

		addSniperSnapshot(sniper.getSnapshot());	
		sniper.addSniperListener(new SwingThreadSniperListener(this));
	}

	private void
	addSniperSnapshot(SniperSnapshot sniperSnapshot) {

		snapshots.add(sniperSnapshot);
		int row = snapshots.size() - 1; 
		fireTableRowsInserted(row, row);
	}
	
	public void
	sniperStateChanged(SniperSnapshot snapshot){
		
		for (int i = 0; i < snapshots.size(); i++) {
			if (snapshot.isForSameItemAs(snapshots.get(i))) {
				snapshots.set(i, snapshot); 
				fireTableRowsUpdated(i, i);
				return;
	        }
	    }
	    
		throw new Defect("No existing Sniper state for " + snapshot.itemId);
	}
}

