package com.laundry.LaundryManagement.dto;

public class Product {
	
	private String Id;
	private String productName;
	private double price;
	private String category;
	/**
	 * @return the id
	 */
	public String getId() {
		return Id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		Id = id;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
