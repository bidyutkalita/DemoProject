package com.eot.banking.service.impl;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.eot.banking.common.CoreUrls;
import com.eot.banking.common.EOTConstants;
import com.eot.banking.common.OtpStatusEnum;
import com.eot.banking.dto.DstvDTO;
import com.eot.banking.dto.DstvPackageDetails;
import com.eot.banking.dto.KwiksyDTO;
import com.eot.banking.dto.KwiksyExchangeRateDTO;
import com.eot.banking.dto.KwiksyProvider;
import com.eot.banking.dto.KwiksyResponse;
import com.eot.banking.dto.KwiksyTransaction;
import com.eot.banking.dto.NileBatDTO;
import com.eot.banking.dto.ServiceChargeDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.server.data.OtpDTO;
import com.eot.banking.service.OtherBankingService;
import com.eot.banking.service.OtherTransactionService;
import com.eot.banking.utils.DateUtil;
import com.eot.dtos.banking.RemittanceTransactionDTO;
import com.eot.dtos.common.Header;
import com.eot.dtos.utilities.VoucherTopupDTO;
import com.eot.entity.Account;
import com.eot.entity.BusinessPartner;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.ExternalTransaction;
import com.eot.entity.Otp;
import com.eot.entity.RemittanceTransaction;

@Service
public class OtherTransactionServiceImpl implements OtherTransactionService {
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private BaseServiceImpl baseServiceImpl;

	@Autowired
	private OtherBankingService otherBankingService;

	@Override
	public KwiksyDTO getProvicers(KwiksyDTO dto) throws EOTException {

		baseServiceImpl.handleRequest(dto);

		if (StringUtils.isEmpty(dto.getCountry()))
			throw new EOTException(ErrorConstants.COUNTRY_FIELD_EMPTY);

		restTemplate.headForHeaders("http://app.kwiksy.com/api/getProviders");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("clientId", "69873");
		map.add("clientSecret", "107b3d1cf23362e6");
		map.add("country", dto.getCountry());

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

		ResponseEntity<KwiksyProvider[]> response = restTemplate.postForEntity("http://app.kwiksy.com/api/getProviders/", request, KwiksyProvider[].class);
		if (response.getBody() == null || response.getBody().length == 0)
			throw new EOTException(ErrorConstants.NO_PROVIDER_FOUND);

		try {
			dto=getexchangerate(dto);
		}catch (Exception e) {
//			e.printStackTrace();
		}

		dto.setProviderList(response.getBody());
		return dto;
	}


	@Override
	public KwiksyDTO getexchangerate(KwiksyDTO dto) throws EOTException {

		//		baseServiceImpl.handleRequest(dto);

		if (StringUtils.isEmpty(dto.getCountry()))
			throw new EOTException(ErrorConstants.COUNTRY_FIELD_EMPTY);

		//		restTemplate.headForHeaders("http://app.kwiksy.com/api/getexchangerate");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("clientId", "69873");
		map.add("clientSecret", "107b3d1cf23362e6");
		map.add("reciever_country", dto.getCountry());
		map.add("sender_country", "211");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

		ResponseEntity<KwiksyExchangeRateDTO> response = restTemplate.postForEntity("http://app.kwiksy.com/api/getexchangerate/", request, KwiksyExchangeRateDTO.class);
		if (response.getBody() == null )
			throw new EOTException(ErrorConstants.NO_PROVIDER_FOUND);

		dto.setExangeRate(response.getBody());
		return dto;
	}

	@Override
	public KwiksyDTO remittanceOutwardValidate(KwiksyDTO dto) throws EOTException {

		baseServiceImpl.handleRequest(dto);

		RemittanceTransaction remitance = null;
		dto.setRefTxnNumber(genTxnRef());

		/*if(!baseServiceImpl.customer.getTransactionPin().equalsIgnoreCase(dto.getTransactionPIN()))
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);*/
		remitance = baseServiceImpl.eotMobileDao.getRemitanceTxnByRefTxnId(dto.getRefTxnNumber());
		if (remitance != null) {
			dto.setRefTxnNumber(genTxnRef());
		}
		Customer sender = baseServiceImpl.eotMobileDao.getCustomerByMobile(dto.getSender());
		if(sender==null)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);

