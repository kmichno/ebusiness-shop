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

class PersonController @Inject()(repo: PersonRepository,
                                  cc: MessagesControllerComponents
                                )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val personForm: Form[CreatePersonForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "age" -> number.verifying(min(0), max(140)),
      "address" -> nonEmptyText,
    )(CreatePersonForm.apply)(CreatePersonForm.unapply)
  }

  def index = Action { implicit request =>
    Ok(views.html.index(personForm))
  }

  def addUser = Action.async { implicit request =>
    personForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.index(errorForm)))
      },
      person => {
        repo.create(person.name, person.age, person.address).map { _ =>
          Redirect(routes.PersonController.index).flashing("success" -> "user.created")
        }
      }
    )
  }

  def getPersons = Action.async { implicit request =>
    repo.list().map { people =>
      Ok(Json.toJson(people))
    }
  }

  def getPerson(id: Integer) = Action.async { implicit request =>
    repo.list().map { people =>
      Ok(Json.toJson(people.filter(_.id == id)))
    }
  }
}

case class CreatePersonForm(name: String, age: Int, address: String)
