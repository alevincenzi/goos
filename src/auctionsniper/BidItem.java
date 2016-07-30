package auctionsniper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class BidItem {

    public final String identifier;
    public final int    stopPrice;

    public
    BidItem(String identifier, int stopPrice) { 
    
    	this.identifier = identifier;
    	this.stopPrice = stopPrice; 
    }

    public boolean
    allowsBid(int bid) {
    
    	return bid <= stopPrice;
    } 
    
    @Override
    public boolean
    equals(Object obj) {
    	
    	return EqualsBuilder.reflectionEquals(this, obj);
    }
    
    @Override
    public int hashCode() {
    	
    	return HashCodeBuilder.reflectionHashCode(this);
    }
    
    @Override
    public String toString() {
    	
    	return "Item: " + identifier + ", stop price: " + stopPrice;
    }
}
