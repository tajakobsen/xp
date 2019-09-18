package com.enonic.xp.web.jetty.impl.websocket;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.jetty.impl.JettyController;
import com.enonic.xp.web.jetty.impl.JettyTestSupport;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketServiceImplTest
    extends JettyTestSupport
{
    private TestEndpoint endpoint;

    private TestWebSocketServlet servlet;

    private WebSocketServiceImpl service;

    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    @Override
    protected void configure()
        throws Exception
    {
        this.endpoint = new TestEndpoint();

        final JettyController controller = Mockito.mock( JettyController.class );

        this.server.setVirtualHosts( new String[]{DispatchConstants.VIRTUAL_HOST_PREFIX + DispatchConstants.XP_CONNECTOR} );
        Mockito.when( controller.getServletContexts() ).thenReturn( List.of( this.server.getHandler().getServletContext() ) );

        this.service = new WebSocketServiceImpl();
        this.service.setController( controller );
        this.service.activate();

        this.servlet = new TestWebSocketServlet();
        this.servlet.service = this.service;
        this.servlet.endpoint = this.endpoint;

        addServlet( this.servlet, "/ws" );

        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();
    }

    @Override
    protected void destroy()
        throws Exception
    {
        this.service.deactivate();
    }

    private void newWebSocketRequest( final WebSocketListener listener )
    {
        final Request request = new Request.Builder().
            url( "ws://localhost:" + this.server.getPort() + "/ws" ).
            build();

        WebSocketCall.create( this.client, request ).enqueue( listener );
    }

    @Test
    public void testNotSocket()
        throws Exception
    {
        this.req.setMethod( "GET" );
        this.servlet.service( this.req, this.res );
        assertEquals( 404, this.res.getStatus() );
    }

    @Test
    @Disabled("failing on travis-ci all the time")
    public void sendFromServer()
        throws Exception
    {
        final ClientTestListener listener1 = new ClientTestListener();
        final ClientTestListener listener2 = new ClientTestListener();

        assertEquals( 0, this.endpoint.sessions.size() );

        newWebSocketRequest( listener1 );
        newWebSocketRequest( listener2 );

        listener1.waitForConnect();
        listener2.waitForConnect();

        this.endpoint.sendToAll( "Hello from server" );

        listener1.waitForMessage();
        listener2.waitForMessage();

        assertEquals( "TEXT", listener1.type );
        assertEquals( "Hello from server", listener1.message );

        assertEquals( "TEXT", listener2.type );
        assertEquals( "Hello from server", listener2.message );
    }

    @Test
    @Disabled("failing on travis-ci all the time")
    public void sendFromClient()
        throws Exception
    {
        final ClientTestListener listener1 = new ClientTestListener();
        final ClientTestListener listener2 = new ClientTestListener();

        this.endpoint.expectMessages( 2 );

        newWebSocketRequest( listener1 );
        newWebSocketRequest( listener2 );

        listener1.waitForConnect();
        listener2.waitForConnect();

        listener1.sendMessage( "Hello from client" );
        listener2.sendMessage( "Hello from client" );

        this.endpoint.waitForMessages();

        assertEquals( 2, this.endpoint.sessions.size() );
        assertEquals( 2, this.endpoint.messages.size() );
        assertEquals( "[Hello from client, Hello from client]", this.endpoint.messages.values().toString() );
    }
}
