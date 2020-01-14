import org.json4s.JsonDSL._
import org.json4s.{DefaultFormats, _}
import org.json4s.jackson.JsonMethods._
import scalaj.http._

case class Credentials(user: String, password: String)
case class AppCredentials(role_id: String, secret_id: String)

object Main extends App {
  implicit val formats: DefaultFormats.type = DefaultFormats
  val rootToken = "s.ww3GwhiDkA1Ymbw2lNsleKWi"
  val secretId = getSecretId(rootToken)
  val roleId = getRoleId(rootToken)
  val appToken = getToken(roleId, secretId)
  val credentials = getCredentials(appToken, "2")
  println(credentials)

  /*var response: HttpResponse[String] = Http("http://127.0.0.1:8200/v1/secret/data/mysqlmb").header("X-Vault-Token", "s.OyRYmrJVc0tC24jhuM8G6do9").params(("version", "2")).asString
  //val response = "{\"request_id\":\"103156e6-396f-6a16-48ad-6cbdd50367e6\",\"lease_id\":\"\",\"renewable\":false,\"lease_duration\":0,\"data\":{\"data\":{\"rouser\":\"rouser\"},\"metadata\":{\"created_time\":\"2020-01-13T13:47:20.750853Z\",\"deletion_time\":\"\",\"destroyed\":false,\"version\":1}},\"wrap_info\":null,\"warnings\":null,\"auth\":null}"

  if (response.code == 403) {
    println("Token Expired...")
    println("Auto renew token")
    val secretId = getSecretId(rootToken)
    val appToken = getToken("9400081d-cf6e-22de-19ea-6b86e0c0ed21", secretId)
    response = Http("http://127.0.0.1:8200/v1/secret/data/mysqlmb").header("X-Vault-Token", appToken).params(("version", "2")).asString
  }

  val data = parse(response.body).asInstanceOf[JObject]
  val credentialsVault = data.values("data").asInstanceOf[Map[String, String]]("data").asInstanceOf[Map[String, String]]
  val credentials = Credentials(credentialsVault.keySet.head, credentialsVault("rouser"))
  println(credentials)*/

  def getToken(role_id: String, secret_id: String): String = {
    val json = ("role_id" -> role_id) ~ ("secret_id" -> secret_id)
    val appRoleData = compact(render(json))

    val response = Http("http://127.0.0.1:8200/v1/auth/approle/login")
      .postData(appRoleData)
      .asString
    val tokenResponseParse = parse(response.body).asInstanceOf[JObject]
    val appToken = tokenResponseParse.values("auth").asInstanceOf[Map[String, String]]("client_token")
    println(appToken)
    appToken
  }

  def getSecretId(rootToken: String): String = {
    val response = Http("http://127.0.0.1:8200/v1/auth/approle/role/mysqlmb/secret-id").header("X-Vault-Token", rootToken).postData("")
      .asString
    val secretIdResponseParse = parse(response.body).asInstanceOf[JObject]
    val secret_id = secretIdResponseParse.values("data").asInstanceOf[Map[String, String]]("secret_id")
    println("Secret_id: " + secret_id)
    secret_id
  }

  def getRoleId(rootToken: String): String = {
    val response: HttpResponse[String] = Http("http://127.0.0.1:8200/v1/auth/approle/role/mysqlmb/role-id").header("X-Vault-Token", rootToken).asString
    val data = parse(response.body).asInstanceOf[JObject]
    val roleId = data.values("data").asInstanceOf[Map[String, String]]("role_id")
    println("RoleId: " + roleId)
    roleId
  }

  def getCredentials(appToken: String, version: String): Credentials = {
   val response: HttpResponse[String] = Http("http://127.0.0.1:8200/v1/secret/data/mysqlmb").header("X-Vault-Token", appToken).params(("version", version)).asString
    /*if (response.code == 403) {
      println("Token Expired...")
      println("Auto renew token")
      val secretId = getSecretId(rootToken)
      val appToken = getToken("9400081d-cf6e-22de-19ea-6b86e0c0ed21", secretId)
      response = Http("http://127.0.0.1:8200/v1/secret/data/mysqlmb").header("X-Vault-Token", appToken).params(("version", "2")).asString
    }*/
    val data = parse(response.body).asInstanceOf[JObject]
    val credentialsVault = data.values("data").asInstanceOf[Map[String, String]]("data").asInstanceOf[Map[String, String]]
    val credentials = Credentials(credentialsVault.keySet.head, credentialsVault("rouser"))
    credentials
  }

}


