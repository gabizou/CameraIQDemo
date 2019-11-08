#!/usr/bin/env bash

curl -i -X POST localhost:9000/api/user/ \
-d '{"firstName":"James","lastName":"Smith","address":"123 Maple St, Sampleton, ExampleState, 12345","email":"james.smith@example.com","phoneNumber":"123-456-7890"}'

