package xyz.cofe.stsl.tast.isect

import xyz.cofe.stsl.tast.AnonymousObject.MethodBuilder
import xyz.cofe.stsl.tast.ToasterError
import xyz.cofe.stsl.types.{Fields, Funs, Methods, TAnon, Type}

case class TAnonReductor(
                          optionalField: OptionalField,
                          optBuilder:OptionalBuilder = OptBaker(),
                          methodBuilder: MethodBuilder = MethodBuilder.ThisCallable
                        ) {
  case class Acum( anons:List[TAnon]=List[TAnon]() )
  object AnonCollector extends AnonCollector[Acum] {
    /** Начальное значением аккумулятора */
    override def initial: Acum = Acum()
    
    /**
     * аккумуляция
     *
     * @param acum аккумулятор
     * @param obj  анонимный тип
     * @return аккумулятор
     */
    override def collect(acum: Acum, obj: Type): Acum = obj match {
      case t:TAnon =>
        acum.copy( t :: acum.anons )
      case _ => throw ToasterError(s"${obj} not instance of TAnon")
    }
  }
  
  object AnonReductor extends AnonReductor[Acum] {
    /**
     * Редукция аккумулятора к анонимному типу
     *
     * @param acum аккумулятор разных типов
     * @return анонимный тип
     */
    override def reduce(acum: Acum): TAnon = {
      val fields = Fields(FieldsReductor(optionalField,optBuilder).reduce(FieldsCollector(acum.anons)))
      val methods = acum.anons
        .map(_.methods)
        .foldLeft(MethodCollector())( (mcoll,meths) => mcoll.join(meths) )
        .common
        .map { case(name, funs) => name ->
          new Funs(funs.map( impl => methodBuilder.build(name,impl)).toList )
        }
      TAnon(fields,new Methods(methods))
    }
  }
}
