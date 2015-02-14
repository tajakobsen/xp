package com.enonic.wem.api.node;

import java.text.MessageFormat;

import com.enonic.wem.api.exception.BaseException;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.acl.Permission;

public final class NodeAccessException
    extends BaseException
{
    private final User user;

    private final NodePath nodePath;

    private final Permission permission;

    public NodeAccessException( final User user, final NodePath nodePath, final Permission permission )
    {
        super( MessageFormat.format( "Access denied to [{0}] for [{1}] by user [{2}] {3}", nodePath, permission,
                                     user == null ? "unknown" : user.getKey(),
                                     user != null && user.getDisplayName() != null ? "''" + user.getDisplayName() + "''" : "" ) );
        this.user = user;
        this.nodePath = nodePath;
        this.permission = permission;
    }

    public User getUser()
    {
        return user;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public Permission getPermission()
    {
        return permission;
    }
}
