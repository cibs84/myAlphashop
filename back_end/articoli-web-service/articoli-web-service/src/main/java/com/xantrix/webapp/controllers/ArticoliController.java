package com.xantrix.webapp.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;
import com.xantrix.webapp.services.BarcodeService;



@RestController
@RequestMapping("api/articoli")
public class ArticoliController 
{
	private static final Logger logger = LoggerFactory.getLogger(ArticoliController.class);
	
	@Autowired
	private BarcodeService barcodeService;
	
	@GetMapping(value = "/cerca/ean/{barcode}", produces = "application/json")
	public ResponseEntity<Articoli> listArtByEan(@PathVariable("barcode") String Barcode)
	{
		logger.info("****** Otteniamo l'articolo con barcode " + Barcode + " *******");
		
		Articoli articolo;
		Barcode Ean = barcodeService.SelByBarcode(Barcode);
		
		if (Ean == null)
		{
			String ErrMsg = String.format("Il barcode %s non è stato trovato!", Barcode);
			
			logger.warn(ErrMsg);
			 
			return new ResponseEntity<Articoli>(HttpStatus.NOT_FOUND);
		}
		else
		{
			articolo = Ean.getArticolo();
		}
		
		return new ResponseEntity<Articoli>(articolo, HttpStatus.OK);
		
	}
}
