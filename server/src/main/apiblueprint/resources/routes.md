FORMAT: 1A

# Route Configuration

A route describes a redirected request destination and configuration.
Route `A` redirects to `B` with a request configured as either plain text, one or two way ssl.

Example configuration:

```
routes = [
  {
    twoWaySSL {
      routeId: apple,
      addresses: "localhost:9000",
      sslConfig: ${keystore}
    }
  },
  {
    plainText {
      routeId: banana,
      addresses: "localhost:10000"
    }
  }
],

keystore = {
  keyStore: "/certificates/keystore.jks",
  keyStorePass: password,
  trustStore: "/certificates/truststore.jks",
  trustStorePass: password
}
```

## Table of Contents

+ [Types of Routes](#routetypes)

+ [Configuration Properties](#configprops)

+ [Default Routes](#defaultroutes)

+ [Modifying the Configuration](#reloading)

+ [Additional Configuration Details](#more)


## <a name="routetypes"></a> Types of Routes

Routes can be configured to make requests three ways: `plaintext`, `oneWaySSL`, and `twoWaySSL`;
these are the _route types_ available in the configuration.

#### plainText

A plaintext route makes an unencrypted request and performs no key verification.

#### oneWaySSL

A oneWaySSL route means only the server key is verified.

#### twoWaySSL

a twoWaySSL route means both client and server keys are verified.

## <a name="configprops"></a> Configuration Properties

There are two kinds of configuration properties- route configuration and ssl configuration.

### Route configuration properties

Example:
```
    twoWaySSL {
      routeId: apple,
      addresses: "localhost:9000",
      path: "/apple/banana",
      sslConfig: ${keystore}
    }
```

#### routeId [required]

A unique id of the route. This will be referenced in the request.
A route id must be unique in the configuration.
The route id in an incoming request is the first uri segment:

+ `http://localhost/apple/banana/123`

`apple` is the route id

+ `https://10.10.10:8080/1/vanilla/banana?topping=caramel`

`1` is the route id

The route id is removed from the uri when creating the outbound (proxied) request.

#### addresses [required]

A list of addresses (ips or domains) with ports, comma-delimited.
When more than one address is defined, requests will be redirected to 
each of the them in a round-robin fashion.

Example:

+ `addresses` = `10.10.10.10:8080,20.20.20.20:7777,google.com:443`

The first request will be sent to `10.10.10.10:8080`,
the second request will be sent to `20.20.20.20:7777`,
the third request will be sent to `google.com:443`,
the fourth request will be sent to `10.10.10.10:8080`,
the fifth request will be sent to `20.20.20.20:7777`,
etc.

#### path [optional]

Uri segment to add to outgoing requests.
This can be a useful configuration option for restricting access to an endpoint.

Example route configuration:

```
    plainText {
      routeId: fruits,
      addresses: "localhost:10000",
      path:"/apple/banana/grape/"
    }
```

Assuming an incoming request to the agent is: `http://10.10.10.10/fruits/orange/1.0?key=value`

The agent url will be removed.
The route id `fruits` will be removed from the uri.
The path will be added to the beginning of the uri.
The remainder of the uri will be appended.
The request uri sent to the endpoint will look like this:

```
/apple/banana/grape/orange/1.0?key=value
```

*Note* paths beginning and ending with `/` will not be modified. If either `/` are missing they will be automatically added.

### TwoWaySSL configuration properties

`twoWaySSL` configuration can be configured a couple of different ways: embedded within a route or on it's own with a placeholder.

##### <a name="independent"></a> Independently Configured SSL

In this example, `keystore` is configured independently and referenced with a placeholder in the routes. 
This is particularly useful if more than one twoWaySSL routes are defined and need to use the same certificates.

```
routes = [
  {
    twoWaySSL {
      routeId: apple,
      addresses: "localhost:7000",
      sslConfig: ${keystore}
    }
  },
  {
    twoWaySSL {
      routeId: banana,
      addresses: "localhost:8000",
      sslConfig: ${keystore}
    }
  },
  {
    twoWaySSL {
      routeId: grape,
      addresses: "localhost:9000",
      sslConfig: ${keystore}
    }
  },
  {
    twoWaySSL {
      routeId: strawberry,
      addresses: "localhost:10000",
      sslConfig: ${keystore}
    }
  }
],

keystore = {
  keyStore: "/certificates/keystore.jks",
  keyStorePass: password,
  trustStore: "/certificates/truststore.jks",
  trustStorePass: password
}
```

##### Embedded SSL Configuration

```
routes = [
  {
    twoWaySSL {
      routeId: apple,
      addresses: "localhost:9000",
      sslConfig: {
        keyStore: "/certificates/keystore.jks",
        keyStorePass: password,
        trustStore: "/certificates/truststore.jks",
        trustStorePass: password
      }
    }
  },
  {
    plainText {
      routeId: banana,
      addresses: "localhost:10000"
    }
  }
],

```

#### keyStore [required]
Full path to the key store file.

#### keyStorePass [required]
Password for the key store.

#### trustStore [required]
Full path to the trust store file.

#### trustStorePass [required]
Password for the trust store.


## <a name="defaultroutes"></a> Default Routes

The route configuration reserves the id `default` for special behavior.
If a `default` route is configured, then requests that do not match other defined routes 
will be redirected to the `default` route.

### Default example

```
routes = [
  {
    plainText {
      routeId: apple,
      addresses: "localhost:10000",
    }
  },
  {
    plainText {
      routeId: banana,
      addresses: "localhost:9000",
    }
  },
  {
    plainText {
      routeId: default,
      addresses: "google.com:443"
    }
  }
],
```

In the above example,

- a request to `http://agent/apple` will be redirected to `localhost:10000`

- a request to `http://agent/banana` will be redirected to `localhost:9000`

- an undefined route id (eg `http://agent/grape`) will be redirected to `google.com:443`


## <a name="reloading"></a> Modifying the Configuration

Route configuration can be modified at runtime. There are a few ways to do this:

1 - via environment properties

Environment properties with the same configuration path are over-ridden.

### Example:

Given an [Independent SSL Configuration]("/routes.html#independent"):

```
keystore = {
  keyStore: "/certificates/keystore.jks",
  keyStorePass: password,
  trustStore: "/certificates/truststore.jks",
  trustStorePass: password
}
```

Setting an environment property,

```
$> export keystore.keyStorePassword=newPassword
```

will override the configuration password. Activating the change requires reloading the configuration
via the API (`http://agent/reloadRoute`)

*Note* While environment overrides may seem extremely useful, in practice it's complicated by the the fact that 
routes are defined in an array.

It _may_ be possible to reference a specific array position like this: `-Dfoo.0=a` where 0 indicates position in the array.
However this is not published, supported behavior.

2 - modifying the existing configuration file

Making a change to the existing configuration file and then calling `/reloadRoute` will trigger
the agent server to reload the existing configuration, picking up any changes.

3 - providing a new configuration file

The [routeConfig](/rest.html#reload) request path has an optional parameter `configPath` that takes a fully-qualified path
to a file. It will read this file and replace the existing configuration

## <a name="more"></a> Additional Configuration Details

The route configuration is parsed using the [Lightbend Config library](https://typesafehub.github.io/config/),
Some additional details about config behavior can be found there.

*Note*

The agent parses route configuration in the following manner:

1st - `providedConfig = ConfigFactory.parseFileAnySyntax(provided file path)`

2nd - `merged = ConfigFactory.load(providedConfig)`

Where merged is the final product. This behavior ensures that the provided configuration is parsed
as an independent configuration object first, and then merged with any environment properties or other 
configurations in the classpath.
[Per the Config docs](http://typesafehub.github.io/config/v1.2.0/com/typesafe/config/ConfigFactory.html#load()),
`The Config object will be sandwiched between the default reference config and default overrides and then resolved.`

*Note 2* - Invalidating cache

The configuration library has some internal caches and may not see new values for system properties. 
Use `http://agent/reloadRoute?invalidateCache=true` to reload the configuration and reevaluate the cache.

## See Also

+ [Service Overview](home.html)
+ [RESTful API documention](rest.html)
