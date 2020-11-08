package xyz.cofe.sparse

/**
  * Указатель на конец лексемы
 *
  * @tparam P Указатель
  */
trait Tok[P <: Pointer[_,_,_] ] {
  /**
    * Возвращает указатель на конец лексемы
    * @return Указатель
    */
  def end():P
}
