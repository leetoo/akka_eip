package org.michal.domain

import java.util.UUID

trait Identifiable {
  val ccid: String
}

case class CCReq(ccid: String) extends Identifiable{

}

case class CCItem(ccid: String, user: User) extends Identifiable

