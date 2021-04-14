package com.enonic.xp.scheduler;

import java.io.Serializable;
import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public final class ScheduledJob
    implements Serializable
{
    private static final long serialVersionUID = 0;

    private final ScheduledJobName name;

    private final String description;

    private final ScheduleCalendar calendar;

    private final boolean enabled;

    private final DescriptorKey descriptor;

    private final PropertyTree payload;

    private final PrincipalKey user;

    private final PrincipalKey author;

    private final Instant lastRun;

    private ScheduledJob( final Builder builder )
    {
        this.name = builder.name;
        this.description = builder.description;
        this.calendar = builder.calendar;
        this.enabled = builder.enabled;
        this.descriptor = builder.descriptor;
        this.payload = builder.payload;
        this.user = builder.user;
        this.author = builder.author;
        this.lastRun = builder.lastRun;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ScheduledJobName getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public ScheduleCalendar getCalendar()
    {
        return calendar;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public DescriptorKey getDescriptor()
    {
        return descriptor;
    }

    public PropertyTree getPayload()
    {
        return payload;
    }

    public PrincipalKey getUser()
    {
        return user;
    }

    public PrincipalKey getAuthor()
    {
        return author;
    }

    public Instant getLastRun()
    {
        return lastRun;
    }

    public static class Builder
    {
        private ScheduledJobName name;

        private String description;

        private ScheduleCalendar calendar;

        private boolean enabled;

        private DescriptorKey descriptor;

        private PropertyTree payload = new PropertyTree();

        private PrincipalKey user;

        private PrincipalKey author;

        private Instant lastRun;

        public Builder name( final ScheduledJobName name )
        {
            this.name = name;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder calendar( final ScheduleCalendar calendar )
        {
            this.calendar = calendar;
            return this;
        }

        public Builder enabled( final boolean enabled )
        {
            this.enabled = enabled;
            return this;
        }

        public Builder descriptor( final DescriptorKey descriptor )
        {
            this.descriptor = descriptor;
            return this;
        }

        public Builder payload( final PropertyTree payload )
        {
            this.payload = payload;
            return this;
        }

        public Builder user( final PrincipalKey user )
        {
            this.user = user;
            return this;
        }

        public Builder author( final PrincipalKey author )
        {
            this.author = author;
            return this;
        }

        public Builder lastRun( final Instant lastRun )
        {
            this.lastRun = lastRun;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( name, "name must be set." );
            Preconditions.checkNotNull( calendar, "calendar must be set." );
            Preconditions.checkNotNull( descriptor, "descriptor must be set." );
            Preconditions.checkNotNull( payload, "payload must be set." );
        }

        public ScheduledJob build()
        {
            validate();
            return new ScheduledJob( this );
        }
    }
}