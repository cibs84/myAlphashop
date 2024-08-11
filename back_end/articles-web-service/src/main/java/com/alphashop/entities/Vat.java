package com.alphashop.entities;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "vat")
@Data
public class Vat {
	@Id
	@Column(name = "idvat")
	private Integer idVat;

	@Column(name = "description")
	private String description;

	@Column(name = "taxrate")
	private Integer taxRate;

	@OneToMany(mappedBy = "vat")
	@JsonBackReference
	private Set<Article> article;
}
