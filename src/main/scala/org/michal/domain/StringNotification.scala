package org.michal.domain

import com.michal.domain.proto.schema.StringNotificationProto
import org.michal.Notification

case class StringNotification(notification: String) extends Notification(StringNotification.msgType) {
  override def toProto = StringNotificationProto(msgType, notification)
}

object StringNotification {
  val msgType = "StringNotification"
}
