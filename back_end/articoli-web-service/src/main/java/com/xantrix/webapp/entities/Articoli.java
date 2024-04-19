package com.xantrix.webapp.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
@Table(name = "articoli")
public class Articoli implements Serializable {

	private static final long serialVersionUID = 557697126855586977L;

	@Id
	@Column(name = "codart")
	private String codArt;

	@Column(name = "descrizione")
	private String descrizione;

	@Column(name = "um")
	private String um;

	@Column(name = "codstat")
	private String codStat;

	@Column(name = "pzcart")
	private Integer pzCart;

	@Column(name = "pesonetto")
	private double pesoNetto;

	@Column(name = "idstatoart")
	private String idStatoArt;

	@Temporal(TemporalType.DATE)
	@Column(name = "datacreazione")
	private Date dataCreaz;

	@OneToOne(mappedBy = "articolo", cascade = CascadeType.ALL, orphanRemoval = true)
	private Ingredienti ingredienti;

	
	@JsonManagedReference
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy = "articolo", orphanRemoval = true)
	private Set<Barcode> barcode = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "idfamass", referencedColumnName = "id")
	private FamAssort famAssort;

	@ManyToOne
	@JoinColumn(name = "idiva", referencedColumnName = "idIva")
	private Iva iva;
}
