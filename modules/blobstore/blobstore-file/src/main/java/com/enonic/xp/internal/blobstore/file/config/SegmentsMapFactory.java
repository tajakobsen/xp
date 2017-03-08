package com.enonic.xp.internal.blobstore.file.config;

import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.config.Configuration;

final class SegmentsMapFactory
{
    private final Configuration config;

    private final Segment[] requiredSegments;

    private final String configName;

    private final String collectionPrefix;

    private SegmentsMapFactory( final Builder builder )
    {
        config = builder.config;
        requiredSegments = builder.requiredSegments;
        configName = builder.configName;
        collectionPrefix = builder.collectionPrefix;
    }

    Map<Segment, String> execute()
    {
        final ImmutableMap.Builder<Segment, String> builder = ImmutableMap.builder();

        final Map<Segment, String> multiBucketsConfig = getMultiBucketsConfig();
        builder.putAll( multiBucketsConfig );

        addMissingRequired( builder, multiBucketsConfig );

        return builder.build();
    }

    private void addMissingRequired( final ImmutableMap.Builder<Segment, String> builder, final Map<Segment, String> multiBucketsConfig )
    {
        if ( this.config.exists( configName ) && !Strings.isNullOrEmpty( this.config.get( configName ) ) )
        {
            final String defaultBucket = this.config.get( configName );

            for ( final Segment segment : requiredSegments )
            {
                if ( multiBucketsConfig.get( segment ) == null )
                {
                    builder.put( segment, defaultBucket );
                }
            }
        }
    }

    private Map<Segment, String> getMultiBucketsConfig()
    {
        final Map<Segment, String> segmentsMap = Maps.newHashMap();

        final Configuration bucketConfig = this.config.subConfig( collectionPrefix );

        final Map<String, String> segmentMap = bucketConfig.asMap();

        for ( final String segment : segmentMap.keySet() )
        {
            segmentsMap.put( Segment.from( segment ), segmentMap.get( segment ) );
        }

        return segmentsMap;
    }


    static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Configuration config;

        private Segment[] requiredSegments;

        private String configName;

        private String collectionPrefix;

        private Builder()
        {
        }

        Builder configuration( final Configuration val )
        {
            config = val;
            return this;
        }

        Builder requiredSegments( final Segment[] val )
        {
            requiredSegments = val;
            return this;
        }

        Builder configName( final String val )
        {
            configName = val;
            return this;
        }

        Builder collectionPrefix( final String val )
        {
            collectionPrefix = val;
            return this;
        }

        SegmentsMapFactory build()
        {
            return new SegmentsMapFactory( this );
        }
    }
}