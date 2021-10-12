# Shipping Service

The Shipping Service contains service implementation, including the
[domain model](src/main/java/com/oracle/coherence/examples/sockshop/spring/shipping/Shipment.java),
[REST API](src/main/java/com/oracle/coherence/examples/sockshop/spring/shipping/ShippingResource.java), as well as the
[data repository abstraction](src/main/java/com/oracle/coherence/examples/sockshop/spring/shipping/ShipmentRepository.java)
and its [Coherence](https://coherence.community/) [implementation](src/main/java/com/oracle/coherence/examples/sockshop/spring/shipping/CoherenceShipmentRepository.java).

## API

The service exposes REST API on port 8080. 

TBD (add OpenAPI support/link)

## Building the service

In order to build all the modules and create container images for the service, simply run the 
following commands inside the top-level `shipping` directory:

```bash
$ mvn clean install
$ mvn package -Pcontainer -DskipTests
```

The first command will build all the modules, run unit and integration tests, and install the
artifacts that need to be included into the container images into the local Maven repo.

The second command will then package those artifacts, and all of their dependencies, into
the local container image with the same names as the corresponding module (one image per module).

You can then manually push generated image to a container repository of your choice in order
to make it available to other environments.

Alternatively, you can build and push the image directly to a remote container repository by
running the following command instead:

```bash
$ mvn package -Pcontainer -DskipTests -Dcontainer.repo=<your_container_repo> -Djib.goal=build
```

You should replace `<your_container_repo>` in the command above with the name of the 
container repository that you can push images to.

## Running the service

Coherence is embedded into your application and runs as part
of your application container so it allows you to easily scale your service to hundreds of **stateful**,
and optionally **persistent** nodes.

To run Coherence implementation of the service, simply execute

```bash
$ docker run -p 8080:8080 ghcr.io/coherence-spring-sockshop/shipping
```

Once the container is up and running, you should be able to access [service API](./README.md#api)
by navigating to http://localhost:8080/shipping/.

As a basic test, you should be able to perform an HTTP GET against `/health` endpoint:

```bash
$ curl -i http://localhost:8080/health
```
which should return HTTP status code 200, and a JSON response with health check details.

To learn how to run the service in Kubernetes, as part of a larger Sock Shop application,
please refer to the [main documentation page](../README.md).

## License

The Universal Permissive License (UPL), Version 1.0
