## Vault4s

### INSTALLATION VAULT IN LOCAL MODE

#### Install vault
```brew install vault```

#### Init server
```vault server -dev```

#### Init with docker
```docker run -p 8200:8200 --cap-add=IPC_LOCK -e 'VAULT_DEV_ROOT_TOKEN_ID=test' vault```

#### Export address and root token
```export VAULT_ADDR='http://127.0.0.1:8200'```
```export VAULT_DEV_ROOT_TOKEN_ID="test"```
```export VAULT_TOKEN="test"```


#### Create secret
```vault kv put secret/test user=test```

#### Get secret
```vault kv get secret/test```

#### Enable approle to access with apps
```vault auth enable approle```

#### Create role for app
```
vault write auth/approle/role/test \
    secret_id_ttl=10m \
    token_num_uses=10 \
    token_ttl=20m \
    token_max_ttl=30m \
    secret_id_num_uses=40
```

#### Create policy
```
curl -X POST -H "X-Vault-Token:$VAULT_TOKEN" -d '{"rules": "{\"name\": \"dev\", \"path\": {\"secret/*\": {\"policy\": \"write\"}}}"}' http://127.0.0.1:8200/v1/sys/policy/dev
curl -X GET -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/sys/policy/dev
```

#### Add policy to role
```
curl \
    --header "X-Vault-Token: test" \
    --request POST \
    --data '{"policies": "dev"}' \
    http://127.0.0.1:8200/v1/auth/approle/role/test
```

#### Read role_id vault command
```
vault read auth/approle/role/test/role-id

Example:
Key        Value
---        -----
role_id    9400081d-cf6e-22de-19ea-6b86e0c0ed21
```

#### Write secret_id vault command
```
vault write -f auth/approle/role/test/secret-id

Example
Key                   Value
---                   -----
secret_id             3883f832-20c1-2195-435e-53527a021d51
secret_id_accessor    fde6adad-ee0e-2754-7c8e-22726a49d7f4
```

#### Change name of variable root token
```export VAULT_TOKEN=$VAULT_DEV_ROOT_TOKEN_ID```

#### Get role_id of app
```curl -X GET -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/auth/approle/role/test/role-id```

#### Get secret_id of app
```curl -X POST -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/auth/approle/role/test/secret-id```

#### Get token for app
```curl -X POST -d '{"role_id":"role_id","secret_id":"secret_id"}' http://127.0.0.1:8200/v1/auth/approle/login```

#### Set token app into variable
```export APP_TOKEN=app_token```
```export VAULT_TOKEN=$APP_TOKEN```

#### Get secret with app role authentication
```curl -X GET -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/secret/data/test```

#### Official requests examples from hashicorp
https://gist.github.com/exAspArk/e210523a4bcb988cdfb24a114d46ddf0