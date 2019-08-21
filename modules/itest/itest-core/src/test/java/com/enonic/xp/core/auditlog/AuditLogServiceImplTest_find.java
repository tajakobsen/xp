package com.enonic.xp.core.auditlog;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogIds;
import com.enonic.xp.auditlog.AuditLogParams;
import com.enonic.xp.auditlog.FindAuditLogParams;
import com.enonic.xp.auditlog.FindAuditLogResult;
import com.enonic.xp.core.impl.auditlog.AuditLogContext;

import static org.junit.Assert.*;

public class AuditLogServiceImplTest_find
    extends AbstractAuditLogServiceTest
{

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void find_anonymous()
    {
        AuditLogParams params = AuditLogParams.create().type( "test" ).build();
        AuditLog log = auditLogService.log( params );

        FindAuditLogResult result = auditLogService.find( FindAuditLogParams.create().ids( AuditLogIds.from( log.getId() ) ).build() );
        assertEquals( 0L, result.getCount() );
    }

    private FindAuditLogResult find_helper( FindAuditLogParams params )
    {
        return AuditLogContext.createAdminContext().callWith( () -> auditLogService.find( params ) );
    }

    @Test
    public void find()
    {
        AuditLogParams params = AuditLogParams.create().type( "test" ).build();
        AuditLog log = auditLogService.log( params );
        FindAuditLogResult result = find_helper( FindAuditLogParams.create().
            ids( AuditLogIds.from( log.getId() ) ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log, result.getHits().first() );
    }

    @Test
    public void find_none()
    {
        AuditLogParams params = AuditLogParams.create().type( "test" ).build();
        auditLogService.log( params );
        FindAuditLogResult result = find_helper( FindAuditLogParams.create().build() );
        assertEquals( 0L, result.getCount() );
    }

    @Test
    public void find_from()
    {
        AuditLog log = auditLogService.log( AuditLogParams.create().
            type( "test" ).
            time( Instant.now().minus( 30, ChronoUnit.DAYS ) ).
            build() );

        FindAuditLogResult result = find_helper( FindAuditLogParams.create().
            from( Instant.now() ).
            build() );
        assertEquals( 0L, result.getCount() );

        result = find_helper( FindAuditLogParams.create().
            from( Instant.now().minus( 31, ChronoUnit.DAYS ) ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log, result.getHits().first() );
    }

    @Test
    public void find_to()
    {
        AuditLog log = auditLogService.log( AuditLogParams.create().
            type( "test" ).
            time( Instant.now() ).
            build() );

        FindAuditLogResult result = find_helper( FindAuditLogParams.create().
            to( Instant.now().minus( 30, ChronoUnit.DAYS ) ).
            build() );
        assertEquals( 0L, result.getCount() );

        result = find_helper( FindAuditLogParams.create().
            to( Instant.now() ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log, result.getHits().first() );
    }


    @Test
    public void find_type()
    {
        AuditLog log1 = auditLogService.log( AuditLogParams.create().
            type( "type1" ).
            build() );

        AuditLog log2 = auditLogService.log( AuditLogParams.create().
            type( "type2" ).
            build() );

        FindAuditLogResult result = find_helper( FindAuditLogParams.create().
            type( "type1" ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log1, result.getHits().first() );

        result = find_helper( FindAuditLogParams.create().
            type( "type2" ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log2, result.getHits().first() );
    }

    @Test
    public void find_source()
    {
        AuditLog log1 = auditLogService.log( AuditLogParams.create().
            type( "test" ).
            source( "source1" ).
            build() );

        AuditLog log2 = auditLogService.log( AuditLogParams.create().
            type( "test" ).
            source( "source2" ).
            build() );

        FindAuditLogResult result = find_helper( FindAuditLogParams.create().
            source( "source1" ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log1, result.getHits().first() );

        result = find_helper( FindAuditLogParams.create().
            source( "source2" ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log2, result.getHits().first() );
    }
}
