import java.net.InetAddress;
import java.net.UnknownHostException;

public class Utils {

	public static String getFQDN() {

	    byte[] ipAddress = new byte[] {(byte)127, (byte)0, (byte)0, (byte)1 };
	    InetAddress address = null;
		try {
			address = InetAddress.getByAddress(ipAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	    String hostnameCanonical = address.getCanonicalHostName();
	    return hostnameCanonical;
	}
}
