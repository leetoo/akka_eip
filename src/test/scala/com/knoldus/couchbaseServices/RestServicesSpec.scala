

package com.knoldus.couchbaseServices

import java.util.UUID

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.michal.domain.User
import org.michal.services.RestService
import org.scalatest.{Matchers, WordSpec}

class RestServicesSpec extends WordSpec with Matchers with ScalatestRouteTest with RestService {

  override val sc = _
  override val keyspace = _
  override val tableName = _

  val documentId = "user::" + UUID.randomUUID().toString
  val jsonObject = User("1", "mici", "lolo@zuru.com")
  create(jsonObject)
  "The service" should {

    "be able to insert data in the couchbase" in {
      Get("/create/name/mici/email/lolo@zuru.com") ~> sparkRoutes ~> check {
        responseAs[String].contains("Data is successfully persisted with id") shouldEqual true
      }
    }

    "to be able to retrieve data via N1Ql" in {
      Get("/retrieve/id/1") ~> sparkRoutes ~> check {
        responseAs[String].contains("lolo@zuru.com") shouldEqual true
      }
    }}


}
