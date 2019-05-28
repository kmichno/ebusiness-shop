package controllers

import javax.inject._

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class OrderController @Inject()(repo: OrderRepository,
                                  cc: MessagesControllerComponents
                                )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  def takeOrder = {
    Ok("ok")
  }

  def getOrders = Action.async { implicit request =>
    repo.list().map { category =>
      Ok(Json.toJson(category))
    }
  }

  val orderForm: Form[CreateOrderForm] = Form {
    mapping(
      "order_date" -> nonEmptyText,
      "userId" -> number.verifying(min(0), max(140))
    )(CreateOrderForm.apply)(CreateOrderForm.unapply)
  }

  def addOrder = Action.async { implicit request =>
    orderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok("df"))
      },
      order => {
        repo.create(order.order_date, order.userId).map { _ =>
          Redirect(routes.ProductController.addingProduct()).flashing("success" -> "user.created")
        }
      }
    )
  }

  def getOrder(id: Integer) = {
    Ok("ok")
  }
}

case class CreateOrderForm(order_date: String, userId: Int)