/*
 * Copyright (c) 2023 Picture Soluções em TI - All Rights Reserved
 */

package br.com.picture.demosharepoint;

import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

public class GraphClientBuilder {
    private final DefaultLogger defaultLogger = new DefaultLogger();
    private GraphServiceClient<Request> graphServiceClient;
    private String clientId;
    private String clientSecret;
    private String tenantId;
    private String scope;
    private boolean debug = false;

    public static GraphClientBuilder newBuilder() {
        return new GraphClientBuilder();
    }

    public GraphServiceClient<Request> build() {
        if(StringUtils.isAnyBlank(clientId, clientSecret, tenantId, scope)) {
            throw new IllegalArgumentException("invalid clientId, clientSecret, tenantId or scope");
        }

        if (debug) {
            defaultLogger.setLoggingLevel(LoggerLevel.DEBUG);
        }

        TokenCredentialAuthProvider tokenCredentialAuthProvider = buildAuthProvider();
        return buildGraphClient(tokenCredentialAuthProvider, defaultLogger);
    }

    public GraphClientBuilder debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public GraphClientBuilder clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public GraphClientBuilder clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public GraphClientBuilder tenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public GraphClientBuilder scope(String scope) {
        this.scope = scope;
        return this;
    }

    private TokenCredentialAuthProvider buildAuthProvider() {
        HttpLogOptions httpLogOptions = new HttpLogOptions();
        if (Application.settings.isGraphDebug()) {
            httpLogOptions = new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BODY_AND_HEADERS);
        }

        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .httpLogOptions(httpLogOptions)
                .build();

        return new TokenCredentialAuthProvider(
                Collections.singletonList(scope),
                clientSecretCredential);
    }

    private GraphServiceClient<Request> buildGraphClient(TokenCredentialAuthProvider authProvider, DefaultLogger logger) {
        return GraphServiceClient.builder()
                .logger(logger)
                .authenticationProvider(authProvider)
                .buildClient();
    }
}
