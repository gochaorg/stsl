package xyz.cofe.stsl.types

object syntax {
  /**
   * Свертка ошибок в одну
   * @param errors ошибки
   */
  implicit class FoldErr( errors:Seq[Option[String]] ){
    /**
     * Свертка ошибок в одну
     * @return ошибка
     */
    def foldErr:Option[String] = {
      errors.foldLeft( None:Option[String] )( (a,b) => (a,b) match {
        case (Some(x),Some(y)) => Some( x+"\n"+y )
        case (None,Some(y)) => Some( y )
        case (Some(x),None) => Some( x )
        case _ => a
      })
    }
  }
}
