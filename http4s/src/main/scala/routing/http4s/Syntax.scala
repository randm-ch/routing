package routing
package http4s

import cats.{Applicative, Defer}
import cats.data.OptionT
import org.{http4s => h}
import scala.annotation.tailrec

object syntax extends SyntaxCompat {
  implicit class Http4sMethodOps(val method: Method) extends AnyVal {
    def toHttp4s: h.Method = method match {
      case Method.GET => h.Method.GET
      case Method.POST => h.Method.POST
      case Method.PUT => h.Method.PUT
      case Method.DELETE => h.Method.DELETE
      case Method.PATCH => h.Method.PATCH
      case Method.OPTIONS => h.Method.OPTIONS
      case Method.HEAD => h.Method.HEAD
    }
  }

  implicit class Http4sReverseQueryOps(val query: ReverseQuery) extends AnyVal {
    def toHttp4s: h.Query = h.Query.fromVector(query)
  }

  protected def queryToHttp4s(query: ReverseQuery): h.Query = new Http4sReverseQueryOps(query).toHttp4s

  implicit class Http4sReverseUriOps(val uri: ReverseUri) extends AnyVal {
    def toHttp4s: h.Uri = uriToHttp4s(uri)
  }

  @tailrec
  private def tryRoutes[F[_]: Applicative](
    request: h.Request[F],
    handlers: List[Handled[h.Request[F] => F[h.Response[F]]]]
  ): OptionT[F, h.Response[F]] =
    handlers match {
      case Nil => OptionT.none[F, h.Response[F]]
      case handler :: rest =>
        handler.route.unapply(request) match {
          case Some(params) => OptionT.liftF(handler.handle(params)(request))
          case None => tryRoutes(request, rest)
        }
    }

  class MkHttpRoutes(val dummy: Boolean = false) extends AnyVal {
    def apply[F[_]: Applicative: Defer](handlers: Handled[h.Request[F] => F[h.Response[F]]]*): h.HttpRoutes[F] =
      h.HttpRoutes[F](tryRoutes(_, handlers.toList))
  }

  implicit class Http4sRouteObjectOps(val route: Route.type) extends AnyVal {
    def httpRoutes: MkHttpRoutes = new MkHttpRoutes
  }
}
