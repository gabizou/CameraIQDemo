#!/usr/bin/env bash

mvn \
    -Dlog4j.debug=true \
    -Dlogger.file=./user-impl/src/main/resources/log4j2.properties \
    lagom:runAll \
    -f pom.xml \
| tee run.log

