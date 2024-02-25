package com.xantrix.webapp.dtos.common;

import java.util.Objects;

import org.springframework.data.domain.Page;

public class PaginationDto {
	private int currentPage = 1;
	private int totalPages = 1;
	private int nextPage = 1;
	private int previousPage = 1;
	private int pageSize = 10; // numero massimo di elementi presenti nella pagina
	private int totalElements = 10; // numero elementi presenti nella pagina corrente


	public PaginationDto() {
	}
	
	public PaginationDto(Page<?> page) {
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
		this.nextPage = getCurrentPage() + (getCurrentPage() < getTotalPages() ? 1 : 0);
	}

	public int getPreviousPage() {
		return previousPage;
	}

	private void setPreviousPage() {
		this.previousPage = getCurrentPage() - (getCurrentPage() >= 1 ? 1 : 0);
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
		PaginationDto other = (PaginationDto) obj;
		return currentPage == other.currentPage && nextPage == other.nextPage && previousPage == other.previousPage
				&& totalPages == other.totalPages;
	}
}
