package net.uncrash.authorization.basic.aop;

import net.uncrash.authorization.annotation.Authorize;
import net.uncrash.authorization.api.web.Authentication;
import net.uncrash.authorization.basic.define.DefaultBasicAuthorizeDefinition;
import net.uncrash.authorization.basic.define.EmptyAuthorizeDefinition;
import net.uncrash.authorization.define.AuthorizationConst;
import net.uncrash.authorization.define.AuthorizeDefinition;
import net.uncrash.core.utils.AopUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sendya
 */
public class DefaultAopMethodAuthorizeDefinitionParser implements AopMethodAuthorizeDefinitionParser {

    private final Logger logger = LoggerFactory.getLogger(AuthorizationConst.LOGGER_NAME);

    private Map<CacheKey, AuthorizeDefinition> cache = new ConcurrentHashMap<>();

    @Override
    public AuthorizeDefinition parse(Class target, Method method, Authentication authentication) {

        logger.info("===========================");
        logger.info("AuthorizeDefinition: Class: {}, Method: {}, Context: {}", target.getName(), method.getName(), authentication);
        logger.info("MethodName: {}", method.getName());
        logger.info("===========================");

        CacheKey key = buildCacheKey(target, method);
        AuthorizeDefinition definition = cache.get(key);
        if (definition instanceof EmptyAuthorizeDefinition) {

        }
        if (definition != null) {
            return definition;
        }

        Authorize classAuth = AopUtils.findAnnotation(target, Authorize.class);
        Authorize methodAuth = AopUtils.findMethodAnnotation(target, method, Authorize.class);
        if (classAuth == null && methodAuth == null) {
            cache.put(key, EmptyAuthorizeDefinition.INSTANCE);
            return null;
        }

        boolean isIgnore = (methodAuth != null && methodAuth.ignore()) || (classAuth != null && classAuth.ignore());
        if (isIgnore) {
            cache.put(key, EmptyAuthorizeDefinition.INSTANCE);
            return null;
        }

        DefaultBasicAuthorizeDefinition authorizeDefinition = new DefaultBasicAuthorizeDefinition();
        authorizeDefinition.setAuthentication(authentication);
        authorizeDefinition.setTargetClass(target);
        authorizeDefinition.setTargetMethod(method);

        if (methodAuth == null || methodAuth.merge()) {
            authorizeDefinition.put(classAuth);
        }

        authorizeDefinition.put(methodAuth);


        if (authorizeDefinition.getPermissionDescription().length == 0) {
            if (classAuth != null) {
                String[] desc = classAuth.description();
                if (desc.length > 0) {
                    authorizeDefinition.setPermissionDescription(desc);
                }
            }
        }

        if (authorizeDefinition.getActionDescription().length == 0) {
            if (methodAuth != null) {
                if (methodAuth.description().length != 0) {
                    authorizeDefinition.setActionDescription(methodAuth.description());
                }
            }
        }

        logger.info("parsed authorizeDefinition {}.{} => {}.{} permission:{} actions:{}",
            target.getName(),
            method.getName(),
            authorizeDefinition.getPermissionDescription(),
            authorizeDefinition.getActionDescription(),
            authorizeDefinition.getPermissions(),
            authorizeDefinition.getActions());
        cache.put(key, authorizeDefinition);

        return authorizeDefinition;
    }

    @Override
    public List<AuthorizeDefinition> getAllParsed() {
        return new ArrayList<>(cache.values());
    }

    public CacheKey buildCacheKey(Class target, Method method) {
        return new CacheKey(ClassUtils.getUserClass(target), method);
    }

    public void destroy() {
        cache.clear();
    }

    class CacheKey {
        private Class type;
        private Method method;

        public CacheKey(Class type, Method method) {
            this.type = type;
            this.method = method;
        }

        @Override
        public int hashCode() {
            return Arrays.asList(type, method).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && this.hashCode() == obj.hashCode();
        }
    }
}
