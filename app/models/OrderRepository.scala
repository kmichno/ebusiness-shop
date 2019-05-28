package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, personRepository: PersonRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class OrderTable(tag: Tag) extends Table[Order](tag, "orders") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def order_date = column[String]("order_date")

    def userId = column[Int]("userId")

    def * = (id, order_date, userId) <> ((Order.apply _).tupled, Order.unapply)
//    def user = foreignKey("user_fk",userId, users)(_.id)
  }

  import personRepository.PeopleTable
  private val orders = TableQuery[OrderTable]
  private val users = TableQuery[PeopleTable]

  def create( order_date: String, userId: Int): Future[Order] = db.run {
    (orders.map(p => (p.order_date, p.userId))
      returning orders.map(_.id)
      into ((nameAge, id) => Order(id, nameAge._1, nameAge._2))
    ) += (order_date, userId)
  }

  def list(): Future[Seq[Order]] = db.run {
    orders.result
  }
}
