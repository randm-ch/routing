package org.http4s
package routing
package macros

import scala.reflect.macros.blackbox.Context

object ApplyNested {
  def impl(c: Context)(@unused params: c.Tree*): c.Tree = {
    import c.universe._

    c.macroApplication match {
      case Apply(Select(x, TermName(method)), args) =>
        def res(xs: List[c.Tree]): c.Tree = Apply(Select(x, TermName(s"${method}Raw")), xs)

        args match {
          case Nil => res(List(q"()"))
          case h :: t => res(List(t.reverse.foldRight(q"((), $h)")((x, acc) => q"($acc, $x)")))
        }
      case t => t
    }
  }
}
