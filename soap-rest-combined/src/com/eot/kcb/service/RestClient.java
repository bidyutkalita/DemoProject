package com.eot.kcb.service;

import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.jackson.JacksonFeature;

import com.eot.kcb.dto.BaseDTO;

public class RestClient {

	public static <T extends BaseDTO> T processRequest(String url, T ObjRequest, T objResponse, Class<T> type,Class returnType) {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		WebTarget webTarget = client.target(url);
		objResponse = (T)webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(ObjRequest, MediaType.APPLICATION_JSON)).readEntity(returnType);  
		System.out.println();
		return objResponse;
	}

}
