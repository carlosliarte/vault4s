
import core.{Rule, VaultCore}
import models.AppCredentials
import utils.Vault4s

object Main extends App with Vault4s with VaultCore {
  val rootToken = System.getenv("VAULT_ROOT_TOKEN_LOCAL")

  logger.info(s"Token from env variables -> ${rootToken}")

  val appName = "test"
  val secretId = getSecretId(rootToken, appName)
  val roleId = getRoleId(rootToken, appName)
  val appToken = getToken(AppCredentials(roleId, secretId))
  val credentials = getDataSourceCredentials(appToken, appName)
  val rule = Rule("testnewrule", "*", "write")
  createPolicy(rootToken, "testCodeNew", rule)
}
