package com.xantrix.webapp.tests.RepositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.entity.Barcode;
import com.xantrix.webapp.repository.BarcodeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class BarcodeRepositoryTest
{
	@Autowired
	private BarcodeRepository barcodeRepository;
	
	
	@Test
	public void TestfindByBarcode()
	{
		assertThat(barcodeRepository.findByBarcode("8008490000021"))
		.extracting(Barcode::getBarcode)
		.isEqualTo("8008490000021");
	}
	
}