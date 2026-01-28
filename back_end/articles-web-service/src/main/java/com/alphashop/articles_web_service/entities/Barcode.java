package com.alphashop.articles_web_service.entities;

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
	
	@Column(name = "idtypeart")
	private String idTypeArt;

	// 'codart' is the FK that will only appear in the db and is referenced
	// to the 'codart' column of the 'articles' table
	// which corresponds to the variable 'codart' of the class Article
	@JsonBackReference
	@EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codart", referencedColumnName = "codart")
    private Article article;
}
