package lib;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Formatter;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class MyCrypto {

	//Returns a random salt
	public static String generateRandomSalt() {
		byte[] salt = new byte[8];
		try {
			SecureRandom.getInstanceStrong().nextBytes(salt);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return salt.toString();
	}

	// returns random iterations between 1024 and 4096
	public static int getRandomIterations() {

		Random random = new Random();
		int maximum = 4096;
		int minimum = 1024;

		int n = maximum - minimum + 1;
		int i = random.nextInt() % n;
		return  minimum + i;
	}

	//this method concatenates hashed password and salt iterations count of time
	public static String saltParameter(String salt, String parameter, int iterations) {
		String result = parameter;

		for (int i = 0; i < iterations; i++) {
			result = generateHash(salt + result);
		}

		return result;
	}

	//returns hashed password
	public static String generateHash(String input) {
		String sha256 = "";
		try {
			MessageDigest hash = MessageDigest.getInstance("SHA-256");
			hash.reset();
			hash.update(input.getBytes("UTF-8"));
			sha256 = byteToHex(hash.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sha256;
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static String saltRegistrationPass(String salt, int iterations) {

		StringBuilder sb = new StringBuilder();
		sb.append(FTPConstants.REGISTRATION_PASS);

		for (int i = 0; i < iterations; i++) {
			sb.append(salt);
		}

		return sb.toString();
	}

	//encode base64
	public static String encodeBase64(String input) {

		return DatatypeConverter.printBase64Binary(input.getBytes());
	}
	//decode base64
	public static String decodeBase64(String input) {

		return new String(DatatypeConverter.parseBase64Binary(input));
	}



}
