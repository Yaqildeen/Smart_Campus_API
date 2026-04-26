**Student:** Yaqil Badurdeen  
**Module:** 5COSC022W Client-Server Architectures  

# Smart Campus Sensor & Room Management API  
---

## Overview
The **Smart Campus API** is a RESTful web service built using **JAX-RS (Jersey)**. It is designed to support the university’s Smart Campus initiative by enabling facilities managers and automated systems to manage:

- Rooms  
- Sensors  
- Sensor Readings (historical data)  

## Technology Stack
- Java 8
- JAX-RS (Jersey 2.32)
- Apache Tomcat
- Maven
- In-memory storage (HashMap)
---

##  Key Architectural Features

- **Resource-Oriented Design**  
  Clear REST structure based on real-world entities: Room, Sensor, SensorReading  

- **Single Application Path**  
  All endpoints are available under `/api/v1`  

- **In-Memory Data Store**  
  Uses `HashMap` and `ArrayList` (no database used, as required)  

- **Sub-Resource Locator Pattern**  
  `/sensors/{sensorId}/readings` handled by a dedicated resource class  

- **Advanced Error Handling**  
  Custom exception mappers return clean JSON responses:
  - 409 Conflict  
  - 422 Unprocessable Entity  
  - 403 Forbidden  
  - 500 Internal Server Error  

- **Logging Filter**  
  Logs request method, URI, and response status  

---

## API Endpoints
- GET /api/v1 → Discovery
- GET /api/v1/rooms → Get all rooms
- POST /api/v1/rooms → Create room
- GET /api/v1/rooms/{roomId} → Get room
- DELETE /api/v1/rooms/{roomId} → Delete room
- GET /api/v1/sensors → Get sensors (supports ?type=)
- POST /api/v1/sensors → Create sensor
- GET /api/v1/sensors/{sensorId} → Get sensor
- GET /api/v1/sensors/{id}/readings → Get readings
- POST /api/v1/sensors/{id}/readings → Add reading

## How to Build and Run
 
### Prerequisites
- Java 8 or higher
- Apache Maven
- Apache Tomcat
- NetBeans IDE (recommended)
### Steps
1. Clone the repository
2. Open the project in NetBeans
3. Right-click project → Clean and Build
4. Right-click project → Run
5. Server starts at: `http://localhost:8080/SmartCampusAPI/api/v1/`
---
## Sample curl Commands
 
### 1. Discovery endpoint
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/
```
 
### 2. Get all rooms
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms
```
 
### 3. Create a room
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"HALL-201\",\"name\":\"Main Hall\",\"capacity\":100}"
```
 
### 4. Filter sensors by type
```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2"
```
 
### 5. Add a sensor reading
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":24.5}"
```
