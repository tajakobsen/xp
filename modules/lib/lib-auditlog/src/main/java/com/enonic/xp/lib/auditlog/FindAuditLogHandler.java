package com.enonic.xp.lib.auditlog;

import java.util.stream.Collectors;

import com.enonic.xp.auditlog.AuditLogIds;
import com.enonic.xp.auditlog.FindAuditLogParams;
import com.enonic.xp.lib.auditlog.mapper.FindAuditLogResultMapper;
import com.enonic.xp.script.ScriptValue;

public class FindAuditLogHandler
    extends BaseAuditLogHandler
{

    private AuditLogIds ids;

    @Override
    protected Object doExecute()
    {
        return new FindAuditLogResultMapper( this.auditLogService.find( FindAuditLogParams.
            create().
            ids( ids ).
            build() ) );
    }

    public void setIds( final ScriptValue ids )
    {
        if ( ids == null || ids.getList() == null )
        {
            return;
        }
        this.ids = AuditLogIds.from( ids.getList().stream().map( o -> o.toString() ).collect( Collectors.toList() ) );
    }
}
