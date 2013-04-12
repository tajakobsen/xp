package com.enonic.wem.api.content.schema.content.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.content.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

import static com.enonic.wem.api.content.data.type.DataTool.newDataChecker;

public class Color
    extends BaseInputType
{
    public Color()
    {
    }

    @Override
    public void checkBreaksRequiredContract( final Data data )
        throws BreaksRequiredContractException
    {
        final String stringValue = (String) data.getObject();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( data, this );
        }
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        newDataChecker().pathRequired( "red" ).type( DataTypes.WHOLE_NUMBER ).range( 0, 255 ).check( data );
        newDataChecker().pathRequired( "green" ).type( DataTypes.WHOLE_NUMBER ).range( 0, 255 ).check( data );
        newDataChecker().pathRequired( "blue" ).type( DataTypes.WHOLE_NUMBER ).range( 0, 255 ).check( data );
    }

    @Override
    public Value newValue( final String value )
    {
        throw new UnsupportedOperationException();
    }
}
