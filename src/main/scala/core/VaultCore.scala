package core

import com.typesafe.scalalogging.Logger
import scalaj.http.Http

case class Rule(name: String, path: String, policy: String)

trait VaultCore {

  val logger = Logger(classOf[VaultCore])

  implicit class `literally the string`(val sc: StringContext) {
    def lit(args: Any*): String = {
      sc.checkLengths(args)
      sc.standardInterpolator(identity, args)
    }}

  def createRole() = ???
  def addRole() = ???

  def createPolicy(rootToken: String, policyName: String, rule: Rule): Int = {
    logger.info(s"Creating rule '${rule.name}' in policy '$policyName'...")
    val policyRuleBody = lit"""{"rules": "{\"name\": \"${rule.name}\", \"path\": {\"secret/${rule.path}\": {\"policy\": \"${rule.policy}\"}}}"}"""

    val response = Http(s"http://127.0.0.1:8200/v1/sys/policy/${policyName}")
      .header("X-Vault-Token", rootToken)
      .put(policyRuleBody)
      .asString

    response.isError match {
      case false => logger.info("Created policy correctly")
      case true => logger.error(s"Error when try to create policy -> ${response.body}")
    }

    response.code
  }

}
