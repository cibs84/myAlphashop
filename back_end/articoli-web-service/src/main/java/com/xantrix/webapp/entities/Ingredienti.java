package com.xantrix.webapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ingredienti")
@Data
public class Ingredienti {

	@Id
	@Column(name = "codart")
	private String codArt;

	@Column
	private String info;

	@OneToOne
	@PrimaryKeyJoinColumn
	@JsonIgnore
	private Articolo articolo;
}
