package net.uncrash.authorization.basic.aop;


import net.uncrash.authorization.AuthenticationHolder;
import net.uncrash.authorization.api.web.Authentication;
import net.uncrash.authorization.define.AuthorizeDefinition;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 权限控制定义解析器,用于解析被拦截的请求是否需要进行权限控制,以及权限控制的方式
 */
public interface AopMethodAuthorizeDefinitionParser {

    Set<String> EXCLUDE_METHOD_NAME = new HashSet<>(Arrays.asList("toString", "clone", "hashCode", "getClass"));

    /**
     * 解析权限控制定义
     *
     * @param target class
     * @param method method
     * @return 权限控制定义, 如果不进行权限控制则返回{@code null}
     */
    AuthorizeDefinition parse(Class target, Method method, Authentication authentication);

    default AuthorizeDefinition parse(Class target, Method method) {
        return parse(target, method, null); // AuthenticationHolder.get()
    }

    List<AuthorizeDefinition> getAllParsed();
}