		if(sender.getType().intValue()!=EOTConstants.REFERENCE_TYPE_CUSTOMER)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		
		

		if(baseServiceImpl.customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT)
		{

			OtpDTO otpDto = new OtpDTO();
			otpDto.setOtphash(dto.getCustomerOTP());
			otpDto.setReferenceId(sender.getCustomerId() + "");
			otpDto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
			otpDto.setOtpType(Constants.OTP_TYPE_REMITTANCE_OUTWARD);
			otpDto.setAmount(dto.getAmount());
			Otp otp = baseServiceImpl.eotMobileDao.verifyOTP(otpDto);

			System.out.println("customerPin - " + dto.getCustomerOTP());
			System.out.println("db - " + otpDto.getOtphash());
			if (otp == null) {
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_OTP);
			}
			otp.setStatus(OtpStatusEnum.USED.getCode());
			baseServiceImpl.eotMobileDao.update(otp);

		}

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("clientId", "69873");
		map.add("clientSecret", "107b3d1cf23362e6");
		map.add("country", dto.getCountryCode().toString());
		map.add("requestReference", dto.getRefTxnNumber());
		map.add("destinationPhone",dto.getCountryCode().toString()+ dto.getReceiver());
		map.add("amount", dto.getAmount().toString());
		map.add("provider_id", dto.getProviderId().toString());

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

		String result =
			restTemplate.postForEntity("http://app.kwiksy.com/api/validateTransaction",
				request, String.class).getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		KwiksyResponse kwiksyResponse;
		remitance = new RemittanceTransaction();
		try {
			kwiksyResponse = objectMapper.readValue(result, KwiksyResponse.class);


			if(kwiksyResponse.getResponseCode().equals("00") && kwiksyResponse.getResponseStatus().equals("true")) {
				KwiksyTransaction kwiksyTransaction = objectMapper.readValue(result, KwiksyTransaction.class);
				remitance.setValidationCode(kwiksyTransaction.getResponseMessage().get(0).getValidation_code().longValue());
				remitance.setCommissionCurrency(kwiksyTransaction.getResponseMessage().get(0).getCommissioncurrency());
				remitance.setCommission(kwiksyTransaction.getResponseMessage().get(0).getCommissionamount().doubleValue());
				dto.setCommissioncurrency(remitance.getCommissionCurrency());
				dto.setCommission(remitance.getCommission());
			}else {
				throw new EOTException(ErrorConstants.SERVICE_ERROR);
			}
		} catch (Exception e1) {
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}
		ServiceChargeDTO serviceChargeDTO = new ServiceChargeDTO();
		serviceChargeDTO.setTransactionType(dto.getTransactionType());
		serviceChargeDTO.setTxnBankingType(EOTConstants.TXN_TYPE_INTRA);
		serviceChargeDTO.setTxnTypeId(dto.getTransactionType());
		serviceChargeDTO.setApplicationType("1");
		serviceChargeDTO.setAmount(dto.getAmount());
		serviceChargeDTO.setTxnAmount(dto.getAmount());

		try {
			serviceChargeDTO=otherBankingService.getServiceCharge(serviceChargeDTO);
		}catch (Exception e) {
//			e.printStackTrace();
		}


		remitance.setAmount(dto.getAmount());
		remitance.setClientId(dto.getClientId());
		remitance.setCountryCode(dto.getCountryCode());
		remitance.setProviderId(dto.getProviderId());
		remitance.setProviderName(dto.getProviderName());
		remitance.setReceiver(dto.getReceiver());
		//remitance.setReceiverCode(receiverCode);
		remitance.setReferenceId(dto.getRefTxnNumber());
		remitance.setReferenceType(dto.getReferenceType());
		remitance.setRefTxnNumber(dto.getRefTxnNumber());
		remitance.setReferenceType(baseServiceImpl.customer.getType());
		remitance.setRemittanceDate(dto.getRemittanceDate());
		remitance.setSender(dto.getSender());
		remitance.setStatus(Constants.REMITANCE_STATUS_INITIATED);
		remitance.setClientId("10");
		remitance.setCountryCode(dto.getCountryCode());
		remitance.setRemittanceDate(new Date());
		if(StringUtils.isEmpty(dto.getBankCode()));
		

		baseServiceImpl.eotMobileDao.save(remitance);


		dto.setServiceCharge(serviceChargeDTO.getServiceChargeAmt());
