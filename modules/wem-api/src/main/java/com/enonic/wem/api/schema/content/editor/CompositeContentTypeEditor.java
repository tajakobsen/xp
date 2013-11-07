package com.enonic.wem.api.schema.content.editor;

import com.enonic.wem.api.schema.content.ContentType;

final class CompositeContentTypeEditor
    implements ContentTypeEditor
{
    private final ContentTypeEditor[] editors;

    CompositeContentTypeEditor( final ContentTypeEditor... editors )
    {
        this.editors = editors;
    }

    @Override
    public ContentType edit( final ContentType contentType )
        throws Exception
    {
        boolean modified = false;
        ContentType contentEdited = contentType;
        for ( final ContentTypeEditor editor : this.editors )
        {
            final ContentType updatedContent = editor.edit( contentEdited );
            if ( updatedContent != null )
            {
                contentEdited = updatedContent;
                modified = true;
            }
        }
        return modified ? contentEdited : null;
    }
}
