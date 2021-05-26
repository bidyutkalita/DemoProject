/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: Card.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:25 PM Sambit Created
*/
package com.eot.banking.server.data;


// TODO: Auto-generated Javadoc
/**
 * The Class Card.
 */
public class Card {

	/** The card alias. */
	public String cardAlias;
	
	/** The is confirmed. */
	public boolean isConfirmed;
	
	/**
	 * Instantiates a new card.
	 * 
	 * @param cardAlias
	 *            the card alias
	 * @param isConfirmed
	 *            the is confirmed
	 */
	public Card( String cardAlias, boolean isConfirmed ) {
		
		this.cardAlias = cardAlias;
		this.isConfirmed = isConfirmed;
	}
}
