package xyz.cofe.sparse

/**
 * Итератор по лексемам
 * @param ptr указатель
 * @param parsers парсеры
 * @param none лексема соответ отсуствию (next() : T)
 * @tparam P Указатель
 * @tparam T Лексема
 */
class Tokenizer[P <: Pointer[_,_,_],T <: Tok[P]](val ptr:P, val parsers:Seq[GR[P,_ <: T]], val none:T) extends Iterable[T] {
  /**
   * Итератор
   * @return итератор
   */
  override def iterator: Iterator[T] = new Iterator[T] {
    def fetch(p:P):(P,Option[T]) = {
      var r:Option[T] = None
      var np = p
      parsers.foreach( prs=>{
        if( r.isEmpty ){
          r = prs(p)
          if( r.nonEmpty ){
            np = r.get.end()
          }
        }
      })
      (np,r)
    }

    /**
     * Текущее значение
     */
    var fetched = fetch(ptr)

    /**
     * Проверка на существование следующей лексемы
     * @return true - есть лексемма / false - нет
     */
    override def hasNext: Boolean = fetched._2.nonEmpty

    /**
     * Получение следующей лексемы
     * @return лексема
     */
    override def next(): T = {
      if( fetched._2.nonEmpty ){
        val r = fetched._2.get
        fetched = fetch(fetched._1)
        r
      }else{
        none
      }
    }
  }
}

/**
 * Создание итератора по токенам
 */
object Tokenizer {
  /**
   * Создание лексера
   * @param ptr указатель на входные символы
   * @param parsers парсинг лексем
   * @param none нулевая лексема
   * @tparam P тип указателя
   * @tparam T тип лексемы
   * @return лексер
   */
  def tokens[P <: Pointer[_,_,_],T <: Tok[P]]( ptr:P, parsers:Seq[GR[P,T]], none:T ):Tokenizer[P,T] = {
    require(ptr!=null)
    require(parsers!=null)
    new Tokenizer[P,T](ptr,parsers,none)
  }

  /**
   * Создание лексера для текста
   * @param offset смещение в тексте
   * @param text текст
   * @param parsers парсинг лексем
   * @param none нулевая лексема
   * @tparam T тип лексемы
   * @return лексер
   */
  def tokens[T <: Tok[CharPointer]](offset: Int, text:String, parsers:Seq[GR[CharPointer,T]], none:T ):Tokenizer[CharPointer,T] = {
    require(offset>=0)
    require(text!=null)
    require(parsers!=null)
    val ptr : CharPointer = new BasicCharPointer(offset,text)
    new Tokenizer[CharPointer,T](ptr,parsers,none)
  }

//  /**
//   * Создание лексера для текста
//   * @param text текст
//   * @param parsers парсинг лексем
//   * @param none нулевая лексема
//   * @return лексер
//   */
//  def tokens[T <: CToken](text:String, parsers:Seq[GR[CharPointer,_ <: T]], none:_ <: T ):Tokenizer[CharPointer,T] = {
//    require(text!=null)
//    require(parsers!=null)
//    //tokens(0,text,parsers,none)
//
//    val ptr : CharPointer = new BasicCharPointer(0,text)
//    new Tokenizer(ptr,parsers,none)
//  }

  def tokens(text:String, parsers:Seq[GR[CharPointer,_ <: CToken]], none: CToken):Tokenizer[CharPointer,CToken] = {
    val ptr : CharPointer = new BasicCharPointer(0,text)
    new Tokenizer[CharPointer,CToken](ptr, parsers, none)
  }
}
