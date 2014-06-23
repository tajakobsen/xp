package com.enonic.wem.api.content;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Workspace;

public class ContentConstants
{
    public static final Workspace WORKSPACE_STAGE = Workspace.from( "stage" );

    public static final Workspace WORKSPACE_PROD = Workspace.from( "prod" );

    public static final Context CONTEXT_STAGE = new Context( WORKSPACE_STAGE );
}
