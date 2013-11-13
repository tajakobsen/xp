package com.enonic.wem.api.content.site;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.wem.api.support.Changes;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public class Site
{
    private final SiteTemplateName templateName;

    private final ImmutableList<ModuleConfig> moduleConfigs;

    private Site( final BaseBuilder builder )
    {
        this.templateName = builder.templateName;
        this.moduleConfigs = new ImmutableList.Builder<ModuleConfig>().addAll( builder.moduleConfigs ).build();
    }

    public SiteTemplateName getTemplateName()
    {
        return templateName;
    }

    public ImmutableList<ModuleConfig> getModuleConfigs()
    {
        return moduleConfigs;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Site that = (Site) o;

        return Objects.equals( this.templateName, that.templateName ) && Objects.equals( this.moduleConfigs, that.moduleConfigs );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( templateName, moduleConfigs );
    }

    public static Builder newSite()
    {
        return new Builder();
    }

    public static Builder newSite( final Site source )
    {
        return new Builder( source );
    }

    public static EditBuilder editSite( final Site toBeEdited )
    {
        return new EditBuilder( toBeEdited );
    }

    public static class BaseBuilder
    {
        SiteTemplateName templateName;

        List<ModuleConfig> moduleConfigs = new ArrayList<>();

        public BaseBuilder( final Site source )
        {
            this.templateName = source.getTemplateName();
            this.moduleConfigs.addAll( source.getModuleConfigs() );
        }

        public BaseBuilder()
        {

        }
    }

    public static class EditBuilder
        extends BaseBuilder
    {
        private final Site original;

        private final Changes.Builder changes = new Changes.Builder();

        public EditBuilder( final Site original )
        {
            super( original );
            this.original = original;
        }

        public EditBuilder template( SiteTemplateName value )
        {
            changes.recordChange( newPossibleChange().from( this.original.getTemplateName() ).to( value ).build() );
            this.templateName = value;
            return this;
        }

        public EditBuilder moduleConfigs( Iterable<ModuleConfig> configs )
        {
            final ImmutableList.Builder<ModuleConfig> newConfigsBuilder = new ImmutableList.Builder<>();
            newConfigsBuilder.addAll( configs );
            changes.recordChange( newPossibleChange().from( original.getModuleConfigs() ).to( Lists.newArrayList( configs ) ).build() );
            moduleConfigs = Lists.newArrayList( configs );
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


        public Site build()
        {
            return new Site( this );
        }

    }

    public static class Builder
        extends BaseBuilder
    {
        public Builder( final Site source )
        {
            super( source );
        }

        public Builder()
        {
            super();
        }

        public Builder template( SiteTemplateName value )
        {
            this.templateName = value;
            return this;
        }

        public Builder addModuleConfig( ModuleConfig value )
        {
            moduleConfigs.add( value );
            return this;
        }

        public Builder moduleConfigs( Iterable<ModuleConfig> configs )
        {
            moduleConfigs = Lists.newArrayList( configs );
            return this;
        }

        public Site build()
        {
            return new Site( this );
        }

    }
}
