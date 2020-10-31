package xyz.cofe.stsl.pset

class PartialSet[A](
                   val set:Set[A],
                   val eq:(A,A)=>Boolean,
                   val relation:(A,A)=>Boolean,
                   val parentChildren:Map[A,Set[A]] = Map(),
                   val childParents:Map[A,Set[A]] = Map()
                   ) {
  require(set!=null)
  require(eq!=null)
  require(relation!=null)
  require(parentChildren!=null)
  require(childParents!=null)

  def descending(from:A, to:A):List[List[A]] = paths(from,to,parentChildren)
  def ascending(from:A, to:A):List[List[A]] = paths(from,to,childParents)

  def paths(from:A, to:A, direction:Map[A,Set[A]]):List[List[A]] = {
    require(from!=null)
    require(to!=null)
    require(direction!=null)

    if( from==to ){
      List(List(from))
    }else{
      var matched : List[List[A]] = List()
      var ws : List[List[A]] = List(List(from))
      var stop = false
      while( !stop ){
        if( ws.isEmpty ){
          stop = true
        }else{
          val nextWs = ws.map( path => {
            val nextNodes = direction.getOrElse(path.head,Set[A]())
            val follow = nextNodes.map( nextNode => nextNode :: path ).toList
            val match1 = follow.filter( fpath => fpath.head == to )
            matched = matched ++ match1
            follow.filter( fpath => !match1.contains(fpath) )
          })
          ws = nextWs.flatten
        }
      }
      matched.map( path => path.reverse )
    }
  }
}

object PartialSet {
  def apply[A](set:Set[A], eq:(A,A)=>Boolean, relation:(A,A)=>Boolean): PartialSet[A] = {
    require(set!=null)
    require(relation!=null)
    require(eq!=null)

    var parentChildren:Map[A,Set[A]] = Map()

    var workset = List(GBuild(set,eq,relation))
    while( workset.nonEmpty ){
      val follow = workset.map(ws=>{
        val parentChildRel = ws.roots.map(root=>root -> ws.child(root))
        parentChildRel.foreach({case (parent,childSet)=>
          parentChildren = parentChildren + ( parent -> childSet.roots )
        })
        parentChildRel.map({case(parent,childs)=>childs}).toList
      })
      workset = follow.flatten.filter(w=>w.roots.nonEmpty)
    }

    var childParents: Map[A,Set[A]] = Map()
    parentChildren.foreach({ case (node, children) =>
      if( children.nonEmpty ) {
        children.foreach(ch => {
          val nodes = childParents.getOrElse(ch, Set[A]())
          childParents = childParents + (ch -> (nodes + node))
        })
      }
    })

    new PartialSet[A](set,eq,relation,parentChildren,childParents)
  }
}