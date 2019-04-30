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

  def getOrders = {
    Ok("ok")
  }

  def getOrder(id: Integer) = {
    Ok("ok")
  }
}
