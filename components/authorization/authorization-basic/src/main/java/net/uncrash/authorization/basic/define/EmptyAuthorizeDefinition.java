package net.uncrash.authorization.basic.define;


import net.uncrash.authorization.api.web.Authentication;
import net.uncrash.authorization.define.AuthorizeDefinition;
import net.uncrash.authorization.define.Logical;
import net.uncrash.authorization.define.Phased;

import java.util.Set;

public class EmptyAuthorizeDefinition implements AuthorizeDefinition {

    public static final EmptyAuthorizeDefinition INSTANCE = new EmptyAuthorizeDefinition();

    private EmptyAuthorizeDefinition() {
    }

    @Override
    public Phased getPhased() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPriority() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getPermissions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getPermissionDescription() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getActionDescription() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getActions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Authentication getAuthentication() {
        return null;
    }

    @Override
    public Set<String> getRoles() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMessage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logical getLogical() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

}
