# Truphone Device Manager API

This API will allow to:

- Add
- Modify (Full and Partial)
- Fetch
- List All
- Delete

The devices.

## Assumptions
The following assumptions are made for developing the Device Manager API

- **Database** - Since this is a sample exercise, I have used in memory h2 database, so no data is persisted, and data will be in memory until the Spring Boot API is running.
- **Framework** - Chos Java + Spring Boot 2.5 as it provides many features out of the box.
- **Data** - I assume that the device means mobile phones.
- **Run/Compile scripts** - The application is deployed/run in Linux/Mac environment, additional instructions for running the API standalone in windows environment is given below under Additional Details section.
- **Configuration** - It is best practice to store configuration in a common location like git repository and fetch it during the application start. In case of Spring Boot we could use Spring Cloud for this, as this is a simple demo API I have included the configuration along with the API itself.
- **Device Identifier** - This will be default database identity key as Java Long.

## Software/Framework Versions
I have used the following Software/framework versions:

- JDK - Open JDK version "15.0.2" 2021-01-19 (Java 11)
- Spring Boot 2.5.0
- Spring Boot Starter JPA
- JUnit 4.13
- Lombok 1.18.20

## Pre-Requirement

- **JDK** - Open JDK version 15 or above/Java 11 (Note that Oracle Java can be used for development but not for commericial purpose without license)
- **Docker** (Optional, see Additional Details)

## Build

Use the following script to build the API. Make sure that relevent permissions are granted for running shell script files in your environment.

    ./build.sh

## Run
Use the following script to Run the the API. This will build the API once again and run it as a java commandline.

    ./run.sh

## API Endpoints

Assuming API is run in the localhost the following endpoints are provided


|Requirement| HTTP Request | URL |Success|Error
|--|--|--|--|--|
| Add Device | POST | `http://localhost:8080/api/device`| Return HTTP 201 Created |Return HTTP 500 internal server error for malformed data, HTTP 400 Bad request with error detail in body for data validation error|
| Get Device by Identifier | GET | `http://localhost:8080/api/device/{id}`|Return HTTP 200 OK | HTTP 404 Not Found if no data found for the given id, HTTP 500 Internal Server Error for any other scenarios|
| List All Devices | GET | `http://localhost:8080/api/device/list`| Return HTTP 200 OK with list of devices as array of json if data exists, othwerise returns empty array | HTTP 500 Internal Server Error for any other scenarios|
| Update Devices (Full and Partial) | PATCH | `http://localhost:8080/api/device`| Return HTTP 204 No Content for Successful update| HTTP 400 Bad Request for any data error, HTTP 500 Internal Server Error for any other scenarios|
| Delete Device | DELETE | `http://localhost:8080/api/device/{id}`| HTTP 204 No Content for Successful Deletion | HTTP 400 Bad request if no data found, HTTP 500 Internal Server Error for any other scenarios|
| Search device by Brand | GET | `http://localhost:8080/api/device/search/{searchTerm}`| HTTP 200 OK with Search Result containing array of devices json matching the search criteria, empty array in case no match found |  HTTP 500 Internal Server Error for any other scenarios|

### JSON Data format .
The body must contain below JSON format and must have Content-Type as application/json in the header for Add Device.

    HEADER - Content-Type: application/json
    BODY -
    {
	    "name":"iPhone 12",
	    "brand":"Apple"
	}


The body must have mandator id for updating a device

    HEADER - Content-Type: application/json
    BODY -
    {
    	"id": 1,
        "name":"iPhone 12",
        "brand":"Apple"
    }

# Additional Details
A Dockerfile has been provided to build a docker image.
You can use the following scripts for building docker image

`./build-docker.sh`

The following script can be used for running the API locally using docker (note that the API runs in port 8080 for Java and Docker runs)

`./run-docker.sh`

you can use `mavenw.bat` file to run the same commands as provided in the .sh file for running compiling and running the application
# Conclusion
Feel free to get in touch with me if you require any further information.