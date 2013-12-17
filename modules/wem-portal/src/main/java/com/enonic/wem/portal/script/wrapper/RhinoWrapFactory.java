package com.enonic.wem.portal.script.wrapper;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

public final class RhinoWrapFactory
    extends WrapFactory
{
    public RhinoWrapFactory()
    {
        setJavaPrimitiveWrap( true );
    }

    @Override
    public Object wrap( final Context cx, final Scriptable scope, final Object obj, final Class<?> staticType )
    {
        if ( obj instanceof Map )
        {
            return new MapWrapper( (Map) obj );
        }

        return super.wrap( cx, scope, obj, staticType );
    }
}
