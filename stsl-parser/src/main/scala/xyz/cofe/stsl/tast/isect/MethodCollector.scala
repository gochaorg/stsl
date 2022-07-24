package xyz.cofe.stsl.tast.isect

import xyz.cofe.stsl.types.{Fn, Fun, Funs, Methods, MutableFuns}

/**
 * Коллектор методов
 *
 * @param common общая проекция методов
 * @param joinCount кол-во объеденных классов
 */
case class MethodCollector( common:Map[String,Funs] = Map(),
                            joinCount:Int=0
                          ) {
  def join( methods:Methods ):MethodCollector = {
    joinCount match {
      case 0 => joinInitial(methods)
      case _ => joinContinue(methods)
    }
  }
  
  private def joinInitial( methods:Methods ):MethodCollector = {
    copy(joinCount=joinCount+1, common=methods.funs)
  }
  
  //noinspection SimplifyBooleanMatch
  private def joinContinue( methods:Methods ):MethodCollector = {
    val y: Map[String, Seq[Fun]] = common.map { case(cmName,cmFuns) =>
      val x: Seq[Fun] = methods.get(cmName) match {
        case Some(jmFuns) =>
          val joinedMeth: Seq[Fun] = cmFuns.flatMap { cmFun =>
            val joinedFun: Option[Fun] = jmFuns.map { jmFun => {
              val retType = (jmFun.returns.assignable(cmFun.returns), cmFun.returns.assignable(jmFun.returns)) match {
                case (false, false) => None
                case (true, true) => Some(cmFun.returns)
                case (true, false) => Some(jmFun.returns)
                case (false, true) => Some(cmFun.returns)
              }
              
              (cmFun.assignable(jmFun) match {
                case true => jmFun.assignable(cmFun) match {
                  case true => Some(cmFun)
                  case false => Some(jmFun)
                }
                case false => jmFun.assignable(cmFun) match {
                  case true => Some(cmFun)
                  case false => None
                }
              }).flatMap( f => retType.map( rt =>
                Fn( f.generics, f.parameters, rt )
              ))
            }
            }.find {
              _.isDefined
            }.flatten
            joinedFun
          }
          joinedMeth
        case None =>
          Seq()
      }
      cmName -> x
    }
    val z = y.map { case (name,funs) =>
      name -> funs.foldLeft(new MutableFuns())( (mfuns,f) => {mfuns.append(f); mfuns} ).asInstanceOf[Funs]
    }.filter{ case (_,funs) => funs.nonEmpty }
    copy(
      joinCount=joinCount+1,
      common = z
    )
  }
}
