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
---
# Conceptual Report Answers
---
# Part 1: Service Architecture & Setup

## Question 1
In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

By default, JAX-RS instantiates a new instance of each resource class for every HTTP request (request-scoping). This makes it impossible to store per-request data in instance fields in a resource class, as these are re-initialized for each request.

This design choice affects how in-memory data is stored. Because a new resource instance is created for every request, this means that data cannot be stored in instance fields in the resource class. Instead, all data must be stored in static fields in another class (the Datastore class in this project stores data in static final HashMap: rooms, sensors, readings. Static fields are owned by the class and not the instance so they remain for the duration of the application's execution.

In a production server that is multi-threaded, Concurrent HashMap would be used rather than HashMap or access to shared data structures would be synchronized to prevent race conditions. For this assignment, basic HashMap are used as the API is only single threaded for testing.

---

## Question 2
Why is the provision of “Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

HATEOAS (Hypermedia as the Engine of Application State) is seen as a key feature of mature REST API designs as it makes them discoverable and interactive. Rather than expect clients to know URLs or read documentation, HATEOAS provides links in API responses that direct clients to other resources and actions they can take.

For instance, when the client makes a request to GET /API/v1/, the response contains links to /API/v1/rooms and /API/v1/sensors. The client can traverse the whole API starting from this URL without knowing any other URLs upfront.This has a number of advantages for client developers. First, it decouples the client from the server, if the URL changes, the server modifies the link and the client will navigate to the new URL. Second, it decreases the need for maintaining static documentation because the API documents itself. Third, it simplifies working with the API, particularly for developers coming on to a new project.

---

# Part 2: Room Management

## Question 3
When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.

Returning IDs makes the response payload smaller, which is quicker to transfer and consumes less bandwidth. But the client has to make a separate request for each ID to get the details, so more HTTP requests are required, which might increase latency and reduce the speed of the user experience.

Returning room objects increases the size of the response payload but provides all the information the client needs in one go, so there are fewer round trips and no need to make additional calls for each ID. This is preferable when clients generally need all the data.

In this API, list responses contain full room objects because the room objects are small and the benefit of reduced round trips makes up for the larger response size.

---

## Question 4
Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple time.

Yes, the DELETE operation is idempotent in this implementation. Idempotency means that making the same request multiple times produces the same server state as making it once.

In this REST API, if a client issues DELETE /api/v1/rooms/{roomID} room exists and is not associated with any sensors, then the room is deleted and the server returns a 200 response. If the client makes the same request again, the room doesn't exist, so the server responds with a 404 Not Found. The server state is the same after both requests. The status code is different (200 vs 404), but the state of the data is identical, so it is idempotent. There can be no duplicated or conflicting deletes in response to multiple DELETE requests.

---

# Part 3: Sensor Operations & Linking

## Question 5
We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

If a client makes a request with a Content-Type header that is not application/json such as text or application, JAX-RS will automatically reject the request, before it reaches the resource method. The server responds with an HTTP 415 Unsupported Media Type error. This is done automatically by the JAX-RS framework, based on the @Consumes annotation and does not require any extra code in the resource class. This ensures the API is not exposed to bad or invalid content.

---

## Question 6
You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?

Using @QueryParam for filtering is superior to path parameters for several reasons.

Since query parameters are optional, the same resource can be accessed with or without filters. Path parameters would require a different route for every combination. Query parameters also better at conveying intentions, they indicate to the client that they are looking at a filtered collection of resources, and not a new resource. Also, query parameters can be easily without changing the URL structure, while multiple filters in the paths become deeply nested and messy. Finally, REST principles also imply that path segments are used to identify resources and query strings are used to modify results, so query parameters are the proper way to search and filter a resource.

---

# Part 4: Deep Nesting with Sub- Resources

## Question 7
Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/id/readings/rid) in one massive controller class

The Sub-Resource Locator pattern enables a resource class to return another class to handle a sub-path. In this API, the SensorResource class delegates handling of /sensorId/readings to the SensorReadingResource class by returning an instance of it.

The pattern has a number of advantages. First, it promotes the single responsibility principle, each class has one job. SensorResource handles sensors and SensorReadingResource handles readings. Second, it makes the API easier to maintain, as the API grows, we can add new sub-resources without changing the resource classes. Third, it's easier to read, large APIs with lots of nested paths are hard to manage in a controller class. Having sub-resource classes helps control a single file's size. And finally, it reflects the physical relationship of the domain, a reading is owned by a sensor which is owned by a room and makes the code more readable.

---

# Part 5 Advanced Error Handling, Exception Mapping & Logging

## Question 8
Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

HTTP 404 Not Found means the resource URL does not exist. But when a client tries to create a new sensor with a non-existent roomId, the URL /api/v1/sensors is valid - the issue is with the request body. HTTP 422 Unprocessable Entity means the server does understand the format of the request and the URL is valid, but the semantic meaning of the request body is not valid. Given that the problem is that the roomId does not exist in the system 422 business logic validation failure is much more appropriate. It confirms exactly that the request is well-formed JSON but contains an invalid reference, guiding the client to quickly resolve the problem.

---

## Question 9
From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

Revelations of raw Java stack traces to external API users pose a number of security threats.

First, stack traces contain internal file paths and package names, enabling an attacker to map out the structure of the application and find vulnerabilities. Second, they reveal library names and versions that attackers could use to search known CVE vulnerability databases for potential vulnerabilities. Third, stack traces expose class names and method names, which can help attackers understand the business logic and develop targeted attacks. Fourth, they can leak data and variable values.

This API's Global ExceptionMapper catches all unhandled Throwables and logs their details on the server side for administrators to view, while only sending a generic, safe error message to the client.

---

## Question 10
Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info () statements inside every single resource method?

Logging with JAX-RS filters is preferred because it provides cross-cutting logging. Logging is done automatically for every request and response by a single LoggingFilter class without any changes to resource methods. Using manual logging in resource methods would be against the DRY (Don't Repeat Yourself) Principle because it would lead to dozens of duplicate log statements that would need to be updated if the logging statement changes. It would be possible to overlook adding logging to new methods. Filters ensure 100% coverage of all requests and responses with no chance of missing any and allows resource classes to focus entirely on business logic without worrying about logging.
