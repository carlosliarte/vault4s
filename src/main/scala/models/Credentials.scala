package models

import scala.util.control.NonFatal

case class AppCredentials(role_id: String, secret_id: String)

case class Credentials(user: String, password: String)

object Credentials {
  def unapply(values: Map[String, String]): Option[Credentials] = try {
    Some(Credentials(values("user"), values("password")))
  } catch {
    case NonFatal(_) => None
  }
}