//		dto.setMessageDescription(baseServiceImpl.messageSource.getMessage("REMITTANCE_OUTWORDS_VALIDATION", new String[] { DateUtil.formattedDateAndTime(new Date()),dto.getProviderName() ,null != dto.getAmount() ? new DecimalFormat("#0.00").format(dto.getAmount().doubleValue()) : "0.00", dto.getMobileNumber(), null != remitance.getCommission() ? new DecimalFormat("#0.00").format(remitance.getCommission().doubleValue()) : "0.00", serviceChargeDTO.getServiceChargeAmt()+"",dto.getRemarks()==null?"International Remittance":dto.getRemarks() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
//		dto.setSuccessResponse(baseServiceImpl.messageSource.getMessage("REMITTANCE_OUTWORDS_VALIDATION", new String[] { DateUtil.formattedDateAndTime(new Date()),dto.getProviderName(), null != dto.getAmount() ? new DecimalFormat("#0.00").format(dto.getAmount().doubleValue()) : "0.00", dto.getMobileNumber(), null != remitance.getCommission() ? new DecimalFormat("#0.00").format(remitance.getCommission().doubleValue()) : "0.00", serviceChargeDTO.getServiceChargeAmt()+"", dto.getRemarks()==null?"International Remittance":dto.getRemarks()}, new Locale(baseServiceImpl.customer.getDefaultLanguage())));

		return dto;

	}

	@Override
	public KwiksyDTO remittanceOutward(KwiksyDTO dto) throws EOTException {

		baseServiceImpl.handleRequest(dto);

		RemittanceTransaction remitance = null;
//		dto.setRefTxnNumber(genTxnRef());

		if(!baseServiceImpl.customer.getTransactionPin().equalsIgnoreCase(dto.getTransactionPIN()))
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		remitance = baseServiceImpl.eotMobileDao.getRemitanceTxnByRefTxnId(dto.getRefTxnNumber());
		if (remitance == null) {
//			dto.setRefTxnNumber(genTxnRef());
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}
		Customer sender = baseServiceImpl.eotMobileDao.getCustomerByMobile(dto.getSender());
		if(sender==null)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);

		System.out.println("customer Type:====> "+sender.getType().intValue());
		if(sender.getType().intValue()!=EOTConstants.REFERENCE_TYPE_CUSTOMER)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);


