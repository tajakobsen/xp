package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RoutableNodeVersionId;
import com.enonic.xp.node.RoutableNodeVersionIds;
import com.enonic.xp.node.UpdateNodeParams;

public class UnpublishContentCommand
    extends AbstractContentCommand
{
    private final UnpublishContentParams params;

    private UnpublishContentCommand( final Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public UnpublishContentsResult execute()
    {
        final Context masterContext = ContextBuilder.from( ContextAccessor.current() ).branch( ContentConstants.BRANCH_MASTER ).build();

        final ContentIds contentIds = masterContext.callWith( this::delete );

        removePublishInfo( contentIds );

        this.nodeService.refresh( RefreshMode.ALL );

        final UnpublishContentsResult.Builder resultBuilder = UnpublishContentsResult.create().addUnpublished( contentIds );
        if ( contentIds.getSize() == 1 )
        {
            resultBuilder.setContentPath( this.getContent( contentIds.first() ).getPath() );
        }


        return resultBuilder.build();
    }

    private ContentIds delete()
    {
        final ContentIds.Builder contentBuilder = ContentIds.create();

        for ( final ContentId contentId : this.params.getContentIds() )
        {
            final NodeIds nodeIds = this.nodeService.deleteById( NodeId.from( contentId ) );

            if ( nodeIds.isNotEmpty() )
            {
                if ( params.getPublishContentListener() != null )
                {
                    params.getPublishContentListener().contentPushed( nodeIds.getSize() );
                }
                contentBuilder.addAll( ContentNodeHelper.toContentIds( nodeIds ) );
            }
        }
        return contentBuilder.build();
    }

    private void removePublishInfo( final ContentIds contentIds )
    {
        final Instant now = Instant.now();
        for ( final ContentId contentId : contentIds )
        {
            final Node updated = nodeService.update( UpdateNodeParams.create().editor( toBeEdited -> {

                if ( toBeEdited.data.getInstant(
                    PropertyPath.from( ContentPropertyNames.PUBLISH_INFO, ContentPropertyNames.PUBLISH_FROM ) ) != null )
                {
                    PropertySet publishInfo = toBeEdited.data.getSet( ContentPropertyNames.PUBLISH_INFO );

                    publishInfo.removeProperties( ContentPropertyNames.PUBLISH_FROM );

                    publishInfo.removeProperties( ContentPropertyNames.PUBLISH_TO );

                    if ( publishInfo.getInstant( ContentPropertyNames.PUBLISH_FIRST ).isAfter( now ) )
                    {
                        publishInfo.removeProperty( ContentPropertyNames.PUBLISH_FIRST );
                    }
                }
            } ).id( NodeId.from( contentId ) ).build() );

            nodeService.commit( NodeCommitEntry.create().message( ContentConstants.UNPUBLISH_COMMIT_PREFIX ).build(),
                                RoutableNodeVersionIds.from( RoutableNodeVersionId.from( updated.id(), updated.getNodeVersionId() ) ) );
        }
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private UnpublishContentParams params;

        public Builder params( final UnpublishContentParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public UnpublishContentCommand build()
        {
            validate();
            return new UnpublishContentCommand( this );
        }
    }

}

