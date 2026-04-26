package com.smartcampus.exception;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER)
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {
    @Override
    public Response toResponse(SensorUnavailableException e) {
        ErrorResponse err = new ErrorResponse(403, "Forbidden", e.getMessage());
        return Response.status(403).entity(err).type(MediaType.APPLICATION_JSON).build();
    }
}