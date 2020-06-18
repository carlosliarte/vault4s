package core

import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods.{compact, render}
import scalaj.http.Http

import scala.util.parsing.json._


case class Rule(name: String, path: String, policy: String)

trait VaultCore {

  implicit class `literally the string`(val sc: StringContext) {
    def lit(args: Any*): String = {
      sc.checkLengths(args)
      sc.standardInterpolator(identity, args)
    }}

  def createRole() = {

  }

  def createPolicy(rootToken: String, policyName: String, rule: Rule): Int = {
    val policyRuleBody = lit"""{"rules": "{\"name\": \"${rule.name}\", \"path\": {\"secret/${rule.path}\": {\"policy\": \"${rule.policy}\"}}}"}"""

    val response = Http(s"http://127.0.0.1:8200/v1/sys/policy/${policyName}")
      .header("X-Vault-Token", rootToken)
      .put(policyRuleBody)
      .asString

    if (response.isError){
      println(s"Error when try to create policy -> ${response.body}")
    }
    println(response.code)
    response.code
  }

}
