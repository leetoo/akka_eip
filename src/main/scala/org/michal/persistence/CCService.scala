package org.michal.persistence

import akka.serialization.SerializerWithStringManifest
import io.getquill.{CassandraAsyncContext, SnakeCase}

class CCService(cassandraContext: CassandraAsyncContext[SnakeCase], manifestSer: SerializerWithStringManifest)
  extends CCRepository with Serializable {

  override protected val ctx: CassandraAsyncContext[SnakeCase] = cassandraContext
  override val serializer: SerializerWithStringManifest = manifestSer

  def apply(cassandraContext: CassandraAsyncContext[SnakeCase], manifestSer: SerializerWithStringManifest): CCService =
    new CCService(cassandraContext, manifestSer)

}
