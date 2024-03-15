## Complete Application Deployment

* [Additional Pre-Requisites](#additional-pre-requisites)
* [Install Prometheus and Grafana](#install-prometheus-and-grafana)
* [Expose Application via a Load Balancer](#expose-application-via-a-load-balancer)
* [Install the Jaeger Operator](#install-the-jaeger-operator)
* [Access Swagger](#access-swagger)
* [Cleanup](#cleanup)  

The Quick Start shows how you can run the application locally, but that may not
be enough if you want to experiment by scaling individual services, look at tracing data in Jaeger,
monitor services via Prometheus and Grafana, or make API calls directly via Swagger UI.

To do all of the above, you need to deploy the services into a managed Kubernetes cluster
in the cloud, by following the same set of steps described above (except for port forwarding,
which is not necessary), and performing a few additional steps.

### Additional Pre-Requisites

1. Install `envsubst`

   You must download and install `envsubst` for your platform from
   [https://github.com/a8m/envsubst](https://github.com/a8m/envsubst) and make it
   available in your `PATH`.

### Install Prometheus and Grafana

Install the Prometheus Operator, as documented in the Prometheus Operator [Quick Start](https://prometheus-operator.dev/docs/prologue/quick-start/) page.

Prometheus can then be accessed as documented in the
[Access Prometheus section of the Quick Start](https://prometheus-operator.dev/docs/prologue/quick-start/#access-prometheus) page.

**IMPORTANT**
If installing Prometheus into a RBAC enabled k8s cluster you may need to create the required RBAC resources
as described in the [Prometheus RBAC](https://prometheus-operator.dev/docs/operator/rbac/) documentation.

In some environments, installation can fail with the error message "path /sys is not shared or slave mount".
For this demo, if necessary, it will be sufficient to comment out `mountPropagation: HostToContainer` in the
`manifests/nodeExporter-daemonset.yaml` file.

**IMPORTANT**
If you installed the Sock Shop back-end into K8s before you installed Prometheus Operator, you must
run the following to delete and re-add the deployments for Prometheus to pick up the Pods. This is because the Coherence Operator will not have been able to create Prometheus `ServiceMonitor` resources before Prometheus was installed.

   ```bash
   kubectl delete -k k8s/coherence --namespace sockshop

   kubectl apply -k k8s/coherence --namespace sockshop
   ```

#### Import the Grafana Dashboards

A set of Grafana dashboards can be downloaded from the Coherence Operator GitHub repository and imported into Grafana.
Full instructions for importing dashboards into Grafana can be found in the
[Coherence Operator documentation](https://oracle.github.io/coherence-operator/docs/latest/#/metrics/030_importing).

There is an additional Sock Shop specific Grafana dashboard located in the project source 
`k8s/optional/grafana-dashboards/sockshop-dashboard.json` This file can be manually imported into Grafana using the
instructions in the [Add a Datasource](https://grafana.com/docs/grafana/latest/datasources/add-a-data-source/) section
of the Grafana documentation.

### Expose Application via a Load Balancer

> Note: This is assuming you have deployed the back-ends via the instructions in
> the previous section.

> Note: This step can be skipped if one wants to access application and Grafana/Prometheus/Jaeger services
> just by using `localhost`.
> In that case add port forwarding setup for each service:
>```bash
> $ kubectl --namespace sockshop port-forward svc/front-end 8079:80
> $ kubectl --namespace monitoring port-forward svc/prometheus-k8s 9090
> $ kubectl --namespace monitoring port-forward svc/alertmanager-main 9093
> $ kubectl --namespace monitoring port-forward svc/grafana 3000
>```

1. Create the Load Balancer

    ```bash
    kubectl apply -f k8s/optional/ingress-controller.yaml
    ```

    You can get the status of the Load Balancer using:

    ```bash
    kubectl get services --namespace ingress-nginx
    ```

    The command should return a result similar to the following:

    ```bash
    NAME            TYPE           CLUSTER-IP       EXTERNAL-IP       PORT(S)                      AGE
    ingress-nginx   LoadBalancer   AAA.BBB.CCC.DDD   WWW.XXX.YYY.ZZZ  80:31475/TCP,443:30578/TCP   17s
    ```

    Once you have been assigned an external IP address, continue to the next step.

2. Setup Domains

   You must have access to a top level domain for which you can create sub-domains to
   allow access to the application via a Load Balancer (LB).

   For example if your top level domain is `mycompany.com` then you
   should create a single wildcard DNS entry `*.sockshop.mycompany.com` to
   point to your external load balancer IP address.

   **Tip:** You can also run Ingress just on `localhost`. However, in that case you must update you `hosts` file with
   the used urls.
   ```
   127.0.0.1       sockshop.mycompany.com
   127.0.0.1       coherence.sockshop.mycompany.com
   127.0.0.1       jaeger.coherence.sockshop.mycompany.com
   127.0.0.1       mp.coherence.sockshop.mycompany.com
   127.0.0.1       grafana.sockshop.mycompany.com
   127.0.0.1       prometheus.sockshop.mycompany.com
   ```

3. Create the ingress

   In your terminal, export (or SET for Windows) your top level domain
   and the backend you are using.

   For example for domain `sockshop.mycompany.com` use the following

    ```bash
    export SOCKSHOP_DOMAIN=sockshop.mycompany.com

    envsubst -i k8s/optional/ingress.yaml | kubectl apply --namespace sockshop -f -

    kubectl get ingress --namespace sockshop

    NAME               HOSTS                                                                                                           ADDRESS           PORTS   AGE
    mp-ingress         mp.coherence.sockshop.mycompany.com                                                                             XXX.XXX.XXX.XXX   80      12d
    sockshop-ingress   coherence.sockshop.mycompany.com,jaeger.coherence.sockshop.mycompany.com,api.coherence.sockshop.mycompany.com   XXX.XXX.XXX.XXX   80      12d
    ```
4. Create the ingress for Grafana and Prometheus

   Ensuring you have the `SOCKSHOP_DOMAIN` environment variable set and issue the following:

    ```bash
    envsubst -i k8s/optional/ingress-grafana.yaml | kubectl apply --namespace monitoring -f -

    kubectl get ingress --namespace monitoring

    NAME              HOSTS                                                             ADDRESS          PORTS   AGE
    grafana-ingress   grafana.sockshop.mycompany.com,prometheus.sockshop.mycompany.com  XXX.YYY.XXX.YYY  80      12s
    ```

   The following URLs can be used to access Grafana and Prometheus. Please use the `admin` user password that was setup
   in the section `Install Prometheus Operator` above when using the URLs below:
   * http://grafana.sockshop.mycompany.com/
   * http://prometheus.sockshop.mycompany.com/

5. Access the application

   Access the application via the endpoint http://coherence.sockshop.mycompany.com/ (or http://localhost:8079)

### Install the Jaeger Operator

1. Install the Jaeger Operator

   The Jaeger Operator requires `cert-manager` to be installed. If it's missing, `cert-manager`
   can be installed with the following command:
   ```bash
   kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.14.2/cert-manager.yaml
   ```

   If you have not done, yet, create the `monitoring` namespace:

   ```bash
   kubectl create namespace monitoring
   ```

   The command below will install the Jaeger Operator. You only need to do this once, regardless of the number of
   backends you want to deploy.

    ```bash
    kubectl create -f k8s/optional/jaeger-operator.yaml
    ```

2. Deploy All-in-One Jaeger Instance

    ```bash
    kubectl create -f k8s/optional/jaeger.yaml --namespace sockshop
    ```

   > Note: To access Jaeger UI over localhost add port forwarding setup:
   > ```bash
   > $ kubectl --namespace sockshop port-forward svc/jaeger-query 16686
   >```

   > Note: to view not just Sockshop traces but also Coherence traces
   > edit `application.yaml` and set `coherence.tracing.ratio` to 1.
   >```yaml
   > coherence:
   >   tracing:
   >     ratio: 1
   >```
   > Due to known issues between Coherence and Spring, Coherence traces will not
   > be properly associated with the Spring traces.

3. Enable Jaeger tracing in the `app.yaml` files and rebuild and redeploy services

    Set `management.tracing.enabled` to `true`, e.g.:

    ```yaml
    application:
      main: com.oracle.coherence.examples.sockshop.spring.carts.CartsApp
      args:
        - "--port=8080"
        - "--management.tracing.enabled=true"
    ```

4. Exercise the Application and access Jaeger

   Accessing the Jaeger UI at http://jaeger.coherence.sockshop.mycompany.com/,
   you should see the trace information similar to the images below, allowing you
   to see how long each individual operation in the call tree took.

   ![Jaeger Search](./images/jaeger-search.png)

   ![Jaeger Detail](./images/jaeger-detail.png)

### Access Swagger

1. Deploy Swagger UI

    ```bash
    kubectl create -f k8s/optional/swagger.yaml --namespace sockshop
    ```

   Access the Swagger UI at http://mp.coherence.sockshop.mycompany.com/swagger/.

   Enter /carts/swagger/carts-1.0.yml into the Explore field at the top of the screen and click on Explore button.
   You should see the screen similar to the following, showing you all the endpoints for the Carts
   service (and their payloads), and allowing you to make API requests to it directly from your browser.

   ![Swagger UI](./images/swagger.png)

### Complete 🍻

Once you are done, you should have the following URLs available:

- Sock Shop - http://coherence.sockshop.mycompany.com/
- Prometheus - http://prometheus.sockshop.mycompany.com/
- Grafana - http://grafana.sockshop.mycompany.com/
- Jaeger - http://jaeger.coherence.sockshop.mycompany.com/
- Swagger - http://mp.coherence.sockshop.mycompany.com/swagger/

### Cleanup

1. Cleanup the ingress for applications

   To clean up the ingress for your deployment, execute the following:

    ```bash
    export SOCKSHOP_DOMAIN=sockshop.mycompany.com

    envsubst -i k8s/optional/ingress.yaml| kubectl delete -f - --namespace sockshop
    ```

2. Cleanup the ingress for Grafana and Prometheus

   If you installed Prometheus Operator, execute the following:

    ```bash
    envsubst -i k8s/optional/ingress-grafana.yaml | kubectl delete --namespace monitoring -f -
    ```

3. Remove the deployed services

   To clean up the deployed services, execute the following:

    ```bash
    export SOCKSHOP_DOMAIN=sockshop.mycompany.com
    kubectl delete -k k8s/coherence --namespace sockshop
    ```

4. Remove the Load Balancer

   If you wish to remove your load balancer, execute the following:

    ```bash
    kubectl delete -f k8s/optional/ingress-controller.yaml
    ```

5. Remove Jaeger

    ```bash
    kubectl delete -f k8s/optional/jaeger-operator.yaml
    ```

   Execute the following:

    ```bash
    kubectl delete -f k8s/optional/jaeger.yaml --namespace sockshop
    ```

6. Remove Swagger

   Execute the following:

    ```bash
   kubectl delete -f k8s/optional/swagger.yaml --namespace sockshop
    ```

7. Remove Prometheus and Grafana

   To remove the Prometheus Operator follow the instructions in the [Remove kube-prometheus](https://prometheus-operator.dev/docs/prologue/quick-start/#remove-kube-prometheus) section of the Prometheus Operator Quick Start.
