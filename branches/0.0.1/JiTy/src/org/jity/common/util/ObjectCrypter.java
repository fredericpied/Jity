package org.jity.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.jity.UIClient.swt.Main;

public class ObjectCrypter {

	private Cipher deCipher;
	private Cipher enCipher;
	private SecretKeySpec key;

	public ObjectCrypter(byte[] keyBytes) {
		try {
			DESKeySpec dkey = new DESKeySpec(keyBytes);
			key = new SecretKeySpec(dkey.getKey(), "AES");
			deCipher = Cipher.getInstance("AES");
			
			// "AES/CTR/NoPadding"
			// "DES/CBC/PKCS5Padding"
			enCipher = Cipher.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] encrypt(Object obj) throws InvalidKeyException, InvalidAlgorithmParameterException, IOException,
			IllegalBlockSizeException, ShortBufferException, BadPaddingException {
		byte[] input = convertToByteArray(obj);
		enCipher.init(Cipher.ENCRYPT_MODE, key);

		return enCipher.doFinal(input);

		// cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		// byte[] encypted = new byte[cipher.getOutputSize(input.length)];
		// int enc_len = cipher.update(input, 0, input.length, encypted, 0);
		// enc_len += cipher.doFinal(encypted, enc_len);
		// return encypted;

	}

	public Object decrypt(byte[] encrypted) throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException, IOException, ClassNotFoundException {
		deCipher.init(Cipher.DECRYPT_MODE, key);

		return convertFromByteArray(deCipher.doFinal(encrypted));

	}

	public static Object convertFromByteArray(byte[] byteObject) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais;

		ObjectInputStream in;
		bais = new ByteArrayInputStream(byteObject);
		in = new ObjectInputStream(bais);
		Object o = in.readObject();
		in.close();
		return o;

	}

	public static byte[] convertToByteArray(Object complexObject) throws IOException {
		ByteArrayOutputStream baos;

		ObjectOutputStream out;

		baos = new ByteArrayOutputStream();

		out = new ObjectOutputStream(baos);

		out.writeObject(complexObject);

		out.close();

		return baos.toByteArray();

	}

	public static void main(String[] args) {
		try {
		
		 byte[] keyValue = new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't',
		'S', 'e', 'c', 'r','e', 't', 'K', 'e', 'y' };
		
		ObjectCrypter o = new ObjectCrypter(keyValue);

		String stringToCrypt = "J'AIME LE CHOCOLAT !!!";
		
		System.out.println("String To Crypt: "+stringToCrypt);
		
		byte[] cryptedBytes = o.encrypt(stringToCrypt);

		System.out.println(convertFromByteArray(cryptedBytes));
		
		String decryptedString = (String)o.decrypt(cryptedBytes);
		
		System.out.println("DecryptedString: "+decryptedString);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	
}