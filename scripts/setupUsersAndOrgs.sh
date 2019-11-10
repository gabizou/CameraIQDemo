#!/usr/bin/env bash

curl -i -X POST localhost:9000/api/user/ \
  -d '{"firstName":"Gabriel","lastName":"Harris-Rouquette","address":"123 Maple St, Sampleton, ExampleState, 12345","email":"gabizou@gabizou.com","phoneNumber":"123-456-7890"}'

curl -X POST localhost:9000/api/organization/ \
  -d '{"name":"ExampleCompany","address":"123 Sample Blvd. Representation City, Object","phoneNumber":"123-456-7890"}'

curl -i -X POST localhost:9000/api/user/ \
  -d '{"firstName":"John","lastName":"Doe","address":"100 Main St, AnVille, AnyState, 12345","email":"john.doe@somewhere.com","phoneNumber":"123-456-7890"}'

curl -i -X POST localhost:9000/api/user/ \
  -d '{"firstName":"James","lastName":"Smith","address":"123 Maple St, Sampleton, ExampleState, 12345","email":"james.smith@example.com","phoneNumber":"123-456-7890"}'

curl -i -X POST localhost:9000/api/organization/ExampleCompany/members \
  -d '"8255b90d-9ff7-52f7-b28b-19054a3e9f1e"'

