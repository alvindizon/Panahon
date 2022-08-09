# Panahon

## About
* This app is still under construction
* An Android app that fetches weather forecasts via the [OpenWeather API](https://openweathermap.org/api)
* Uses Android Jetpack libraries (Compose, ViewModel, Navigation, Room), Hilt for dependency injection,
  and Coroutines/Flow for async tasks
* Uses JUnit5 and MockK for unit tests
* For CI, Github Actions is used
* Features are separated into their own separate module

## Features
* Fetch the weather forecast in your area
* Search for a location's current forecast
* Save a location

## How to build
1. You need to obtain an OpenWeather API key in order to interact with the OpenWeather API. Once you've done that, in your
   `local.properties` file add the following line:

 ```
 ACCESS_KEY=<your_openweather_api_key>
 ```

