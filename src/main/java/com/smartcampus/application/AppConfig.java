package com.smartcampus.application;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("")
public class AppConfig extends Application {
    // JAX-RS entry point
    // @ApplicationPath("") means our base URL is handled by web.xml's /api/v1/*
    // A new resource instance is created per request by default (request-scoped)
    // This means our DataStore must use static fields to persist data across requests
}