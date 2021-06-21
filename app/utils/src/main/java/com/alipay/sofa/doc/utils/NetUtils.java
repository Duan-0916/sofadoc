package com.alipay.sofa.doc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class NetUtils {

    /**
     * Logger for NetUtils
     **/
    private static final Logger LOGGER = LoggerFactory.getLogger(NetUtils.class);

    /**
     * 得到缓存的本机地址
     *
     * @return 本机地址
     */
    public static String getLocalHost() {
        if (LOCALHOST == null) {
            LOCALHOST = getLocalIpv4();
        }
        return LOCALHOST;
    }

    /**
     * 缓存了本机地址
     */
    private static String LOCALHOST;

    /**
     * 得到本机IPv4地址
     *
     * @return ip地址
     */
    public static String getLocalIpv4() {
        InetAddress address = getLocalAddress();
        return address == null ? null : address.getHostAddress();
    }

    /**
     * 遍历本地网卡，返回第一个合理的IP，保存到缓存中
     *
     * @return 本地网卡IP
     */
    public static InetAddress getLocalAddress() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error when retrieving ip address: " + e.getMessage(), e);
            }
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            try {
                                InetAddress address = addresses.nextElement();
                                if (isValidAddress(address)) {
                                    return address;
                                }
                            } catch (Throwable e) {
                                if (LOGGER.isWarnEnabled()) {
                                    LOGGER.warn("Error when retrieving ip address: " + e.getMessage(), e);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        if (LOGGER.isWarnEnabled()) {
                            LOGGER.warn("Error when retrieving ip address: " + e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error when retrieving ip address: " + e.getMessage(), e);
            }
        }
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error("Can't get valid host, will use 127.0.0.1 instead.");
        }
        return localAddress;
    }


    /**
     * 任意地址
     */
    public static final String   ANYHOST          = "0.0.0.0";
    /**
     * 本机地址正则
     */
    private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");

    /**
     * IPv4地址
     */
    public static final Pattern  IPV4_PATTERN     = Pattern
            .compile(
                    "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    /**
     * 是否本地地址 127.x.x.x 或者 localhost
     *
     * @param host 地址
     * @return 是否本地地址
     */
    public static boolean isLocalHost(String host) {
        return StringUtils.isNotBlank(host)
                && (LOCAL_IP_PATTERN.matcher(host).matches() || "localhost".equalsIgnoreCase(host));
    }

    /**
     * 是否默认地址 0.0.0.0
     *
     * @param host 地址
     * @return 是否默认地址
     */
    public static boolean isAnyHost(String host) {
        return ANYHOST.equals(host);
    }

    /**
     * 是否IPv4地址 0.0.0.0
     *
     * @param host 地址
     * @return 是否默认地址
     */
    public static boolean isIPv4Host(String host) {
        return StringUtils.isNotBlank(host)
                && IPV4_PATTERN.matcher(host).matches();
    }

    /**
     * 是否非法地址（本地或默认）
     *
     * @param host 地址
     * @return 是否非法地址
     */
    static boolean isInvalidLocalHost(String host) {
        return StringUtils.isBlank(host)
                || isAnyHost(host)
                || isLocalHost(host);
    }

    /**
     * 是否合法地址（非本地，非默认的IPv4地址）
     *
     * @param address InetAddress
     * @return 是否合法
     */
    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null
                && !isAnyHost(name)
                && !isLocalHost(name)
                && isIPv4Host(name));
    }
}
