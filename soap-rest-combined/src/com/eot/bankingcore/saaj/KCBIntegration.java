package com.eot.bankingcore.saaj;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.NodeList;

public class KCBIntegration {

	public static SOAPMessage createSoapRequet(KCBParameter requestParam) throws Exception {

		//		MessageFactory factory =  MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		//		MessageFactory factory =  MessageFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);
		MessageFactory factory = MessageFactory.newInstance();
		SOAPMessage message = factory.createMessage();

		SOAPPart soapPart = message.getSOAPPart();
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.setPrefix("soapenv");
		envelope.removeNamespaceDeclaration("SOAP-ENV");
		//        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
		envelope.addNamespaceDeclaration("web", "http://webserviceserver.billerengine.kcb/");

		SOAPHeader header = envelope.getHeader();
		header.setPrefix("soapenv");

		//		header.detachNode();// if header is not required

		SOAPBody body = message.getSOAPBody();
		body.setPrefix("soapenv");
		/* QName bodyName = new QName("http://API.Interface.kcb/", "GetLastTradePrice", "m");
		 * SOAPBodyElement bodyElement = body.addBodyElement(bodyName); */
		QName kcbInterfaceBody = new QName("KCBBillersWebserviceInterface");
		SOAPElement kcbInterfaceParam = message.getSOAPBody().addChildElement(kcbInterfaceBody);
		kcbInterfaceParam.setPrefix("web");

		QName UserName = new QName("UserName");
		SOAPElement symbol = kcbInterfaceParam.addChildElement(UserName);
		symbol.addTextNode(requestParam.getUserName());

		QName Password = new QName("Password");
		symbol = kcbInterfaceParam.addChildElement(Password);
		symbol.addTextNode(requestParam.getPassword());

		QName CompanyId = new QName("CompanyId");
		symbol = kcbInterfaceParam.addChildElement(CompanyId);
		symbol.addTextNode(requestParam.getCompanyId());

		QName ChannelId = new QName("ChannelId");
		symbol = kcbInterfaceParam.addChildElement(ChannelId);
		symbol.addTextNode(requestParam.getChannelId() + "");

		QName BillerId = new QName("BillerId");
		symbol = kcbInterfaceParam.addChildElement(BillerId);
		symbol.addTextNode(requestParam.getBillerId() + "");

		QName ServiceId = new QName("ServiceId");
		symbol = kcbInterfaceParam.addChildElement(ServiceId);
		symbol.addTextNode(requestParam.getServiceId() + "");

		QName SessionId = new QName("SessionId");
		symbol = kcbInterfaceParam.addChildElement(SessionId);
		symbol.addTextNode(requestParam.getSessionId()+"");
		
		QName CustomerMobileNo = new QName("MobileNo");
		symbol = kcbInterfaceParam.addChildElement(CustomerMobileNo);
		symbol.addTextNode(requestParam.getCustomerMobileNo()+"");

		QName TimeStamp = new QName("TimeStamp");
		symbol = kcbInterfaceParam.addChildElement(TimeStamp);
		symbol.addTextNode(requestParam.getTimeStamp()+"");
		

		QName MessageTypeIndicator = new QName("MessageTypeIndicator");
		symbol = kcbInterfaceParam.addChildElement(MessageTypeIndicator);
		symbol.addTextNode(requestParam.getMessageTypeIndicator());
//--------------------------------------------------------------------------
		QName PayLoad = new QName("ServiceParams");
		SOAPElement payLoad = kcbInterfaceParam.addChildElement(PayLoad);

		QName TransactionAmount = new QName("TransactionAmount");
		kcbInterfaceParam = payLoad.addChildElement(TransactionAmount);
		kcbInterfaceParam.addTextNode(requestParam.getTransactionAmount()+"");

		QName TransactionDate = new QName("TransactionDate");
		kcbInterfaceParam = payLoad.addChildElement(TransactionDate);
		kcbInterfaceParam.addTextNode(requestParam.getTransactionDate()+"");

		QName TransactionReference = new QName("TransactionReference");
		kcbInterfaceParam = payLoad.addChildElement(TransactionReference);
		kcbInterfaceParam.addTextNode(requestParam.getTransactionReference()+"");

		QName TransactionCurrency = new QName("Currency");
		kcbInterfaceParam = payLoad.addChildElement(TransactionCurrency);
		kcbInterfaceParam.addTextNode(requestParam.getTransactionCurrency()+"");

		QName CreditAccount = new QName("CreditAccount");
		kcbInterfaceParam = payLoad.addChildElement(CreditAccount);
		kcbInterfaceParam.addTextNode(requestParam.getCreditAccount()+"");

		QName DebitAccount = new QName("DebitAccount");
		kcbInterfaceParam = payLoad.addChildElement(DebitAccount);
		kcbInterfaceParam.addTextNode(requestParam.getDebitAccount()+"");

		QName PaymentDetails = new QName("PaymentDetails");
		kcbInterfaceParam = payLoad.addChildElement(PaymentDetails);
		kcbInterfaceParam.addTextNode(requestParam.getPaymentDetails()+"");

		/*QName CustomerId = new QName("CustomerId");
		kcbInterfaceParam = payLoad.addChildElement(CustomerId);
		kcbInterfaceParam.addTextNode(requestParam.getCustomerId()+"");*/

		

		System.out.println("----------SOAP Request------------\n");
		message.writeTo(System.out);

		return message;

	}

