package models

import play.api.libs.json.Json

case class Order(id: Long, order_date: String, userId: Int)

object Order {
  implicit val orderFormat = Json.format[Order]
}


