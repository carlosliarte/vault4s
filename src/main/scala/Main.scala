import core.{Rule, VaultCore}
import models.AppCredentials
import utils.Vault4s

object Main extends App with Vault4s with VaultCore{
  val appName = "test"
  val rootToken = "test"
  /*val secretId = getSecretId(rootToken, appName)
  val roleId = getRoleId(rootToken, appName)
  val appToken = getToken(AppCredentials(roleId, secretId))
  val credentials = getCredentials(appToken, appName)
  println(credentials)*/
  val rule = Rule("testnewrule", "*", "write")
  createPolicy(rootToken, "testCodeNew", rule)
}
