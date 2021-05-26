package com.encryption;

import java.security.KeyPair;
import java.text.DecimalFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

public class SendRequest {
	static{
//		System.setProperty("javax.net.ssl.keyStore", "D:\\Bidyut\\project\\eclipse-workspace\\Demo-Projects\\JKS-RSA-HMAC\\src\\airtime_keystore.jks");
	    System.setProperty("javax.net.ssl.keyStorePassword","changeit");
	    System.setProperty("javax.net.ssl.trustStore", "D:\\Bidyut\\project\\eclipse-workspace\\Demo-Projects\\JKS-RSA-HMAC\\src\\airtime_keystore.jks");

//	    System.setProperty("javax.net.ssl.keyAlias", "airtime_topup");
	    System.setProperty("javax.net.ssl.trustStorePassword","changeit");
	    
//	    System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
	    System.setProperty("javax.net.ssl.keyStoreType", "jks");
//	    System.setProperty("javax.net.ssl.keyAlias", "interface-ssc");
//	    System.setProperty("javax.net.ssl.trustStorePassword","G3k@siwMjR5z");
	    
	    System.out.println(System.getProperty("javax.net.ssl.trustStore"));
        System.out.println(System.getProperty("javax.net.ssl.trustStorePassword"));
	}
	 private static void sendPost() throws Exception {

	        HttpPost post = new HttpPost("https://87.106.192.97:9710/telcopay/fininst/rest");
	        post.addHeader("content-type", "application/json;charset=utf-8");
	        post.addHeader("version", "010000");
	        post.addHeader("http-date", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)));
	        post.addHeader("username", "trinity");
	        post.addHeader("key-seq", "1");// what is key seq
	        post.addHeader("success", "1");// what is key seq
	        post.addHeader("replay", "0");
	     
	        
	        // add request parameter, form parameters
	      /*  List<NameValuePair> urlParameters = new ArrayList<>();
	        urlParameters.add(new BasicNameValuePair("username", "abc"));
	        urlParameters.add(new BasicNameValuePair("password", "123"));
	        urlParameters.add(new BasicNameValuePair("custom", "secret"));*/
	        
	        StringBuilder entity = new StringBuilder();
	        entity.append("{");
	        entity.append("\"transactionId\":\"900000000019\",");
	        entity.append("\"requestId\":" + null + ",");
	        entity.append("\"serviceId\":"+2+",");
	        entity.append("\"debitedAccountType\":"+1+",");
	        entity.append("\"debitedAccount\":\"917609113\",");// what is key seq
	        
	        entity.append("\"creditedAccountType\":"+1+",");// plaintext required
	        entity.append("\"creditedAccount\":\"917640765\",");// plaintext required
	        entity.append("\"amount\":"+new Double(new DecimalFormat("#0.00").format(10))+",");// plaintext required
	        entity.append("\"currencyId\":"+1+",");
	        entity.append("\"languageId\":"+1+",");
	        entity.append("\"requestParams\":"+null);
	        entity.append("}");
	        post.addHeader("token", Rsa.signEAirtime(entity.toString()));
//	        post.addHeader("Content-Length", entity.toString().length()+"");// need to calculate content length

	        System.out.println();
	        System.out.println("Request \n"+entity.toString()+"\n");
	        // send a JSON data
	        post.setEntity(new StringEntity(entity.toString()));

	     /*   try (CloseableHttpClient httpClient = HttpClients.createDefault();
	             CloseableHttpResponse response = httpClient.execute(post)) {

	            System.out.println(EntityUtils.toString(response.getEntity()));
	        }*/
	        try {
	        	
	        	SSLContextBuilder builder = new SSLContextBuilder();
	        	builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
	        	SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
	        	        builder.build(),NoopHostnameVerifier.INSTANCE);//bypass hostname varification
	        	
	        	CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(
	        	        sslsf).build();
	        	CloseableHttpResponse response = httpClient.execute(post);
	        	System.out.println();
	        	System.out.println("Response\n"+EntityUtils.toString(response.getEntity())+"\n");
	        }catch (Exception e) {
	        	e.printStackTrace();
				// TODO: handle exception
			}

	    }
	 public static void main(String[] args) {
		 try {
		 SendRequest.sendPost();
		 }catch (Exception e) {
			e.printStackTrace();
		}
	}

}
