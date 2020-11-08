package xyz.cofe.sel.cmpl.rt

import xyz.cofe.sel.types.Type
import xyz.cofe.stsl.ast.IdentifierAST

/**
 * Переменная аргумент, переданная через стек
 */
class StackedArgumentAST( val callStack: CallStack,
                          identifier:IdentifierAST,
                          val argumentTypeFn : ()=>Type
                        ) extends IdentifierAST(identifier.begin(), identifier.end(), identifier.tok ) {
  require(callStack!=null)
  require(identifier!=null)
  require(argumentTypeFn!=null)

  def argumentType:Type = argumentTypeFn()
  def argumentName:String = identifier.tok.name
  def value:Any = callStack.get(argumentName)

  override def toString: String = s"StackedArgumentAST ${tok}"
}

object StackedArgumentAST {
  def apply(
             callStack: CallStack,
             identifier: IdentifierAST,
             argumentType: Type
           ): StackedArgumentAST = {
    require(callStack!=null)
    require(identifier!=null)
    require(argumentType!=null)
    new StackedArgumentAST(callStack, identifier, ()=>argumentType)
  }
  def apply(
             callStack: CallStack,
             identifier: IdentifierAST,
             argumentTypeFn: ()=>Type
           ): StackedArgumentAST = {
    require(callStack!=null)
    require(identifier!=null)
    require(argumentTypeFn!=null)
    new StackedArgumentAST(callStack, identifier, argumentTypeFn)
  }
}
