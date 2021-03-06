package net.uncrash.core.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class WebUtil {


    /**
     * 将对象转为http请求参数:
     * <pre>
     *     {name:"test",org:[1,2,3]} => {"name":"test","org[0]":1,"org[1]":2,"org[2]":3}
     * </pre>
     *
     * @param object
     * @return
     */
    public static Map<String, String> objectToHttpParameters(Object object) {
        return new HttpParameterConverter(object).convert();
    }

    public static Map<String, String> queryStringToMap(String queryString, String charset) {
        try {
            Map<String, String> map = new HashMap<>();

            String[] decode = URLDecoder.decode(queryString, charset).split("&");
            for (String keyValue : decode) {
                String[] kv = keyValue.split("[=]", 2);
                map.put(kv[0], kv.length > 1 ? kv[1] : "");
            }
            return map;
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 尝试获取当前请求的HttpServletRequest实例
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String name = String.valueOf(enumeration.nextElement());
            parameters.put(name, request.getParameter(name));
        }
        return parameters;
    }

    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    public static Map<String, String> getHeaders() {
        HttpServletRequest request = getHttpServletRequest();
        return getHeaders(request);
    }

    static final String[] IP_HEADERS = {
        "X-Forwarded-For",
        "X-Real-IP",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP"
    };

    /**
     * 获取请求客户端的真实ip地址
     *
     * @param request 请求对象
     * @return ip地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        for (String ipHeader : IP_HEADERS) {
            String ip = request.getHeader(ipHeader);
            if (!StringUtils.isEmpty(ip) && !ip.contains("unknown")) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public static String getIpAddr() {
        return getIpAddr(getHttpServletRequest());
    }

    /**
     * web应用绝对路径
     *
     * @param request 请求对象
     * @return 绝对路径
     */
    public static String getBasePath(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
        return basePath;
    }

    public static String getBasePath() {
        return getBasePath(getHttpServletRequest());
    }

}
