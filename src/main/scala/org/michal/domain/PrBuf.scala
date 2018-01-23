package org.michal.domain

import com.google.protobuf.ByteString

trait PrBuf {
  def toByteArray: Array[Byte]
  def toByteString: ByteString
}
