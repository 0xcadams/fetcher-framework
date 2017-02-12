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

## Getting Started

Since this project was created to simplify the (longwinded) process of creating concurrent processes in Java, we'll start with a few simple examples.

```java
private static final Fetcher<String> FETCHER = Fetchers.getBlockingConcurrentFetcher(() -> {    
    try {
        Thread.sleep(1000); // simulated IO
    }
    catch (InterruptedException e) {
        throw new FetcherException(e);
    }    
    return "fetched";    
});
```

In nine lines of code, we've created a concurrent lazy-loading "singleton" which is thread-safe and will block when called and return a String.

```java
try {
    String fetcherValue = FETCHER.fetch();
}
catch (FetcherException e) {
    // TODO Implement error handling
}
```

This will block for one second and return `"fetched"`. Let's now say that we want to attempt to access a resource, such as a web API or local file.

To be resistant to failure, we want to have a backup value in case networking is unavailable or the file system is unable to be accessed. Typically this is a complicated mess of launching Threads and checking their respective Futures, with even more complicated error handling.

With Fetcher, it simplifies that process.

```java
private static final Fetcher<String> FETCHER = Fetchers.getMultiConcurrentFetcher(() -> {
    try {
        Thread.sleep(1000);
        throw new IOException("Networking unavailable.");
    }
    catch (final InterruptedException | IOException e) {
        throw new FetcherException(e);
    }
}, () -> {
    return "backup value";
});
```

