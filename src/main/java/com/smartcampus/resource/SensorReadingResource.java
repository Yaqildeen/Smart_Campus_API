package com.smartcampus.resource;

import com.smartcampus.dao.DataStore;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(404)
                    .entity("{\"message\":\"Sensor not found: " + sensorId + "\"}")
                    .type(MediaType.APPLICATION_JSON).build();
        }
        List<SensorReading> history = DataStore.readings.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(history).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(404)
                    .entity("{\"message\":\"Sensor not found: " + sensorId + "\"}")
                    .type(MediaType.APPLICATION_JSON).build();
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is under MAINTENANCE and cannot accept readings."
            );
        }
        SensorReading newReading = new SensorReading(reading.getValue());
        DataStore.readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(newReading);
        sensor.setCurrentValue(reading.getValue());
        return Response.status(201).entity(newReading).build();
    }
}