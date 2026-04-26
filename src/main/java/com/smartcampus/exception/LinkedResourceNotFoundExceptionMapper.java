package com.smartcampus.exception;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER)
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException e) {
        ErrorResponse err = new ErrorResponse(422, "Unprocessable Entity", e.getMessage());
        return Response.status(422).entity(err).type(MediaType.APPLICATION_JSON).build();
    }
}