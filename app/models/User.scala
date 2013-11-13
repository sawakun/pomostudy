package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import models.helpers._

case class User(email: String, name: String, password: String)
  
  object User {
    val simple = {
      get[String]("users.email") ~
        get[String]("users.name") ~
        get[String]("users.password") map {
          case email ~ name ~ password => User(email, name, password)
        }
    }

    def findById(id: Long): Option[User] = {
      DB.withConnection { implicit c =>
        SQL("select * from users where user_id = {id}").on(
            'id -> id
            ).as(User.simple.singleOpt)
      }
    }

    def findByEmail(email: String): Option[User] = {
      DB.withConnection { implicit c =>
        SQL("select * from users where email = {email}").on(
            'email -> email
            ).as(User.simple.singleOpt)
      }
    }

    def findAll: Seq[User] = {
      DB.withConnection { implicit c =>
        SQL("select * from users").as(User.simple *)
      }
    }

    def authenticate(email: String, password: String): Option[User] = { 
      implicit def String2ShaDigest(s: String): ShaDigest = new ShaDigest(s)
      DB.withConnection { implicit c =>
        SQL(
            """
            select * from users where
            email = {email} and password = {password}
            """
           ).on(
             'email -> email,
             'password -> password.digestString
             ).as(User.simple.singleOpt)
      }
    }

    def create(user: User): User = {
      implicit def String2ShaDigest(s: String): ShaDigest = new ShaDigest(s)
      DB.withConnection { implicit c =>
        SQL(
            """
            insert into users (
              email, name, password
              )
            values (
              {email}, {name}, {password}
              )
            """
           ).on(
             'email -> user.email,
             'name -> user.name,
             'password -> user.password.digestString
             ).executeUpdate()

           user
      }
    }
  }
