package com.xantrix.webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;
import com.xantrix.webapp.repository.BarcodeRepository;

@Service
@Transactional(readOnly = true)
public class BarcodeServiceImpl implements BarcodeService
{
	@Autowired
	BarcodeRepository barcodeRepository;
	
	@Override
	public Barcode SelByBarcode(String Barcode)
	{
		Barcode barcode =  barcodeRepository.findByBarcode(Barcode);
		
		Articoli articolo = barcode.getArticolo();
		articolo.setUm(articolo.getUm().trim());
		articolo.setIdStatoArt(articolo.getIdStatoArt().trim());
		
		barcode.setArticolo(articolo);
		
		return barcode;
	}
}
