package com.example.deviceStorageJDBC.domain;

public class ToOrder {
	
	private long idOrder;

	private long idStorage;

	private int orderedAmount;

	private double price;

	public ToOrder() {
	}

	public ToOrder(int orderedAmount, double price) {
		this.orderedAmount = orderedAmount;
		this.price = price;
	}

	public long getIdOrder() {
		return idOrder;
	}

	public void setIdOrder(long idOrder) {
		this.idOrder = idOrder;
	}

	public long getIdStorage() {
		return idStorage;
	}

	public void setIdStorage(long idStorage) {
		this.idStorage = idStorage;
	}

	public int getOrderedAmount() {
		return orderedAmount;
	}

	public void setOrderedAmount(int orderedAmount) {
		this.orderedAmount = orderedAmount;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	

}
