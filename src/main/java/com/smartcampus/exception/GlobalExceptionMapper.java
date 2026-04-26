package com.smartcampus.exception;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER + 1000)
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable e) {
        System.out.println("=== GLOBAL MAPPER CAUGHT: " + e.getClass().getName());
        System.out.println("=== MESSAGE: " + e.getMessage());
        e.printStackTrace();
        ErrorResponse err = new ErrorResponse(
                500,
                "Internal Server Error",
                "Caught: " + e.getClass().getName() + " - " + e.getMessage()
        );
        return Response.status(500)
                .entity(err)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}