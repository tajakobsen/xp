package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public abstract class SchemaMapper<T extends BaseSchema<? extends BaseSchemaName>>
    implements MapSerializable
{
    protected final T descriptor;

    private final Resource resource;

    SchemaMapper( final DynamicSchemaResult<T> descriptor )
    {
        this.descriptor = descriptor.getSchema();
        this.resource = descriptor.getResource();
    }

    public void serialize( final MapGenerator gen )
    {
        gen.value( "name", descriptor.getName() );
        gen.value( "displayName", descriptor.getDisplayName() );
        gen.value( "displayNameI18nKey", descriptor.getDisplayNameI18nKey() );
        gen.value( "description", descriptor.getDescription() );
        gen.value( "descriptionI18nKey", descriptor.getDescriptionI18nKey() );
        gen.value( "createdTime", descriptor.getCreatedTime() );
        gen.value( "creator", descriptor.getCreator() );
        gen.value( "modifiedTime", descriptor.getModifiedTime() );
        gen.value( "modifier", descriptor.getModifier() );
        gen.value( "resource", resource.readString() );
        gen.value( "type", getType() );

        DynamicSchemaSerializer.serializeIcon( gen, descriptor.getIcon() );
    }

    protected abstract String getType();
}
