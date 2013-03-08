package com.enonic.wem.api.content.schema.content.form;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.schema.mixin.MixinFetcher;

public final class Form
    implements Iterable<FormItem>
{
    private final FormItems formItems;

    private Form( final Builder builder )
    {
        this.formItems = new FormItems( null );
        for ( final FormItem formItem : builder.formItems )
        {
            this.formItems.add( formItem );
        }
    }

    public void addFormItem( final FormItem formItem )
    {
        this.formItems.add( formItem );
    }

    public Iterable<FormItem> formItemIterable()
    {
        return formItems;
    }

    public HierarchicalFormItem getFormItem( final String path )
    {
        return formItems.getFormItem( FormItemPath.from( path ) );
    }

    public HierarchicalFormItem getFormItem( final FormItemPath path )
    {
        return formItems.getFormItem( path );
    }

    public FormItems getFormItems()
    {
        return formItems;
    }

    public Input getInput( final FormItemPath path )
    {
        return formItems.getInput( path );
    }

    public Input getInput( final String path )
    {
        return FormItemPath.hasNotPathElementDivider( path ) ? formItems.getInput( path ) : formItems.getInput( FormItemPath.from( path ) );
    }

    public FormItemSet getFormItemSet( final FormItemPath path )
    {
        return formItems.getFormItemSet( path );
    }

    public FormItemSet getFormItemSet( final String path )
    {
        return FormItemPath.hasNotPathElementDivider( path )
            ? formItems.getFormItemSet( path )
            : formItems.getFormItemSet( FormItemPath.from( path ) );
    }

    public MixinReference getMixinReference( final FormItemPath path )
    {
        return formItems.getMixinReference( path );
    }

    public MixinReference getMixinReference( final String path )
    {
        return FormItemPath.hasNotPathElementDivider( path )
            ? formItems.getMixinReference( path )
            : formItems.getMixinReference( FormItemPath.from( path ) );
    }

    public void mixinReferencesToFormItems( final MixinFetcher mixinFetcher )
    {
        formItems.mixinReferencesToFormItems( mixinFetcher );
    }

    @Override
    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "formItems", formItems );
        return s.toString();
    }

    public Form copy()
    {
        return newForm( this ).build();
    }

    public static Builder newForm()
    {
        return new Builder();
    }

    public static Builder newForm( final Form form )
    {
        return new Builder( form );
    }

    @Override
    public Iterator<FormItem> iterator()
    {
        return formItems.iterator();
    }

    public static class Builder
    {
        private List<FormItem> formItems;

        private Builder()
        {
            this.formItems = new ArrayList<>();
        }

        private Builder( final Form source )
        {
            Preconditions.checkNotNull( source, "Given form cannot be null" );

            this.formItems = new ArrayList<>();
            for ( FormItem formItem : source.formItems )
            {
                formItems.add( formItem.copy() );
            }
        }

        public Builder addFormItem( final FormItem formItem )
        {
            this.formItems.add( formItem );
            return this;
        }

        public Form build()
        {
            return new Form( this );
        }
    }
}
