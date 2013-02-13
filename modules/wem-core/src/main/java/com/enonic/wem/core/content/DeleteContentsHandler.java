package com.enonic.wem.core.content;


import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.DeleteContents;
import com.enonic.wem.api.content.ContentDeletionResult;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.exception.ContentNotFoundException;
import com.enonic.wem.api.exception.UnableToDeleteContentException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.search.IndexService;

@Component
public class DeleteContentsHandler
    extends CommandHandler<DeleteContents>
{
    private ContentDao contentDao;

    private IndexService indexService;

    public DeleteContentsHandler()
    {
        super( DeleteContents.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteContents command )
        throws Exception
    {
        final ContentDeletionResult contentDeletionResult = new ContentDeletionResult();
        final Iterable<ContentSelector> selectors = command.getSelectors();
        final Session session = context.getJcrSession();
        for ( ContentSelector contentSelector : selectors )
        {
            try
            {
                contentDao.delete( contentSelector, session );
                contentDeletionResult.success( contentSelector );
                session.save();
            }
            catch ( ContentNotFoundException e )
            {
                contentDeletionResult.failure( contentSelector, e );
            }
            catch ( UnableToDeleteContentException e )
            {
                contentDeletionResult.failure( contentSelector, e );
            }
        }
        command.setResult( contentDeletionResult );
    }

    @Autowired
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Autowired
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
