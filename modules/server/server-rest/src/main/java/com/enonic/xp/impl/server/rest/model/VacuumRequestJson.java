package com.enonic.xp.impl.server.rest.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VacuumRequestJson
{

    private final Long ageThreshold;

    private final List<String> tasks;

    @JsonCreator
    public VacuumRequestJson( @JsonProperty("ageThreshold") final Long ageThreshold,
                              @JsonProperty("tasks") final List<String> tasks )
    {
        this.ageThreshold = ageThreshold;
        this.tasks = tasks;
    }

    public Long getAgeThreshold()
    {
        return ageThreshold;
    }

    public List<String> getTasks()
    {
        return tasks;
    }
}
