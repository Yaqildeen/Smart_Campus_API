package com.smartcampus.exception;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER)
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {
    @Override
    public Response toResponse(RoomNotEmptyException e) {
        ErrorResponse err = new ErrorResponse(409, "Conflict", e.getMessage());
        return Response.status(409).entity(err).type(MediaType.APPLICATION_JSON).build();
    }
}