package com.enonic.wem.core.schema.mixin;

import javax.inject.Inject;
import javax.jcr.Session;

import org.joda.time.DateTime;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.entity.CreateNode;
import com.enonic.wem.api.entity.CreateNodeResult;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;

public final class CreateMixinHandler
    extends CommandHandler<CreateMixin>
{
    private MixinDao mixinDao;

    @Override
    public void handle()
        throws Exception
    {
        final DateTime currentTime = DateTime.now();

        final Mixin.Builder mixinBuilder =
            newMixin().name( command.getName() ).formItems( command.getFormItems() ).displayName( command.getDisplayName() ).
                createdTime( currentTime ).modifiedTime( currentTime ).icon( command.getIcon() );

        final Mixin mixin = mixinBuilder.build();

        final Session session = context.getJcrSession();
        mixinDao.create( mixin, session );
        session.save();
        command.setResult( mixin.getQualifiedName() );

        // Storing using Item
        final NodePath parentNodePath = NodePath.newPath( "/mixins" ).build();
        final UserKey creator = UserKey.superUser();
        final MixinDataSetTranslator translator = new MixinDataSetTranslator();

        final CreateNode createNodeCommand = Commands.item().create().
            name( command.getName() ).
            parent( parentNodePath ).
            data( translator.toRootDataSet( command ) );

        final CreateNodeResult createNodeResult = context.getClient().execute( createNodeCommand );
        //final Mixin persistedMixin = MixinFactory.fromItem( createItemResult.getPersistedItem() );
        // TODO when Item persisting is ready: command.setResult( persistedMixin.getQualifiedName() );
    }

    @Inject
    public void setMixinDao( final MixinDao value )
    {
        this.mixinDao = value;
    }
}
