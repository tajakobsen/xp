package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.repo.impl.node.NodeConstants;

class NgramQueryBuilder
    extends SimpleQueryStringBuilder
{
    public static final String NAME = "ngram";

    NgramQueryBuilder( final PropertySet expression )
    {
        super( expression );
    }

    public QueryBuilder create()
    {
        final org.elasticsearch.index.query.SimpleQueryStringBuilder builder =
            ( (org.elasticsearch.index.query.SimpleQueryStringBuilder) super.create() ).analyzer(
                NodeConstants.DEFAULT_NGRAM_SEARCH_ANALYZER );

        fields.forEach( field -> {
            final String resolvedName = nameResolver.resolve( field.getBaseFieldName(), IndexValueType.NGRAM );
            if ( field.getWeight() != null )
            {
                builder.field( resolvedName, field.getWeight() );
            }
            else
            {
                builder.field( resolvedName );
            }
        } );

        return builder;

    }
}
