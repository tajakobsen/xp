package com.enonic.wem.api.content.schema.content.form;


public abstract class Layout
    extends FormItem
{
    Layout( final String name )
    {
        super( name );
    }


    @Override
    FormItemPath resolvePath()
    {
        return resolveParentPath();
    }

    public abstract FormItem getFormItem( final String name );
}
