package com.nerdylegend;

import io.vertx.core.Vertx;

public class App {
    public static void main(String[] args) {
       var vertx = Vertx.vertx();
       var httpServer = vertx.createHttpServer().listen(8080);
       httpServer.requestHandler();

    }
}

