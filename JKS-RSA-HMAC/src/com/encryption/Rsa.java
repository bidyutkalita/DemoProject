package com.encryption;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.InputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Rsa {

	public static KeyPair generateKeyPair() throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, new SecureRandom());
		KeyPair pair = generator.generateKeyPair();

		return pair;
	}

	public static KeyPair getKeyPairFromKeyStore() throws Exception {
		// Generated with:
		// keytool -genkeypair -alias mykey -storepass s3cr3t -keypass s3cr3t -keyalg
		// RSA -keystore keystore.jks

		InputStream ins = Rsa.class.getResourceAsStream("/airtime_keystore.jks");
		// KeyStore keyStore = KeyStore.getInstance("jks");
		KeyStore keyStore = KeyStore.getInstance("pkcs12");// RSA/ECB/PKCS1Padding
		keyStore.load(ins, "changeit".toCharArray()); // Keystore password
		KeyStore.PasswordProtection keyPassword = // Key password
				new KeyStore.PasswordProtection("G3k@siwMjR5z".toCharArray());
	
		KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("interface-ssc",
				keyPassword);

		java.security.cert.Certificate cert = keyStore.getCertificate("interface-ssc");// alias for the key
		PublicKey publicKey = cert.getPublicKey();
		PrivateKey privateKey = privateKeyEntry.getPrivateKey();

		return new KeyPair(publicKey, privateKey);
	}

	public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
		Cipher encryptCipher = Cipher.getInstance("RSA");
		encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

		byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));

		return Base64.getEncoder().encodeToString(cipherText);
	}

	public static String encrypt1(byte[] plainText) throws Exception {
		String private_key="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCjQtiPC6RvneRBnSrcGDJKmnbYT+j8rMdJh22RQ0Cg4SP7/6zYVefXC70xoOw4AGuDLJr06NLT6u2iFjB0qmdl/9TzlUYBMpuZtXX+g/FOx7unz9TsR+TDW++ZNptWTHAT1MYR73UMxdzhaLnuqAiAovaqNKwYlhYnP0W84hsv+i9xprbGJ1XLGjEm2uLlm4aKJdjA9sCdb4TLxeEI9UHZqbNcxaAA0tmcLSdtiEdbZiy3UC23dTQHuOhdXoXeZQ2Jat7Ry5hKIgnI1E64bAWDJ21bSi0jpJGv0bkclTSteC8H1jADXdk63MTtJmOB8Et830spceGIFhUTv3Q+4tM3AgMBAAECggEAZUv5gC/SFCscUBXLXQpqUd2rNaXHQteVQysTMZlPxUJMCHRn80bB9azj86IMTub7zkTj1GeNQE24qeQopoqAY0osAAKzj9SttN5hWszU8XN+HK4YR46IAyrM6/x0v0PoGnXfe4tezdEKrpVDIDAELoRulcberNaa4jkg9QQ5qDcNCLuxOgOVwQ5bJVR9AXDqIKve+Z1ez+EWlOx2c4iz1ThU0L9JS4epLQi+GFV1u2DmWYDNLlxmfuP+reK45s8AoJk27tvxPdtykTIM3VzeSqwgrjEZbdLCea1r95OTyeA0JlAdaMpoKercDjDV+1Y62Uqtbq1zhF1iJxzsH2XMcQKBgQDjiTT0l6lAwCGhUUk+NbxcLPd8HartsOIVpW7kNPtThzZahpWGfUQ5QvTu/twgpsr7TCMZw1t2K4iSKnnRblKqPzq28pLUPI/qrvwSlOALdX9sihvOKPtxDdNCkey5q90k2vSeY9g2OsJchUugptYco7LBApP+IQF+2WFGFk87uQKBgQC3r0AHiU1ZWliGcXBcxbFmGYcu4Mb+4PGlOrS4nvjlagoSKUVcHEVfPabHDBQ/1RtPrEqBe/T+raRZb34maS9bkUrLq/UBT5n9BlyusryZqd4ZeAei4m8N9Qco2nO6GTIKbiCox3Tb7g9G/PFHlGT+N6saU4XEUN63zk12wGpebwKBgQCelamn8/BOJdsKoSnoRvGSWPDgu5i/c3IP3i6stVSZwfqbG4QpqTMcqdMmo9DunPGKNmWcdjFP6kWUgjBHTJom+EzG58h4TgGMcwtTNBn3QpxeHOAGTz/4asdcdXJFckh+gHDP2A0AtvFyJKEyRjWi3mEe7toQ3BY7v+xxOeldWQKBgD/VOEQmM+okWnVGQM0MqqyXnIHu/Vif5XoNH7a1EDvxkNRk4US0UmtZqnarvPG95kLW1gGPiUFkKKQn+cfxb9K7eCm1dWxiNFpY9qaDWMVx4cwqCUVSuVM5XqaU2/jFPr2fLdxZjEPNxCNgTZ0ZdctenyiD9A2YKP3pRMRK8+wjAoGBAJVYiDFBuxOD59xa5PDBwixHJQIqTGiDrpG8+ZEku1dFafOzK6gFSyJLU3QQpLohRdjA269p1TzCIx3pizls6TTac2+xMrfYwKL7nLqinIIggZ9Kiytmp5tcj1kWq5gXydb5Fx5WEdLEtFFTXRi/UmYj0abmUP4qeTAjYLCaAwao";
		Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(private_key));
		PrivateKey privateKey1 = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
		encryptCipher.init(Cipher.ENCRYPT_MODE, privateKey1);
		byte[] cipherText = encryptCipher.doFinal(plainText);
		return Base64.getEncoder().encodeToString(cipherText);
	}

	public static String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
		byte[] bytes = Base64.getDecoder().decode(cipherText);

		Cipher decriptCipher = Cipher.getInstance("RSA");
		decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

		return new String(decriptCipher.doFinal(bytes), UTF_8);
	}

	public static String sign(String plainText, PrivateKey privateKey) throws Exception {
		Signature privateSignature = Signature.getInstance("SHA256withRSA");
		privateSignature.initSign(privateKey);
		privateSignature.update(plainText.getBytes(UTF_8));
		byte[] signature = privateSignature.sign();
		return Base64.getEncoder().encodeToString(signature);
	}

	public static String signEAirtime(String plainText) throws Exception {
		String hMacKey = "ECk0CLi9j(T6_ea7#_hHLOlk2y2XkHWx"; // key
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(hMacKey.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key);
		String httpDate = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC));
		byte[] hash=sha256_HMAC.doFinal((plainText + httpDate + "trinity").getBytes(UTF_8));
		return encrypt1(hash);
	}

	public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
		Signature publicSignature = Signature.getInstance("SHA256withRSA");
		publicSignature.initVerify(publicKey);
		publicSignature.update(plainText.getBytes(UTF_8));

		byte[] signatureBytes = Base64.getDecoder().decode(signature);
		return publicSignature.verify(signatureBytes);
	}

	public static void main(String... argv) throws Exception {
		// First generate a public/private key pair
		// KeyPair pair = generateKeyPair();
		KeyPair pair = getKeyPairFromKeyStore();

		// Our secret message
		String message = "the answer to life the universe and everything";

		// Encrypt the message
		String cipherText = encrypt(message, pair.getPublic());

		// Now decrypt it
		String decipheredMessage = decrypt(cipherText, pair.getPrivate());
		System.out.println(decipheredMessage);

		System.out.println(decipheredMessage);

		// Let's sign our message
		String signature = sign("foobar", pair.getPrivate());

		// Let's check the signature
		boolean isCorrect = verify("foobar", signature, pair.getPublic());
		System.out.println("Signature correct: " + isCorrect);
	}

}
