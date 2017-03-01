FORMAT: 1A

# API Documentation

## Table of Contents

+ [Service Overview](home.html)
+ [Route Configuration](routes.html)

# API Overview


### <a name="ping"></a> `/ping` - aliveness check - this will respond `pong` if the service is available

### <a name="/"></a> `/` - Documentation home page

### <a name="home"></a> `/home.html` - Documentation home page

### <a name="rest"></a> `/rest.html` - This page

### <a name="routes"></a> `/routes.html` - Route Configuration

### <a name="everything"></a> `/:*` - HEAD/GET/DELETE/PUT/POST redirect based on the route

### <a name="reload"></a> `/reloadConfig` - Reload the configuration file, optionally expunging the cache.

 There are two parameters available:

 1 - `configPath` = a full path to a configuration file to load
 (`http://agent/reloadConfig?configPath=/path/to/new/config.conf`)

 2 - `invalidateCache` = a boolean option to expunge the cache or not. Default is false.
(`http://agent/reloadConfig?invalidateCache=true`)

## See Also

+ [Service Overview](home.html)
+ [Route Configuration](routes.html)
