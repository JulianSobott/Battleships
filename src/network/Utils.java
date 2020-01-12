package network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Utils {

    public static String getIpAddress() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
