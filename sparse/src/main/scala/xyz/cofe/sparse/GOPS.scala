package xyz.cofe.sparse

/**
  * Грамматические правила
  */
object GOPS {
  private def rp2grSeq[P <: Pointer[_,_,_],U <: Tok[P]]( g:Rp[P,U] ):GR[P, Tok[P] with Seq[U]] = (ptr) => {
    val o2 = g.parse(ptr)
    if( o2.isEmpty ){
      None
    }else{
      Some(new Tok[P] with Seq[U] {
        /**
         * Возвращает указатель на конец лексемы
         *
         * @return Указатель
         */
        override def end(): P = o2.get._2
        override def apply(i: Int): U = o2.get._3.apply(i)
        override def length: Int = o2.get._3.length
        override def iterator: Iterator[U] = o2.get._3.iterator
      })
    }
  }

  /**
    * Грамматическая конструкция
    * @tparam P Тип указателя
    * @tparam T Тип токена
    */
  trait GOP[P <: Pointer[_,_,_],T <: Tok[P]] {
  }

  /**
    * Последоваетльность грамматических конструкций из двух t1, t2
    * @tparam P  Тип указателя
    * @tparam T1 Тип первго токена
    * @tparam T2 Тип второго токена
    */
  trait Sq2[P <: Pointer[_,_,_],T1 <: Tok[P], T2 <: Tok[P]] {
    val t1:GR[P,T1]
    val t2:GR[P,T2]
    def +[U <: Tok[P]]( gop:GR[P,U] ):Sq3[P,T1,T2,U] = {
      val self = this
      new Sq3[P,T1, T2, U] {
        override val prev: Sq2[P, T1, T2] = self
        override val t3: GR[P, U] = gop
      }
    }
    def +[U <: Tok[P]]( g:Rp[P,U] ):Sq3[P,T1,T2,Tok[P] with Seq[U]] = {
      def self = this
      new Sq3[P,T1,T2,Tok[P] with Seq[U]] {
        override val prev: Sq2[P, T1, T2] = self
        override val t3: GR[P, Tok[P] with Seq[U]] = rp2grSeq(g)
      }
    }
    def ==>[U <: Tok[P]](f: (T1, T2) => U): GR[P, U] = {
      val self = this
      new GR[P, U] {
        override def apply(ptr: P): Option[U] = {
          var res : Option[U] = None
          val v1 = self.t1.apply(ptr)
          if( v1.nonEmpty ) {
            val v2 = self.t2.apply(v1.get.end())
            if (v2.nonEmpty) {
              res = Some( f(v1.get, v2.get) )
            }
          }
          res
        }
      }
    }
  }

  /**
    * Последоваетльность грамматических конструкций из трех t1, t2, t3
    * @tparam P  Тип указателя
    * @tparam T1 Тип первго токена
    * @tparam T2 Тип второго токена
    * @tparam T3 Тип третьего токена
    */
  trait Sq3[P <: Pointer[_,_,_],T1 <: Tok[P], T2 <: Tok[P], T3 <: Tok[P]] {
    val prev:Sq2[P,T1, T2]
    lazy val t1:GR[P,T1] = prev.t1
    lazy val t2:GR[P,T2] = prev.t2
    val t3:GR[P,T3]
    def +[U <: Tok[P]]( gop:GR[P,U] ):Sq4[P,T1,T2,T3,U] = {
      val self = this
      new Sq4[P,T1,T2,T3,U]{
        override val prev: Sq3[P, T1, T2, T3] = self
        override val t4: GR[P, U] = gop
      }
    }
    def +[U <: Tok[P]]( g:Rp[P,U] ):Sq4[P,T1,T2,T3,Tok[P] with Seq[U]] = {
      val self = this
      new Sq4[P,T1,T2,T3,Tok[P] with Seq[U]] {
        override val prev: Sq3[P, T1, T2, T3] = self
        override val t4: GR[P, Tok[P] with Seq[U]] = rp2grSeq(g)
      }
    }
    def ==>[U <: Tok[P]](f: (T1, T2, T3) => U): GR[P, U] = {
      val self = this
      new GR[P, U] {
        override def apply(ptr: P): Option[U] = {
          var res : Option[U] = None
          val v1 = self.t1.apply(ptr)
          if( v1.nonEmpty ) {
            val v2 = self.t2.apply(v1.get.end())
            if (v2.nonEmpty) {
              val v3 = self.t3.apply(v2.get.end())
              if( v3.nonEmpty ){
                res = Some( f(v1.get, v2.get, v3.get) )
              }
            }
          }
          res
        }
      }
    }
  }

