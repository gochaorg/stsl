package xyz.cofe.stsl.ast

/**
 * Дамп AST дерева
 */
object ASTDump {
  /**
   * Создание дампа
   * @param out куда будет выведено содержание дерева
   * @param ast дерево
   * @param level уровень узла дерева
   */
  def dump(out: Appendable, ast: AST, level:Int):Unit = {
    require(out!=null)
    require(ast!=null)

    if( level>0 )out.append("-|"*level).append(" ")
    out.append(ast.toString).append("\n")

    ast.children().foreach( a => {
      dump(out, a, level+1)
    })
  }

  /**
   * Создание дампа
   * @param out куда будет выведено содержание дерева
   * @param ast дерево
   */
  def dump(out: Appendable, ast: AST):Unit = {
    require(out!=null)
    require(ast!=null)
    dump(out, ast, 0)
  }

  /**
   * Создание дампа
   * @param ast дерево
   */
  def dump(ast: AST):Unit = {
    require(ast!=null)
    dump(System.out, ast, 0)
  }
}
