package xyz.cofe.stsl.tast.isect

import xyz.cofe.stsl.tast.ToasterError
import xyz.cofe.stsl.types.{Fields, TAnon, Type}

/**
 * Работает с элементами TAnon, иначе генерирует ошибку
 *
 * @param optionalField опциональный generic тип для поля
 * @param optBuilder создание optional
 */
case class AnonFieldsReductor(
                      optionalField: OptionalField,
                      optBuilder:OptionalBuilder = OptBaker()
                    ) {
  case class FieldAcum( anons:List[TAnon]=List[TAnon]() )
  object AnonCollector extends AnonCollector[FieldAcum] {
    /** Начальное значением аккумулятора */
    override def initial: FieldAcum = FieldAcum()

    /**
     * аккумуляция
     *
     * @param acum аккумулятор
     * @param obj  анонимный тип
     * @return аккумулятор
     */
    override def collect(acum: FieldAcum, obj: Type): FieldAcum = {
      obj match {
        case t:TAnon =>
          acum.copy( t :: acum.anons )
        case _ => throw ToasterError(s"${obj} not instance of TAnon")
      }
    }
  }
  
  object AnonReductor extends AnonReductor[FieldAcum] {
    /**
     * Редукция аккумулятора к анонимному типу
     *
     * @param acum аккумулятор разных типов
     * @return анонимный тип
     */
    override def reduce(acum: FieldAcum): TAnon = {
      TAnon(Fields(FieldsReductor(optionalField,optBuilder).reduce(FieldsCollector(acum.anons))))
    }
  }
}
