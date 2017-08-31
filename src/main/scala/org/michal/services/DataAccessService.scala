package org.michal.services

import akka.actor.ActorSystem
import org.apache.spark.SparkContext
import org.michal.domain.CCItem
import org.michal.factory.DatabaseAccess

case class DataAccessService(override val sc: SparkContext, override val keyspace: String, override val tableName: String)(implicit val system: ActorSystem) extends DatabaseAccess {

}
