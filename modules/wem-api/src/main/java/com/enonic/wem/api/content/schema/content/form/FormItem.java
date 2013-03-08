package com.enonic.wem.api.content.schema.content.form;


import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.schema.mixin.Mixin;

public abstract class FormItem
{
    private final String name;

    private FormItems parent;

    FormItem( final String name )
    {
        Preconditions.checkNotNull( name, "a name is required for a FormItem" );
        Preconditions.checkArgument( StringUtils.isNotBlank( name ), "a name is required for a FormItem" );
        Preconditions.checkArgument( !name.contains( "." ), "name cannot contain punctations: " + name );

        this.name = name;
    }

    void setParent( final FormItems parent )
    {
        this.parent = parent;
    }

    public FormItems getParent()
    {
        return parent;
    }

    public final String getName()
    {
        return name;
    }

    public final FormItemPath getPath()
    {
        return resolvePath();
    }

    FormItemPath resolvePath()
    {
        return FormItemPath.from( resolveParentPath(), name );
    }

    final FormItemPath resolveParentPath()
    {
        if ( parent == null )
        {
            return FormItemPath.ROOT;
        }
        else
        {
            return parent.getPath();
        }
    }

    public abstract FormItem copy();

    public Input toInput()
    {
        if ( !( this instanceof Input ) )
        {
            throw new IllegalArgumentException( "This FormItem [" + getName() + "] is not an Input: " + this.getClass().getSimpleName() );
        }
        return (Input) this;
    }

    public FormItemSet toFormItemSet()
    {
        if ( !( this instanceof FormItemSet ) )
        {
            throw new IllegalArgumentException(
                "This FormItem [" + getName() + "] is not a FormItemSet: " + this.getClass().getSimpleName() );
        }
        return (FormItemSet) this;
    }

    public Layout toLayout()
    {
        if ( !( this instanceof Layout ) )
        {
            throw new IllegalArgumentException( "This FormItem [" + getName() + "] is not a Layout: " + this.getClass().getSimpleName() );
        }
        return (Layout) this;
    }

    @Override
    public String toString()
    {
        return name;
    }

    static FormItem from( final Mixin mixin, final MixinReference mixinReference )
    {
        final FormItem formItem = mixin.getFormItem();
        final FormItem newFormItem;
        if ( formItem instanceof FormItemSet )
        {
            newFormItem = FormItemSet.newFormItemSet( (FormItemSet) formItem ).
                name( mixinReference.getName() ).build();
        }
        else if ( formItem instanceof Input )
        {
            newFormItem = Input.newInput( (Input) formItem ).
                name( mixinReference.getName() ).build();
        }
        else
        {
            throw new IllegalArgumentException(
                "Cannot create FormItem from mixin [" + mixin.getQualifiedName() + "] of type: " + formItem.getClass().getSimpleName() );
        }
        return newFormItem;
    }
}