//		remitance = new RemittanceTransaction();
		remitance.setAmount(dto.getAmount());
		remitance.setClientId(dto.getClientId());
		remitance.setCountryCode(dto.getCountryCode());
		remitance.setProviderId(dto.getProviderId());
		remitance.setProviderName(dto.getProviderName());
		remitance.setReceiver(dto.getReceiver());
		//		remitance.setReceiverCode(receiverCode);
		remitance.setReferenceId(dto.getRefTxnNumber());
		remitance.setReferenceType(dto.getReferenceType());
		remitance.setRefTxnNumber(dto.getRefTxnNumber());
		remitance.setReferenceType(baseServiceImpl.customer.getType());
		remitance.setRemittanceDate(dto.getRemittanceDate());
		remitance.setSender(dto.getSender());
		remitance.setStatus(Constants.REMITANCE_STATUS_INITIATED);
		remitance.setClientId("10");
		remitance.setCountryCode(dto.getCountryCode());
		remitance.setRemittanceDate(new Date());

		baseServiceImpl.eotMobileDao.update(remitance);

		BusinessPartner bp = baseServiceImpl.eotMobileDao.getBusinessPartnerByPartnerType(EOTConstants.BP_TYPE_REMITTANCE);

		Account remittanceAcc = baseServiceImpl.eotMobileDao.getAccount(bp.getAccountNumber());


		CustomerAccount senderAcc = baseServiceImpl.eotMobileDao.getCustomerAccountWithCustomerId(sender.getCustomerId());

		com.eot.dtos.common.Account senderAccount = new com.eot.dtos.common.Account();
		senderAccount.setAccountAlias(senderAcc.getAccount().getAlias());
		senderAccount.setAccountNO(senderAcc.getAccountNumber());
		senderAccount.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		senderAccount.setBankCode(senderAcc.getBank().getBankId().toString());
		senderAccount.setBranchCode(senderAcc.getBranch().getBranchId().toString());

		com.eot.dtos.common.Account otherAcc = new com.eot.dtos.common.Account();
		otherAcc.setAccountAlias(remittanceAcc.getAlias());
		otherAcc.setAccountNO(remittanceAcc.getAccountNumber());//only for testing
		otherAcc.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		otherAcc.setBankCode(senderAcc.getBank().getBankId().toString());
		otherAcc.setBranchCode(senderAcc.getBranch().getBranchId().toString());


		/**
		 * transactional call to core
		 */

		RemittanceTransactionDTO remittanceTransactionDTO = new RemittanceTransactionDTO();
		remittanceTransactionDTO.setAmount(dto.getAmount());
		remittanceTransactionDTO.setClientId(dto.getClientId());
		remittanceTransactionDTO.setCountryCode(dto.getCountryCode());
		remittanceTransactionDTO.setProviderId(dto.getProviderId());
		remittanceTransactionDTO.setProviderName(dto.getProviderName());
		//		remittanceTransactionDTO.setReceiverCode(receiverCode);
		remittanceTransactionDTO.setReferenceID(dto.getReferenceId());
		remittanceTransactionDTO.setReferenceType(baseServiceImpl.customer.getType());//
		remittanceTransactionDTO.setRefTxnNumber(dto.getRefTxnNumber());
		remittanceTransactionDTO.setReceiver(dto.getReceiver());
		remittanceTransactionDTO.setRemittanceDate(dto.getRemittanceDate());
		remittanceTransactionDTO.setSender(dto.getSender());
		remittanceTransactionDTO.setValidationCode(remitance.getValidationCode()+"");

		remittanceTransactionDTO.setCustomerAccount(senderAccount);
		remittanceTransactionDTO.setOtherAccount(otherAcc);
		remittanceTransactionDTO.setTransactionType(dto.getTransactionType()+"");
		remittanceTransactionDTO.setReferenceID("1");// need to set remitars id 
		remittanceTransactionDTO.setChannelType(Constants.EOT_CHANNEL);

		remittanceTransactionDTO = processRequest(CoreUrls.REMITANCE_TRANSACTION, remittanceTransactionDTO, com.eot.dtos.banking.RemittanceTransactionDTO.class);
		
		if (remittanceTransactionDTO.getErrorCode() != 0) {
			remitance.setStatus(Constants.REMITANCE_STATUS_FAILED);
			baseServiceImpl.eotMobileDao.update(remitance);
			throw new EOTException(remittanceTransactionDTO.getErrorCode());
		}
		dto.setTransactionId(Long.parseLong(remittanceTransactionDTO.getTransactionNO()));
		dto.setMessageDescription(baseServiceImpl.messageSource.getMessage("REMITTANCE_OUTWORDS_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()),dto.getProviderName() ,null != dto.getAmount() ? new DecimalFormat("#0.00").format(dto.getAmount().doubleValue()) : "0.00",new DecimalFormat("#0.00").format(dto.getAmount().doubleValue()+remittanceTransactionDTO.getServiceChargeAmt()), null != remittanceTransactionDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(remittanceTransactionDTO.getServiceChargeAmt().doubleValue()) : "0.00",remitance.getCommission().toString(),new DecimalFormat("#0.00").format(dto.getAmount()-remittanceTransactionDTO.getServiceChargeAmt()), dto.getMobileNumber(), remittanceTransactionDTO.getRemarks()==null?"International Remittance":remittanceTransactionDTO.getRemarks(), remittanceTransactionDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
		dto.setSuccessResponse(baseServiceImpl.messageSource.getMessage("REMITTANCE_OUTWORDS_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()),dto.getProviderName() ,null != dto.getAmount() ? new DecimalFormat("#0.00").format(dto.getAmount().doubleValue()) : "0.00",new DecimalFormat("#0.00").format(dto.getAmount().doubleValue()+remittanceTransactionDTO.getServiceChargeAmt()), null != remittanceTransactionDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(remittanceTransactionDTO.getServiceChargeAmt().doubleValue()) : "0.00",remitance.getCommission().toString(),new DecimalFormat("#0.00").format(dto.getAmount()-remittanceTransactionDTO.getServiceChargeAmt()), dto.getMobileNumber(), remittanceTransactionDTO.getRemarks()==null?"International Remittance":remittanceTransactionDTO.getRemarks(), remittanceTransactionDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));

		if (remitance != null) {
			remitance.setTransactionId(Long.parseLong(remittanceTransactionDTO.getTransactionNO()));
			remitance.setStatus(Constants.REMITANCE_STATUS_SUCCESS);
			baseServiceImpl.eotMobileDao.update(remitance);
		}
		return dto;

	}

	@Override
	public NileBatDTO purchesNileBettingCoin(NileBatDTO dto) throws EOTException {
		baseServiceImpl.handleRequest(dto);
		int nilebatUnitPer=1;
		if(!baseServiceImpl.customer.getTransactionPin().equalsIgnoreCase(dto.getTransactionPIN()))
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);

		double txnAmount=nilebatUnitPer*dto.getNileBatUnit();
		if(txnAmount==0.0)
			throw new EOTException(ErrorConstants.INVALID_BILL_AMOUNT);

		System.out.println("amount"+ txnAmount);


		ExternalTransaction externalTransaction = new ExternalTransaction();
		externalTransaction.setMobileNumber(baseServiceImpl.customer.getMobileNumber());
		externalTransaction.setAmount(txnAmount);
		externalTransaction.setBeneficiaryMobileNumber(dto.getBeneficiaryMobileNumber());
		externalTransaction.setCustomerName(dto.getCustomerName());
		externalTransaction.setServiceChargeAmount(0);
		externalTransaction.setStan(dto.getStan());
		externalTransaction.setCustomerName(baseServiceImpl.customer.getFirstName()+" "+baseServiceImpl.customer.getLastName());
		externalTransaction.setTransactionType(Constants.TXN_TYPE_PURCHES_NILE_UNIT);
		externalTransaction.setTransactionDate(new Date());
		externalTransaction.setStatus(Constants.REMITANCE_STATUS_INITIATED);
		externalTransaction.setReferencedId(baseServiceImpl.customer.getCustomerId());
		externalTransaction.setEntityName("betting");

		baseServiceImpl.eotMobileDao.save(externalTransaction);
		CustomerAccount custAcc= baseServiceImpl.eotMobileDao.getCustomerAccountWithCustomerId(baseServiceImpl.customer.getCustomerId());

		com.eot.dtos.common.Account customerAccount = new com.eot.dtos.common.Account();
		customerAccount.setAccountAlias(custAcc.getAccount().getAlias());
		customerAccount.setAccountNO(custAcc.getAccountNumber());
		customerAccount.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		customerAccount.setBankCode(custAcc.getBank().getBankId().toString());
		customerAccount.setBranchCode(custAcc.getBranch().getBranchId().toString());

		/*com.eot.dtos.common.Account otherAcc = new com.eot.dtos.common.Account();
		otherAcc.setAccountAlias("Agent Commission Account - mGurush");//only for testing
		otherAcc.setAccountNO("1000000001478");//only for testing
		otherAcc.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		otherAcc.setBankCode(custAcc.getBank().getBankId().toString());
		otherAcc.setBranchCode(custAcc.getBranch().getBranchId().toString());*/

		BusinessPartner bp = baseServiceImpl.eotMobileDao.getBusinessPartnerByPartnerType(EOTConstants.BP_TYPE_NILE_BET);

		Account remittanceAcc = baseServiceImpl.eotMobileDao.getAccount(bp.getAccountNumber());

		com.eot.dtos.common.Account otherAcc = new com.eot.dtos.common.Account();
		otherAcc.setAccountAlias(remittanceAcc.getAlias());
		otherAcc.setAccountNO(remittanceAcc.getAccountNumber());//only for testing
		otherAcc.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		otherAcc.setBankCode(custAcc.getBank().getBankId().toString());
		otherAcc.setBranchCode(custAcc.getBranch().getBranchId().toString());


		Header header = new Header();
		header.setCustomerAccount(customerAccount);
		header.setOtherAccount(otherAcc);
		header.setAmount(txnAmount);
		header.setTransactionType(dto.getTransactionType()+"");
		header.setReferenceID(baseServiceImpl.customer.getCustomerId()+"");
		header.setChannelType(Constants.EOT_CHANNEL);
		header.setMobileNumber(dto.getBeneficiaryMobileNumber());



		externalTransaction=baseServiceImpl.eotMobileDao.getExternalTransaction(dto.getBeneficiaryMobileNumber());

		header = processRequest(CoreUrls.NILE_BETTING_UNIT_PURCHASE, header, Header.class);
		if (header.getErrorCode() != 0) {
			if(header.getTransactionNO()!=null) {
				externalTransaction.setStatus(Constants.REMITANCE_STATUS_FAILED);
				externalTransaction.setTransactionId(Long.parseLong(header.getTransactionNO()));
			}
			baseServiceImpl.eotMobileDao.update(externalTransaction);

			throw new EOTException(header.getErrorCode());
		}
		externalTransaction.setTransactionId(Long.parseLong(header.getTransactionNO()));
		externalTransaction.setStatus(Constants.REMITANCE_STATUS_SUCCESS);
		externalTransaction.setServiceChargeAmount(header.getServiceChargeAmt());

		baseServiceImpl.eotMobileDao.update(externalTransaction);
		if(StringUtils.isEmpty(dto.getRemarks()))
		{
			dto.setRemarks("NileBet Unit Purchase");
		}
		dto.setMessageDescription(baseServiceImpl.messageSource.getMessage("NILE_BETTING_COIN_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), new DecimalFormat("#0.00").format(txnAmount), dto.getBeneficiaryMobileNumber(), null != header.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(header.getServiceChargeAmt().doubleValue()) : "0.00", StringUtils.isEmpty(dto.getRemarks())?"Betting":dto.getRemarks(), header.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
		dto.setSuccessResponse(baseServiceImpl.messageSource.getMessage("NILE_BETTING_COIN_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()),new DecimalFormat("#0.00").format(txnAmount) ,dto.getBeneficiaryMobileNumber(), null != header.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(header.getServiceChargeAmt()) : "0.00", StringUtils.isEmpty(dto.getRemarks())?"Betting":dto.getRemarks(), header.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));


		return dto;
	}

	@Override
	public DstvDTO getDstvPacakages(DstvDTO dto) throws EOTException {

		baseServiceImpl.handleRequest(dto);


		//		restTemplate.headForHeaders("http://app.kwiksy.com/api/getProviders");
		HttpHeaders headers = new HttpHeaders();
//		headers.set("x-api-key", "557hdnk22844757gt"); // uat
		headers.set("x-api-key", "test"); //prod
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<DstvDTO> entityReq = new HttpEntity<DstvDTO>( headers);

		HttpEntity<DstvPackageDetails[]> response = restTemplate.exchange("http://212.71.246.92:8080/apiv1/packages", HttpMethod.GET, entityReq, DstvPackageDetails[].class);// uat
//		HttpEntity<DstvPackageDetails[]> response = restTemplate.exchange("https://agoro.co:4443/apiv1/packages", HttpMethod.GET, entityReq, DstvPackageDetails[].class); //prod

		List<DstvPackageDetails> list = Arrays.asList(response.getBody());

		dto.setData(list);


		return dto;
	}

	@Override
	public DstvDTO rechargeDstv(DstvDTO dto) throws EOTException {
		baseServiceImpl.handleRequest(dto);
		int nilebatUnitPer=1;
		if(!baseServiceImpl.customer.getTransactionPin().equalsIgnoreCase(dto.getTransactionPIN()))
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);

		if(dto.getAmount()==0.0)
			throw new EOTException(ErrorConstants.INVALID_BILL_AMOUNT);


		ExternalTransaction externalTransaction = new ExternalTransaction();
		externalTransaction.setMobileNumber(dto.getMobileNumber());
		externalTransaction.setAmount(dto.getAmount());
		externalTransaction.setBeneficiaryMobileNumber(dto.getCardSerialNo());
		externalTransaction.setCustomerName(baseServiceImpl.customer.getFirstName()+" "+baseServiceImpl.customer.getLastName());
		externalTransaction.setServiceChargeAmount(0);
		externalTransaction.setStan(dto.getStan());
		externalTransaction.setTransactionType(Constants.TXN_TYPE_RECHARGE_DSTV);
		externalTransaction.setTransactionDate(new Date());
		externalTransaction.setStatus(Constants.REMITANCE_STATUS_INITIATED);
		externalTransaction.setReferencedId(baseServiceImpl.customer.getCustomerId());
		externalTransaction.setEntityName("DSTV");
		baseServiceImpl.eotMobileDao.save(externalTransaction);

		CustomerAccount custAcc= baseServiceImpl.eotMobileDao.getCustomerAccountWithCustomerId(baseServiceImpl.customer.getCustomerId());

		com.eot.dtos.common.Account customerAccount = new com.eot.dtos.common.Account();
		customerAccount.setAccountAlias(custAcc.getAccount().getAlias());
		customerAccount.setAccountNO(custAcc.getAccountNumber());
		customerAccount.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		customerAccount.setBankCode(custAcc.getBank().getBankId().toString());
		customerAccount.setBranchCode(custAcc.getBranch().getBranchId().toString());

		/*com.eot.dtos.common.Account otherAcc = new com.eot.dtos.common.Account();
		otherAcc.setAccountAlias("Agent Commission Account - mGurush");//only for testing
		otherAcc.setAccountNO("1000000001478");//only for testing
		otherAcc.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		otherAcc.setBankCode(custAcc.getBank().getBankId().toString());
		otherAcc.setBranchCode(custAcc.getBranch().getBranchId().toString());*/


		BusinessPartner bp = baseServiceImpl.eotMobileDao.getBusinessPartnerByPartnerType(EOTConstants.BP_TYPE_DSTV);

		Account remittanceAcc = baseServiceImpl.eotMobileDao.getAccount(bp.getAccountNumber());

		com.eot.dtos.common.Account otherAcc = new com.eot.dtos.common.Account();
		otherAcc.setAccountAlias(remittanceAcc.getAlias());
		otherAcc.setAccountNO(remittanceAcc.getAccountNumber());//only for testing
		otherAcc.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		otherAcc.setBankCode(custAcc.getBank().getBankId().toString());
		otherAcc.setBranchCode(custAcc.getBranch().getBranchId().toString());

		VoucherTopupDTO voucherTopupDTO = new VoucherTopupDTO();
		voucherTopupDTO.setCustomerAccount(customerAccount);
		voucherTopupDTO.setOtherAccount(otherAcc);
		voucherTopupDTO.setAmount(dto.getAmount());
		voucherTopupDTO.setTransactionType(dto.getTransactionType()+"");
		voucherTopupDTO.setReferenceID(baseServiceImpl.customer.getCustomerId()+"");
		voucherTopupDTO.setChannelType(Constants.EOT_CHANNEL);
		voucherTopupDTO.setVoucherSrNo(dto.getCardSerialNo());
		voucherTopupDTO.setMobileNumber(dto.getMobileNumber());



		externalTransaction=baseServiceImpl.eotMobileDao.getExternalTransaction(dto.getCardSerialNo());

		voucherTopupDTO = processRequest(CoreUrls.DSTV_RECHARGE, voucherTopupDTO, VoucherTopupDTO.class);
		if (voucherTopupDTO.getErrorCode() != 0) {
			if(voucherTopupDTO.getTransactionNO()!=null) {
				externalTransaction.setStatus(Constants.REMITANCE_STATUS_FAILED);
				externalTransaction.setTransactionId(Long.parseLong(voucherTopupDTO.getTransactionNO()));
			}
			baseServiceImpl.eotMobileDao.update(externalTransaction);

			throw new EOTException(voucherTopupDTO.getErrorCode());
		}
		externalTransaction.setTransactionId(Long.parseLong(voucherTopupDTO.getTransactionNO()));
		externalTransaction.setStatus(Constants.REMITANCE_STATUS_SUCCESS);
		externalTransaction.setServiceChargeAmount(voucherTopupDTO.getServiceChargeAmt());
		baseServiceImpl.eotMobileDao.update(externalTransaction);
		dto.setMessageDescription(baseServiceImpl.messageSource.getMessage("DSTV_RECHARGE_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != dto.getAmount() ? new DecimalFormat("#0.00").format(dto.getAmount().doubleValue()) : "0.00", dto.getCardSerialNo(),dto.getPackageName(), null != voucherTopupDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(voucherTopupDTO.getServiceChargeAmt().doubleValue()) : "0.00",StringUtils.isEmpty(dto.getRemarks())?"Dstv":dto.getRemarks(), voucherTopupDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
		dto.setSuccessResponse(baseServiceImpl.messageSource.getMessage("DSTV_RECHARGE_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != dto.getAmount() ? new DecimalFormat("#0.00").format(dto.getAmount().doubleValue()) : "0.00", dto.getCardSerialNo(),dto.getPackageName(), null != voucherTopupDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(voucherTopupDTO.getServiceChargeAmt().doubleValue()) : "0.00", StringUtils.isEmpty(dto.getRemarks())?"Dstv":dto.getRemarks(), voucherTopupDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));


		return dto;
	}
	/**
	 * @author bidyut
	 * @param url
	 * @param obj
	 * @param type
	 * @return
	 */
	public <T extends com.eot.dtos.common.Header> T processRequest(String url, T obj, Class<T> type) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
		obj.setRequestChannel(EOTConstants.REQUEST_CHANNEL_MOBILE);
		obj = restTemplate.postForObject(url, obj, type);
		return obj;
	}

	private String genTxnRef() {
		Random r = new Random(System.currentTimeMillis());
		return "MGRU" + (10000000 + r.nextInt(90000000));
	}

	public static void main(String[] args) {
		/* try { KwiksyServiceImpl aiImpl = new KwiksyServiceImpl();
		 * aiImpl.getProvicers(new KwiksyDTO()); } catch (Exception e) { // TODO: handle
		 * exception } */
		System.out.println(new OtherTransactionServiceImpl().genTxnRef());
	}

}
