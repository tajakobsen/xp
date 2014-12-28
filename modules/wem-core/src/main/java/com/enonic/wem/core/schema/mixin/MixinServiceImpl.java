package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

public class MixinServiceImpl
    implements MixinService
{
    private MixinDao mixinDao;

    @Override
    public Mixin getByName( final MixinName name )
    {
        return this.mixinDao.getMixin( name );
    }

    @Override
    public Mixins getAll()
    {
        return mixinDao.getAllMixins();
    }

    @Override
    public Mixins getByModule( final ModuleKey moduleKey )
    {
        return mixinDao.getByModule( moduleKey );
    }

    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
