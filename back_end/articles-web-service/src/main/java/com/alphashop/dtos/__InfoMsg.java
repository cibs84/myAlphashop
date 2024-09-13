package com.alphashop.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class __InfoMsg {

	private LocalDate date;
	
	private String message;
}
