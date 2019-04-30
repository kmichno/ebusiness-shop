package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderedProductRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, productRepository: ProductRepository, orderRepository: OrderRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class OrderedProductTable(tag: Tag) extends Table[OrderedProduct](tag, "ordered_products") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def orderId = column[Long]("order_id")

    def productId = column[Long]("product_id")

    def * = (id, orderId, productId) <> ((OrderedProduct.apply _).tupled, OrderedProduct.unapply)
    def order = foreignKey("order_fk",orderId, orders)(_.id)
    def product = foreignKey("product_fk", productId, products)(_.id)
  }

  import orderRepository.OrderTable
  private val ordered_products = TableQuery[OrderedProductTable]
  private val orders = TableQuery[OrderTable]
  private val products = TableQuery[OrderTable]

  def create(orderId: Long, productId: Long): Future[OrderedProduct] = db.run {
    (ordered_products.map(p => (p.orderId, p.productId))
      returning ordered_products.map(_.id)
      into ((nameAge, id) => OrderedProduct(id, nameAge._1, nameAge._2))
    ) += (orderId, productId)
  }

  def list(): Future[Seq[OrderedProduct]] = db.run {
    ordered_products.result
  }
}
