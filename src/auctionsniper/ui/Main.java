package auctionsniper.ui;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

public class Main {

	public static final String MAIN_WINDOW_NAME   = "Auction Sniper Main";
	public static final String SNIPER_STATUS_NAME = "sniper status";
	
	public static final String STATUS_JOINING = "Joining";
	public static final String STATUS_LOST    = "Lost";
	
	private MainWindow ui;
	
	public static class MainWindow extends JFrame {
		
		private final JLabel sniperStatus = createLabel(STATUS_JOINING);

		private static final long serialVersionUID = 1L;

		public MainWindow() {
			super("Auction Sniper");
			setName(MAIN_WINDOW_NAME);
			add(sniperStatus);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
		}
		
		private static JLabel createLabel(String initialText){
			JLabel result = new JLabel(initialText);
			result.setName(SNIPER_STATUS_NAME);
			result.setBorder(new LineBorder(Color.BLACK));
			return result;			
		}
	}

	
	public Main() throws Exception {
		startUserInterface();
	}
	
	private void startUserInterface() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				ui = new MainWindow();
			}
		});
	}
	
	public static void main(
			String xmppHostname,
			String sniperId,
			String sniperPassword,
			String itemId) throws Exception {
		
		Main main = new Main();
	}
}
