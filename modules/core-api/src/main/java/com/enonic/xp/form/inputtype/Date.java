package com.enonic.xp.form.inputtype;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;

final class Date
    extends InputType
{
    Date()
    {
        super( DateConfig.class );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return DateConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer()
    {
        return DateConfigXmlSerializer.DEFAULT;
    }

    @Override
    public Value newValue( final String value )
    {
        return Value.newLocalDate( ValueTypes.LOCAL_DATE.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }
}