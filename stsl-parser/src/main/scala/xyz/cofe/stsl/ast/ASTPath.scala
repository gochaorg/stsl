package xyz.cofe.stsl.ast

/**
 * Путь в AST дереве
 * @param reversePath реверсивный путь (от узла к корню)
 */
class ASTPath( val reversePath:List[AST] = List() ) {
  require(reversePath!=null)

  /**
   * Путь в дереве от корня к узлу
   */
  lazy val path:List[AST] = reversePath.reverse

  /**
   * Последний узел в пути
   */
  lazy val last:AST = reversePath.head

  /**
   * Проверка что путь пустой
   */
  lazy val empty:Boolean = reversePath.isEmpty

  /**
   * Создание нового пути, добавляет узел к концу пути
   * @param ast узел
   * @return новый путь с добавленным узлом
   */
  def +(ast: AST):ASTPath = {
    require(ast!=null)
    new ASTPath(ast :: reversePath)
  }
}

/**
 * Создание пути в дереве
 */
object ASTPath {
  /**
   * Указывает начальный узел в пути
   * @param ast узел
   * @return путь
   */
  def apply(ast: AST):ASTPath = {
    require(ast!=null)
    new ASTPath(List(ast))
  }
}