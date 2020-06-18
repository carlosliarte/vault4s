#!/bin/bash

export VAULT_ADDR='http://127.0.0.1:8200'
export VAULT_DEV_ROOT_TOKEN_ID="test"
export VAULT_TOKEN="test"

vault kv put secret/test user=test password=test

vault auth enable approle

vault write auth/approle/role/test \
    secret_id_ttl=10m \
    token_num_uses=10 \
    token_ttl=20m \
    token_max_ttl=30m \
    secret_id_num_uses=40

curl -X POST -H "X-Vault-Token:$VAULT_TOKEN" -d '{"rules": "{\"name\": \"dev\", \"path\": {\"secret/*\": {\"policy\": \"write\"}}}"}' http://127.0.0.1:8200/v1/sys/policy/dev
curl -X GET -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/sys/policy/dev

curl \
    --header "X-Vault-Token: test" \
    --request POST \
    --data '{"policies": "dev"}' \
    http://127.0.0.1:8200/v1/auth/approle/role/test

