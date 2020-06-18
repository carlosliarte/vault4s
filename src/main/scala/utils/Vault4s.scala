package utils

import models.{AppCredentials, Credentials}
import org.json4s.jackson.JsonMethods.{compact, parse, render}
import org.json4s.{DefaultFormats, JObject}
import scalaj.http.{Http, HttpResponse}
import org.json4s.JsonDSL._

trait Vault4s {
  implicit val formats: DefaultFormats.type = DefaultFormats

  def getToken(appCredentials: AppCredentials): String = {
    val json = ("role_id" -> appCredentials.role_id) ~ ("secret_id" -> appCredentials.secret_id)
    val appRoleData = compact(render(json))

    val response = Http("http://127.0.0.1:8200/v1/auth/approle/login")
      .postData(appRoleData)
      .asString

    val tokenResponseParse = parse(response.body).asInstanceOf[JObject]
    tokenResponseParse.values("auth").asInstanceOf[Map[String, String]]("client_token")
  }

  def getSecretId(rootToken: String, appName: String): String = {
    val response = Http(s"http://127.0.0.1:8200/v1/auth/approle/role/$appName/secret-id")
      .header("X-Vault-Token", rootToken)
      .postData("")
      .asString
    val secretIdResponseParse = parse(response.body).asInstanceOf[JObject]
    secretIdResponseParse.values("data").asInstanceOf[Map[String, String]]("secret_id")
  }

  def getRoleId(rootToken: String, appName: String): String = {
    val response: HttpResponse[String] = Http(s"http://127.0.0.1:8200/v1/auth/approle/role/$appName/role-id")
      .header("X-Vault-Token", rootToken)
      .asString
    val data = parse(response.body).asInstanceOf[JObject]
    data.values("data").asInstanceOf[Map[String, String]]("role_id")
  }

  def getDataSourceCredentials(appToken: String, appName: String): Credentials = {
    val response: HttpResponse[String] = Http(s"http://127.0.0.1:8200/v1/secret/data/$appName")
      .header("X-Vault-Token", appToken)
      .asString
    val data = parse(response.body).asInstanceOf[JObject]
    val Credentials(credentials) = data.values("data").asInstanceOf[Map[String, String]]("data").asInstanceOf[Map[String, String]]
    credentials
  }

}
