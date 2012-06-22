package com.enonic.wem.core.jcr;

public class JcrCmsConstants
{
    public static final String ENONIC_CMS_NAMESPACE = "http://www.enonic.com/cms";

    public static final String ENONIC_CMS_NAMESPACE_PREFIX = "cms";


    public static final String ROOT_NODE = "enonic";

    public static final String USERSTORES_NODE = "userstores";

    public static final String USERSTORES_NODE_TYPE = "cms:userstores";

    public static final String USERSTORE_NODE_TYPE = "cms:userstore";

    public static final String GROUPS_NODE = "groups";

    public static final String GROUPS_NODE_TYPE = "cms:groups";

    public static final String GROUP_NODE_TYPE = "cms:group";

    public static final String MEMBERS_NODE = "members";

    public static final String USERS_NODE = "users";

    public static final String USERS_NODE_TYPE = "cms:users";

    public static final String USER_NODE_TYPE = "cms:user";

    public static final String ROLES_NODE = "roles";

    public static final String ROLES_NODE_TYPE = "cms:roles";

    public static final String SYSTEM_USERSTORE_NODE = "system";

    public static final int SYSTEM_USERSTORE_KEY = 0;

    public static final String USERSTORES_PATH = JcrCmsConstants.ROOT_NODE + "/" + JcrCmsConstants.USERSTORES_NODE + "/";

    public static final String USERSTORES_ABSOLUTE_PATH = "/" + USERSTORES_PATH;


}
