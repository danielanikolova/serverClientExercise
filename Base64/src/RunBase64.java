import java.nio.charset.Charset;
import java.util.Base64;

import javax.xml.bind.DatatypeConverter;

public class RunBase64 {

	public static void main(String[] args) {


		String username = "Daniela";

//		byte [] usernameInBytes = username.getBytes();
//
//		byte[] encodedUsernameBytes = encodedBytes(usernameInBytes);
//
//		String encodedUsername = encodedUsernameBytes.toString();
//
//		System.out.println("Encoded username: " + encodedUsername);
//
//		System.out.println("Decoded username: "+ decodedBytes(encodedUsernameBytes).toString());
//
//	}
//
//	public static byte[] encodedBytes(byte[] b)
//	{
//		return Base64.getEncoder().encode(b);
//	}
//
//	public static byte[] decodedBytes(byte[] bytesToDecode)
//	{
//		return Base64.getDecoder().decode(bytesToDecode);



		        String str = "Daniela";
		        // encode data using BASE64
		        String encoded = DatatypeConverter.printBase64Binary(str.getBytes());
		        System.out.println("encoded value is \t" + encoded);

		        // Decode data
		        String decoded = new String(DatatypeConverter.parseBase64Binary(encoded));
		        System.out.println("decoded value is \t" + decoded);

		        System.out.println("original value is \t" + str);


//		String decoded =  base64Encode(username);
//
//		System.out.println("Encoded: " +decoded);
//		System.out.println("Decoded: " + base64Encode(decoded));


	}

//	 public static String base64Encode(String token) {
//		    byte[] encodedBytes = Base64.getEncoder().encode(token.getBytes());
//		    return new String(encodedBytes);
//		}
//
//
//		public static String base64Decode(String token) {
//		    byte[] decodedBytes = Base64.getDecoder().decode(token.getBytes());
//		    return new String(decodedBytes);
//		}


}
