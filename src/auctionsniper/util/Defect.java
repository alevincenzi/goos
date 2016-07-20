package auctionsniper.util;

public class Defect extends RuntimeException {

	  private static final long serialVersionUID = 5269052036344976541L;

	  public Defect() {
	    super();
	  }

	  public Defect(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public Defect(String message) {
	    super(message);
	  }

	  public Defect(Throwable cause) {
	    super(cause);
	  }

}
