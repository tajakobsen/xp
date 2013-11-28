package com.enonic.wem.core.index.elastic.searchsource;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.core.index.content.ContentIndexField;

public class SearchSourceFactory
{
    public static SearchSourceBuilder create( final ContentIndexQuery contentIndexQuery )
    {
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        final String fullTextSearchString = contentIndexQuery.getFullTextSearchString();

        searchSourceBuilder.size( contentIndexQuery.getSize() );

        QueryBuilder query = null;

        if ( Strings.isNullOrEmpty( fullTextSearchString ) )
        {
            query = QueryBuilders.matchAllQuery();
        }
        else
        {
            final QueryStringQueryBuilder fulltextQuery = QueryBuilders.queryString( fullTextSearchString );
            fulltextQuery.lenient( true );

            query = fulltextQuery;
        }

        final FilterBuilder filterBuilder = buildFilters( contentIndexQuery );

        if ( filterBuilder != null )
        {
            searchSourceBuilder.query( new FilteredQueryBuilder( query, filterBuilder ) );
        }
        else
        {
            searchSourceBuilder.query( query );
        }

        if ( contentIndexQuery.isIncludeFacets() )
        {
            final String facetsDefinition = contentIndexQuery.getFacets();

            if ( !Strings.isNullOrEmpty( facetsDefinition ) )
            {
                searchSourceBuilder.facets( facetsDefinition.getBytes() );
            }
        }

        return searchSourceBuilder;
    }

    private static FilterBuilder buildFilters( final ContentIndexQuery contentIndexQuery )
    {
        Set<FilterBuilder> filtersToApply = Sets.newHashSet();

        createAndAddContentTypeFilterIfApplicable( contentIndexQuery, filtersToApply );
        createAndAddRangeFilterIfApplicable( contentIndexQuery, filtersToApply );

        if ( filtersToApply.isEmpty() )
        {
            return null;
        }

        if ( filtersToApply.size() == 1 )
        {
            return filtersToApply.iterator().next();
        }

        return createFilteredQuery( filtersToApply );
    }

    private static FilterBuilder createFilteredQuery( final Set<FilterBuilder> filtersToApply )
    {
        BoolFilterBuilder boolFilterBuilder = new BoolFilterBuilder();

        final Iterator<FilterBuilder> filterIterator = filtersToApply.iterator();

        while ( filterIterator.hasNext() )
        {
            boolFilterBuilder.must( filterIterator.next() );
        }

        return boolFilterBuilder;
    }

    private static void createAndAddRangeFilterIfApplicable( final ContentIndexQuery contentIndexQuery,
                                                             final Set<FilterBuilder> filtersToApply )
    {
        final Set<ContentIndexQuery.Range> ranges = contentIndexQuery.getRanges();

        if ( ranges.isEmpty() )
        {
            return;
        }

        final Set<RangeFilterBuilder> rangeFilters = Sets.newHashSet();

        for ( ContentIndexQuery.Range range : ranges )
        {

            RangeFilterBuilder rangeFilterBuilder = new RangeFilterBuilder( ContentIndexField.LAST_MODIFIED + ".date" );
            rangeFilterBuilder.
                from( range.getLower() ).
                to( range.getUpper() ).
                includeLower( range.isIncludeLower() ).
                includeUpper( range.isIncludeUpper() );

            rangeFilters.add( rangeFilterBuilder );
        }

        if ( rangeFilters.size() == 1 )
        {
            filtersToApply.add( rangeFilters.iterator().next() );
        }
        else if ( rangeFilters.size() > 1 )
        {
            BoolFilterBuilder boolFilterBuilder = new BoolFilterBuilder();

            for ( RangeFilterBuilder rangeFilter : rangeFilters )
            {
                boolFilterBuilder.should( rangeFilter );
            }

            filtersToApply.add( boolFilterBuilder );
        }
    }

    private static void createAndAddContentTypeFilterIfApplicable( final ContentIndexQuery contentIndexQuery,
                                                                   final Set<FilterBuilder> filtersToApply )
    {
        if ( contentIndexQuery.getContentTypeNames() != null && contentIndexQuery.getContentTypeNames().isNotEmpty() )
        {
            Collection<String> contentTypeNames = getCollectionAsLowercase( contentIndexQuery.getContentTypeNames().getAsStringSet() );
            final FilterBuilder contentTypeFilter = createTermsFilter( contentTypeNames, ContentIndexField.CONTENT_TYPE );
            filtersToApply.add( contentTypeFilter );
        }
    }

    private static FilterBuilder createTermsFilter( final Collection<String> filterElementNames, final String fieldName )
    {
        return new TermsFilterBuilder( fieldName, filterElementNames );
    }


    private static Collection<String> getCollectionAsLowercase( final Collection<String> collection )
    {
        Collection<String> lowerCaseStrings = Collections2.transform( collection, new Function<String, String>()
        {
            public String apply( String str )
            {
                return str.toLowerCase();
            }
        } );

        return lowerCaseStrings;
    }

}
