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

class ProductController @Inject()(repo: ProductRepository,
                                  cc: MessagesControllerComponents
                                )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "price" -> number.verifying(min(0), max(140))
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  def addingProduct = Action { implicit request =>
    Ok(views.html.indexProduct(productForm))
  }

  def addProduct = Action.async { implicit request =>
    productForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.indexProduct(errorForm)))
      },
      person => {
        repo.create("sf", 34, "add", 1).map { _ =>
          Redirect(routes.ProductController.addingProduct()).flashing("success" -> "user.created")
        }
      }
    )
  }

  def getProducts = Action.async { implicit request =>
    repo.list().map { people =>
      Ok(Json.toJson(people))
    }
  }

  def getProduct(id: Integer) = Action.async { implicit request =>
    repo.list().map { people =>
      Ok(Json.toJson(people.filter(_.id == id)))
    }
  }

  def getProductsForCategory(id: Integer) = {
    Ok("ok")
  }
}

case class CreateProductForm(name: String, price: Int)
