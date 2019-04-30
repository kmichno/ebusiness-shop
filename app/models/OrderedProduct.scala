package models

import play.api.libs.json.Json

case class OrderedProduct(id: Long, orderId: Long, productId: Long)

object OrderedProduct {
  implicit val orderedProductFormat = Json.format[OrderedProduct]
}

