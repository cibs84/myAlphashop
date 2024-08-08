package com.xantrix.webapp.entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "barcode")
@Data
public class Barcode implements Serializable {

	private static final long serialVersionUID = 8682477643109847337L;

	@Id
	@Column(name = "barcode")
	private String barcode;
	
	@Column(name = "idtipoart")
	private String idTipoArt;

	// 'codart' è la FK che apparirà solo nel db ed è riferita
	// alla colonna 'codart' della tabella 'articoli'
	// che corrisponde alla variabile 'codArt' della classe Articolo
	@JsonBackReference
	@EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codart", referencedColumnName = "codArt")
    private Articolo articolo;
}
