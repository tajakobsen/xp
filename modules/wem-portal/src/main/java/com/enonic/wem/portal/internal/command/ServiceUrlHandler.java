package com.enonic.wem.portal.internal.command;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.url.PortalUrlBuilder;
import com.enonic.wem.portal.url.PortalUrlBuildersHelper;

@Component(immediate = true)
public final class ServiceUrlHandler
    extends AbstractUrlHandler
{
    public ServiceUrlHandler()
    {
        super( "serviceUrl" );
    }

    @Override
    protected PortalUrlBuilder createBuilder( final Multimap<String, String> map )
    {
        return PortalUrlBuildersHelper.apply( createBuilders().serviceUrl(), map );
    }
}
