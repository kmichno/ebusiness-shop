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

class CategoryController @Inject()(repo: CategoryRepository, cc: MessagesControllerComponents
                                )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val categoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }

  def index = Action { implicit request =>
    Ok("gh")
  }

  def getCategories = Action.async { implicit request =>
    repo.list().map { category =>
      Ok(Json.toJson(category))
    }
  }

  def add = Action.async { implicit request =>
    categoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          Ok("df")
        )
      },
      // There were no errors in the from, so create the person.
      category => {
        repo.create(category.name, category.description).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.PersonController.index).flashing("success" -> "category.created")
        }
      }
    )
  }


  def getPerson(id: Integer) = Action.async { implicit request =>
    repo.list().map { people =>
      Ok(Json.toJson(people.filter(_.id == id)))
    }
  }
}

case class CreateCategoryForm(name: String, description: String)
