package com.eot.banking.dto;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class LocateUsDTO.
 */
public class LocateUsDTO extends TransactionBaseDTO{

	/** The location type name. */
	private String locationTypeName;

	/** The country id. */
	private Integer countryId;

	/** The city id. */
	private Integer cityId;

	/** The quater id. */
	private Integer quaterId;

	/** The bank id. */
	private String bankId;
	
	/** The network type id. */
	private Integer networkTypeId;

	/** The location type id. */
	private Integer locationTypeId;

	/** The address. */
	private List<String> address;
	
	/** The address. */
	private List<Address> addressList;

	public List<Address> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<Address> addressList) {
		this.addressList = addressList;
	}

	/** The latitude. */
	private String latitude;

	/** The longitude. */
	private String longitude;
	
	/**
	 * Gets the city id.
	 *
	 * @return the city id
	 */
	public Integer getCityId() {
		return cityId;
	}

	/**
	 * Sets the city id.
	 *
	 * @param cityId the new city id
	 */
	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	/**
	 * Gets the quater id.
	 *
	 * @return the quater id
	 */
	public Integer getQuaterId() {
		return quaterId;
	}

	/**
	 * Sets the quater id.
	 *
	 * @param quaterId the new quater id
	 */
	public void setQuaterId(Integer quaterId) {
		this.quaterId = quaterId;
	}

	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public List<String> getAddress() {
		return address;
	}

	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(List<String> address) {
		this.address = address;
	}

	/**
	 * Gets the latitude.
	 *
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * Sets the latitude.
	 *
	 * @param latitude the new latitude
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * Sets the longitude.
	 *
	 * @param longitude the new longitude
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * Gets the country id.
	 *
	 * @return the country id
	 */
	public Integer getCountryId() {
		return countryId;
	}

	/**
	 * Sets the country id.
	 *
	 * @param countryId the new country id
	 */
	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	/**
	 * Gets the bank id.
	 *
	 * @return the bank id
	 */
	public String getBankId() {
		return bankId;
	}

	/**
	 * Sets the bank id.
	 *
	 * @param bankId the new bank id
	 */
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	/**
	 * Gets the location type id.
	 *
	 * @return the location type id
	 */
	public Integer getLocationTypeId() {
		return locationTypeId;
	}

	/**
	 * Sets the location type id.
	 *
	 * @param locationTypeId the new location type id
	 */
	public void setLocationTypeId(Integer locationTypeId) {
		this.locationTypeId = locationTypeId;
	}

	/**
	 * Gets the location type name.
	 *
	 * @return the location type name
	 */
	public String getLocationTypeName() {
		return locationTypeName;
	}

	/**
	 * Sets the location type name.
	 *
	 * @param locationTypeName the new location type name
	 */
	public void setLocationTypeName(String locationTypeName) {
		this.locationTypeName = locationTypeName;
	}

	public Integer getNetworkTypeId() {
		return networkTypeId;
	}

	public void setNetworkTypeId(Integer networkTypeId) {
		this.networkTypeId = networkTypeId;
	}
	
	public class Address{
		
		private String name;
		private String address;
		private String mobile;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		
		
	}

}