  /**
    * Последоваетльность грамматических конструкций из четырех t1, t2, t3, t4
    * @tparam P  Тип указателя
    * @tparam T1 Тип первго токена
    * @tparam T2 Тип второго токена
    * @tparam T3 Тип третьего токена
    * @tparam T4 Тип 4-го токена
    */
  trait Sq4[P <: Pointer[_,_,_],T1 <: Tok[P], T2 <: Tok[P], T3 <: Tok[P], T4 <: Tok[P]] {
    val prev:Sq3[P,T1, T2, T3]
    lazy val t1:GR[P,T1] = prev.t1
    lazy val t2:GR[P,T2] = prev.t2
    lazy val t3:GR[P,T3] = prev.t3
    val t4:GR[P,T4]
    def +[U <: Tok[P]]( gop:GR[P,U]):Sq5[P,T1,T2,T3,T4,U] = {
      val self = this
      new Sq5[P,T1,T2,T3,T4,U]{
        override val prev: Sq4[P, T1, T2, T3, T4] = self
        override val t5: GR[P, U] = gop
      }
    }
    def +[U <: Tok[P]]( g:Rp[P,U] ):Sq5[P,T1,T2,T3,T4,Tok[P] with Seq[U]] = {
      val self = this
      new Sq5[P,T1,T2,T3,T4,Tok[P] with Seq[U]] {
        override val prev: Sq4[P, T1, T2, T3, T4] = self
        override val t5: GR[P, Tok[P] with Seq[U]] = rp2grSeq(g)
      }
    }
    def ==>[U <: Tok[P]](f: (T1, T2, T3, T4) => U): GR[P, U] = {
      val self = this
      new GR[P, U] {
        override def apply(ptr: P): Option[U] = {
          var res : Option[U] = None
          val v1 = self.t1.apply(ptr)
          if( v1.nonEmpty ) {
            val v2 = self.t2.apply(v1.get.end())
            if (v2.nonEmpty) {
              val v3 = self.t3.apply(v2.get.end())
              if( v3.nonEmpty ){
                val v4 = self.t4.apply(v3.get.end())
                if( v4.nonEmpty ) {
                  res = Some(f(v1.get, v2.get, v3.get, v4.get))
                }
              }
            }
          }
          res
        }
      }
    }
  }

