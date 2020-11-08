package xyz.cofe.sparse

/**
  * Символы
  */
object Chars {
  /**
   * Создание грамматического правила для символа из предиката
   * @param cond предикат
   * @return грамматическое правило
   */
  def charTok( cond:Char=>Boolean ):GR[CharPointer,CToken] = {
    (ptr) => {
      if( ptr.eof() ){
        None
      }else{
        if( cond(ptr.lookup(0).get) ){
          Some( CToken(ptr,ptr.move(1)) )
        }else{
          None
        }
      }
    }
  }

  /**
   * Создание грамматического правила.
   * Правило проверяет что в текущей позиции начало текста совпадает с одним из указанных шаблонов
   * @param samples шаблоны
   * @param ignoreCase ингор регистра символов при сравнении
   * @param map функция создания лексемы
   * @tparam U тип лексемы
   * @return правило
   */
  def textTok[U <: CToken]( samples:Seq[String], ignoreCase:Boolean, map:(CharPointer,CharPointer)=>U ):GR[CharPointer,U] = {
    require(samples!=null)
    require(map!=null)
    val sortedSamples = samples.filter(_.length>0).sortBy(_.length).reverse
    val maxLen : Int = if( sortedSamples.isEmpty ){ 0 } else sortedSamples.head.length
    new GR[CharPointer,U] {
      override def apply(ptr: CharPointer): Option[U] = {
        if( ptr.eof() ){
          None
        }else{
          if( maxLen < 1 ) {
            None
          }else{
            val capturedText = ptr.text(maxLen)
            if( capturedText.length<1 ){
              None
            }else{
              val matchSeq = sortedSamples.map(
                ss => {
                  val trimCapText = if( capturedText.length > ss.length ){
                    capturedText.substring(0,ss.length)
                  } else {
                    capturedText
                  }

                  if( ignoreCase ){
                    if( ss.equalsIgnoreCase(trimCapText) ){
                      trimCapText
                    }else{
                      null
                    }
                  } else {
                    if( ss.equals(trimCapText) ){
                      trimCapText
                    }else{
                      null
                    }
                  }
                }
              ).filter( _ != null )

              if( matchSeq.isEmpty ){
                None
              }else{
                val matchText = matchSeq.head
                val endPtr = ptr.move(matchText.length)
                Some( map(ptr,endPtr) )
              }
            }
          }
        }
      }
    }
  }

  /**
   * Создание грамматического правила.
   * Правило проверяет что в текущей позиции начало текста совпадает с одним из указанных шаблонов
   * @param samples шаблоны
   * @param ignoreCase ингор регистра символов при сравнении
   * @return правило
   */
  def textTok( samples:Seq[String], ignoreCase:Boolean ):GR[CharPointer,CToken] =
    textTok(samples,ignoreCase,(begin,end)=>new CToken(begin,end))

  /**
   * Создание грамматического правила.
   * Правило проверяет что в текущей позиции начало текста совпадает с указаным шаблоном
   * @param sample шаблон
   * @param ignoreCase ингор регистра символов при сравнении
   * @param map функция создания лексемы
   * @tparam U тип лексемы
   * @return правило
   */
  def textTok[U <: CToken]( sample:String, ignoreCase:Boolean, map:(CharPointer,CharPointer)=>U ):GR[CharPointer,U] = {
    require(sample!=null)
    require(sample.length>0)
    require(map!=null)
    new GR[CharPointer,U] {
      override def apply(ptr: CharPointer): Option[U] = {
        if( ptr.eof() ){
          None
        }else{
          val lookText = ptr.text(sample.length)
          if( ignoreCase ){
            if( sample.equalsIgnoreCase(lookText) ){
              Some(map(ptr,ptr.move(sample.length)))
            }else{
              None
            }
          }else{
            if( sample.equals(lookText) ){
              Some(map(ptr,ptr.move(sample.length)))
            }else{
              None
            }
          }
        }
      }
    }
  }

  /**
   * Создание грамматического правила.
   * Правило проверяет что в текущей позиции начало текста совпадает с указаным шаблоном
   * @param sample шаблон
   * @param map функция создания лексемы
   * @tparam U тип лексемы
   * @return правило
   */
  def textTok[U <: CToken]( sample:String, map:(CharPointer,CharPointer)=>U ):GR[CharPointer,U] = textTok(sample,false,map)

  /**
   * Создание грамматического правила.
   * Правило проверяет что в текущей позиции начало текста совпадает с указаным шаблоном
   * @param sample шаблон
   * @return правило
   */
  def textTok( sample:String ):GR[CharPointer,CToken] = textTok(sample, {(begin,end)=>new CToken(begin,end)})

  /**
   * Захват captureSize символов, при условии что из перечисленых ни одное не совпало
   * @param captureSize кол-во захватыавемых символов
   * @param stopSeq условия исключения
   * @param map Конструктор результата
   * @param catched Совпавшее правило
   * @tparam U тип результата
   * @return правило
   */
  def exclude[U <: CToken,C <: CToken](
                                        captureSize:Int,
                                        stopSeq:Seq[GR[CharPointer,C]],
                                        map:(CharPointer,CharPointer)=>U,
                                        catched:C=>Any
                            ):GR[CharPointer,U] = new GR[CharPointer,U] {
    require(captureSize>0)
    require(stopSeq.nonEmpty)
    require(map!=null)
    override def apply(ptr: CharPointer): Option[U] = {
      if(ptr.eof()){
        None
      }else{
        var hasMatch = false
        var matched : Option[C] = None
        stopSeq.foreach(gexl => {
          if( !hasMatch ){
            val m = gexl(ptr)
            if( m.nonEmpty ){
              hasMatch = true
              matched = m
            }
          }
        })
        if( hasMatch ){
          if( catched!=null ){
            catched(matched.get)
          }
          None
        }else{
          Some(map(ptr,ptr.move(captureSize)))
        }
      }
    }
  }

  /**
   * Захват captureSize символов, при условии что из перечисленых ни одное не совпало
   * @param captureSize кол-во захватыавемых символов
   * @param stopSeq условия исключения
   * @param map Конструктор результата
   * @tparam U тип результата
   * @return правило
   */
  def exclude[U <: CToken,C <: CToken]
  (
    captureSize:Int,
    stopSeq:Seq[GR[CharPointer,C]],
    map:(CharPointer,CharPointer)=>U
  ):GR[CharPointer,U] = exclude(captureSize, stopSeq, map, null)

  /**
   * Захват captureSize символов, при условии что из перечисленых ни одное не совпало
   * @param captureSize кол-во захватыавемых символов
   * @param stopSeq условия исключения
   * @return правило
   */
  def exclude[C <: CToken]
  (
    captureSize:Int,
    stopSeq:Seq[GR[CharPointer,C]],
  ):GR[CharPointer,CToken] = exclude(captureSize, stopSeq, (begin,end)=>new CToken(begin,end), null)

  /**
   * Буква
   */
  val Letter: GR[CharPointer, CToken] = charTok( Character.isLetter );

  /**
   * Буква или цифра
   */
  val LetterOrDigit: GR[CharPointer, CToken] = charTok( Character.isLetterOrDigit );

  /**
   * Цифра
   */
  val Digit: GR[CharPointer, CToken] = charTok( Character.isDigit );

  /**
   * Точка
   */
  val Dot: GR[CharPointer, CToken] = charTok(c => c=='.' );

  /**
   * Пробел
   */
  val Whitespace: GR[CharPointer, CToken] = charTok( Character.isWhitespace );
}
