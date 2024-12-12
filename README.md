# Bookstore Demo

### Dubbo

Start a Zookeeper server with Docker:

```bash
docker run --rm --name zookeeper -p 2181:2181 zookeeper
```

Then, you can start the project with the following command:

```bash
java -jar bookwarehouse/target/bookwarehouse-0.0.1-SNAPSHOT.jar --spring.profiles.active=dubbo,dev
java -jar bookstore/target/bookstore-0.0.1-SNAPSHOT.jar --spring.profiles.active=dubbo,dev
java -jar bookstore/target/bookstore-0.0.1-SNAPSHOT.jar --spring.profiles.active=dubbo,dev
```
