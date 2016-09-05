# hubble-server

This document is addressed assuming you are working on Ubuntu. For other Operating systems, please refer the equivalent/corresponding documentation of these tools. 

### Database settings.
```
spring.datasource.url=jdbc:mysql://localhost/h2o
spring.datasource.username=h2o
spring.datasource.password=h2o123
spring.datasource.driver-class-token=com.mysql.jdbc.Driver
```

The code now has template for all the layers, i.e. service, endpoint, pojo, entity, and dao layers. If you are adding anything new in any layer, please look at existing code and follow on that path.

Many things can be just achieved with annotations and no need to write new code.

## External Dependencies
### Redis server
You need to install redis-server
```
sudo apt-get install redis-server
```
Also make sure you have set the password in the conf file of redis. 
Make sure the config file has following two lines:
```
requirepass redis123
```

and
```
masterauth redis123
```

### MySQL
Install MySQL.
```
sudo apt-get install mysql-server
```
Create a user named "h2o" with password "h2o123"

### Cassandra
Install Cassandra.
Execute the following commands in that order.
```
echo "deb http://www.apache.org/dist/cassandra/debian 22x main" | sudo tee -a /etc/apt/sources.list.d/cassandra.sources.list
echo "deb-src http://www.apache.org/dist/cassandra/debian 22x main" | sudo tee -a /etc/apt/sources.list.d/cassandra.sources.list
gpg --keyserver pgp.mit.edu --recv-keys F758CE318D77295D
gpg --export --armor F758CE318D77295D | sudo apt-key add -
gpg --keyserver pgp.mit.edu --recv-keys 2B5C1B00
gpg --export --armor 2B5C1B00 | sudo apt-key add -
gpg --keyserver pgp.mit.edu --recv-keys 0353B12C
gpg --export --armor 0353B12C | sudo apt-key add -
sudo apt-get update
sudo apt-get install cassandra
```

### Memcache
Install memcache
```
sudo apt-get install memcached
```

## Running the project

Build normally with the tests enabled. Must do this before requesting for pull request. 
```
sudo  kill -9 $(sudo lsof -i 6:9001 -t)
sudo  kill -9 $(sudo lsof -i 6:9002 -t)
sudo  kill -9 $(sudo lsof -i 6:9003 -t)
sudo  kill -9 $(sudo lsof -i 6:9004 -t)
mysql -uh2o -ph2o123 -e "drop database h2o"
mysql -uh2o -ph2o123 -e "create database h2o"
cqlsh -f /home/bitnami/run.cql 127.0.0.1 9042
redis-cli -h 127.0.0.1 -a redis123 -r 1 flushall

mvn clean verify 
```
Please note that only the last line will build the project. However the previous commands are necessary as pre-build/pre-test setup.


Build without tests and in quiet mode
```
mvn clean verify -Dmaven.test.skip -q
```

## Run the project in Jetty
There are property files for all three different profiles in all three servers i.e. mainapp, identity-server, upload-server

- application-development.properties
- application-staging.properties
- application-production.properties.

Currently all these files are identical and need to be changed to have appropriate URLs, database settings, cassandra settings etc for different profiles.  Please make these changes to all the three servers.

The default profile is development. So when somebody runs
```
$ java -jar identity-server/target/h2o-identity-server-1.9.9.jar 
```
It is equivalent to
```
$ java -jar -Dspring.profiles.active=development identity-server/target/h2o-identity-server-1.9.9.jar 
```

Of course if one wants to run a server in production profile the command will change to
```
$ java -jar -Dspring.profiles.active=production identity-server/target/h2o-identity-server-1.9.9.jar 
```

Once the server is running, to test the apis, run the following commands in browser or [postman](https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop?hl=en), as usual:

```
http://0.0.0.0:8080/v1/users/1
```

You can access the swagger page here:
http://0.0.0.0:8080/swagger-ui.html