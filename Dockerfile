
ENV LANGUAGE='en_US:en'
# Use a Maven image as the builder stage
FROM maven:3.8.4-openjdk-11-slim AS builder

# Set the working directory
WORKDIR .

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the project
RUN mvn package

FROM registry.access.redhat.com/ubi8/openjdk-11:1.18


=======
# Test comment to check trigger in dev adding stuff and more stuff in dev !

# We make four distinct layers so if there are application changes the library layers can be re-used
#COPY --chown=185 target/lib/ /deployments/lib/
COPY --from=builder --chown=185 target/*.jar /deployments/
COPY --from=builder --chown=185 target/ /deployments/app/
#COPY --chown=185 target/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185
ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/code-with-quarkus-SNAPSHOT-runner.jar"

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]
