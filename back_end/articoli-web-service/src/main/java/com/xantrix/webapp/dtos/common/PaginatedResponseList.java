package com.xantrix.webapp.dtos.common;

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
public class PaginatedResponseList<L> extends PaginationDto {
	
	private List<L> itemList;
	

	public <P> PaginatedResponseList(Page<P> page, List<L> itemList) {
		super(page);
		this.itemList = itemList;
	}
}
