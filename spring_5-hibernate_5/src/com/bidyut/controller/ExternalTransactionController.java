package com.bidyut.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bidyut.dao.ExternalTransactionDao;
import com.bidyut.dto.DepositReqDTO;
import com.bidyut.dto.DepositeResponse;
import com.bidyut.service.ExternalTransactionService;
import com.eot.entity.Customer;


@RestController
@RequestMapping("/ext/txn")
public class ExternalTransactionController {
	
	@Autowired
	ExternalTransactionService externaltransactionService;
	
	@RequestMapping(value = "/deposit", method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public DepositeResponse deposite(@RequestBody DepositReqDTO reqDTO) {
		System.out.println("request received");
		return externaltransactionService.processNileBatDeposit(reqDTO);
	}
	@RequestMapping(value = "/test", method = RequestMethod.GET,consumes=MediaType.APPLICATION_JSON_VALUE)
	public String test() {
		return "test";
	}
}
