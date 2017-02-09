# Fetcher [![Build Status](https://travis-ci.org/rentworthy/fetcher-framework.svg?branch=master)](https://travis-ci.org/rentworthy/fetcher-framework)

Fetcher is a Java framework for effortlessly writing loosely-coupled concurrent code designed for failure.

## Requirements

* Java 8 or higher, 64-bit
* Maven 3.3.9+ (for building)

## Building Fetcher

Fetcher is a standard Maven project. Simply run the following command from the project root directory:

    mvn clean install

On the first build, Maven will download all the dependencies from the internet and cache them in the local repository (`~/.m2/repository`), which can take a considerable amount of time. Subsequent builds will be faster.

Fetcher has a comprehensive set of unit tests that can take several minutes to run. You can disable the tests when building:

    mvn clean install -DskipTests