  /**
    * Последоваетльность грамматических конструкций из пяти конструкций t1, t2, t3, t4, t5
    * @tparam P  Тип указателя
    * @tparam T1 Тип первго токена
    * @tparam T2 Тип второго токена
    * @tparam T3 Тип третьего токена
    * @tparam T4 Тип 4-го токена
    * @tparam T5 Тип 5-го токена
    */
  trait Sq5[
      P <: Pointer[_,_,_],
      T1 <: Tok[P], T2 <: Tok[P],
      T3 <: Tok[P], T4 <: Tok[P],
      T5 <: Tok[P]
  ] {
    val prev:Sq4[P,T1, T2, T3, T4]
    lazy val t1:GR[P,T1] = prev.t1
    lazy val t2:GR[P,T2] = prev.t2
    lazy val t3:GR[P,T3] = prev.t3
    lazy val t4:GR[P,T4] = prev.t4
    val t5:GR[P,T5]
    def +[U <: Tok[P]]( gop:GR[P,U]):Sq6[P,T1,T2,T3,T4,T5,U] = {
      val self = this
      new Sq6[P,T1,T2,T3,T4,T5,U]{
        override val prev: Sq5[P, T1, T2, T3, T4, T5] = self
        override val t6: GR[P, U] = gop
      }
    }
    def +[U <: Tok[P]]( g:Rp[P,U] ):Sq6[P,T1,T2,T3,T4,T5,Tok[P] with Seq[U]] = {
      val self = this
      new Sq6[P,T1,T2,T3,T4,T5,Tok[P] with Seq[U]] {
        override val prev: Sq5[P, T1,T2,T3,T4,T5] = self
        override val t6: GR[P, Tok[P] with Seq[U]] = rp2grSeq(g)
      }
    }
    def ==>[U <: Tok[P]](f: (T1, T2, T3, T4, T5) => U): GR[P, U] = {
      val self = this
      new GR[P, U] {
        override def apply(ptr: P): Option[U] = {
          var res : Option[U] = None
          val v1 = self.t1.apply(ptr)
          if( v1.nonEmpty ) {
            val v2 = self.t2.apply(v1.get.end())
            if (v2.nonEmpty) {
              val v3 = self.t3.apply(v2.get.end())
              if( v3.nonEmpty ){
                val v4 = self.t4.apply(v3.get.end())
                if( v4.nonEmpty ) {
                  val v5 = self.t5.apply(v4.get.end())
                  if( v5.nonEmpty ) {
                    res = Some(f(v1.get, v2.get, v3.get, v4.get, v5.get))
                  }
                }
              }
            }
          }
          res
        }
      }
    }
  }

  /**
   * Последоваетльность грамматических конструкций из шетиси конструкций
   * @tparam P  Тип указателя
   * @tparam T1 Тип первго токена
   * @tparam T2 Тип второго токена
   * @tparam T3 Тип третьего токена
   * @tparam T4 Тип 4-го токена
   * @tparam T5 Тип 5-го токена
   * @tparam T6 Тип 6-го токена
   */
  trait Sq6[
    P <: Pointer[_,_,_],
    T1 <: Tok[P], T2 <: Tok[P],
    T3 <: Tok[P], T4 <: Tok[P],
    T5 <: Tok[P], T6 <: Tok[P]
  ] {
    val prev:Sq5[P,T1, T2, T3, T4, T5]
    lazy val t1:GR[P,T1] = prev.t1
    lazy val t2:GR[P,T2] = prev.t2
    lazy val t3:GR[P,T3] = prev.t3
    lazy val t4:GR[P,T4] = prev.t4
    lazy val t5:GR[P,T5] = prev.t5
    val t6:GR[P,T6]
    def +[U <: Tok[P]]( gop:GR[P,U]):Sq7[P,T1,T2,T3,T4,T5,T6,U] = {
      val self = this
      new Sq7[P,T1,T2,T3,T4,T5,T6,U]{
        override val prev: Sq6[P, T1, T2, T3, T4, T5, T6] = self
        override val t7: GR[P, U] = gop
      }
    }
    def +[U <: Tok[P]]( g:Rp[P,U] ):Sq7[P,T1,T2,T3,T4,T5,T6,Tok[P] with Seq[U]] = {
      val self = this
      new Sq7[P,T1,T2,T3,T4,T5,T6,Tok[P] with Seq[U]] {
        override val prev: Sq6[P, T1,T2,T3,T4,T5,T6] = self
        override val t7: GR[P, Tok[P] with Seq[U]] = rp2grSeq(g)
      }
    }
    def ==>[U <: Tok[P]](f: (T1, T2, T3, T4, T5, T6) => U): GR[P, U] = {
      val self = this
      new GR[P, U] {
        override def apply(ptr: P): Option[U] = {
          var res : Option[U] = None
          val v1 = self.t1.apply(ptr)
          if( v1.nonEmpty ) {
            val v2 = self.t2.apply(v1.get.end())
            if (v2.nonEmpty) {
              val v3 = self.t3.apply(v2.get.end())
              if( v3.nonEmpty ){
                val v4 = self.t4.apply(v3.get.end())
                if( v4.nonEmpty ) {
                  val v5 = self.t5.apply(v4.get.end())
                  if( v5.nonEmpty ) {
                    val v6 = self.t6.apply(v5.get.end())
                    if( v6.nonEmpty ){
                      res = Some(f(v1.get, v2.get, v3.get, v4.get, v5.get, v6.get))
                    }
                  }
                }
              }
            }
          }
          res
        }
      }
    }
  }

