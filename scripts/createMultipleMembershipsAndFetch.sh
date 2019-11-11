#!/usr/bin/env bash

printf "\n\nAdding Gabriel"
curl -i -X POST localhost:9000/api/user/ \
  -d '{"firstName":"Gabriel","lastName":"Harris-Rouquette","address":"123 Maple St, Sampleton, ExampleState, 12345","email":"gabizou@gabizou.com","phoneNumber":"123-456-7890"}'
printf "\n\nAdding John Doe"
curl -i -X POST localhost:9000/api/user/ \
  -d '{"firstName":"John","lastName":"Doe","address":"100 Main St, AnVille, AnyState, 12345","email":"john.doe@somewhere.com","phoneNumber":"123-456-7890"}'

printf "\n\nAdding James Smith"
curl -i -X POST localhost:9000/api/user/ \
  -d '{"firstName":"James","lastName":"Smith","address":"123 Maple St, Sampleton, ExampleState, 12345","email":"james.smith@example.com","phoneNumber":"123-456-7890"}'

printf "\n\nAdding ExampleCompany"
curl -X POST localhost:9000/api/organization/ \
  -d '{"name":"ExampleCompany","address":"123 Sample Blvd. Representation City, Object","phoneNumber":"123-456-7890"}'

printf "\n\nAdding Gabriel to ExampleCompany"
# This is a bit of a cheat, the UUID of Gabriel's email: gabizou@gabizou.com
curl -i -X POST localhost:9000/api/organization/ExampleCompany/members \
  -d '"8255b90d-9ff7-52f7-b28b-19054a3e9f1e"'

printf "\n\nAdding John to ExampleCompany"
curl -i -X POST localhost:9000/api/organization/ExampleCompany/members \
  -d '"3eda6d3a-54f3-59a3-8904-d5eabbccd1f2"'

printf "\n\nAdding James to ExampleCompany"
curl -i -X POST localhost:9000/api/organization/ExampleCompany/members \
  -d '"6fad8daf-de1f-56e8-b22c-475e50998e5d"'

printf "\n\nGetting Memberships of ExampleCompany\n"
curl -i localhost:9000/api/organization/ExampleCompany/members

printf "\n\nGetting memberships of Gabriel"
curl -i localhost:9000/api/user/8255b90d-9ff7-52f7-b28b-19054a3e9f1e/memberships
