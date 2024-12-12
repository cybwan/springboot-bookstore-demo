# Bookstore Demo

### Dubbo

Start a Zookeeper server with Docker:

```bash
docker run --rm --name zookeeper -p 2181:2181 zookeeper
```

Then, you can start the project with the following command:

```bash
java -jar bookwarehouse-dubbo.jar --spring.profiles.active=dubbo,dev
java -jar bookstore-dubbo.jar --spring.profiles.active=dubbo,dev
java -jar bookbuyer-dubbo.jar --spring.profiles.active=dubbo,dev
```
