package auctionsniper;

import auctionsniper.util.Defect;

public enum SniperState {
	
	JOINING {
		@Override
		public SniperState whenAuctionClosed() { return LOST; }
		
		@Override
		public String toString() { return "Joining"; }
	},
	
	BIDDING {
		@Override
		public SniperState whenAuctionClosed() { return LOST; }

		@Override
		public String toString() { return "Bidding"; }
	},
	
	WINNING {
		@Override
		public SniperState whenAuctionClosed() { return WON; }
		
		@Override
		public String toString() { return "Winning"; }
	},
	
	LOSING {
		@Override
		public SniperState whenAuctionClosed() { return LOST; }

		@Override
		public String toString() { return "Losing"; }
	},
	
	LOST {
		@Override
		public String toString() { return "Lost"; }
	}, 
	
	WON {
		@Override
		public String toString() { return "Won"; }
	},
	
	FAILED{
		@Override
		public String toString() { return "Failed"; }
	};
	
	
	public static String
	textFor(SniperState state){
	
		return state.toString();
	}

	public SniperState
	whenAuctionClosed() {
	
		throw new Defect("Auction is already closed");
	}
}
