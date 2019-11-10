#!/usr/bin/env bash

curl -X POST localhost:9000/api/organization/ -d '{"name":"ExampleCompany","address":"123 Sample Blvd. Representation City, Object","phoneNumber":"123-456-7890"}'