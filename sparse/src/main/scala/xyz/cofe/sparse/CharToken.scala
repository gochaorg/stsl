package xyz.cofe.sparse

/**
  * Символьная лексема
 *
  * @param beginPointer начало
  * @param endPointer конец
  */
class CharToken
(
  val beginPointer:CharPointer,
  val endPointer: CharPointer
)
  extends Tok[CharPointer]
{
  /**
    * Возвращает указатель на конец лексемы
    * @return Указатель
    */
  override def end(): CharPointer = this.endPointer

  /**
    * Возвращает начало лексемы
    * @return начало лексемы
    */
  def begin():CharPointer = beginPointer

  /**
    * Возвращает текст лексемы
    * @return текст
    */
  def text():String = beginPointer.text( Math.abs(end.pointer - begin.pointer) )

  override def toString: String = this.getClass().getSimpleName()+" text=\""+text()+"\""
}
