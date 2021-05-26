package com.encryption;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author smutuku
 */
public class MD5 {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		// TODO code application logic here
		System.out.println(MD5.EncryptedPassword("Username", "password", "sessionID"));
	}

	public static String EncryptedPassword(String transactionCode, String rawpassword, String sessionID) {
		try {

			// Static getInstance method is called with hashing MD5
			MessageDigest md = MessageDigest.getInstance("MD5");
			/*
			 * Steps:
			 * 
			 * 1. Hash the password into MD5 2. Convert the md5 into caps 3. Do Shah-512
			 * encryption using 1. Mode = SHA-512, 2. secret key = SessionID and 3. plain
			 * String to encrypt = MD% in number 2 4. The plain lowercase output if 3 is
			 * what you pass as password, maintaining the sessionID used
			 */

			// digest() method is called to calculate message digest
			// of an input digest() return array of byte
			String rawPassword = rawpassword;
			String SessionId = sessionID;
			String TransactionCode = transactionCode;
			String MD5PsswordHash = "";
			byte[] messageDigest = md.digest(rawPassword.getBytes());

			// Convert byte array into signum representation
			BigInteger passSignum = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			MD5PsswordHash = passSignum.toString(16);
			while (MD5PsswordHash.length() < 32) {
				MD5PsswordHash = "0" + MD5PsswordHash;
			}
			MD5PsswordHash = MD5PsswordHash.toUpperCase();

			// System.out.println("hashtext " + MD5PsswordHash);
			String Hash_Shah512PlusSalt_output = Hash_Shah512PlusSalt(TransactionCode, SessionId, rawPassword,
					MD5PsswordHash);
			// System.out.println("Hash_Shah512PlusSalt_output = " +
			// Hash_Shah512PlusSalt_output);
			return Hash_Shah512PlusSalt_output;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}

	}

	private static String Hash_Shah512PlusSalt(String transactioncode, String SessionID, String Password,
			String md5Hashed) {
		String hashedOutput = "Auth2DefaultOutput";
		String HashingMode = "SHA-512";
		// System.out.println("TXN ID : " + transactioncode + " : Utils |
		// Hash_Shah512PlusSalt | Timestamp " + SessionID + " Password " + Password);
		try {
			// String md5Hashed = md5Hash(Password,transactioncode);
			// System.out.println("TXN ID : " + transactioncode + " : Utils |
			// Hash_Shah512PlusSalt | md5Hashed " + md5Hashed);
			MessageDigest md = MessageDigest.getInstance(HashingMode);
			md.update(SessionID.getBytes(StandardCharsets.UTF_8));
			byte[] bytes = md.digest(md5Hashed.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			hashedOutput = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("TXN ID : " + transactioncode + " : Utils | Hash_Shah512PlusSalt | " + e);
			e.printStackTrace();
		}
		return hashedOutput;
	}
}