## Development

* [Checking Out the Project](#checking-out-the-project)
* [Building the Code](#building-the-code)
* [Creating Container Images](#creating-container-images)
* [Running Modified Application](#running-modified-application)

If you want to modify the demo, you will need to check out the code for the project, build it
locally, and (optionally) push new container images to the repository of your choice.

### Checking Out the Project


```bash
$ git clone git@github.com:coherence-community/coherence-sockshop-spring.git
```

Once you have the code locally, you can update it to the latest version by running the
following command:

```bash
$ git pull
```

### Building the Code

You can build individual services by running `mvn clean install` within top-level directory
for each service, but if you want to build the whole project at once, there is an easier
way: simply run `mvn clean install` within the `coherence-sockshop-spring` directory.

This will compile the code for each service, run the tests, and install module JARs into
the local Maven repo.

### Creating Container Images

Pre-built container images are already available on [GitHub](https://github.com/orgs/oracle/packages),
so you can simply run `docker pull` to download them and use locally.

However, if you are making changes to various service implementations and need to re-create
container images, you can easily do that in several ways.

If you only want to build container images locally, make sure you have Docker Daemon
running and execute the following:

```bash
$ mvn package -Pcontainer -DskipTests
```

You can then tag images any way you want and push them to a container repo of your choice
using standard Docker commands.

On the other hand, if you cannot or do not want to run Docker locally, you can push images
directly to the remote repository:

```bash
$ mvn package -Pcontainer -DskipTests -Dcontainer.repo=<your_container_repo> -Djib.goal=build
```

> **Note:** You should replace `<your_container_repo>` in the command above with the name of the
> container repository that you can push container images to.

> **Note:** We use [Jib Maven Plugin](https://github.com/GoogleContainerTools/jib) to create and publish
> Docker images, with a default Jib goal set to `dockerBuild`, in order to create local image.
> Changing the goal to `build` via `jib.goal` system property allows you to push images to a
> remote repository directly.

### Running Modified Application

Kubernetes deployment scripts in this repository reference container images from the GitHub
Container Repository `ghcr.io/coherence-sockshop-spring`. You will not be able to push the images to that repo,
which is why you had to specify `-Dcontainer.repo` argument in the command above.

In order to deploy the application that uses your custom container images, you will also have to modify
relevant `deployment.yaml` files within `sockshop` repository to use correct names for your
container images.

