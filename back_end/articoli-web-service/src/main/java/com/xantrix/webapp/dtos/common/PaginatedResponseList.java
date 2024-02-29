package com.xantrix.webapp.dtos.common;

import java.util.List;
import java.util.Objects;

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
		private int pageSize = 10; // numero massimo di elementi presenti nella pagina
		private int totalElements = 10; // numero elementi presenti nella pagina corrente

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

		@Override
		public String toString() {
			return "PaginationDto [currentPage=" + currentPage + ", totalPages=" + totalPages + ", nextPage=" + nextPage
					+ ", previousPage=" + previousPage + "]";
		}

		@Override
		public int hashCode() {
			return Objects.hash(currentPage, nextPage, previousPage, totalPages);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pagination other = (Pagination) obj;
			return currentPage == other.getCurrentPage() && nextPage == other.getNextPage() && previousPage == other.getPreviousPage()
					&& totalPages == other.getTotalPages();
		}
	}
}
