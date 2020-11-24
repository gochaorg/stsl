package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.{Fun, Type}

/**
 * Передача аргумента в функцию
 * @param from исходный тип
 * @param to целевой тип
 * @param ascending путь ковариантного преобразования от from (child) к to (parent)
 * @param conversion имплицитное преобразование
 */
class CallPassing(val from:Type
                  , val to:Type
                  , val ascending:List[List[Type]] = List()
                  , val conversion:Option[Fun]=None
                 ) {
  /**
   * Подсчет цены вызова
   * @param chance вариант вызова
   * @return цена передачи аргмента в функцию
   */
  def cost( chance:CallCase ):Int = {
    if( from==to ){
      0
    }else{
      if( conversion.isDefined ){
        100
      }else {
        ascending.map( _.length ).min
      }
    }
  }
  override def toString: String = {
    if( from==to ){
      "as-is"
    }else{
      if( conversion.isDefined ){
        s"conversion ${conversion.get}"
      }else{
        if( ascending.isEmpty ){
          "bug"
        }else if( ascending.size==1 ){
          "co-variant "+ascending.head
        }else{
          ascending.map( apath => apath.toString ).map(apath => "co-variant "+apath).reduce((a,b) => a+"\n"+b)
        }
      }
    }
  }
}
