package com.enonic.xp.repo.impl.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NodeHasChildResolverTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void has_children()
        throws Exception
    {

        final Node parentNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        createNode( CreateNodeParams.create().
            parent( parentNode.path() ).
            name( "my-child-node" ).
            build() );

        nodeService.refresh( RefreshMode.ALL );

        assertTrue( NodeHasChildResolver.create().
            searchService( this.searchService ).
            build().
            resolve( parentNode.path() ) );
    }

    @Test
    public void no_children()
        throws Exception
    {

        final Node parentNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        assertFalse( NodeHasChildResolver.create().
            searchService( this.searchService ).
            build().
            resolve( parentNode.path() ) );
    }

}
