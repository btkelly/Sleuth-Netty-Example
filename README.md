# Sleuth / Logbook Netty Example

This repo is an example showing issues using [Logbook](https://github.com/zalando/logbook) in combination with Sleuth when running on Netty.

Sleuth log tracing ids do not show up on all Logbook log messages due to the order of when logging is invoked. The table below outlines when Sleuth ids are printed and not.

| **Logbook Event** | **Sleuth Id's Printed** | **Description**                                   |
|-------------------|-------------------------|---------------------------------------------------|
| Incoming Request  | No                      | Request made from a client to the Spring Boot app |
| Outgoing Response | Yes                     | Spring Boot app response to client request        |
| Outgoing Request  | Yes                     | Request made from Spring Boot app using WebClient |
| Incoming Response | No                      | Response from another server using WebClient      |

This example app can be run using `./gradlew clean bootRun`which exposes two endpoints:

- POST `/local` which is a simple request response endpoint showing only the Logbook events `Incoming Request` and `Outgoing Response`.
- POST `/fetch` which will make a request to [httpbin](https://httpbin.org/) leveraging the `WebClient` and will show all of the Logbook events.