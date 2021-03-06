package models

import play.api.libs.json._

case class Person(id: Long, name: String, age: Int, address: String)

object Person {  
  implicit val personFormat = Json.format[Person]
}
