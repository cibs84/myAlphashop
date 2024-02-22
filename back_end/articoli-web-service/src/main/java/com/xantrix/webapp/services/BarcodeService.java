package com.xantrix.webapp.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xantrix.webapp.entities.Barcode;
import com.xantrix.webapp.repository.BarcodeRepository;

@Service
@Transactional(readOnly = true)
public class BarcodeService {
	
	private final BarcodeRepository barcodeRepository;
	
	public BarcodeService(BarcodeRepository barcodeRepository) {
		this.barcodeRepository = barcodeRepository;
	}
	
	public Barcode findByBarcode(String barcode) {
		return barcodeRepository.findByBarcode(barcode);
	}
}
