package com.enonic.wem.api.data2;

import java.util.function.Function;

final class JavaTypeConverter<T>
{
    private final Class<T> type;

    private final Function<Object, T> function;

    public JavaTypeConverter( final Class<T> type, final Function<Object, T> function )
    {
        this.type = type;
        this.function = function;
    }

    public Class<T> getType()
    {
        return this.type;
    }

    public T convertFrom( final Object value )
    {
        return this.function.apply( value );
    }
}
