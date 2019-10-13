package com.nerdylegend;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

public class AppTest extends TestHelper {

    @Before
    public void setup() {
        startTestServer(vertx, TEST_PORT);
    }

    @Test
    public void testAccountCreation() {

        form.clear();
        form.set("name", "Daniel");
        form.set("currencyCode", "GBP");

        JsonObject object = client
                .post(TEST_PORT, LOCALHOST, "/account")
                .rxSendForm(form).blockingGet().bodyAsJsonObject();
        Assert.assertNotNull(object);
        Assert.assertEquals(object.getString("balance"), "0.00 GBP");
        Assert.assertNotNull(object.getString("accountNumber"));
        Assert.assertEquals(object.getString("name"), "Daniel");
    }

    @Test
    public void testGetAllAccounts() {
        createTestAccounts();
        JsonArray object = client
                .get(TEST_PORT, LOCALHOST, "/account")
                .rxSend().blockingGet().bodyAsJsonArray();
        Assert.assertNotNull(object);
        Assert.assertEquals(object.size(), 3);
    }

    @Test
    public void testGetAccount() {
        createTestAccounts();
        JsonObject object = client
                .get(TEST_PORT, LOCALHOST, String.format("/account/%s", fromAccount))
                .rxSend().blockingGet().bodyAsJsonObject();
        Assert.assertNotNull(object);
        Assert.assertEquals(object.getString("balance"), "0.00 USD");
        Assert.assertNotNull(object.getString("accountNumber"));
        Assert.assertEquals(object.getString("accountNumber"), fromAccount);
    }

    @Test
    public void testDeleteAccount() {
        createTestAccounts();
        HttpResponse<Buffer> response= client
                .delete(TEST_PORT, LOCALHOST, String.format("/account/%s", fromAccount))
                .rxSend().blockingGet();
        Assert.assertEquals(response.statusCode(),202);
        String message = response.bodyAsString();
        Assert.assertNotNull(message);
        Assert.assertEquals(message, String.format("account with accountNumber %s deleted", fromAccount));

        HttpResponse<Buffer> responseNew = client
                .delete(TEST_PORT, LOCALHOST, String.format("/account/%s", fromAccount))
                .rxSend().blockingGet();
        Assert.assertEquals(responseNew.statusCode(),404);
        String messageNew = responseNew.bodyAsString();
        Assert.assertNotNull(messageNew);
        Assert.assertEquals(messageNew, String.format("account with accountNumber %s does not exist", fromAccount));

    }

    @Test
    public void testDeposits() {

    }

    @Test
    public void testWithdrawals() {

    }

    @Test
    public void testTransfers() {

    }
    @After
    public void tearDown(){
        tearDownTestServer();
    }

}
