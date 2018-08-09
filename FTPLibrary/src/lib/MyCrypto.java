package lib;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Formatter;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class MyCrypto {

	//generate hash from string
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

	//salts password iterations count of time
	public static String saltPassword(String salt, String passwordHash, int iterations) {
		String result = passwordHash;

		for (int i = 0; i < iterations; i++) {
			result = generateHash(salt + result);
		}

		return result;
	}

	//generates random salt
	public static byte[] generateRandomSalt() {
		byte[] salt = new byte[8];
		try {
			SecureRandom.getInstanceStrong().nextBytes(salt);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return salt;

	}

	// generates random number of iterations between 4096 and 1024
	public static int getRandomIterations() {
		Random random = new Random();
		int maximum = 4096;
		int minimum = 1024;

		return random.nextInt((maximum - minimum) + 1) + minimum;

	}


	public static byte[] intToBytes( final int i ) {
	    ByteBuffer bb = ByteBuffer.allocate(4);
	    bb.putInt(i);
	    return bb.array();
	}


}
