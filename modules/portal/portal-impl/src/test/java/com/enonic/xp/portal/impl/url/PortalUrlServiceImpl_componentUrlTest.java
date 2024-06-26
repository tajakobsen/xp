package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_componentUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl_toMeAndContextIsPage()
    {
        final ComponentUrlParams params = new ComponentUrlParams().
            portalRequest( this.portalRequest ).
            param( "a", 3 );

        final String url = this.service.componentUrl( params );
        assertEquals( "/site/myproject/draft/context/path?a=3", url );
    }

    @Test
    public void createUrl_toMeAndContextIsComponent()
    {
        addComponent();

        final ComponentUrlParams params = new ComponentUrlParams().
            portalRequest( this.portalRequest );

        final String url = this.service.componentUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/component/main/0", url );
    }

    @Test
    public void createUrl_toOtherComponentOnPage()
    {
        final ComponentUrlParams params = new ComponentUrlParams().
            portalRequest( this.portalRequest ).
            component( "other/1" );

        final String url = this.service.componentUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/component/other/1", url );
    }

    @Test
    public void createUrl_toComponentOnOtherPageWithPath()
    {
        final ComponentUrlParams params = new ComponentUrlParams().
            portalRequest( this.portalRequest ).
            path( "/a/b" ).
            component( "other/1" );

        final String url = this.service.componentUrl( params );
        assertEquals( "/site/myproject/draft/a/b/_/component/other/1", url );
    }

    @Test
    public void createUrl_toComponentOnOtherPageWithId()
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final ComponentUrlParams params = new ComponentUrlParams().
            portalRequest( this.portalRequest ).
            id( "123456" ).
            component( "other/1" );

        final String url = this.service.componentUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent/_/component/other/1", url );
    }

    @Test
    public void createUrl_absolute()
    {
        final ComponentUrlParams params = new ComponentUrlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            param( "a", 3 );

        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getServerPort() ).thenReturn( 80 );

        final String url = this.service.componentUrl( params );
        assertEquals( "http://localhost/site/myproject/draft/context/path?a=3", url );
    }

    private void addComponent()
    {
        final PartComponent component = PartComponent.
            create().
            descriptor( "myapp:mycomp" ).
            build();

        final Region region = Region.
            create().
            name( "main" ).
            add( component ).
            build();

        this.portalRequest.setComponent( component );
    }
}
