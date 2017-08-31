package org.michal.domain

import java.util.UUID

trait Identifiable {
  val ccid: String
}

case class CCReq(ccid: String, size: Int) {

}

case class CCItem(ccid: String, id: String, name: String, email: String)

