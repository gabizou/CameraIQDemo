#!/usr/bin/env bash

curl -i -X POST localhost:9000/api/user/ \
-d '{"firstName":"Gabriel","lastName":"Harris-Rouquette","address":"123 Maple St, Sampleton, ExampleState, 12345","email":"gabizou@gabizou.com","phoneNumber":"123-456-7890"}'

