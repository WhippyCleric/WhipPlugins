package com.whippy.sponge.auction.beans;

public class Auction {

	private String itemId;
	private String itemName;
	private int numberOfItem;
	private double startingBid;
	private double increment;
	private int time;
	
	public Auction(String itemId, String itemName, int numberOfItem,
			double startingBid, double increment, int time) {
		super();
		this.itemId = itemId;
		this.itemName = itemName;
		this.numberOfItem = numberOfItem;
		this.startingBid = startingBid;
		this.increment = increment;
		this.time = time;
	}
	
	public String getItemId() {
		return itemId;
	}
	public String getItemName() {
		return itemName;
	}
	public int getNumberOfItem() {
		return numberOfItem;
	}
	public double getStartingBid() {
		return startingBid;
	}
	public double getIncrement() {
		return increment;
	}
	public int getTime() {
		return time;
	}

}
