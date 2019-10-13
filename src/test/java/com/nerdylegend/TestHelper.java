package com.nerdylegend;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.client.WebClient;

public class TestHelper {

    protected static final String LOCALHOST = "localhost";
    protected final static int TEST_PORT = 8091;
    protected final Vertx vertx = Vertx.vertx();

    protected String fromAccount;
    protected String toAccount;

    private HttpServer httpServer;
    protected WebClient client;
    protected MultiMap form;

    public void startTestServer(Vertx vertx, Integer testPort) {
        httpServer = vertx.createHttpServer();
        App.startServer(vertx, httpServer, testPort);
        client = WebClient.create(vertx);
        form = MultiMap.caseInsensitiveMultiMap();
    }

    protected void createTestAccounts(){
        form.clear();
        form.set("name", "Mike");
        form.set("currencyCode", "EUR");
        createUser();

        form.set("name", "John");
        form.set("currencyCode", "USD");
        fromAccount  =  createUser();

        form.set("name", "Paul");
        form.set("currencyCode", "USD");
        toAccount = createUser();
    }

    private String createUser() {
        JsonObject jsonObject  = client
                .post(TEST_PORT, LOCALHOST, "/account")
                .rxSendForm(form).blockingGet().bodyAsJsonObject();
        form.clear();
        return jsonObject.getString("accountNumber");
    }

    public void tearDownTestServer() {
        httpServer.rxClose();
    }


}
