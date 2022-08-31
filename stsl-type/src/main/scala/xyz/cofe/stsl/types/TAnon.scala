package xyz.cofe.stsl.types

import xyz.cofe.stsl.types.TAnon.{assignableByFields, assignableByMethods}

/**
 * Анонимный тип, не имеет названия, не от кого не унаследован
 *
 * @param generics Параметры типа
 * @param extend   Какой тип расширяет
 * @param fields   Атрибуты/поля класса
 */
class TAnon(
             val generics: MutableGenericParams = new MutableGenericParams(),
             val fields: MutableFields = new MutableFields(),
             val methods: MutableMethods = new MutableMethods()
           )
  extends Obj
    with ObjGenericValidation
    with Freezing
    with TypeVarReplace[TAnon] {
  override type GENERICS = MutableGenericParams
  override type FIELDS = MutableFields
  override type METHODS = MutableMethods

  validateTypeVariables()

  //region "Заморозка"

  private var freezedValue: Boolean = false

  /**
   * Проверка что объект уже заморожен
   *
   * @return true - объект уже заморожен, его нельзя изменять
   */
  def freezed: Boolean = freezedValue

  /**
   * Заморозка объекта
   */
  def freeze: Unit = {
    validateTypeVariables()
    freezedValue = true
    methods.freeze
    fields.freeze
    generics.freeze
  }
  //endregion

  //region typeVarReplace() - Замена переменных типа

  /**
   * Замена переменных типа в данном классе
   *
   * @param recipe правило замены переменных
   * @return клон с замененными переменными-типами
   */
  override def typeVarReplace(recipe: TypeVariable => Option[Type]): TAnon = {
    require(recipe != null)

    if (generics.isEmpty) {
      this
    } else {
      var replaceTypeVarMap: Map[String, Type] = Map()

      val replacement: TypeVariable => Option[Type] = (tv) => {
        val trgt = recipe(tv)
        if (trgt.isDefined) {
          //println( s"replacement ${tv} => ${trgt.get}" )
          if (replaceTypeVarMap.contains(tv.name)) {
            //
          } else {
            if (!generics(tv.name).assignable(trgt.get)) {
              throw TypeError(s"can't assign type ${trgt.get} into type variable ${generics(tv.name)}")
            }
            replaceTypeVarMap = replaceTypeVarMap + (tv.name -> trgt.get)
          }
        }
        trgt
      }

      val newGeneric = generics match {
        case tvr: TypeVarReplace[GenericParams] =>
          tvr.typeVarReplace(replacement)
        case _ =>
          generics
      }

      //      val newExtend = if (extend.isDefined) {
      //        val ext: Type = extend.get match {
      //          case t: TypeVarReplace[_] => t.typeVarReplace(replacement).asInstanceOf[Type]
      //          case _ => extend.get
      //        }
      //        Some(ext)
      //      } else {
      //        None
      //      }

      val asIsFields: Seq[Field] = fields.filter(f => !fieldsTypeVariablesMap.contains(f.name))
      val newTvFields: Seq[Field] = fields
        .filter(f => fieldsTypeVariablesMap.contains(f.name))
        .map(f => new Field(f.name,
          f.tip match {
            case tv: TypeVariable =>
              replacement(tv).getOrElse(
                tv match {
                  case tv2: TypeVarReplace[_] => tv2.typeVarReplace(replacement).asInstanceOf[Type]
                  case _ => tv
                }
              )
            case _ =>
              f.tip match {
                case tv2: TypeVarReplace[_] => tv2.typeVarReplace(replacement).asInstanceOf[Type]
                case _ => f.tip
              }
          }
        )
        )
      val newFields = Fields((asIsFields ++ newTvFields).toList)
      val newMethods = new Methods(
        methods.funs.map({ case (name, funs) =>
          name -> new Funs(funs.map(fun => {
            fun.typeVarReplace(replacement)
          }).toList)
        })
      )

      val nnewGeneric = new GenericParams(
        newGeneric.params.map(p => {
          if (replaceTypeVarMap.get(p.name).exists(t => t.isInstanceOf[TypeVariable])) {
            val tv = replaceTypeVarMap(p.name).asInstanceOf[TypeVariable]
            p match {
              case av: AnyVariant => AnyVariant(tv.name)
              case cov: CoVariant => CoVariant(tv.name, cov.tip)
              case ctr: ContraVariant => ContraVariant(tv.name, ctr.tip)
            }
          } else {
            null
          }
        }).filter(_ != null)
      )

      val newTObj = TAnon(nnewGeneric, newFields, newMethods)

      newTObj
    }
  }
  //endregion

  /**
   * Проверка возможности присвоение с учетом параметров типа
   *
   * @param t присваемый тип данных
   * @return true - операция допускается, false - не допускается
   */
  override def assignable(t: Type)(implicit tracer: AssignableTracer): Boolean = {
    t match {
      case tobj: TObject => assignable(tobj)
      case tanon: TAnon => assignable(tanon)
      case _ => super.assignable(t)
    }
  }

  private def assignable(t: TObject)(implicit tracer: AssignableTracer): Boolean = {
    tracer("TAnon", this, t) {
      val genericAssign = generics.assignable(t.generics)

      val fieldsAssign = assignableByFields(fields, t.fields)
      tracer(s"fieldsAssign ${fieldsAssign}")(fieldsAssign.isEmpty)

      val methodsAssign = assignableByMethods(methods, t.methods)
      tracer(s"methodsAssign $methodsAssign")(methodsAssign.isEmpty)

      genericAssign && fieldsAssign.isEmpty && methodsAssign.isEmpty
    }
  }

  private def assignable(t: TAnon)(implicit tracer: AssignableTracer): Boolean = {
    tracer("TAnon", this, t) {
      val genericAssign = generics.assignable(t.generics)

      val fieldsAssign = assignableByFields(fields, t.fields)
      tracer(s"fieldsAssign ${fieldsAssign}")(fieldsAssign.isEmpty)

      val methodsAssign = assignableByMethods(methods, t.methods)
      tracer(s"methodsAssign $methodsAssign")(methodsAssign.isEmpty)

      genericAssign && fieldsAssign.isEmpty && methodsAssign.isEmpty
    }
  }
}

