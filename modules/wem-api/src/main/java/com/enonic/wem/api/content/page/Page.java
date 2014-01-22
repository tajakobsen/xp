package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.rendering.Component;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.EditBuilder;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class Page
    implements Component
{
    private final PageTemplateKey template;

    private final PageRegions regions;

    private final RootDataSet config;

    private Page( final PageProperties properties )
    {
        Preconditions.checkNotNull( properties.template, "template is required" );

        this.template = properties.template;
        this.config = properties.config;
        this.regions = properties.regions;
    }

    public PageTemplateKey getTemplate()
    {
        return template;
    }

    public boolean hasRegions()
    {
        return regions != null;
    }

    public PageRegions getRegions()
    {
        return regions;
    }

    public boolean hasConfig()
    {
        return config != null;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public PageComponent getComponent( final ComponentName componentName )
    {
        return regions.getComponent( componentName );
    }

    public static Builder newPage()
    {
        return new Builder();
    }

    static class PageProperties
    {
        PageTemplateKey template;

        PageRegions regions;

        RootDataSet config;

        PageProperties()
        {
            // nothing
        }

        PageProperties( final Page source )
        {
            this.config = source.config.copy().toRootDataSet();
            this.template = source.getTemplate();
        }

        public PageProperties template( PageTemplateKey value )
        {
            this.template = value;
            return this;
        }
    }

    public static PageEditBuilder editPage( final Page toBeEdited )
    {
        return new PageEditBuilder( toBeEdited );
    }

    public static class PageEditBuilder
        extends PageProperties
        implements EditBuilder<Page>
    {
        private final Page original;

        private final Changes.Builder changes = new Changes.Builder();

        public PageEditBuilder( final Page original )
        {
            super( original );
            this.original = original;
        }

        public PageEditBuilder template( PageTemplateKey value )
        {
            changes.recordChange( newPossibleChange( "template" ).from( this.original.getTemplate() ).to( value ).build() );
            this.template = value;
            return this;
        }

        public PageEditBuilder regions( final PageRegions value )
        {
            changes.recordChange( newPossibleChange( "regions" ).from( this.original.getRegions() ).to( value ).build() );
            this.regions = value;
            return this;
        }

        public PageEditBuilder config( RootDataSet value )
        {
            changes.recordChange( newPossibleChange( "config" ).from( original.getConfig() ).to( value ).build() );
            config = value;
            return this;
        }

        public boolean isChanges()
        {
            return this.changes.isChanges();
        }

        public Changes getChanges()
        {
            return this.changes.build();
        }


        public Page build()
        {
            return new Page( this );
        }

    }

    public static class Builder
        extends PageProperties
    {
        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder regions( final PageRegions value )
        {
            this.regions = value;
            return this;
        }

        public Builder template( final PageTemplateKey value )
        {
            this.template = value;
            return this;
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public Page build()
        {
            return new Page( this );
        }
    }
}
