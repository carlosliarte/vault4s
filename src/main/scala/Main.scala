
object Main extends App with Vault4s {
  val appName = "mysqlmbtest"
  val rootToken = "s.ww3GwhiDkA1Ymbw2lNsleKWi"
  val secretId = getSecretId(rootToken, appName)
  val roleId = getRoleId(rootToken, appName)
  val appToken = getToken(AppCredentials(roleId, secretId))
  val credentials = getCredentials(appToken, "1", appName)
  println(credentials)
}
