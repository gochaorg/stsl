package xyz.cofe.stsl.types.pset

/**
 * Построение графа частично упорядоченного множества
 * @param descendingChildren
 * @param ascendingParents
 * @param roots
 * @param set
 * @param eq
 * @param relation
 * @tparam A
 */
class GBuild[A]( val descendingChildren:Map[A,Set[A]]
               , val ascendingParents:Map[A,Set[A]]
               , val roots:Set[A]
               , val set:Set[A]
               , val eq:(A,A)=>Boolean
               , val relation:(A,A)=>Boolean
               ) {
  def child(child:A):GBuild[A] = {
    require(child!=null)
    val childSet = descendingChildren(child)
    val subSet:Set[A] = set.intersect(childSet)
    GBuild(subSet,eq,relation)
  }
}

/**
 * Построение графа частично упорядоченного множества
 */
object GBuild {
  /**
   * Построение графа
   * @param set множество вершин
   * @param eq отношение равенства
   * @param relation отношение порядка
   * @tparam A тип вершины
   * @return Граф
   */
  def apply[A](set:Set[A], eq:(A,A)=>Boolean, relation:(A,A)=>Boolean): GBuild[A] = {
    require(set!=null)
    require(relation!=null)
    require(eq!=null)

    val descendingChildren:Map[A,Set[A]] = set.map( a=>
      a -> set.filter( b => relation(a,b) && !eq(a,b) )
    ).toMap

    val ascendingParents:Map[A,Set[A]] = set.map( a=>
      a -> set.filter( b => relation(b,a) && !eq(a,b) )
    ).toMap

    val roots:Set[A] = ascendingParents.filter({ case (n, ls) => ls.isEmpty }).keys.toSet

    new GBuild[A](
      descendingChildren,
      ascendingParents,
      roots,
      set,
      eq,
      relation
    )
  }
}
