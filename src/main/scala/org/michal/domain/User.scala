package org.michal.domain

import com.michal.domain.proto.schema.UserProto
import org.michal.Payl

case class User(id: String, name: String, email: String) {
  def toProto: UserProto = UserProto(id = id, name = name, email = email)
}