	private static KCBParameter createSoapResponse(SOAPMessage soapResponse) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		Source sourceContent = soapResponse.getSOAPPart().getContent();
		System.out.println("\n----------SOAP Response-----------\n");
		StreamResult result = new StreamResult(System.out);
		transformer.transform(sourceContent, result);
	
		//------------------------------------------------------------
		NodeList list = soapResponse.getSOAPBody().getElementsByTagName("ns3:BillerTransactionResponse");

		KCBParameter responseParam = new KCBParameter();
		Class cls = responseParam.getClass();
		Method getMethods = null;
		Field field = null;
		for (int i = 0; i < list.getLength(); i++) {
			NodeList innerList = list.item(i).getChildNodes();

			for (int j = 0; j < innerList.getLength(); j++) {
				getMethods = cls.getDeclaredMethod("set" + StringUtils.capitalize(innerList.item(j).getNodeName()), String.class);
				getMethods.invoke(responseParam, innerList.item(j).getTextContent());
				getMethods = cls.getDeclaredMethod("get" + StringUtils.capitalize(innerList.item(j).getNodeName()));
				System.out.println(getMethods.invoke(responseParam));
			}
		}
		return responseParam;
	}

	public static KCBParameter sendSoapMsg(SOAPMessage soapRequest, String url) throws Exception {
		KCBParameter kcbParameter =null;
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			kcbParameter=createSoapResponse(soapConnection.call(soapRequest, url));
			soapConnection.close();
		return kcbParameter;
	}

	public static void main(String[] args) throws Exception {
		//		KCBIntegration.createSoapRequet(new KCBParameter());
		String soapResponse = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + "<soap:Body>" + "<ns2:KCBDarajaInterfaceResponse xmlns:ns2=\"http://API.Interface.kcb/\" xmlns:ns3=\"http://kcbbankgroup.com/KCBDarajaInterface\">" + "<ns3:DarajaResponse>" + "<agentCommission>0</agentCommission>" + "<availableBalance>173022.64</availableBalance>" + "<bankCommission>0</bankCommission>" + "<creditAccount>5500117173</creditAccount>" + "<creditAccountName>LOMODONG CHARLES J W LOMOD0NG</creditAccountName>" + "<creditCurrency>SSP</creditCurrency>" + "<currentBalance>173022.64</currentBalance>" + "<darajaRefNo>DBVCPABBOAGBR61</darajaRefNo>" + "<debitAccount>5590387515</debitAccount>" + "<debitAccountName>SUDAN TEST ACCOUNT</debitAccountName>" + "<debitCurrency>SSP</debitCurrency>" + "<exciseDuty>0</exciseDuty>" + "<lcyAmount>1000</lcyAmount>" + "<originalRefNo>ZZRP1MU2021</originalRefNo>" + "<responseDate>2019-10-24 11:35:55</responseDate>" + "<responseID>DBVCPABBOAGBR61</responseID>" + "<status>1</status>" + "<statusCode>00</statusCode>" + "<statusDesc>Request Queued Successfully</statusDesc>" + "<totalCharge>0</totalCharge>" + "<totalCommission>0</totalCommission>" + "<transactionalAmount>1000</transactionalAmount>" + "<treasuryRate>0</treasuryRate>" + "</ns3:DarajaResponse>" + "</ns2:KCBDarajaInterfaceResponse>" + "</soap:Body>" + "</soap:Envelope> ";

		MessageFactory factory = MessageFactory.newInstance();
		SOAPMessage message = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(soapResponse.getBytes(Charset.forName("UTF-8"))));

		createSoapResponse(message);

	}

}
