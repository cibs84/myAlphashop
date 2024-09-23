package com.alphashop.articles_web_service.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ingredients")
@Data
public class Ingredients {

	@Id
	@Column(name = "codart")
	private String codArt;

	@Column
	private String info;

	@OneToOne
	@PrimaryKeyJoinColumn
	@JsonIgnore
	private Article article;
}
