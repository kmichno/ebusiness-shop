package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, personRepository: PersonRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  import personRepository.PeopleTable
  private class ProductTable(tag: Tag) extends Table[Product](tag, "products") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def price = column[Int]("price")

    def description = column[String]("description")

    def userId = column[Long]("userId")

    def * = (id, name, price, description, userId) <> ((Product.apply _).tupled, Product.unapply)
    def user = foreignKey("user_fk",userId, users)(_.id)
  }

  import personRepository.PeopleTable
  private val products = TableQuery[ProductTable]
  private val users = TableQuery[PeopleTable]

  def create(name: String, price: Int, description: String, userId: Long): Future[Product] = db.run {
    (products.map(p => (p.name, p.price, p.description, p.userId))
      returning products.map(_.id)
      into ((nameAge, id) => Product(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4))
    ) += (name, price, description, userId)
  }

  def list(): Future[Seq[Product]] = db.run {
    products.result
  }
}