object TAnon {
  //region apply(...)

  /**
   * Создать тип
   *
   * @param gparams параметры типа
   * @param fields  поля
   * @param methods методы
   * @return анонимный тип
   */
  def apply(gparams: GenericParams, fields: Fields, methods: Methods): TAnon = {
    new TAnon(
      gparams match {
        case mut: MutableGenericParams => mut
        case _ => new MutableGenericParams(gparams.params)
      },
      fields match {
        case mut: MutableFields => mut
        case _ => new MutableFields(fields.fields)
      },
      methods match {
        case mut: MutableMethods => mut
        case _ => new MutableMethods(methods.funs)
      }
    )
  }

  /**
   * Создать тип
   *
   * @param fields  поля
   * @param methods методы
   * @return анонимный тип
   */
  def apply(fields: Fields, methods: Methods): TAnon = {
    new TAnon(
      new MutableGenericParams(),
      fields match {
        case mut: MutableFields => mut
        case _ => new MutableFields(fields.fields)
      },
      methods match {
        case mut: MutableMethods => mut
        case _ => new MutableMethods(methods.funs)
      }
    )
  }

  /**
   * Создать тип
   *
   * @param fields поля
   * @return анонимный тип
   */
  def apply(fields: Fields): TAnon = {
    new TAnon(
      new MutableGenericParams(),
      fields match {
        case mut: MutableFields => mut
        case _ => new MutableFields(fields.fields)
      },
      new MutableMethods()
    )
  }
  //endregion

