package integration;

import com.nerdylegend.App;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.*;

public class IntegrationTestHelper {

    protected static final String LOCALHOST = "localhost";
    protected final static int TEST_PORT = 8091;
    protected static final Vertx vertx = Vertx.vertx();

    static final String ACCOUNT_NUMBER = "accountNumber";
    static final String BALANCE = "balance";

    static String fromAccount;
    static String toAccount;
    static String deleteAccount;

    private static HttpServer httpServer;
    private static WebClient client;
    static MultiMap form;

    @BeforeClass
    public static void startTestServer() {
        httpServer = vertx.createHttpServer();
        App.startServer(vertx, httpServer, TEST_PORT);
        client = WebClient.create(vertx);
        form = MultiMap.caseInsensitiveMultiMap();
    }

    @Before
    public void setUp()  {
        createTestAccounts();
        form.clear();
    }

    private static void createTestAccounts() {
        form.clear();
        form.set("name", "Mike");
        form.set("currencyCode", "EUR");
        deleteAccount  = createUser();

        form.set("name", "John");
        form.set("currencyCode", "USD");
        fromAccount = createUser();

        form.set("name", "Paul");
        form.set("currencyCode", "USD");
        toAccount = createUser();
    }

    private static String createUser() {
        JsonObject jsonObject = client
                .post(TEST_PORT, LOCALHOST, "/account")
                .rxSendForm(form).blockingGet().bodyAsJsonObject();
        form.clear();
        return jsonObject.getString("accountNumber");
    }

    HttpResponse<Buffer> createAccount() {
        return client
                .post(TEST_PORT, LOCALHOST, "/account")
                .rxSendForm(form).blockingGet();
    }

    JsonArray getAllAccounts() {
        return client
                .get(TEST_PORT, LOCALHOST, "/account")
                .rxSend().blockingGet().bodyAsJsonArray();
    }

    HttpResponse<Buffer> getAccount(String accountNumber) {
        return client
                .get(TEST_PORT, LOCALHOST, String.format("/account/%s", accountNumber))
                .rxSend().blockingGet();
    }

    HttpResponse<Buffer> deleteAccount(String accountNumber) {
        return client
                .delete(TEST_PORT, LOCALHOST, String.format("/account/%s", accountNumber))
                .rxSend().blockingGet();
    }

    HttpResponse<Buffer> depositAmount(String accountNumber) {
        return client
                .post(TEST_PORT, LOCALHOST, String.format("/account/%s/deposit", accountNumber))
                .rxSendForm(form).blockingGet();
    }

    HttpResponse<Buffer> withdrawAmount(String accountNumber) {
        return client
                .post(TEST_PORT, LOCALHOST, String.format("/account/%s/withdraw", accountNumber))
                .rxSendForm(form).blockingGet();
    }

    HttpResponse<Buffer> transfer(String accountNumber) {
        return client
                .post(TEST_PORT, LOCALHOST, String.format("/account/%s/transfer", accountNumber))
                .rxSendForm(form).blockingGet();
    }

    void assertResponseSuccess(HttpResponse<Buffer> object, String balance, int statusCode) {
        Assert.assertNotNull(object);
        Assert.assertEquals(statusCode, object.statusCode());
        JsonObject jsonObject = object.bodyAsJsonObject();
        Assert.assertNotNull(jsonObject.getString(ACCOUNT_NUMBER));
        Assert.assertEquals(balance, jsonObject.getString(BALANCE));
    }

    void assertTextMessage(HttpResponse<Buffer> response, String message, int statusCode) {
        Assert.assertEquals(statusCode, response.statusCode());
        String bodyMessage = response.bodyAsString();
        Assert.assertNotNull(bodyMessage);
        Assert.assertEquals(message, bodyMessage);
    }

    @After
    public void clear() {
        client
                .delete(TEST_PORT, LOCALHOST, "/account")
                .rxSend().blockingGet().bodyAsString();
    }

    @AfterClass
    public static void tearDownTestServer() {
        httpServer.rxClose();
    }


}
