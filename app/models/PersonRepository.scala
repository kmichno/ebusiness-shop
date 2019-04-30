package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class PersonRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PeopleTable(tag: Tag) extends Table[Person](tag, "people") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def age = column[Int]("age")

    def address = column[String]("address")

    def * = (id, name, age, address) <> ((Person.apply _).tupled, Person.unapply)
  }

  val people = TableQuery[PeopleTable]

  def create(name: String, age: Int, address: String): Future[Person] = db.run {
    (people.map(p => (p.name, p.age, p.address))
      returning people.map(_.id)
      into ((nameAge, id) => Person(id, nameAge._1, nameAge._2, nameAge._3))
    ) += (name, age, address)
  }

  def list(): Future[Seq[Person]] = db.run {
    people.result
  }
}
