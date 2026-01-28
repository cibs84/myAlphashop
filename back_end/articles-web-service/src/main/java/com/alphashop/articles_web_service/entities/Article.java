package com.alphashop.articles_web_service.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.alphashop.articles_web_service.converters.IdArtStatusConverter;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Table(name = "articles")
public class Article implements Serializable {

	private static final long serialVersionUID = 557697126855586977L;

	@Id
	@Column(name = "codart")
	private String codart;

	@Column(name = "description")
	private String description;

	@Column(name = "um")
	private String um;

	@Column(name = "codstat")
	private String codStat;

	@Column(name = "pcscart")
	private Integer pcsCart;

	@Column(name = "netweight")
	private Double netWeight;

	@Convert(converter = IdArtStatusConverter.class)
	@Column(name = "idartstatus", columnDefinition = "CHAR(1")
	private Integer idArtStatus;

	@CreatedDate
	@Column(name = "creationdate")
	private LocalDate creationDate;

	@OneToOne(mappedBy = "article", cascade = CascadeType.ALL, 
			  orphanRemoval = true)
	private Ingredients ingredients;

	@JsonManagedReference
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, 
			   mappedBy = "article", orphanRemoval = true)
	private Set<Barcode> barcodes = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "idfamass", referencedColumnName = "id")
	private Category category;

	@ManyToOne
	@JoinColumn(name = "idvat", referencedColumnName = "idVat")
	private Vat vat;
}
