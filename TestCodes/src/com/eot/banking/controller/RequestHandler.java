/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: RequestHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:57:55 PM Sambit Created
*/
package com.eot.banking.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.exception.EOTException;
import com.eot.banking.handlers.BaseHandler;
import com.eot.banking.handlers.HandlerFactory;
import com.eot.banking.server.Constants;
import com.thinkways.util.ByteBuffer;
import com.thinkways.util.HexString;
import com.thinkways.util.TLVUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class RequestHandler.
 */
@Controller
@RequestMapping("/requestHandler/*")
public class RequestHandler {

	/** The message source. */
	@Autowired
	private MessageSource messageSource ;
	
	/** The handler factory. */
	@Autowired
	private HandlerFactory handlerFactory;

	/**
	 * Handle request.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		System.out.println("*********************************************************");
		System.out.println("User agent: " + request.getHeader("user-agent"));
		String headerDataLength = request.getHeader("data-length");
		String headerContentLength = request.getHeader("content-length");
		System.out.println("content-length: " + headerDataLength);
		System.out.println("data-length: " + headerContentLength);

		int dataLength = headerDataLength == null ? 
				(headerContentLength == null ? -1 : Integer.parseInt(headerContentLength)) : 
					Integer.parseInt(headerDataLength);

				try {

					byte[][] requestData = TLVUtil.getAllDataFromLVArray(read(request.getInputStream(), dataLength), 0);

					int requestType = Integer.parseInt(new String(requestData[0]));

					System.out.println("requestType : " + Integer.parseInt(new String(requestData[0])));

					BaseHandler handler = handlerFactory.getHandler(requestType);

					byte[][] responseData = handler.handleRequest(requestData,false,"") ;

					response.getOutputStream().write(TLVUtil.getLVArrayFromMultipleData(responseData));

				} catch (EOTException e) {
					if(e.getErrorMessage()!=null){
						byte[][] resp = new byte[][] { (Constants.MOB_UPDATE_STATUS+"").getBytes() , e.getErrorMessage().getBytes() };

						try {
							response.getOutputStream().write(TLVUtil.getLVArrayFromMultipleData(resp));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}else{
						throw(e);
					}

				} catch (Exception e2) {
					e2.printStackTrace();

					byte[] responseData = messageSource.getMessage("ERROR_"+ErrorConstants.SERVICE_ERROR, null, new Locale("en_US")).getBytes("UTF-8");
					byte[][] resp = new byte[][] { (Constants.MOB_RESP_STATUS_FAILURE+"").getBytes() , responseData };

					try {
						response.getOutputStream().write(TLVUtil.getLVArrayFromMultipleData(resp));
					} catch (IOException e) {
//						e.printStackTrace();
					}

				} 

				System.out.println("*********************************************************");
	}

	/**
	 * Read.
	 * 
	 * @param is
	 *            the is
	 * @param contentLength
	 *            the content length
	 * @return the byte[]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private byte[] read(InputStream is, int contentLength) throws IOException {

		byte[] inputData = null;
		int actualLength = 0;
		if( contentLength == -1 ) {

			ByteBuffer buffer = new ByteBuffer();
			int piece;
			while(( piece = is.read()) != -1) {

				buffer.appendByte((byte)piece);
			}
			System.out.println("Input data = " + HexString.bufferToHex(buffer.getBuffer()));
			inputData = buffer.getBuffer();
			actualLength = inputData.length;
		}
		else {

			inputData = new byte[contentLength];
			int offset = 0;
			while( offset < contentLength ) {

				int dataLenRead = is.read(inputData, offset, contentLength - offset);
				if( dataLenRead == -1 ) {

					break;
				}
				offset += dataLenRead;
			}
			actualLength = offset;
		}

		System.out.println("contentLength = " + contentLength);
		System.out.println("Actual Length = " + actualLength);
		System.out.println("Input data = " + HexString.bufferToHex(inputData));

		return inputData;
	}

}
