package com.enonic.wem.portal.content;


import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteStreams;
import com.sun.jersey.api.client.ClientResponse;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.api.command.content.attachment.GetAttachment;
import com.enonic.wem.api.command.content.blob.GetBlob;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.blobstore.memory.MemoryBlobRecord;
import com.enonic.wem.portal.AbstractResourceTest;

import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentResourceTest
    extends AbstractResourceTest
{
    private ContentResource resource;

    private Client client;

    @Override
    protected Object getResourceInstance()
    {
        client = mock( Client.class );
        resource = new ContentResource();
        resource.client = client;
        return resource;
    }

    @Before
    public void setup()
        throws IOException
    {
        mockCurrentContextHttpRequest();
    }

    @Test
    public void getContentFound()
        throws Exception
    {
        Content content = createContent( "content-id", "path/to/content", "image" );
        when( client.execute( isA( GetContentByPath.class ) ) ).thenReturn( content );
        final BlobKey blobKey = new BlobKey( "<blobkey-1>" );
        final Attachment attachment = newAttachment().
            blobKey( blobKey ).
            name( "enonic-logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build();
        final byte[] imageData = ByteStreams.toByteArray( getClass().getResourceAsStream( "enonic-logo.png" ) );
        when( client.execute( isA( GetAttachment.class ) ) ).thenReturn( attachment );
        final Blob blob = new MemoryBlobRecord( blobKey, imageData );
        when( client.execute( isA( GetBlob.class ) ) ).thenReturn( blob );

        resource.mode = "live";
        resource.contentPath = "path/to/content";
//        resource.fileName = "enonic-logo.png";
        final ClientResponse resp = resource().path( "/portal/live/path/to/content/_/image/enonic-logo.png" ).get( ClientResponse.class );

        assertEquals( 200, resp.getStatus() );
        assertEquals( "image/png", resp.getHeaders().getFirst( "content-type" ) );
    }

    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            createdTime( DateTime.now() ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( DateTime.now() ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            build();
    }
}