  /**
   * Создание типа из объекта
   *
   * @param tObj тип объекта
   * @return анонимный тип
   */
  def from(tObj: TObject): TAnon = {
    require(tObj != null)
    val generics = new MutableGenericParams(tObj.generics.params)
    val fields = new MutableFields()
    val methods = new MutableMethods()
    tObj.publicFields.foreach(fld => {
      fields.remove(f => f.name == fld.name).append(fld)
    })
    tObj.publicMethods.foreach { case (name, fun) => {
      methods.remove { case (e_name, e_f) => e_name == name && e_f.sameTypes(fun) }.append(name, fun)
    }
    }
    apply(generics, fields, methods)
  }

  /**
   * Проверка что к указанным полям можно присвоить указанные
   *
   * @param taFields целевые поля
   * @param tbFields исходные поля
   * @return ошибка в случае не возможности
   */
  def assignableByFields(taFields: Fields, tbFields: Fields)(implicit tracer: AssignableTracer): Option[String] = {
    import syntax._

    val f_a_bOptList = taFields
      .map { fa => (fa, tbFields.get(fa.name)) }

    val nonExistField = f_a_bOptList.map { case (fa, fb) => fb match {
      case Some(_) => {
        tracer(s"field ${fa.name} found")(true)
        None
      }
      case None => {
        tracer(s"field ${fa.name} not found")(false)
        Some(s"field ${fa.name} not exists")
      }
    }
    }
      .filter { err => err.isDefined }
      .foldErr

    if (nonExistField.isDefined) {
      nonExistField
    } else {
      val f_a_bList = f_a_bOptList.map { case (fa, fb) => (fa, fb.get) }
      if (f_a_bList.isEmpty) {
        None
      } else {
        val nonAssignFields = f_a_bList
          .map { case (fa, fb) => (fa, fa.tip, fb, fb.tip) }
          .map { case (fa, ta, fb, tb) => (fa, fb, tracer(s"field ${fa.name}")(ta.assignable(tb))) }
          .map { case (fa, fb, asgn) => if (asgn) {
            None
          } else {
            Some(s"${fa.name}:${fa.tip} not assignable from ${fb.name}:${fb.tip}")
          }
          }.foldErr

        if (nonAssignFields.isDefined) {
          nonAssignFields
        } else {
          None
        }
      }
    }
  }

  /**
   * Проверка что методы объекта `a` совместимы с методами объекта `b`
   *
   * @param taMethods методы `a`
   * @param tbMethods методы `b`
   * @return ошибка в случае не возможности
   */
  def assignableByMethods(taMethods: Methods, tbMethods: Methods)(implicit tracer: AssignableTracer): Option[String] = {
    import syntax._
    if (taMethods.isEmpty) {
      None
    } else {
      val aFunsMap = taMethods.funs
      val bFunsMap = tbMethods.funs
      if (aFunsMap.isEmpty) {
        None
      } else {
        val nonExistsMethods = aFunsMap.keySet.map(k => (k, bFunsMap.keySet.contains(k))).filter(!_._2)
        if (nonExistsMethods.nonEmpty) {
          nonExistsMethods.map { case (mname, _) => {
            tracer(s"method ${mname} not found")(false)
            Some(s"method ${mname} not exists"): Option[String]
          }
          }.toSeq.foldErr
        } else {
          aFunsMap.map { case (methName, aFuns) =>
            bFunsMap.get(methName) match {
              case None => {
                tracer(s"method ${methName} not found")(false)
                Some(s"method ${methName} not found")
              }
              case Some(bFuns) =>
                aFuns.map { aFun =>
                  if (bFuns.exists(bFun => aFun.assignable(bFun))) {
                    {
                      tracer(s"$methName $aFun is assignable from $bFuns")(true)
                      None
                    }
                  } else {
                    tracer(s"$methName $aFun is NOT assignable $bFuns")(false)
                    Some(s"not found compatible method ${aFun}")
                  }
                }.foldErr
            }
          }.toList.foldErr
        }
      }
    }
  }
}