  /**
   * Последоваетльность грамматических конструкций из шетиси конструкций
   * @tparam P  Тип указателя
   * @tparam T1 Тип первго токена
   * @tparam T2 Тип второго токена
   * @tparam T3 Тип третьего токена
   * @tparam T4 Тип 4-го токена
   * @tparam T5 Тип 5-го токена
   * @tparam T6 Тип 6-го токена
   * @tparam T7 Тип 7-го токена
   */
  trait Sq7[
    P <: Pointer[_,_,_],
    T1 <: Tok[P], T2 <: Tok[P],
    T3 <: Tok[P], T4 <: Tok[P],
    T5 <: Tok[P], T6 <: Tok[P],
    T7 <: Tok[P]
  ] {
    val prev:Sq6[P,T1, T2, T3, T4, T5, T6]
    lazy val t1:GR[P,T1] = prev.t1
    lazy val t2:GR[P,T2] = prev.t2
    lazy val t3:GR[P,T3] = prev.t3
    lazy val t4:GR[P,T4] = prev.t4
    lazy val t5:GR[P,T5] = prev.t5
    lazy val t6:GR[P,T6] = prev.t6
    val t7:GR[P,T7]
    def +[U <: Tok[P]]( gop:GR[P,U]):Sq8[P,T1,T2,T3,T4,T5,T6,T7,U] = {
      val self = this
      new Sq8[P,T1,T2,T3,T4,T5,T6,T7,U]{
        override val prev: Sq7[P, T1, T2, T3, T4, T5, T6, T7] = self
        override val t8: GR[P, U] = gop
      }
    }
    def +[U <: Tok[P]]( g:Rp[P,U] ):Sq8[P,T1,T2,T3,T4,T5,T6,T7,Tok[P] with Seq[U]] = {
      val self = this
      new Sq8[P,T1,T2,T3,T4,T5,T6,T7,Tok[P] with Seq[U]] {
        override val prev: Sq7[P, T1,T2,T3,T4,T5,T6,T7] = self
        override val t8: GR[P, Tok[P] with Seq[U]] = rp2grSeq(g)
      }
    }
    def ==>[U <: Tok[P]](f: (T1, T2, T3, T4, T5, T6, T7) => U): GR[P, U] = {
      val self = this
      new GR[P, U] {
        override def apply(ptr: P): Option[U] = {
          var res : Option[U] = None
          val v1 = self.t1.apply(ptr)
          if( v1.nonEmpty ) {
            val v2 = self.t2.apply(v1.get.end())
            if (v2.nonEmpty) {
              val v3 = self.t3.apply(v2.get.end())
              if( v3.nonEmpty ){
                val v4 = self.t4.apply(v3.get.end())
                if( v4.nonEmpty ) {
                  val v5 = self.t5.apply(v4.get.end())
                  if( v5.nonEmpty ) {
                    val v6 = self.t6.apply(v5.get.end())
                    if( v6.nonEmpty ){
                      val v7 = self.t7.apply(v6.get.end())
                      if( v7.nonEmpty ) {
                        res = Some(f(v1.get, v2.get, v3.get, v4.get, v5.get, v6.get, v7.get))
                      }
                    }
                  }
                }
              }
            }
          }
          res
        }
      }
    }
  }

