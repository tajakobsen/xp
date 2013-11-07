package com.enonic.wem.core.schema.content.dao;


import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.jcr.JcrHelper;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.ContentTypes.newContentTypes;


final class ContentTypeDaoHandlerSelect
    extends AbstractContentTypeDaoHandler
{
    ContentTypeDaoHandlerSelect( final Session session )
    {
        super( session );
    }

    ContentTypes select( final ContentTypeNames contentTypeNames )
        throws RepositoryException
    {
        final ContentTypeInheritorResolver contentTypeInheritorResolver = new ContentTypeInheritorResolver( doFetchAll() );
        final List<ContentType> contentTypeList = Lists.newArrayList();
        for ( ContentTypeName contentTypeName : contentTypeNames )
        {
            final ContentType contentType = doSelect( contentTypeName, contentTypeInheritorResolver );
            if ( contentType != null )
            {
                contentTypeList.add( contentType );
            }
        }
        return ContentTypes.from( contentTypeList );
    }

    private ContentType doSelect( final ContentTypeName contentTypeName, final ContentTypeInheritorResolver contentTypeInheritorResolver )
        throws RepositoryException
    {
        final Node contentTypeNode = this.getContentTypeNode( contentTypeName );
        if ( contentTypeNode == null )
        {
            return null;
        }

        return this.contentTypeJcrMapper.toContentType( contentTypeNode, contentTypeInheritorResolver );
    }

    private ContentTypes doFetchAll()
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node contentTypesNode = JcrHelper.getNodeOrNull( rootNode, "/content-types/");

        final List<ContentType> contentTypeList = Lists.newArrayList();

        final NodeIterator contentTypeNodes = contentTypesNode.getNodes();
        while ( contentTypeNodes.hasNext() )
        {
            final Node contentTypeNode = contentTypeNodes.nextNode();
            final ContentType contentType = this.contentTypeJcrMapper.toContentType( contentTypeNode, null );
            contentTypeList.add( contentType );
        }

        return ContentTypes.from( contentTypeList );
    }

    ContentTypes selectAll()
        throws RepositoryException
    {
        final ContentTypes contentTypes = doFetchAll();
        final ContentTypeInheritorResolver contentTypeInheritorResolver = new ContentTypeInheritorResolver( contentTypes );
        final ContentTypes.Builder builder = newContentTypes();
        for ( ContentType contentType : contentTypes )
        {
            builder.add( newContentType( contentType ).inheritors(
                contentTypeInheritorResolver.resolveInheritors( contentType ).isNotEmpty() ).build() );
        }
        return builder.build();
    }
}
