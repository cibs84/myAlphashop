package com.alphashop.articles_web_service.common;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;
 /**
  * @param <P> Type of Item Page
  * @param <T> Type of Item List
  * @param <W> Type of Item Page in the Pagination Inner Class
  */
@Getter
public class PaginatedResponseList<P, T> {
	
	private final Pagination<P> pagination;
	private final List<T> itemList;

	public PaginatedResponseList(Page<P> page, List<T> itemList) {
		this.pagination = new Pagination<P>(page);
		this.itemList = itemList;
	}
	
	// INNER CLASS IMMUTABILE
	@Getter
	public class Pagination<W> {
		private final int currentPage;
		private final int totalPages;
		private final int pageSize; // maximum number of elements on the page
		private final int totalElements; // number of elements on the current page

		public Pagination(Page<W> page) {
			this.currentPage = page.getNumber()+1; // da 0-based → 1-based
			this.pageSize = page.getSize();
	        this.totalPages = page.getTotalPages();
	        this.totalElements = (int) page.getTotalElements();
		}
	}
}
