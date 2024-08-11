package com.alphashop.entities;

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
@Table(name = "articles")
public class Article implements Serializable {

	private static final long serialVersionUID = 557697126855586977L;

	@Id
	@Column(name = "codart")
	private String codArt;

	@Column(name = "description")
	private String description;

	@Column(name = "um")
	private String um;

	@Column(name = "codstat")
	private String codStat;

	@Column(name = "pzcart")
	private Integer pzCart;

	@Column(name = "netweight")
	private double netWeight;

	@Column(name = "idartstatus")
	private String idArtStatus;

	@Temporal(TemporalType.DATE)
	@Column(name = "creationdate")
	private Date creationDate;

	@OneToOne(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
	private Ingredients ingredients;

	
	@JsonManagedReference
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy = "article", orphanRemoval = true)
	private Set<Barcode> barcode = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "idfamass", referencedColumnName = "id")
	private FamAssort famAssort;

	@ManyToOne
	@JoinColumn(name = "idvat", referencedColumnName = "idVat")
	private Vat vat;
}
