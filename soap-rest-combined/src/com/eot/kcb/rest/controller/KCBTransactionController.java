package com.eot.kcb.rest.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.eot.bankingcore.saaj.KCBIntegration;
import com.eot.bankingcore.saaj.KCBParameter;

@Path("/kcb")
public class KCBTransactionController {
	
	@POST()
	@Path("/transaction")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public  KCBParameter transaction(KCBParameter kcbParameter) {
		try {
			kcbParameter=KCBIntegration.sendSoapMsg(KCBIntegration.createSoapRequet(kcbParameter), "http://172.16.115.72:7071/BillerEngineNew/BillerCustomWebService");
		} catch (Exception e) {
			kcbParameter.setStatus("1");
			e.printStackTrace();
		}
		
		return kcbParameter;
	}
	
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public String  sayPlainTextHello() {
		System.out.println("resonding to the server");
		return "test successufl";
	}

}
