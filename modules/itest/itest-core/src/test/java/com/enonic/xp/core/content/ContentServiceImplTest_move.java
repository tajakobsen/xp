package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

public class ContentServiceImplTest_move
    extends AbstractContentServiceTest
{

    @Test
    public void move_to_folder_starting_with_same_name()
        throws Exception
    {

        final Content site = createContent( ContentPath.ROOT, "site" );
        final Content child1 = createContent( site.getPath(), "child1" );
        createContent( child1.getPath(), "child1_1" );
        createContent( child1.getPath(), "child2_1" );
        final Content site2 = createContent( ContentPath.ROOT, "site2" );

        final MoveContentParams params = MoveContentParams.create().
            contentId( child1.getId() ).
            parentContentPath( site2.getPath() ).
            build();
        final MoveContentsResult result = this.contentService.move( params );

        final Content movedContent = contentService.getById( result.getMovedContents().first() );

        assertEquals( 1, result.getMovedContents().getSize() );
        assertEquals( movedContent.getParentPath(), site2.getPath() );

    }

    @Test
    public void move_from_site_to_root()
        throws Exception
    {
        final PropertyTree siteData = new PropertyTree();
        siteData.setSet( "siteConfig", this.createSiteConfig( siteData ) );
        final Content site = createContent( ContentPath.ROOT, "site", siteData, ContentTypeName.site() );

        final Content content = createContent( site.getPath(), "child", new PropertyTree(), this.createExtraDatas() );

        final MoveContentParams params =
            MoveContentParams.create().contentId( content.getId() ).parentContentPath( ContentPath.ROOT ).build();

        final MoveContentsResult result = this.contentService.move( params );

        final Content movedContent = contentService.getById( result.getMovedContents().first() );

        assertEquals( movedContent.getAllExtraData().getSize(), 1 );

    }

    @Test
    public void move_to_the_same_parent()
        throws Exception
    {
        final PropertyTree siteData = new PropertyTree();
        siteData.setSet( "siteConfig", this.createSiteConfig( siteData ) );
        final Content site = createContent( ContentPath.ROOT, "site", siteData, ContentTypeName.site() );

        final Content content = createContent( site.getPath(), "child", new PropertyTree(), this.createExtraDatas() );

        final MoveContentParams params =
            MoveContentParams.create().contentId( content.getId() ).parentContentPath( content.getParentPath() ).build();

        assertThrows( ContentAlreadyMovedException.class, () -> this.contentService.move( params ) );

    }

    @Test
    public void move_already_exists()
    {
        final Content content = createContent( ContentPath.ROOT, "child", new PropertyTree(), ContentTypeName.site() );
        createContent( ContentPath.ROOT, "child-2", new PropertyTree(), ContentTypeName.site() );

        final Content content3 = createContent( content.getPath(), "child-2", new PropertyTree() );

        final MoveContentParams params =
            MoveContentParams.create().contentId( content3.getId() ).parentContentPath( content.getParentPath() ).build();

        assertThrows( ContentAlreadyExistsException.class, () -> this.contentService.move( params ) );
    }

    @Test
    void audit_data()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final Content site = createContent( ContentPath.ROOT, "site" );
        final Content child1 = createContent( site.getPath(), "child1" );

        final MoveContentParams params = MoveContentParams.create().
            contentId( child1.getId() ).
            parentContentPath( ContentPath.ROOT ).
            build();

        Mockito.reset( auditLogService );

        this.contentService.move( params );

        verify( auditLogService, atMostOnce() ).log( captor.capture() );

        final LogAuditLogParams log = captor.getValue();
        assertThat( log ).extracting( LogAuditLogParams::getType).isEqualTo( "system.content.move" ) ;
        assertThat( log ).extracting( l -> l.getData().getSet( "result" ) )
            .extracting( result -> result.getString( "movedContents" ) )
            .isEqualTo( child1.getId().toString() );
    }

    private ExtraDatas createExtraDatas()
    {
        return ExtraDatas.create().
            add( new ExtraData( XDataName.from( "com.enonic.app.test:mixin" ), new PropertyTree() ) ).
            build();
    }

    private PropertySet createSiteConfig(PropertyTree tree)
    {
        PropertySet set = tree.newSet();
        set.addString( "applicationKey", "com.enonic.app.test" );
        set.addSet( "config" );
        return set;
    }
}
