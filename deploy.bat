@echo on
@echo =============================================================
@echo $                                                           $
@echo $                     Nepxion Discovery                     $
@echo $                                                           $
@echo $                                                           $
@echo $                                                           $
@echo $  Nepxion Studio All Right Reserved                        $
@echo $  Copyright (C) 2017-2050                                  $
@echo $                                                           $
@echo =============================================================
@echo.
@echo off

@title Nepxion Discovery
@color 0a

call mvn clean deploy -DskipTests -e -P release -pl discovery-plugin-starter-eureka,discovery-plugin-starter-consul,discovery-plugin-starter-zookeeper,discovery-plugin-starter-nacos,discovery-plugin-config-center-starter-apollo,discovery-plugin-config-center-starter-nacos,discovery-plugin-config-center-starter-redis,discovery-console-starter-apollo,discovery-console-starter-nacos,discovery-console-starter-redis,discovery-plugin-strategy-starter-service,discovery-plugin-strategy-starter-zuul,discovery-plugin-strategy-starter-gateway,discovery-plugin-strategy-starter-hystrix,discovery-plugin-strategy-starter-opentracing,discovery-plugin-strategy-starter-skywalking,discovery-plugin-strategy-sentinel-starter-local,discovery-plugin-strategy-sentinel-starter-apollo,discovery-plugin-strategy-sentinel-starter-nacos,discovery-plugin-strategy-sentinel-starter-opentracing,discovery-plugin-strategy-sentinel-starter-skywalking,discovery-plugin-strategy-starter-service-sentinel,discovery-plugin-test-starter -am

pause