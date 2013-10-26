package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(email: String, name: String, password: String)

  object User {
    val simple = {
      get[String]("user.email") ~
        get[String]("user.name") ~
        get[String]("user.password") map {
          case email ~ name ~ password => User(email, name, password)
        }
    }

    def findById(id: Long): Option[User] = {
      DB.withConnection { implicit c =>
        SQL("select * from user where user_id = {id}").on(
            'id -> id
            ).as(User.simple.singleOpt)
      }
    }

    def findByEmail(email: String): Option[User] = {
      DB.withConnection { implicit c =>
        SQL("select * from user where email = {email}").on(
            'email -> email
            ).as(User.simple.singleOpt)
      }
    }

    def findAll: Seq[User] = {
      DB.withConnection { implicit c =>
        SQL("select * from user").as(User.simple *)
      }
    }

    def authenticate(email: String, password: String): Option[User] = { 
      DB.withConnection { implicit c =>
        SQL(
            """
            select * from user where
            email = {email} and password = {password}
            """
           ).on(
             'email -> email,
             'password -> password
             ).as(User.simple.singleOpt)
      }
    }

    def create(user: User): User = {
      DB.withConnection { implicit c =>
        SQL(
            """
            insert into user (
              email, name, password
              )
            values (
              {email}, {name}, {password}
              )
            """
           ).on(
             'email -> user.email,
             'name -> user.name,
             'password -> user.password
             ).executeUpdate()

           user
      }
    }
  }
