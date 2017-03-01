# GM Agent JVM

GM Agent JVM is a generic http passthrough agent service. It's built using the [Grey Matter Fabric JVM project](https://github.com/DecipherNow/gm-fabric-jvm) and provides metrics and authentication in addition to proxying requests to multiple endpoints.

Features include:

- multiple redirected endpoints with unique identifiers
- multiple addresses for a single endpoint
- plaintext, ssl, and two-way ssl configured endpoints
- hot-swappable route definitions
- plain text and two-way ssl agent requests
- real-time request metrics
- administration interface
- encrypted key passwords

Further [Documentation can be found here](https://github.com/DecipherNow/gm-fabric-jvmagent/tree/master/server/src/main/apiblueprint/resources).

## Building

### First build the tagged release of the [gm-fabric-jvm](https://github.com/DecipherNow/gm-fabric-jvm)

`mvn clean package` will compile and package the agent.


## Run Locally

Finagle provides the ability to run locally although it's a bit awkward:

```
./server/target/gm-[...]-app/gm-[...]/bin/gm-agent-server console
```

### Unit tests

All unit tests are written using [scalatest](http://www.scalatest.org/) and are located in the `server` and `business` modules.

```
mvn test
```

will run the unit tests.

### Running the agent

`mvn clean package -Prpm` will create an rpm which can be installed and run.

### Configuration

The agent contains a few important configuration files:

- `whitelist` - a whitelist of accepted PKI certificate DNs needs to be configured. A single dn per line is sufficient.
The whitelist is used for filtering requests via the [AclRestFilter](https://github.com/DecipherNow/gm-fabric-jvm/blob/master/documentation/AclRestFilter.md).
The [AclRestFilter](https://github.com/DecipherNow/gm-fabric-jvm/blob/master/documentation/AclRestFilter.md) provides a description of the process and details the implementation.

- `parameters.config` - this is a gm-fabric-jvm configuration file with three additions. [The complete parameter list is here](https://github.com/DecipherNow/gm-fabric-jvm/blob/master/documentation/Parameters.md).
The additional parameters include:
+ com.deciphernow.gm.agent.requestTimeout - defined in seconds (default is 10)
+ com.deciphernow.gm.agent.maxResponseSize - defined in megabytes (default is 5)
+ com.deciphernow.gm.agent.maxRequestSize - defined in megabytes (default is 5)
+ com.deciphernow.gm.agent.routeConfig - full path to the route configuration file. Defaults to etc/application.conf

The default `parameters.config` provides a [commented example](https://github.com/DecipherNow/gm-fabric-jvmagent-old/blob/master/server/src/main/package/etc/parameters.config).

- `application.conf` - route configuration, [described in detail here](https://github.com/DecipherNow/gm-fabric-jvmagent/blob/master/server/src/main/apiblueprint/resources/routes.md)
This does not exist by default but is required to do anything meaningful. Examples are provided in the [route configuration document](https://github.com/DecipherNow/gm-fabric-jvmagent/blob/master/server/src/main/apiblueprint/resources/routes.md).

#### Two-way SSL certificate passwords

To make a two-way SSL request from the agent to a route endpoint, passwords for the key and trust stores will need to be added to the configuration.
It is prudent to encrypt these passwords, and the agent can decrypt them using a custom. Details for implementing a decryptor can be [found here](https://github.com/DecipherNow/gm-fabric-jvm/blob/master/documentation/ResourceDecrypter.md).
The agent will attempt to decrypt passwords defined in the route configuration using the defined decryptor, if defined.

### Testing a running agent

Out of the box, `http://localhost:8888/ping` is a good test- note that such a request requires a `user_d` header with value conforming to a dn in the whitelist.
