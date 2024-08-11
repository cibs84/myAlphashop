package com.alphashop.common;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.Setter;
 /**
  * 
  * @param <L> Type of Item List
  * @param Page<P> Page object 
  */
@Getter
@Setter
public class PaginatedResponseList<L> {
	
	private Pagination pagination;
	private List<L> itemList;

	public <P> PaginatedResponseList(Page<P> page, List<L> itemList) {
		this.pagination = new Pagination(page);
		this.itemList = itemList;
	}
	
	
	public class Pagination<P> {
		private int currentPage = 1;
		private int totalPages = 1;
		private int nextPage = 1;
		private int previousPage = 1;
		private int pageSize = 10; // maximum number of elements on the page
		private int totalElements = 10; // number of elements on the current page
		private int[] totalPagesArray = new int[getTotalPages()]; // print page number in the frontend

		public Pagination() {
		}
		
		public Pagination(Page<?> page) {
			super();
			setCurrentPage(page.getNumber()+1);
			setPageSize(page.getSize());
			setTotalElements(page.getNumberOfElements());
			setTotalPages(page.getTotalPages());
			setNextPage();
			setPreviousPage();
			setTotalPagesArray();
		}

		public int getCurrentPage() {
			return currentPage;
		}

		public void setCurrentPage(int currentPage) {
			this.currentPage = currentPage;
		}

		public int getTotalPages() {
			return totalPages;
		}

		public void setTotalPages(int totalPages) {
			this.totalPages = totalPages;
		}

		public int getNextPage() {
			return nextPage;
		}

		private void setNextPage() {
			this.nextPage = Math.min(getCurrentPage() + 1, getTotalPages());
		}

		public int getPreviousPage() {
			return previousPage;
		}

		private void setPreviousPage() {
			this.previousPage = Math.max(getCurrentPage() - 1, 1);
		}
		
		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public int getTotalElements() {
			return totalElements;
		}

		public void setTotalElements(int totalElements) {
			this.totalElements = totalElements;
		}

		public int[] getTotalPagesArray() {
			return totalPagesArray;
		}
		
		public void setTotalPagesArray() {
			int[] newArr = new int[getTotalPages()];
			for (int i = 0; i < newArr.length; i++) {
				newArr[i] = i + 1;
			}
			this.totalPagesArray = newArr;
		}

		public void setTotalPagesArray(int[] totalPagesArray) {
			this.totalPagesArray = totalPagesArray;
		}

		@Override
		public String toString() {
			return "Pagination [currentPage=" + currentPage + ", totalPages=" + totalPages + ", nextPage=" + nextPage
					+ ", previousPage=" + previousPage + ", pageSize=" + pageSize + ", totalElements=" + totalElements
					+ ", totalPagesArray=" + Arrays.toString(totalPagesArray) + "]";
		}
	}
}
