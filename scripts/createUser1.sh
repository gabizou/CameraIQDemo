#!/usr/bin/env bash

curl -i -X POST localhost:9000/api/user/ \
-d '{"firstName":"John","lastName":"Doe","address":"100 Main St, AnVille, AnyState, 12345","email":"john.doe@somewhere.com","phoneNumber":"123-456-7890"}'
