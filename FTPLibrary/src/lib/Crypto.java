package lib;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Formatter;

public class Crypto {

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

	//generate salt
	public static byte[] generateRandomBytes(int length) {
		byte[] bytes = new byte[length];
		try {
			SecureRandom.getInstanceStrong().nextBytes(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	// TODO Add the session ID to the salting process for extra security.
	public static String saltPassword(String salt, String passwordHash, int iterations) {
		String result = passwordHash;

		for (int i = 0; i < iterations; i++) {
			result = generateHash(salt + result);
		}

		return result;
	}
}
