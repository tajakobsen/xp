package com.enonic.wem.api.content.type.form;


import com.google.common.base.Objects;

public final class Form
{
    private final FormItems formItems;

    private Form( final FormItems formItems )
    {
        this.formItems = formItems;
    }

    // TODO move to builder
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
        return formItems.getFormItem( new FormItemPath( path ) );
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
        return FormItemPath.hasNotPathElementDivider( path ) ? formItems.getInput( path ) : formItems.getInput( new FormItemPath( path ) );
    }

    public FormItemSet getFormItemSet( final FormItemPath path )
    {
        return formItems.getFormItemSet( path );
    }

    public FormItemSet getFormItemSet( final String path )
    {
        return FormItemPath.hasNotPathElementDivider( path )
            ? formItems.getFormItemSet( path )
            : formItems.getFormItemSet( new FormItemPath( path ) );
    }

    public SubTypeReference getSubTypeReference( final FormItemPath path )
    {
        return formItems.getSubTypeReference( path );
    }

    public SubTypeReference getSubTypeReference( final String path )
    {
        return FormItemPath.hasNotPathElementDivider( path )
            ? formItems.getSubTypeReference( path )
            : formItems.getSubTypeReference( new FormItemPath( path ) );
    }

    public void subTypeReferencesToFormItems( final SubTypeFetcher subTypeFetcher )
    {
        formItems.subTypeReferencesToFormItems( subTypeFetcher );
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

    public static class Builder
    {
        private FormItems formItems;

        private Builder()
        {
            this.formItems = new FormItems();
        }

        private Builder( final Form form )
        {
            this.formItems = new FormItems();
            for ( FormItem formItem : form.formItems )
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
            return new Form( this.formItems );
        }
    }
}
