package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._
import controllers._

object Application extends Controller with Secured {
  // ログインフォーム
  val loginForm = Form(
      tuple(
        "email" -> nonEmptyText,
        "password" -> nonEmptyText
        ) verifying ("Invalid email or password", result => result match {
          case (email, password) => User.authenticate(email, password).isDefined
          })
      )

    // トップページ
    def index = IsAuthenticated { email => _ =>
      Ok(views.html.index("Your new application is ready."))
    }

  // ログインページ
  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  // ユーザ認証
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
        formWithErrors => BadRequest(html.login(formWithErrors)),
        user => Redirect(routes.Application.index).withSession("email" -> user._1)
        )
  }

  // ログアウト
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
        "success" -> "You've been logged out"
        )
  }

  // 登録フォーム
  val signupForm = Form(
      tuple(
        "email" -> nonEmptyText.verifying(
          "This email address is already registered.",
          email => User.findByEmail(email).isEmpty
          ),
        "name" -> nonEmptyText,
        "password" -> tuple(
          "main" -> nonEmptyText,
          "confirm" -> nonEmptyText
          ).verifying(
            "Password is not match.",
            password => password._1 == password._2
            )
        )
      )

      // ユーザ登録ページ
      def signup = Action {
        Ok(html.signup(signupForm))
      }

  // ユーザ登録
  def register = Action { implicit request =>
    signupForm.bindFromRequest.fold(
        errors => BadRequest(html.signup(errors)),
        form => {
        val user = User(form._1, form._2, form._3._1)
        User.create(user)
        Ok(html.registered(user))
        }
        )
  }
}
