package xyz.cofe.sparse.hlist

trait Fetch[W, HL] {
  def fetch(hl: HL): W
}
