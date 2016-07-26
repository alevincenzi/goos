package auctionsniper.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.util.Defect;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
	
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

	public void addSniper(SniperSnapshot newSnapshot) {

		snapshots.add(newSnapshot);
		fireTableRowsInserted(snapshots.size() - 1, snapshots.size() - 1);
	}
	
	public void
	sniperStateChanged(SniperSnapshot snapshot){
	
		int row = rowMatching(snapshot);
		snapshots.set(row, snapshot);
		fireTableRowsUpdated(row, row);
	}

	private int
	rowMatching(SniperSnapshot snapshot){
		
		for (int i = 0; i < snapshots.size(); i++){
			if (snapshot.isForSameItemAs(snapshots.get(i))) {
				return i;
			}
		}
		throw new Defect("Cannot find match for " + snapshot);
	}
}

