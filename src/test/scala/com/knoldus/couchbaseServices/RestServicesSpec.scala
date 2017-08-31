

package com.knoldus.couchbaseServices

import java.util.UUID

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.michal.boot.StartApp.sc
import org.michal.domain.User
import org.michal.factory.Context
import org.michal.services.{DataAccessService, RestService}
import org.scalatest.{Matchers, WordSpec}

class RestServicesSpec extends WordSpec with Matchers with ScalatestRouteTest with RestService {

  val sc = Context.sc
  val keyspace = Context.keyspace
  val tableName = Context.tableName
  val dao: DataAccessService = DataAccessService(sc, keyspace, tableName)

  val documentId = "user::" + UUID.randomUUID().toString
  val jsonObject = User(documentId, "mici", "lolo@zuru.com")
  dao.createUser(jsonObject)
  "The service" should {

    "be able to insert data in the couchbase" in {
      Get("/create/name/mici/email/lolo@zuru.com") ~> sparkRoutes ~> check {
        val str = responseAs[String]
        str.contains("Data is successfully persisted with id") shouldEqual true
      }
    }

    "to be able to retrieve data via N1Ql" in {
      Get("/retrieve/id/1") ~> sparkRoutes ~> check {
        val str = responseAs[String]
        str.contains("lolo@zuru.com") shouldEqual true
      }
    }}


}
