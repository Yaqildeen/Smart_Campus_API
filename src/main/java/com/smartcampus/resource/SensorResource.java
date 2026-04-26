package com.smartcampus.resource;

import com.smartcampus.dao.DataStore;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/sensors")
public class SensorResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> result = new ArrayList<>();
        for (Sensor s : DataStore.sensors.values()) {
            if (type == null || s.getType().equalsIgnoreCase(type)) {
                result.add(s);
            }
        }
        return Response.ok(result).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        if (sensor.getRoomId() == null || !DataStore.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "Cannot register sensor: Room with ID '" + sensor.getRoomId() + "' does not exist."
            );
        }
        if (sensor.getId() == null || sensor.getId().isEmpty()) {
            return Response.status(400)
                    .entity("{\"message\":\"Sensor ID is required\"}")
                    .type(MediaType.APPLICATION_JSON).build();
        }
        DataStore.sensors.put(sensor.getId(), sensor);
        Room room = DataStore.rooms.get(sensor.getRoomId());
        if (!room.getSensorIds().contains(sensor.getId())) {
            room.getSensorIds().add(sensor.getId());
        }
        DataStore.readings.put(sensor.getId(), new ArrayList<>());
        return Response.status(201).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(404)
                    .entity("{\"message\":\"Sensor not found: " + sensorId + "\"}")
                    .type(MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}