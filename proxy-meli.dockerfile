#FROM gradle:jre-alpine
FROM gradle:jdk

USER gradle

ARG SPRING_REDIS_HOST
ARG SPRING_METRICS_EXPORT_STATSD_HOST
ENV SRPING_REDIS_HOST=${SPRING_REDIS_HOST:-redis}
ENV SPRING_METRICS_EXPORT_STATSD_HOST=${SPRING_METRICS_EXPORT_STATSD_HOST:-localhost}

COPY --chown=gradle:gradle build.gradle /home/gradle/build.gradle
COPY --chown=gradle:gradle settings.gradle /home/gradle/settings.gradle
COPY --chown=gradle:gradle src/ /home/gradle/src

WORKDIR /home/gradle

RUN cd /home/gradle
RUN gradle build -is

ENTRYPOINT ["sh", "-c", "java -jar /home/gradle/build/libs/proxy-jpb-0.0.1-SNAPSHOT.jar"]
#CMD ["@package.product.component@", "/var/log/iway/@norm.product@/apiGateway/intrawayApiGateway.log"]
