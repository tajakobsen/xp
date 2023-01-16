package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeId;

public final class DeleteNodeByIdCommand
    extends AbstractDeleteNodeCommand
{
    private final NodeId nodeId;

    private DeleteNodeByIdCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
    }

    public NodeBranchEntries execute()
    {
        final Node node = doGetById( nodeId );

        return node != null ? deleteNodeWithChildren( node ) : NodeBranchEntries.empty();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static class Builder
        extends AbstractDeleteNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        Builder()
        {
            super();
        }

        Builder( final AbstractNodeCommand source )
        {
            super( source );
        }


        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.nodeId );
        }

        public DeleteNodeByIdCommand build()
        {
            this.validate();
            return new DeleteNodeByIdCommand( this );
        }
    }

}
