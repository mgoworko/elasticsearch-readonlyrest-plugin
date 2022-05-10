/*
 *    This file is part of ReadonlyREST.
 *
 *    ReadonlyREST is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    ReadonlyREST is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with ReadonlyREST.  If not, see http://www.gnu.org/licenses/
 */
package tech.beshu.ror.es.actions.rrauthmock

import org.apache.logging.log4j.scala.Logging
import org.elasticsearch.action.ActionResponse
import org.elasticsearch.common.io.stream.StreamOutput
import org.elasticsearch.xcontent.{ToXContent, ToXContentObject, XContentBuilder}
import tech.beshu.ror.api.AuthMockApi
import tech.beshu.ror.api.AuthMockApi.AuthMockResponse

class RRAuthMockResponse(response: AuthMockApi.AuthMockResponse)
  extends ActionResponse with ToXContentObject with Logging {

  override def toXContent(builder: XContentBuilder, params: ToXContent.Params): XContentBuilder = {
    response match {
      case AuthMockApi.Success(message) => addResponseJson(builder, "ok", message)
      case AuthMockApi.Failure(message) => addResponseJson(builder, "ko", message)
    }
    builder
  }

  override def writeTo(out: StreamOutput): Unit = ()

  private def addResponseJson(builder: XContentBuilder, status: String, message: String): Unit = {
    builder.startObject
    builder.field("status", status)
    builder.field("message", message)
    builder.endObject
  }
}
object RRAuthMockResponse extends Logging {
  def apply(response: Either[Throwable, AuthMockApi.AuthMockResponse]): RRAuthMockResponse = {
    response match {
      case Left(ex) =>
        logger.error("RRAdmin internal error", ex)
        new RRAuthMockResponse(AuthMockResponse.internalError)
      case Right(value) =>
        new RRAuthMockResponse(value)

    }
  }
}
