package integration;

import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import org.junit.Assert;
import org.junit.Test;

public class AppIntegrationIntegrationTest extends IntegrationTestHelper {

    @Test
    public void testAccountCreation() {
        //No currency Code
        //Given
        form.set("name", "Daniel");

        //When
        HttpResponse<Buffer> object = createAccount();

        //Then
        assertTextMessage(object, "Invalid currency code", 400);


        //Given
        form.set("name", "Daniel");
        form.set("currencyCode", "GBP");

        //When
        object = createAccount();

        //Then
        assertResponseSuccess(object, "0.00 GBP", 201);
        Assert.assertEquals("Daniel", object.bodyAsJsonObject().getString("name"));
    }

    @Test
    public void testGetAllAccounts() {
        //When
        JsonArray object = getAllAccounts();

        //Then
        Assert.assertNotNull(object);
        Assert.assertEquals(3, object.size());
    }

    @Test
    public void testGetAccount() {
        //When
        HttpResponse<Buffer> object = getAccount(fromAccount);

        //Then
        assertResponseSuccess(object, "0.00 USD", 200);
        Assert.assertEquals(fromAccount, object.bodyAsJsonObject().getString(ACCOUNT_NUMBER));
    }


    @Test
    public void testDeleteAccount() {
        //When
        HttpResponse<Buffer> response = deleteAccount(deleteAccount);

        //Then
        assertTextMessage(response, String.format("account with accountNumber %s deleted", deleteAccount), 202);

        //When
        HttpResponse<Buffer> responseNew = deleteAccount(deleteAccount);

        //Then
        assertTextMessage(responseNew, String.format("account with accountNumber %s does not exist", deleteAccount), 404);
    }

    @Test
    public void testDeposits() {
        //Given
        //Invalid Amount
        form.set("amount", "2.00s");
        form.set("currencyCode", "USD");

        //When
        HttpResponse<Buffer> object = depositAmount(fromAccount);

        //Then
        assertTextMessage(object, "Invalid currency format", 400);

        //Given
        //Invalid Currency Code
        form.set("amount", "2.00s");
        form.set("currencyCode", "USDA");

        //When
        object = depositAmount(fromAccount);

        //Then
        assertTextMessage(object, "Invalid currency format", 400);

        //Given
        form.set("amount", "2.00");
        form.set("currencyCode", "USD");

        //When
        object = depositAmount(fromAccount);

        //Then
        assertResponseSuccess(object, "2.00 USD", 200);

        //When
        object = depositAmount(fromAccount);

        //Then
        assertResponseSuccess(object, "4.00 USD", 200);

        //Given
        form.clear();
        form.set("amount", "2.00");
        form.set("currencyCode", "GBP");

        //When
        object = depositAmount(fromAccount);

        //Then
        assertTextMessage(object, "Invalid currency format", 400);
    }

    @Test
    public void testWithdrawals() {
        //Given
        form.set("amount", "2.00");
        form.set("currencyCode", "USD");

        //When
        HttpResponse<Buffer> object = withdrawAmount(fromAccount);

        //Then
        assertTextMessage(object, "Insufficient funds or invalid currency format", 400);

        //When
        object = depositAmount(fromAccount);

        //Then
        assertResponseSuccess(object, "2.00 USD", 200);


        //When
        object = withdrawAmount(fromAccount);

        //Then
        assertResponseSuccess(object, "0.00 USD", 200);

        //When
        object = depositAmount(fromAccount);

        //Then
        assertResponseSuccess(object, "2.00 USD", 200);

        //Given
        form.set("amount", "1.01");

        //When
        object = withdrawAmount(fromAccount);

        //Then
        assertResponseSuccess(object, "0.99 USD", 200);

    }

    @Test
    public void testTransfers() {
        //Given
        form.set("amount", "2.00");
        form.set("currencyCode", "USD");

        //When
        HttpResponse<Buffer> fromAccountResponse = depositAmount(fromAccount);

        //Then
        assertResponseSuccess(fromAccountResponse, "2.00 USD", 200);

        //When
        HttpResponse<Buffer> toAccountResponse = getAccount(toAccount);

        //Then
        assertResponseSuccess(toAccountResponse, "0.00 USD", 200);

        //Given
        form.set("amount", "1.05");
        form.set("currencyCode", "USD");
        form.set("toAccountNumber", toAccount);

        //When
        fromAccountResponse = transfer(fromAccount);

        //Then
        assertResponseSuccess(fromAccountResponse, "0.95 USD", 200);

        //When
        toAccountResponse = getAccount(toAccount);

        //Then
        assertResponseSuccess(toAccountResponse, "1.05 USD", 200);

        //Given
        //Invalid beneficiary
        form.set("amount", "0.95");
        form.set("currencyCode", "USD");
        form.set("toAccountNumber", "1234");

        //When
        fromAccountResponse = transfer(fromAccount);

        //Then
        assertTextMessage(fromAccountResponse, "Transaction failed", 400);

        //Given
        //Invalid Amount
        form.set("amount", "1.95");

        //When
        fromAccountResponse = transfer(fromAccount);

        //Then
        assertTextMessage(fromAccountResponse, "Insufficient funds or invalid currency format", 400);
    }

}
