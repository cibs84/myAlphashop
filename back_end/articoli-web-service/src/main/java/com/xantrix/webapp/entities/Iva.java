package com.xantrix.webapp.entities;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "iva")
@Data
public class Iva {
	@Id
	@Column(name = "idiva")
	private Integer idIva;

	@Column(name = "descrizione")
	private String descrizione;

	@Column(name = "aliquota")
	private Integer aliquota;

	@OneToMany(mappedBy = "iva")
	@JsonBackReference
	private Set<Articolo> articolo;
}
