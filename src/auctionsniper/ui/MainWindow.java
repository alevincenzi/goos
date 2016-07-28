package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import auctionsniper.SniperSnapshot;
import auctionsniper.UserRequestListener;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.util.Announcer;

public class MainWindow extends JFrame {
		
	private static final long serialVersionUID = 1L;

	public static final String APPLICATION_TITLE  = "Auction Sniper";
	public static final String MAIN_WINDOW_NAME   = "Auction Sniper Main";
	public static final String SNIPER_STATUS_NAME = "sniper status";
	public static final String SNIPERS_TABLE_NAME = "Snipers Table";
	public static final String NEW_ITEM_ID_NAME   = "item id";
	public static final String JOIN_BUTTON_NAME   = "join button";
	  
	private final SnipersTableModel snipers;

	private final Announcer<UserRequestListener> userRequests
		= Announcer.to(UserRequestListener.class);
	
	public
	MainWindow(SnipersTableModel snipers) {
		
		super(APPLICATION_TITLE);
		
		this.snipers = snipers;
		
		setName(MAIN_WINDOW_NAME);
		fillContentPane(makeSnipersTable(), makeControls());
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void
	fillContentPane(JTable snipersTable, JPanel controls){
	
		final Container contentPane = getContentPane();
		
		contentPane.setLayout(new BorderLayout());
		contentPane.add(controls, BorderLayout.NORTH);		
		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
	}
	
	private JTable
	makeSnipersTable(){
	
		final JTable snipersTable = new JTable(snipers);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}
	
	private JPanel
	makeControls() {
		
		final JTextField itemIdField = new JTextField();
		itemIdField.setColumns(25);
		itemIdField.setName(NEW_ITEM_ID_NAME);
		
		JButton joinAuctionButton = new JButton("Join Auction");
		joinAuctionButton.setName(JOIN_BUTTON_NAME);
		joinAuctionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				userRequests.announce().joinAuction(itemIdField.getText());
			}
		});
		
		JPanel controls = new JPanel(new FlowLayout());

		controls.add(itemIdField);
		controls.add(joinAuctionButton);
		
		return controls;
	}
	
	public void
	sniperStatusChanged(SniperSnapshot snapshot){
	
		snipers.sniperStateChanged(snapshot);
	}

	public void
	addUserRequestListener(UserRequestListener userRequestListener) {
	
		userRequests.addListener(userRequestListener);
	}
}
