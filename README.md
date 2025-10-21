# User Service

A user service responsible for managing user data and functionality. Facilitates the creation of new users, profile 
management, authorization, authentication, etc. Users can be deleted or restricted through this service. 

## Project Info

**SDK:** Oracle OpenJDK 25

**Gradle:** Gradle 9.1.0

**Kotlin:** 2.2.0

## Running Locally (Dev)

Instructions for running the app locally in a docker container with a local database.

### Build Commands

Build the latest version of the app:

```
gradle build
```

### Compose and Run Docker Container

Build and run the app, postgres database in a docker container:

```
docker compose up -d
```

To view running containers:

```
docker ps
```

You should see the `user-service` and `users_db` containers running. The `user-service` container will be listening on 
port 8080. The postgres database will be listening on port 5432.

### Database

To interact with the postgres database within its container, run the following command:

```
docker compose exec db psql psql -U postgres -d users
```

List tables with: `\dt`

Run any additional sql queries from the `users=#` prompt. Run `exit` to leave the psql prompt.

### Stop and Remove Containers and Volumes

To stop all running containers (note this will stop and remove the currently running containers, but it will not remove
the volumes):

```
docker compose down
```

To check if any volumes remain after stopping the containers:
```
docker volume ls
```

You will likely see one volume with very long alphanumeric name and a `user-service-pgdata` volume. The 
`user-service-pgdata` volume is the postgres database data volume and will contain all data written to the database 
during local testing. To remove all volumes:

```
docker volume rm $(docker volume ls -q)
```

To remove a single volume:
```
docker volume rm {volume-name}
```

If you want to remove all containers and volumes, run:
```
docker compose down -v
```