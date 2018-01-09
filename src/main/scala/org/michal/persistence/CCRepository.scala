package org.michal.persistence

import java.util.UUID

import akka.serialization.SerializerWithStringManifest
import com.typesafe.scalalogging.LazyLogging
import io.getquill.{CassandraAsyncContext, SnakeCase}
import org.michal.{Msg, Payl}

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object CCRepository {
  class NoCheckedItemsException extends Exception
}

trait CCRepository extends LazyLogging {

  val serializer: SerializerWithStringManifest

  protected val ctx: CassandraAsyncContext[SnakeCase]
  import ctx._

  def create(msg: List[Msg[_ <: Payl]]): String = {
    val cid = UUID.randomUUID().toString
    val rows: List[CCRow] = msg.map(el => CCRow(el.id, cid, serializer.manifest(el), serializer.toBinary(el)))

    ctx.run(quote(liftQuery(rows).foreach(e => querySchema[CCRow]("cctable").insert(e))))

    cid
  }

  def retrieveC(ccid: String): Future[List[Msg[_ <: Payl]]] = {
    (for {
      qr <- ctx.run(quote(querySchema[CCRow]("cctable").filter(c => c.ccid == lift(ccid))))
    } yield qr.map(r => serializer.fromBinary(r.msg, r.manifest).asInstanceOf[Msg[Payl]])).
      recoverWith {
        case e =>
          logger.error(s"Claiming data failed for ccid: $ccid", e)
          Future.failed(e)
      }
  }

  private case class CCRow(id: String, ccid: String, manifest: String, msg: Array[Byte])

}


