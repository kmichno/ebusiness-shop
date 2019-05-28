package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, categoryRepository: CategoryRepository)(implicit ec: ExecutionContext) {
   private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
   private class ProductTable(tag: Tag) extends Table[Product](tag, "products") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def price = column[Int]("price")

    def description = column[String]("description")

    def categoryId = column[Long]("categoryId")

    def * = (id, name, price, description,categoryId) <> ((Product.apply _).tupled, Product.unapply)
     def category_fk = foreignKey("cat_fk",categoryId, category)(_.id)
  }

  import categoryRepository.CategoryTable
   private val products = TableQuery[ProductTable]
  private val category = TableQuery[CategoryTable]

  def create(name: String, price: Int, description: String, categoryId: Long): Future[Product] = db.run {
    (products.map(p => (p.name, p.price, p.description, p.categoryId))
      returning products.map(_.id)
      into ((nameAge, id) => Product(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4))
    ) += (name, price, description, categoryId)
  }

  def list(): Future[Seq[Product]] = db.run {
    products.result
  }
}
