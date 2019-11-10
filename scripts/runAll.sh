#!/usr/bin/env bash

mvn \
    -Dlogger.file=./demo-utils/src/main/resources/debug.xml \
    lagom:runAll \
    -f pom.xml \
| tee run.log

