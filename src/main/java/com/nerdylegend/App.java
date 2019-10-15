package com.nerdylegend;

import com.nerdylegend.controller.AccountController;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class App {
    private static final Integer SERVER_PORT = 8092;
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    private static final AccountController accountController = new AccountController();

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        startServer(vertx, httpServer, SERVER_PORT);
    }

    public static synchronized void startServer(Vertx vertx, HttpServer httpServer, Integer serverPort) {
        Router router = Router.router(vertx);
        router
                .route("/account*")
                .handler(BodyHandler.create());
        router
                .get("/account")
                .handler(accountController::getAll);
        router
                .delete("/account")
                .handler(accountController::deleteAll);
        router
                .post("/account")
                .handler(accountController::createAccount);
        router
                .get("/account/:id")
                .handler(accountController::getAccount);
        router
                .delete("/account/:id")
                .handler(accountController::deleteAccount);

        router
                .post("/account/:id/withdraw")
                .handler(accountController::withdraw);

        router
                .post("/account/:id/deposit")
                .handler(accountController::deposit);

        router
                .post("/account/:id/transfer")
                .handler(accountController::transfer);
        httpServer
                .exceptionHandler(App::logExceptions);
        httpServer.requestHandler(router).listen(serverPort);
        LOG.info("Server started successfully at port: {0} ", serverPort);
    }

    private static void logExceptions(Throwable throwable) {
        LOG.error("Exception Occurred: ", throwable);
    }
}

