package org.jity.common.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * AES Crypting and DeCrypting
 * 
 * @author 09344a
 * 
 */
public abstract class StringCrypter {

	/**
	 * Encrypt input String
	 * 
	 * @param stringToCrypt
	 * @param keyString
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String encrypt(String stringToCrypt, String keyString) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		SecretKeySpec key = new SecretKeySpec(keyString.getBytes(), "AES");

		Cipher enCipher = Cipher.getInstance("AES");
		enCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = enCipher.doFinal(stringToCrypt.getBytes());
		String encryptedValue = new Base64().encodeToString(encVal);
		return encryptedValue;
	}

	/**
	 * Decrypt input String
	 * 
	 * @param encryptedString
	 * @param keyString
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String decrypt(String encryptedString, String keyString) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		SecretKeySpec key = new SecretKeySpec(keyString.getBytes(), "AES");

		Cipher deCipher = Cipher.getInstance("AES");
		deCipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue = new Base64().decode(encryptedString);
		byte[] decValue = deCipher.doFinal(decordedValue);
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}

	public static void main(String[] args) {
		try {

			String stringToCrypt = "<org.jity.common.protocol.JityRequest>  <instructionName>STARTEXECMANAGER</instructionName></org.jity.common.protocol.JityRequest>";
			String keyString = "JiTyCedricFred13";

			System.out.println("String To Crypt: " + stringToCrypt);

			String cryptedString = StringCrypter.encrypt(stringToCrypt, keyString);

			System.out.println(cryptedString);

			String decryptedString = StringCrypter.decrypt(cryptedString, keyString);

			System.out.println("DecryptedString: " + decryptedString);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}