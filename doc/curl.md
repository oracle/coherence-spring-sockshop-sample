## Interacting with the REST API

The REST API of the Coherence Spring Sock Shop Sample should be compatible with the original REST API as provided by
Weaveworks:

https://microservices-demo.github.io/api/index.html

Below you will find several [curl](https://curl.se/) examples of interacting with the microservices' REST API
deployed to a Kubernetes cluster as described in the [Complete Application Deployment Guide](complete-application-deployment.md).

### Get Users

### Credit Card

```bash
curl -X POST http://mp.coherence.sockshop.mycompany.com/users/cards \
-H "Content-type: application/json" \
-d '{"longNum": "99999999", "expires": "44/44",  "ccv": "abcd", "userID": "user"}'
```

#### Delete Credit Card

```bash
curl -X DELETE http://mp.coherence.sockshop.mycompany.com/users/cards/user:9999
```

#### Get Credit Card

```bash
curl -X GET http://mp.coherence.sockshop.mycompany.com/users/cards/user:99999
```

### Addresses

#### Add Address for User

```bash
curl -u user:password -X POST http://mp.coherence.sockshop.mycompany.com/users/addresses \
-H "Content-type: application/json" \
-d '{
  "street": "My Street",
  "number": "1234",
  "country": "Germany",
  "city": "Berlin",
  "postcode": "12345",
  "userID": "user"
}'
```

#### Get Addresses for User

```bash
curl -u user:password -X GET http://mp.coherence.sockshop.mycompany.com/users/addresses
```

#### Delete Address for User

```bash
curl -u user:password -X DELETE http://mp.coherence.sockshop.mycompany.com/users/addresses/user:1
```