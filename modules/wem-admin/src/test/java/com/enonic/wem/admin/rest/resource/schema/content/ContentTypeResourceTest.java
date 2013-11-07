package com.enonic.wem.admin.rest.resource.schema.content;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.acme.DummyCustomInputType;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.command.schema.content.GetAllContentTypes;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.schema.content.UpdateContentType;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.MixinReference;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;

import static com.enonic.wem.api.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.form.inputtype.InputTypes.TEXT_LINE;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;


public class ContentTypeResourceTest
    extends AbstractResourceTest
{
    private UploadService uploadService;

    private ContentTypeResource resource = new ContentTypeResource();

    private Client client;

    private static final ContentTypeName MY_CTY_QUALIFIED_NAME = ContentTypeName.from( "my_cty" );

    public ContentTypeResourceTest()
    {
        super();
    }

    @Override
    protected Object getResourceInstance()
    {
        client = Mockito.mock( Client.class );
        resource = new ContentTypeResource();
        resource.setClient( client );

        uploadService = Mockito.mock( UploadService.class );
        resource.setUploadService( uploadService );

        return resource;
    }

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();
    }

    @Test
    public void get_contentType_with_only_one_input()
        throws Exception
    {
        // setup
        final ContentType contentType = newContentType().
            name( MY_CTY_QUALIFIED_NAME ).
            createdTime( new DateTime( 2013, 1, 1, 12, 0, 0, DateTimeZone.UTC ) ).
            superType( ContentTypeName.unstructured() ).
            displayName( "My ContentType" ).
            addFormItem( newInput().
                name( "myTextLine" ).
                inputType( TEXT_LINE ).
                label( "My text line" ).
                required( true ).
                build() ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );

        // execute
        MultivaluedMap<String, String> qualifiedNames = new MultivaluedMapImpl();
        qualifiedNames.add( "qualifiedName", MY_CTY_QUALIFIED_NAME.toString() );
        String jsonString =
            resource().path( "schema/content" ).queryParams( qualifiedNames ).queryParam( "mixinReferencesToFormItems", "false" ).get(
                String.class );

        // verify
        assertJson( "ContentTypeResourceTest-get_contentType_with_only_one_input-result.json", jsonString );
    }

    @Test
    public void get_contentType_with_all_formItem_types()
        throws Exception
    {
        // setup

        Input myTextLine = newInput().
            name( "myTextLine" ).
            inputType( TEXT_LINE ).
            label( "My text line" ).
            required( true ).
            build();

        Input myCustomInput = newInput().
            name( "myCustomInput" ).
            inputType( new DummyCustomInputType() ).
            label( "My custom input" ).
            required( false ).
            build();

        FieldSet myFieldSet = newFieldSet().
            name( "myFieldSet" ).
            label( "My field set" ).
            addFormItem( newInput().
                name( "myTextLine" ).
                inputType( TEXT_LINE ).
                label( "My text line" ).
                required( false ).
                build() ).
            build();

        FormItemSet myFormItemSet = newFormItemSet().
            name( "myFormItemSet" ).
            label( "My form item set" ).
            addFormItem( newInput().
                name( "myTextLine" ).
                inputType( TEXT_LINE ).
                label( "My text line" ).
                required( false ).
                build() ).
            build();

        MixinReference myMixinReference = newMixinReference().
            name( "myMixinReference" ).
            mixin( "mymixin" ).
            build();

        ContentType contentType = newContentType().
            createdTime( new DateTime( 2013, 1, 1, 12, 0, 0, DateTimeZone.UTC ) ).
            name( MY_CTY_QUALIFIED_NAME ).
            superType( ContentTypeName.unstructured() ).
            addFormItem( myTextLine ).
            addFormItem( myCustomInput ).
            addFormItem( myFieldSet ).
            addFormItem( myFormItemSet ).
            addFormItem( myMixinReference ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );

        // execute
        MultivaluedMap<String, String> qualifiedNames = new MultivaluedMapImpl();
        qualifiedNames.add( "qualifiedName", MY_CTY_QUALIFIED_NAME.toString() );
        String jsonString =
            resource().path( "schema/content" ).queryParams( qualifiedNames ).queryParam( "mixinReferencesToFormItems", "false" ).get(
                String.class );

        // verify
        assertJson( "ContentTypeResourceTest-get_contentType_with_all_formItem_types-result.json", jsonString );
    }

    @Test
    public void get_contentType_with_format_as_xml()
        throws Exception
    {
        // setup
        final ContentType contentType = newContentType().
            createdTime( new DateTime( 2013, 1, 1, 12, 0, 0 ) ).
            name( MY_CTY_QUALIFIED_NAME ).
            superType( ContentTypeName.unstructured() ).
            addFormItem( newInput().
                name( "myTextLine" ).
                inputType( TEXT_LINE ).
                label( "My text line" ).
                required( true ).
                build() ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );

        // execute
        MultivaluedMap<String, String> qualifiedNames = new MultivaluedMapImpl();
        qualifiedNames.add( "qualifiedName", MY_CTY_QUALIFIED_NAME.toString() );
        String jsonString = resource().path( "schema/content/config" ).queryParams( qualifiedNames ).get( String.class );

        // verify
        assertJson( "ContentTypeResourceTest-get_contentType_with_format_as_xml-result.json", jsonString );
    }

    @Test
    public void list_one_contentType_with_only_one_input()
        throws Exception
    {
        // setup
        final ContentType contentType = newContentType().
            createdTime( new DateTime( 2013, 1, 1, 12, 0, 0, DateTimeZone.UTC ) ).
            name( MY_CTY_QUALIFIED_NAME ).
            superType( ContentTypeName.unstructured() ).
            addFormItem( newInput().
                name( "myTextLine" ).
                inputType( TEXT_LINE ).
                label( "My text line" ).
                required( true ).
                build() ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetAllContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );

        // execute
        MultivaluedMap<String, String> qualifiedNames = new MultivaluedMapImpl();
        qualifiedNames.add( "qualifiedNames", MY_CTY_QUALIFIED_NAME.toString() );
        String jsonString = resource().
            path( "schema/content/list" ).
            queryParams( qualifiedNames ).
            queryParam( "format", "JSON" ).
            queryParam( "mixinReferencesToFormItems", "false" ).
            get( String.class );

        // verify
        assertJson( "ContentTypeResourceTest-list_one_contentType_with_only_one_input-result.json", jsonString );
    }

    @Test
    public void test_create_new_content_type()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );

        String jsonString = resource().path( "schema/content/create" ).entity( readFromFile( "create_content_type.json" ),
                                                                               MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "create_content_type_result.json", jsonString );
    }

    @Test
    public void test_create_existing_content_type()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( ContentType.newContentType().name( "htmlarea" ).build() ) );
        String resultJson = resource().path( "schema/content/create" ).entity( readFromFile( "create_content_type.json" ),
                                                                               MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "create_existing_content_type_result.json", resultJson );
    }

    @Test
    public void test_create_content_type_with_broken_xml_config()
        throws Exception
    {
        String result = resource().path( "schema/content/create" ).entity( readFromFile( "broken_xml_content_type.json" ),
                                                                           MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "create_content_type_with_broken_xml.json", result );
    }

    @Test
    public void test_fail_to_create_new_content_type()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );
        Mockito.when( client.execute( Mockito.any( CreateContentType.class ) ) ).thenThrow(
            new SystemException( "Content type creation failed" ) );
        String result = resource().path( "schema/content/create" ).entity( readFromFile( "create_content_type.json" ),
                                                                           MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "fail_to_create_new_content_type.json", result );
    }

    @Test
    public void test_update_content_type()
        throws Exception
    {

        String jsonString = resource().path( "schema/content/update" ).entity( readFromFile( "update_content_type.json" ),
                                                                               MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "update_content_type_result.json", jsonString );
    }

    @Test
    public void test_update_content_type_with_broken_xml_config()
        throws Exception
    {
        String result = resource().path( "schema/content/update" ).entity( readFromFile( "broken_xml_content_type.json" ),
                                                                           MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "update_content_type_with_broken_xml_config.json", result );

    }

    @Test
    public void test_fail_to_update_content_type()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( UpdateContentType.class ) ) ).thenThrow(
            new SystemException( "Content type update failed" ) );
        String result = resource().path( "schema/content/update" ).entity( readFromFile( "update_content_type.json" ),
                                                                           MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "fail_to_update_content_type.json", result );
    }

}
