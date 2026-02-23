package gg.harvester;

import io.github.cdimascio.dotenv.Dotenv;
import io.quarkus.logging.Log;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Provider
public class RequestLoggingFilter implements ClientRequestFilter, ClientResponseFilter {

    private final Boolean logEnabled;

    public RequestLoggingFilter() {
        var dotenv = Dotenv.configure().load();

        this.logEnabled = Boolean.valueOf(dotenv.get("LOG_HTTP_REQUEST_ENABLED", System.getenv("LOG_HTTP_REQUEST_ENABLED")));
    }

    // === REQUEST ===
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (!logEnabled) return;
        String contentType = requestContext.getHeaderString("Content-Type");
        // Intercepta o body antes de enviar
        if (requestContext.hasEntity()) {
            String body = requestContext.getEntity().toString();
            Log.infof("➡️  Request: %s %s\nBody: %s\n",
                    requestContext.getMethod(),
                    requestContext.getUri(),
                    body);
        } else {
            Log.infof("➡️  Request: %s %s (sem body)\n",
                    requestContext.getMethod(), requestContext.getUri());
        }
    }

    // === RESPONSE ===
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        if (!logEnabled) return;
        String body = null;
        if (responseContext.hasEntity()) {
            try (InputStream in = responseContext.getEntityStream()) {
                body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                // recria o stream para não quebrar a leitura do client
                responseContext.setEntityStream(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
            }
        }
        Log.infof("⬅️  Response: %d\nBody: %s\n",
                responseContext.getStatus(),
                body != null && !body.isBlank() ? body : "(vazio)");
    }
}
