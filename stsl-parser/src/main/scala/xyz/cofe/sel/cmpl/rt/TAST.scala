package xyz.cofe.sel.cmpl.rt

import java.util.function.Supplier

import xyz.cofe.sel.types.Type
import xyz.cofe.stsl.ast.AST

/**
 * Типизированый узел дерева
 * @param ast исходный узел дерева
 * @param supplierType Тип значения
 * @param supplier "Поставщик" значения
 * @param children Дочерние узлы
 */
class TAST (
             val ast:AST,
             val supplierType:Type,
             val supplier:Supplier[Any],
             val children:List[TAST] = List()
           ) {
  require(ast!=null)
  require(supplierType!=null)
  require(supplier!=null)
  require(children!=null)
}

object TAST {
  def apply(ast: AST, supplierType: Type, supplier: Supplier[Any], children: List[TAST]): TAST = new TAST(ast, supplierType, supplier, children)
  def apply(ast: AST, supplierType: Type, supplier: Supplier[Any]): TAST = new TAST(ast, supplierType, supplier)
}