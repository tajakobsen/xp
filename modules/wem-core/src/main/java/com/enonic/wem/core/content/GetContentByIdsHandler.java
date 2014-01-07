package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentByIds;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.core.command.CommandHandler;


public class GetContentByIdsHandler
    extends CommandHandler<GetContentByIds>
{
    @Override
    public void handle()
        throws Exception
    {
        final Contents contents;

        try
        {
            contents = new GetContentByIdsService( this.context, this.command ).execute();
        }
        catch ( NoEntityWithIdFoundException ex )
        {
            final ContentId contentId = ContentId.from( ex.getId().toString() );
            throw new ContentNotFoundException( contentId );

        }
        command.setResult( contents );
    }

}
