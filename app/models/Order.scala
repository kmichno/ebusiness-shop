package models

import play.api.libs.json.Json

case class Order(id: Long, order_date: String, userId: Long)

object Order {
  implicit val orderFormat = Json.format[Order]
}


