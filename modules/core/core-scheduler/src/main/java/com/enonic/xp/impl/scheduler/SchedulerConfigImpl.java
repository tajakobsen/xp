package com.enonic.xp.impl.scheduler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.scheduler.CalendarService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.SchedulerName;
import com.enonic.xp.security.PrincipalKey;

@Component(configurationPid = "com.enonic.xp.scheduler")
public class SchedulerConfigImpl
    implements SchedulerConfig
{
    private static final String JOB_PROPERTY_PREFIX = "job.";

    private static final Pattern JOB_NAME_PATTERN = Pattern.compile( "^(?<name>[\\w]+)(.+)$" );

    private static final Pattern JOB_PROPERTY_PATTERN = Pattern.compile( "^(?<property>[a-zA-Z]+)(?:(.+)|$)" );

    private static final Pattern CALENDAR_PROPERTY_PATTERN = Pattern.compile( "^(?<calendar>[a-zA-Z]+)$" );

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final PropertyTreeMarshallerService treeMarshallerService;

    private final CalendarService calendarService;

    private Configuration config;

    @Activate
    public SchedulerConfigImpl( final Map<String, String> map, @Reference final PropertyTreeMarshallerService treeMarshallerService,
                                @Reference final CalendarService calendarService )
    {
        this.treeMarshallerService = treeMarshallerService;
        this.calendarService = calendarService;

        this.config = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            addAll( map ).
            build();

        this.config = new ConfigInterpolator().interpolate( this.config );
    }

    @Override
    public Set<CreateScheduledJobParams> jobs()
    {
        final Configuration jobConfig = this.config.subConfig( JOB_PROPERTY_PREFIX );

        final Set<SchedulerName> jobNames = parseNames( jobConfig );

        return jobNames.stream().
            map( name -> parseProperties( name, jobConfig.subConfig( name.getValue() + "." ) ) ).collect( Collectors.toSet() );

    }

    private Set<SchedulerName> parseNames( final Configuration jobConfig )
    {
        return jobConfig.asMap().keySet().
            stream().
            map( JOB_NAME_PATTERN::matcher ).
            filter( Matcher::find ).
            map( matcher -> matcher.group( "name" ) ).
            map( SchedulerName::from ).
            collect( Collectors.toSet() );
    }

    private CreateScheduledJobParams parseProperties( final SchedulerName name, final Configuration properties )
    {
        final CreateScheduledJobParams.Builder job = CreateScheduledJobParams.create().name( name );

        for ( final Map.Entry<String, String> entry : properties.asMap().entrySet() )
        {
            final String value = entry.getValue();

            final Matcher matcher = JOB_PROPERTY_PATTERN.matcher( entry.getKey() );
            if ( matcher.matches() )
            {
                final String propertyName = matcher.group( "property" );

                switch ( propertyName )
                {
                    case ScheduledJobPropertyNames.DESCRIPTION:
                        job.description( value );
                        break;
                    case ScheduledJobPropertyNames.DESCRIPTOR:
                        job.descriptor( DescriptorKey.from( value ) );
                        break;
                    case ScheduledJobPropertyNames.ENABLED:
                        job.enabled( Boolean.parseBoolean( value ) );
                        break;
                    case ScheduledJobPropertyNames.AUTHOR:
                        job.author( PrincipalKey.from( value ) );
                        break;
                    case ScheduledJobPropertyNames.USER:
                        job.user( PrincipalKey.from( value ) );
                        break;
                    case ScheduledJobPropertyNames.PAYLOAD:
                        try
                        {
                            job.payload( treeMarshallerService.marshal( MAPPER.readValue( value, HashMap.class ) ) );
                        }
                        catch ( JsonProcessingException e )
                        {
                            throw new RuntimeException( e );
                        }
                        break;
                    case ScheduledJobPropertyNames.CALENDAR:
                        break;
                    default:
                        throw new IllegalArgumentException( String.format( "[%s] is invalid job property.", propertyName ) );
                }
            }
        }
        final ScheduleCalendar calendar = parseCalendar( properties.subConfig( ScheduledJobPropertyNames.CALENDAR + "." ) );

        return job.calendar( calendar ).
            build();
    }

    private ScheduleCalendar parseCalendar( final Configuration properties )
    {
        ScheduleCalendarType type = null;
        String value = null;
        TimeZone timezone = null;

        for ( final Map.Entry<String, String> entry : properties.asMap().entrySet() )
        {

            final Matcher matcher = CALENDAR_PROPERTY_PATTERN.matcher( entry.getKey() );
            if ( matcher.matches() )
            {
                final String calendarProperty = matcher.group( "calendar" );
                switch ( calendarProperty )
                {
                    case ScheduledJobPropertyNames.CALENDAR_TYPE:
                        type = ScheduleCalendarType.valueOf( entry.getValue().toUpperCase() );
                        break;
                    case ScheduledJobPropertyNames.CALENDAR_VALUE:
                        value = entry.getValue();
                        break;
                    case ScheduledJobPropertyNames.CALENDAR_TIMEZONE:
                        timezone = TimeZone.getTimeZone( entry.getValue() );
                        break;
                    default:
                        throw new IllegalArgumentException( String.format( "[%s] is invalid calendar property.", calendarProperty ) );
                }
            }
        }

        return buildCalendar( type, value, timezone );
    }

    private ScheduleCalendar buildCalendar( final ScheduleCalendarType type, final String value, final TimeZone timezone )
    {
        switch ( type )
        {
            case ONE_TIME:
                return calendarService.oneTime( Instant.parse( value ) );
            case CRON:
                return calendarService.cron( value, timezone );
            default:
                throw new IllegalArgumentException( String.format( "[%s] is invalid calendar type.", type ) );
        }
    }
}
