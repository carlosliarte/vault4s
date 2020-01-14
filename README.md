## INSTALLATION VAULT IN LOCAL MODE

https://gist.github.com/exAspArk/e210523a4bcb988cdfb24a114d46ddf0

#### Create alias
```alias vault='/Users/cliarte/Documents/vault'```

#### Init server
```vault server -dev```

#### Export address and root token
```export VAULT_ADDR='http://127.0.0.1:8200'```
```export VAULT_DEV_ROOT_TOKEN_ID="s.ww3GwhiDkA1Ymbw2lNsleKWi"```

#### Create secret
```vault kv put secret/mysqlmb rouser=rouser```

#### Get secret
```vault kv get secret/mysqlmb```

#### Enable approle to access with apps
```vault auth enable approle```

#### Create role for app
```
vault write auth/approle/role/mysqlmb2 \
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
    --header "X-Vault-Token: s.ww3GwhiDkA1Ymbw2lNsleKWi" \
    --request POST \
    --data '{"policies": "dev"}' \
    http://127.0.0.1:8200/v1/auth/approle/role/mysqlmb
```

#### Read role_id vault command
```
vault read auth/approle/role/mysqlmb/role-id
Key        Value
---        -----
role_id    9400081d-cf6e-22de-19ea-6b86e0c0ed21
```

#### Write secret_id vault command
```
vault write -f auth/approle/role/mysqlmb/secret-id
Key                   Value
---                   -----
secret_id             3883f832-20c1-2195-435e-53527a021d51
secret_id_accessor    fde6adad-ee0e-2754-7c8e-22726a49d7f4
```

#### Change name of variable root token
```export VAULT_TOKEN=$VAULT_DEV_ROOT_TOKEN_ID```

#### Get role_id of app
```curl -X GET -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/auth/approle/role/mysqlmb/role-id```

#### Get secret_id of app
```curl -X POST -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/auth/approle/role/mysqlmb/secret-id```

#### Get token for app
```curl -X POST -d '{"role_id":"9400081d-cf6e-22de-19ea-6b86e0c0ed21","secret_id":"0e1c8402-6ac5-dec0-09fb-38a117ad926f"}' http://127.0.0.1:8200/v1/auth/approle/login```

#### Set token app into variable
```export APP_TOKEN=s.SsUuEN6cckTh7zYPBCUO8RBL```
```export VAULT_TOKEN=$APP_TOKEN```

#### Get secret with app role authentication
```curl -X GET -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/secret/data/mysql```