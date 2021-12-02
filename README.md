# Coherence Spring Sock Shop

This project is an implementation of a stateful, microservices based application that
uses [Oracle Coherence CE](https://coherence.community/) as a scalable embedded data
store, and [Spring Boot](https://spring.io/projects/spring-boot) as application
framework.

The application is an online store that sells socks, and is based
on the [SockShop Microservices Demo](https://microservices-demo.github.io)
originally written and published under Apache 2.0 license by [Weaveworks](https://go.weave.works/socks).

You can see a working demo of the original application [here](http://socks.weave.works/).

This demo still uses the original front end implementation provided by Weaveworks,
but all back end services have been re-implemented from scratch using Spring Boot
and Oracle Coherence, in order to showcase some features of the [Coherence Spring](https://github.com/coherence-community/coherence-spring)
integration.

We also provide the implementations of the same application that use Micronaut or Helidon
as the application framework, in case one of those is your framework of choice.

* [Coherence Micronaut Sock Shop](https://github.com/oracle/coherence-micronaut-sockshop-sample)
* [Coherence Helidon Sock Shop](https://github.com/oracle/coherence-helidon-sockshop-sample)

# Table of Contents

* [Architecture](#architecture)
* [Project Structure](#project-structure)
* [Pre-Requisites](#pre-requisites)
* [Quick Start](#quick-start)
* [Complete Application Deployment](./doc/complete-application-deployment.md)
* [Development](./doc/development.md)
* [License](#license)


## Architecture

The application consists of 6 back end services (rewritten from the ground up on top of
Spring Boot, implementing the API that the legacy `front-end` service expects).

![Architecture Diagram](./doc/images/architecture.png)

- **[Product Catalog](./catalog)**, which provides 
REST API that allows you to search product catalog and retrieve individual product details;

- **[Shopping Cart](./carts)**, which provides 
REST API that allows you to manage customers' shopping carts;

- **[Orders](./orders)**, which provides REST API 
that allows customers to place orders;

- **[Payment](./payment)**, which provides REST API 
that allows you to process payments;

- **[Shipping](./shipping)**, which provides REST API 
that allows you to ship orders and track shipments;

- **[Users](./users)**, which provides REST API 
that allows you to manage customer information and provides registration and 
authentication functionality for the customers.

You can find more details for each service within documentation pages for individual
services, which can be accessed using the links above.

## Project Structure

The main [Sock Shop](.) repository also contains Kubernetes deployment files for the whole application,
top-level POM file which allows you to easily build the whole project and import it
into your favorite IDE.

## Quick Start

Kubernetes scripts depend on [Kustomize](https://kustomize.io/), so make sure that you have a newer
version of `kubectl` that supports it (at least `1.16` or above).
   
The easiest way to try the demo is to use Kubernetes deployment scripts from this repo. If you do, you can simply run
the following commands from the `coherence-spring-sockshop` directory:

* Install the Coherence Operator

  Install the Coherence Operator using the instructions in the
  [Coherence Operator Quick Start](https://oracle.github.io/coherence-operator/docs/latest/#/docs/about/03_quickstart)
  documentation.

* **Installing a Back-end**

  We create a namespace called `sockshop`.

    ```bash
    $ kubectl create namespace sockshop
    namespace/sockshop created
    ```

  Install the back-end into the `sockshop` namespace.

    ```bash
    $ kubectl --namespace sockshop apply -k k8s/coherence 
    ```

  The `-k` parameter above will use `kubectl` with `kustomize` to merge all the files under the specified directory and
  create all Kubernetes resources defined by them, such as deployments and services for each microservice.

TIP: You can see the state of the pods using:

```bash
kubectl --namespace sockshop get pods
```

### (Optional) Install the Original WeaveSocks Front End

> Warning: The original WeaveSocks Front End has a few bugs, as well as some security issues, 
> and it hasn't been actively maintained for a few years. However, if you want to deploy
> it nevertheless to see how it interacts with our back-end services, please follow
> the steps below.

Install the `front-end` service by running the following command:

```bash
$ kubectl apply -f k8s/optional/original-front-end.yaml --namespace sockshop
```

Port-forward to the `front-end` UI using the following

**Mac/Linux**

```bash
$ export FRONT_END_POD=$(kubectl get pods --namespace sockshop -o jsonpath='{.items[?(@.metadata.labels.app == "front-end")].metadata.name}')
$ kubectl port-forward --namespace sockshop $FRONT_END_POD 8079:8079
```

**Windows**

```bash
kubectl get pods --namespace sockshop -o jsonpath='{.items[?(@.metadata.labels.app == "front-end")].metadata.name}' > pod.txt
SET /P FRONT_END_POD=<pod.txt
kubectl port-forward --namespace sockshop %FRONT_END_POD% 8079:8079
```

> Note: If you have installed into a namespace then add the `--namespace` option to all `kubectl` commands in these instructions.

You should be able to access the home page for the application by pointing your browser to http://localhost:8079/.

You should then be able to browse product catalog, add products to shopping cart, register as a new user, place an order,
browse order history, etc.

By default, 3 users are already available:

- username `Eve_Berger` with password `eve`
- username `user` with password `password`
- username `user1` with password `password`

Once you are finished, you can clean up the environment by executing the following:

```bash
$ kubectl delete -f k8s/optional/original-front-end.yaml --namespace sockshop
$ kubectl delete -k k8s/coherence --namespace sockshop
```

### Scale Back-End

If you wish to scale the back-end you can issue the following command

Scale only the orders microservice:

```bash
$ kubectl --namespace sockshop scale coherence orders --replicas=3
```

Or alternatively scale all the microservices:

```bash
$ for name in carts catalog orders payment shipping users
    do kubectl --namespace sockshop scale coherence $name --replicas=3
done
```

## Clean Local Docker/Kubernetes

```bash
kubectl delete --all pods --namespace=sockshop
docker system prune -a
```

## (Optional) Kubernetes Dashboard

It might be useful to get more visual insight into the Kubernetes deployment by using the
[Kubernetes Dashboard](https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/)

For logging in you will need a token which you can generate with:

```bash
kubectl -n kube-system describe secret $(kubectl -n kube-system get secret | awk '/^deployment-controller-token-/{print $1}') | awk '$1=="token:"{print $2}'
```

## Complete Application Deployment

The Quick Start shows how you can run the application locally, but that may not
be enough if you want to experiment by scaling individual services, look at tracing data in Jaeger,
monitor services via Prometheus and Grafana, or make API calls directly via Swagger UI.

To do all the above, you need to deploy the services into a managed Kubernetes cluster
in the cloud, by following the same set of steps described above (except for port forwarding,
which is not necessary), and performing a few additional steps.

 [Go to Complete Application Deployment section](./doc/complete-application-deployment.md)

## Development

If you want to modify the demo, you will need to check out the code for the project, build it
locally, and (optionally) push new container images to the repository of your choice.

 [Go to Development section](./doc/development.md)

## License

The Universal Permissive License (UPL), Version 1.0
