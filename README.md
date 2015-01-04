minig [![Build Status](https://api.travis-ci.org/ksokol/minig.png?branch=master)](https://travis-ci.org/ksokol/minig/) [![Coverage Status](https://coveralls.io/repos/ksokol/minig/badge.png?branch=master)](https://coveralls.io/r/ksokol/minig?branch=master)
=====

MiniG is a webmailer written in Java. This webmailer is a fork of the original at http://code.google.com/p/minig/

Installation
------------

**Prerequisite**

- OpenJDK 7
- Apache Maven 3.x or newer
- IMAP Server

**Build and package**

- run `mvn package`
- You will find a fat jar (Spring Boot application) under `target` and a rpm under `target/rpm/minig/RPMS/noarch/`
- run `java -jar minig.jar` or install rpm
