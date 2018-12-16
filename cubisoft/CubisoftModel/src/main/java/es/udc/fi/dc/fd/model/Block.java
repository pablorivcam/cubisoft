package es.udc.fi.dc.fd.model;

import java.util.List;

public class Block<T> {

	private List<T> items;
	private boolean existMoreItems;

	public Block(List<T> items, boolean existMoreItems) {

		this.items = items;
		this.existMoreItems = existMoreItems;

	}

	public List<T> getItems() {
		return items;
	}

	public boolean getExistMoreItems() {
		return existMoreItems;
	}

}