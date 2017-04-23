minig [![Build Status](https://api.travis-ci.org/ksokol/minig.png?branch=master)](https://travis-ci.org/ksokol/minig/) [![Quality Gate](https://sonarqube.com/api/badges/gate?key=miniG:miniG)](https://sonarqube.com/dashboard/index/miniG:miniG) [![Technical debt ratio](https://sonarqube.com/api/badges/measure?key=miniG:miniG&metric=sqale_debt_ratio)](https://sonarqube.com/dashboard/index/miniG:miniG) 
=====

MiniG is a webmailer written in Java.

Installation
------------

**Prerequisite**

- Oracle Java 8
- Apache Maven 3.1.x or newer
- IMAP Server

**Build and package**

- run `mvn package`
- You will find a fat jar (Spring Boot application) under `target`
- run `java -jar minig.jar`
