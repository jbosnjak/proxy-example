# Proxy for MELI 	

### How to build

The project is built by Gradle plugins. Just download gradle 2.14 + from gradle.org.

		gradle clean build
		
### How to run it?

Local: After running build command, just:

	java -jar build/libs/proxy-jpb-0.0.1-SNAPSHOT.jar 
   
If you want to run the full stack you need to have docker and docker-compose installed:

	docker-compose up -d
	
### Configuration

There is an standard config in place within the jar file. In order to override it, it can be use the `application-routes.yml` in the `extras` folder.

* add a route:

		zuul:
		  routes:
		    $routeId:
		      path: /$path/**
		      stripPrefix: false
		      serviceId: $serviceId
    $serviceId:
	    ribbon:
	      listOfServers: https://api.mercadolibre.com:443 (or other host)
	      IsSecure: true
	      NIWSServerListClassName: com.netflix.loadbalancer.ConfigurationBasedServerList     

For more info visit [https://cloud.spring.io/spring-cloud-netflix/single/spring-cloud-netflix.html#_router_and_filter_zuul](https://cloud.spring.io/spring-cloud-netflix/single/spring-cloud-netflix.html#_router_and_filter_zuul)

* configure rateLimit per route:

	zuul:
	  ratelimit:
	    enabled: true
	    policies:
	      $routeId:
	        limit: 10
	        refresh-interval: 60
	        type:
	          - origin(one, the other, none or both)
	          - user_agent

RateLimit is support by Redis. When you use docker-compose to start the environment, a Redis instance is started. Locally you must provide a Redis instance. If not, the gateway will work in **fail-safe mode**.	     

### Load tests	

Load tests are executed with jmeter.

	./jmeter -n -t loadTest/ProxyMeliLoadTest.jmx
	
	