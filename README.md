minig [![Build Status](https://github.com/ksokol/minig/workflows/CI/badge.svg)](https://github.com/ksokol/minig) [![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=miniG%3AminiG&metric=alert_status)](https://sonarcloud.io/dashboard/index/miniG:miniG)
=====

MiniG is a webmailer written in Java.

Installation
------------

**Dependencies**

- Java 11 or higher
- IMAP Server

**Build and package**

- run `./mvnw package` or `mvnw.cmd package` on Windows
- you will find a fat jar (Spring Boot application) in the `target` folder
- run `java -jar target/minig.jar`
