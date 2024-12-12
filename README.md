 # Brightwheel Coding Example
Katherine Klesch - December 5, 2024
 
## How To Build
You will need a JDK installed -- see [Oracle's site](https://www.oracle.com/java/technologies/downloads/).  This project uses Java 23.0.1. 

To automatically download the necessary dependencies and build the code, navigate to the folder where you have checked out the code and run:
```
gradlew clean build
```

After downloading dependencies the first time, for subsequent builds you can just run:
```
gradlew build
``` 

The build will automatically run unit tests.  To skip running the tests you can run:
```
gradlew build -x test
```


## How To Run The Server
Navigate to the folder where you have checked out the code and run:
```
gradlew bootRun
```

If you wish to run a remote debugger, you can set up a remote debugger in IntelliJ (follow directions here https://www.jetbrains.com/help/idea/tutorial-remote-debug.html#debugger_rc ) and then run:
```
gradlew bootRun --debug-jvm
```

This will partially start the server and then wait for the debugger to connect.  Run the remote debug configuration you created in IntelliJ.  The server will start up the rest of the way and then you can hit breakpoints.

## How to call the APIs

I used curl for testing.  You could also use Postman or another tool for calling REST APIs. I have provided sample curl commands to call the various APIs.  **Please note that I was testing on Windows and Windows command line isn't great at handling escaped quotes.  You may need to tweak the quotes/escape characters in these commands to run them in another environment.** 

## Add Readings API

Add Readings (`/addReadings`) expects an HTTP POST command. 
### Expected Input

```
{
	"id": "36d5658a-6908-479e-887e-a949ec199272",
	"readings": [
		{
			"timestamp": "2021-09-29T16:08:15+01:00",
			"count": 2
		},
		{
			"timestamp": "2021-09-29T16:09:15+01:00",
			"count": 15
		}
	]
}
```
#### Fields
- `id` - a string representing the device's ID
- `readings` - an array of readings for the device
	- `timestamp` - ISO-8601 timestamp of when the reading was taken
	- `count` - integer

### Return Data	
This API does not return any data.

### Sample Commands
```
curl -d "{\"id\": \"36d5658a-6908-479e-887e-a949ec199272\",\"readings\": [{\"timestamp\": \"2021-09-29T16:08:15+01:00\",\"count\": 2},{\"timestamp\": \"2021-09-29T16:09:15+01:00\",\"count\": 15}]}" -H "Content-Type:application/json" -X POST http://localhost:8080/addReadings

curl -d "{\"id\": \"36d5658a-6908-479e-887e-a949ec199272\",\"readings\": [{\"timestamp\": \"2021-09-30T16:08:15+01:00\",\"count\": 6},{\"timestamp\": \"2021-09-31T16:09:15+01:00\",\"count\": 10}]}" -H "Content-Type:application/json" -X POST http://localhost:8080/addReadings

curl -d "{\"id\": \"36d5658a-6908-479e-887e-a949ec199272\",\"readings\": [{\"timestamp\": \"2022-09-30T16:08:15+01:00\",\"count\": 3},{\"timestamp\": \"2022-09-31T16:09:15+01:00\",\"count\": 8}]}" -H "Content-Type:application/json" -X POST http://localhost:8080/addReadings
``` 


## Latest Timestamp API
Latest Timestamp (`/latestTimestamp`) expects an HTTP GET call. 

### Input
The API expects a single parameter, `deviceId`, which is a string of the device's ID.

### Return Data
The API returns:
```
{"latest_timestamp":"2022-10-01T11:09:15.000-04:00"}
```
#### Fields:
- `latest_timestamp` - ISO-8601 of the latest saved timestamp for this device

### Sample Command
```
curl http://localhost:8080/getLatestTimestamp?deviceId=36d5658a-6908-479e-887e-a949ec199272
```

## Get Cumulative Count API
Get Cumulative Count (`/cumulativeCount`) expects an HTTP GET call. 
### Input
The API expects a single parameter, `deviceId`, which is a string of the device's ID.

### Return Data
The API returns
```
{"cumulativeCount":44}
```
#### Fields:
- `cumulativeCount` - the sum of all counts in all readings for the given device

### Sample Command
```
http://localhost:8080/cumulativeCount?deviceId=36d5658a-6908-479e-887e-a949ec199272
```

## Design Considerations and Tradeoffs

- I am using a ConcurrentHashMap to store the mapping between DeviceID and the Readings for that device.  The readings are stored in a ConcurrentSkipListSet.  This provides some thread safety at the cost of some speed during read/write.  I have not used these much in the past so if I was building this for a real system and had more time, and if the real life use case suggested that collisions and race conditions were likely, I would spend a bit more time investigating the characteristics of these and making sure they are the best choice.

- I am choosing to calculate the cumulative count during the addReadings call and storing it.  Doing this on the "write" call moves the time to iterate through the set of readings and add them up to an operation when the the caller is not waiting for a response.  It means that the "read" call to get the cumulative count can be very fast.  However, there's a risk that it may be stale if an addReadings call comes in at the same time as a getCumulativeCount call.  I would need to know more about the expected use case and usage patterns to determine if this is really worth it.  Will we see a lot of readings for a given device such that iterating through the list may take significant time?  Will we often see addReadings calls and getCumulativeCount calls for the same device in a short span of time?  If necessary, it would be trivial to change the code to make this calculation on the "read" call instead.

- As it is currently implemented, everything is stored in memory on a single node and if that node goes down, all the readings are lost.  If I had more time, I would have liked to implement some redundancy -- more than one node, keeping their data redundant between them, but this was outside the scope of what could be done in 2 hours.  

- I've used the older Java Dates because that's what I'm more familiar with and I was in a hurry.  If I had more time I would like to refactor and use the newer date/calendar/timestamp objects.  

- I've played a little fast and loose with MVC layout here as I was trying to get things working quickly. Given more time I would have  liked to refactor and move things around a little.  Some of the annotations weren't working either and I didn't have time to debug that so I worked around; given time I would want the dependencies to be autowired.  

- I did almost no error handling; this would also be something I would add given time.

- I also wrote a couple of unit tests, but if this was a real application I would want to make my tests more atomic (test only one thing per test case) and cover more cases, error handling, etc.  
