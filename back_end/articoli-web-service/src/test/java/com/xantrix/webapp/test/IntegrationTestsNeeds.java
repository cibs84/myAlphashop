package com.xantrix.webapp.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class IntegrationTestsNeeds {
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    protected TransactionTemplate transactionTemplate;

    public void deleteAllFromDb() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            jdbcTemplate.update("delete from articoli where codart = '123Test'");
            jdbcTemplate.update("delete from articoli where codart = 'abcTest'");
        });
    }
}
