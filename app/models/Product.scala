package models

import play.api.libs.json.Json

case class Product(id: Long, name: String, price: Int, description: String, userId: Long)


object Product {
  implicit val personFormat = Json.format[Product]
}