  /**
   * Последоваетльность грамматических конструкций из шетиси конструкций
   * @tparam P  Тип указателя
   * @tparam T1 Тип первго токена
   * @tparam T2 Тип второго токена
   * @tparam T3 Тип третьего токена
   * @tparam T4 Тип 4-го токена
   * @tparam T5 Тип 5-го токена
   * @tparam T6 Тип 6-го токена
   * @tparam T7 Тип 7-го токена
   * @tparam T8 Тип 8-го токена
   */
  trait Sq8[
    P <: Pointer[_,_,_],
    T1 <: Tok[P], T2 <: Tok[P],
    T3 <: Tok[P], T4 <: Tok[P],
    T5 <: Tok[P], T6 <: Tok[P],
    T7 <: Tok[P], T8 <: Tok[P]
  ] {
    val prev:Sq7[P,T1, T2, T3, T4, T5, T6, T7]
    lazy val t1:GR[P,T1] = prev.t1
    lazy val t2:GR[P,T2] = prev.t2
    lazy val t3:GR[P,T3] = prev.t3
    lazy val t4:GR[P,T4] = prev.t4
    lazy val t5:GR[P,T5] = prev.t5
    lazy val t6:GR[P,T6] = prev.t6
    lazy val t7:GR[P,T7] = prev.t7
    val t8:GR[P,T8]
    def +[U <: Tok[P]]( gop:GR[P,U]):Sq9[P,T1,T2,T3,T4,T5,T6,T7,T8,U] = {
      val self = this
      new Sq9[P,T1,T2,T3,T4,T5,T6,T7,T8,U]{
        override val prev: Sq8[P, T1, T2, T3, T4, T5, T6, T7, T8] = self
        override val t9: GR[P, U] = gop
      }
    }
    def +[U <: Tok[P]]( g:Rp[P,U] ):Sq9[P,T1,T2,T3,T4,T5,T6,T7,T8,Tok[P] with Seq[U]] = {
      val self = this
      new Sq9[P,T1,T2,T3,T4,T5,T6,T7,T8,Tok[P] with Seq[U]] {
        override val prev: Sq8[P, T1,T2,T3,T4,T5,T6,T7,T8] = self
        override val t9: GR[P, Tok[P] with Seq[U]] = rp2grSeq(g)
      }
    }
    def ==>[U <: Tok[P]](f: (T1, T2, T3, T4, T5, T6, T7, T8) => U): GR[P, U] = {
      val self = this
      new GR[P, U] {
        override def apply(ptr: P): Option[U] = {
          var res : Option[U] = None
          val v1 = self.t1.apply(ptr)
          if( v1.nonEmpty ) {
            val v2 = self.t2.apply(v1.get.end())
            if (v2.nonEmpty) {
              val v3 = self.t3.apply(v2.get.end())
              if( v3.nonEmpty ){
                val v4 = self.t4.apply(v3.get.end())
                if( v4.nonEmpty ) {
                  val v5 = self.t5.apply(v4.get.end())
                  if( v5.nonEmpty ) {
                    val v6 = self.t6.apply(v5.get.end())
                    if( v6.nonEmpty ){
                      val v7 = self.t7.apply(v6.get.end())
                      if( v7.nonEmpty ) {
                        val v8 = self.t8.apply(v7.get.end())
                        if( v8.nonEmpty ) {
                          res = Some(f(v1.get, v2.get, v3.get, v4.get, v5.get, v6.get, v7.get, v8.get))
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          res
        }
      }
    }
  }

  /**
   * Последоваетльность грамматических конструкций из шетиси конструкций
   * @tparam P  Тип указателя
   * @tparam T1 Тип первго токена
   * @tparam T2 Тип второго токена
   * @tparam T3 Тип третьего токена
   * @tparam T4 Тип 4-го токена
   * @tparam T5 Тип 5-го токена
   * @tparam T6 Тип 6-го токена
   * @tparam T7 Тип 7-го токена
   * @tparam T8 Тип 8-го токена
   */
  trait Sq9[
    P <: Pointer[_,_,_],
    T1 <: Tok[P], T2 <: Tok[P], T3 <: Tok[P],
    T4 <: Tok[P], T5 <: Tok[P], T6 <: Tok[P],
    T7 <: Tok[P], T8 <: Tok[P], T9 <: Tok[P]
  ] {
    val prev:Sq8[P,T1, T2, T3, T4, T5, T6, T7, T8]
    lazy val t1:GR[P,T1] = prev.t1
    lazy val t2:GR[P,T2] = prev.t2
    lazy val t3:GR[P,T3] = prev.t3
    lazy val t4:GR[P,T4] = prev.t4
    lazy val t5:GR[P,T5] = prev.t5
    lazy val t6:GR[P,T6] = prev.t6
    lazy val t7:GR[P,T7] = prev.t7
    lazy val t8:GR[P,T8] = prev.t8
    val t9:GR[P,T9]
    def ==>[U <: Tok[P]](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => U): GR[P, U] = {
      val self = this
      new GR[P, U] {
        override def apply(ptr: P): Option[U] = {
          var res : Option[U] = None
          val v1 = self.t1.apply(ptr)
          if( v1.nonEmpty ) {
            val v2 = self.t2.apply(v1.get.end())
            if (v2.nonEmpty) {
              val v3 = self.t3.apply(v2.get.end())
              if( v3.nonEmpty ){
                val v4 = self.t4.apply(v3.get.end())
                if( v4.nonEmpty ) {
                  val v5 = self.t5.apply(v4.get.end())
                  if( v5.nonEmpty ) {
                    val v6 = self.t6.apply(v5.get.end())
                    if( v6.nonEmpty ){
                      val v7 = self.t7.apply(v6.get.end())
                      if( v7.nonEmpty ) {
                        val v8 = self.t8.apply(v7.get.end())
                        if( v8.nonEmpty ) {
                          val v9 = self.t9.apply(v8.get.end())
                          if( v9.nonEmpty ) {
                            res = Some(f(v1.get, v2.get, v3.get, v4.get, v5.get, v6.get, v7.get, v8.get, v9.get))
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          res
        }
      }
    }
  }

  /**
    * Повтор грамматических конструкций
    * @tparam P  Тип указателя
    * @tparam T Тип токена
    */
  trait Rp[P <: Pointer[_,_,_],T <: Tok[P]] extends GOP[P,T] {
    val expr:GR[P,T]
    def min():Int

    private var name : String = null
    def name( _name : String ) : Rp[P,T] = {
      this.name = _name
      this
    }

    /**
      * Парсинг последовательности токенов
      * @param ptr указатель на входную последовательность токенов
      * @return совпавшие токены
      */
    def parse(ptr:P):Option[(P,P,Seq[T])] = {
      if( ptr.eof() ){
        if( min()==0 ) {
          Some(ptr, ptr, List())
        }else{
          None
        }
      }else {
        val begin = ptr
        var end = ptr
        var p = ptr
        var ml = List[T]()
        var stop = false
        while (!stop) {
          val m = expr.apply(p)
          m match {
            case None => stop = true
            case Some(t) => {
              ml = t :: ml
              p = t.end()
              end = p
            }
          }
          if (p.eof()) {
            stop = true
          }
        }

        if (ml.size >= min) {
          Some((begin, end, ml.reverse))
        } else {
          if( min()==0 ) {
            Some(begin, begin, List())
          }else{
            None
          }
        }
      }
    }

    /**
      * Преобразование в грамматическую конструкцию
      * @param fn Преобразование совпадений
      * @tparam U Тип токена
      * @return грамматическая конструкция
      */
    def ==>[U <: Tok[P]]( fn:(Seq[T]=>U) ):GR[P,U] = {
      new GR[P,U]{
        override def apply(ptr: P): Option[U] = {
          val res = parse(ptr)
          if( res.isEmpty ){
            None
          }else{
            Some( fn(res.get._3) )
          }
        }
      }
    }

    /**
      * Преобразование в грамматическую конструкцию
      * @param fn Преобразование совпадений
      * @tparam U Тип токена
      * @return грамматическая конструкция
      */
    def ==>[U <: Tok[P]]( fn:(P,P)=>U ):GR[P,U]={
      new GR[P,U]{
        override def apply(ptr: P): Option[U] = {
          val res = parse(ptr)
          if( res.isEmpty ){
            None
          }else{
            Some( fn(res.get._1, res.get._2) )
          }
        }
      }
    }

    def +[U <: Tok[P]]( g:GR[P,U] ):Sq2[P,Tok[P] with Seq[T], U] = {
      val self = this
      new Sq2[P,Tok[P] with Seq[T],U] {
        override val t1: GR[P, Tok[P] with Seq[T]] = rp2grSeq(self)
        override val t2: GR[P, U] = g
      }
    }
  }

  /**
    * Альтернативная грамматическая конструкция
    * @tparam P Тип указателя
    * @tparam T Тип токена
    */
  trait Al[P <: Pointer[_,_,_],T <: Tok[P]] extends GOP[P,T] {
    val expr:Seq[GR[P,_ <: T]]
    def |[U <: T]( g:GR[P,U] ):Al[P,T] = {
      def self = this
      new Al[P,T] {
        override val expr:Seq[GR[P,_ <: T]] = self.expr :+ g //self.expr.appended(g)
      }
    }
    def ==>[U <: Tok[P]]( f:T=>U ): GR[P,U] ={
      new GR[P,U]{
        override def apply(ptr: P): Option[U] = {
          var r:Option[T] = None
          expr.foreach( ex => {
            if( r.isEmpty ){
              r = ex(ptr)
            }
          })
          if( r.isEmpty ){
            None
          }else{
            Some( f(r.get) )
          }
        }
      }
    }
  }

  /**
   * Конструкирование грамматических правил
   * @param base1 базовое правило
   * @tparam P тип указателя
   * @tparam T тип лексемы
   */
  implicit class GR2GOP[P <: Pointer[_,_,_],T <: Tok[P]]( val base1:GR[P,T] ){
    def +[U <: Tok[P]]( g:GR[P,U] ):Sq2[P,T, U] = {
      new Sq2[P,T, U] {
        override val t1: GR[P, T] = base1
        override val t2: GR[P, U] = g
      }
    }
    def +[U <: Tok[P]]( g:Rp[P,U] ):Sq2[P,T,Tok[P] with Seq[U]] = {
      new Sq2[P,T,Tok[P] with Seq[U]] {
        override val t1: GR[P, T] = base1
        override val t2: GR[P, Tok[P] with Seq[U]] = rp2grSeq(g)
      }
    }
    def *( n:Int ):Rp[P,T] = {
      val rp = new Rp[P,T] {
        override val expr: GR[P,T] = base1
        override def min(): Int = n
      }
      rp
    }
    def |[U <: T]( g:GR[P,U] ):Al[P,T] = {
      new Al[P,T] {
        override val expr: Seq[GR[P,T]] = List[GR[P,T]](base1,g.asInstanceOf[GR[P,T]])
      }
    }
    def ==>[U <: Tok[P]]( m:(T)=>U ):GR[P,U] = {
      require(m!=null)
      new GR[P,U] {
        override def apply(ptr: P): Option[U] = {
          val o1 = base1.apply(ptr)
          if( o1.isEmpty ){
            None
          }else{
            Some(m.apply(o1.get))
          }
        }
      }
    }
  }
}
