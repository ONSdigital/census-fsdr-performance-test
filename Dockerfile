FROM gradle:5.4.1-jdk11

RUN mkdir /opt/census-fsdr-performance-tests
COPY . /opt/census-fsdr-performance-tests

WORKDIR /opt/census-fsdr-performance-tests
ENTRYPOINT [ "./gradlew", "--stacktrace",  "test" ]
