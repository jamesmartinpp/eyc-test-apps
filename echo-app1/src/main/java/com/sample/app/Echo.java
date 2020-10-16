package com.sample.app;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Echo REST endpoint class
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@Path("/echo")
@Component
public class Echo {

    @Autowired
    private EchoMessageCreator echoer;



    public Echo() throws Exception {
        //File keystore = new File(ClassLoader.getSystemClassLoader().getResource("/selfsigned.jkd").getFile());
        //InputStream keystore = Echo.class.getResourceAsStream("foo.jks");
    }
    /**
     * Receives a simple POST request message containing as payload
     * a text, in text plain format, to be echoed by the service.
     * It returns as response, in JSON, the text to be echoed plus a timestamp of the
     * moment the echo response was created on the server side
     *
     * @param echoText
     * @return
     */
    @POST
    @Consumes({ MediaType.TEXT_PLAIN })
    @Produces({ MediaType.TEXT_PLAIN })
    public String echo(@NotEmpty String echoText) {
        SslContextBuilder sslContextBuilder = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE);
        HttpClient client =
                HttpClient.create()
                        .protocol(HttpProtocol.HTTP11)
                        .headers(h -> h.set("Accept", "text/plain"))
                        .secure(spec -> spec.sslContext(sslContextBuilder));
        String response = client.post()
                .uri("https://localhost:8443/sample-app/echo")
                .send(ByteBufFlux.fromString(Mono.just(echoText)))
                .responseContent()
                .aggregate()
                .asString()
                .block();

        return response;
    }

    public static void main(String[] args) throws Exception {
        Echo echo = new Echo();
        String response = echo.echo("whatever, dude");

        System.out.println(response);
    }

}
