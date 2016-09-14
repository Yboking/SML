package search.speeder
import scala.collection.mutable.Stack
class CTree {

  //    private  final long serialVersionUID = 1808760031169036512L;

  /** Index of the instance represented by this node in the index array. */
  var index = -1
  /** The distance of the furthest descendant of the node. */
  // The maximum distance to any grandchild.
  private[this] var max_dist = 0;

  /** The distance to the nodes parent. */
  private[this] var parent_dist = 0; // The distance to the parent.

  /** The children of the node. */
  private[this] val children: Stack[CNode] = null;

  /** The number of children node has. */
  private[this] var num_children = 0; // The number of children.

  /** The min i that makes base^i &lt;= max_dist. */
  private[this] var scale = 0; // Essentially, an upper bound on the distance to any

  var m_Root: CNode = null;

  /**
   * Array holding the distances of the nearest neighbours. It is filled up both
   * by nearestNeighbour() and kNearestNeighbours().
   */
  var m_DistanceList: Array[Double] = null;

  /** Number of nodes in the tree. */
  var m_NumNodes, m_NumLeaves, m_MaxDepth: Int = 0

  /** Tree Stats variables. */
  //protected TreePerformanceStats m_TreeStats = null;

  /**
   * The base of our expansion constant. In other words the 2 in 2^i used in
   * covering tree and separation invariants of a cover tree. P.S.: In paper
   * it's suggested the separation invariant is relaxed in batch construction.
   */
  var m_Base = 1.3;

  /**
   * if we have base 2 then this can be viewed as 1/ln(2), which can be used
   * later on to do il2*ln(d) instead of ln(d)/ln(2), to get log2(d), in
   * get_scale method.
   */
  var il2 = 1.0 / Math.log(m_Base);

  /**
   * Returns the distance/value of a given scale/level. I.e. the value of base^i
   * (e.g. 2^i).
   *
   * @param s the level/scale
   * @return base^s
   */
  def dist_of_scale(s: Int) {
    Math.pow(m_Base, s);
  }

  /**
   * Finds the scale/level of a given value. I.e. the "i" in base^i.
   *
   * @param d the value whose scale/level is to be determined.
   * @return the scale/level of the given value.
   */
  def get_scale(d: Double) {
    Math.ceil(il2 * Math.log(d)).toInt
  }

  /**
   * Creates a new internal node for a given Instance/point p.
   *
   * @param idx The index of the instance the node represents.
   * @return Newly created CoverTreeNode.
   */
  def new_node(idx: Int) = {
    val new_node = new CNode();
    new_node.index = idx;
    new_node;
  }

  def new_leaf(idx: Int) = {
    new CNode(idx, 0.0, 0.0, null, 0, 100);
  }

  /**
   * Returns the max distance of the reference point p in current node to it's
   * children nodes.
   *
   * @param v The stack of DistanceNode objects.
   * @return Distance of the furthest child.
   */
  def max_set(v: Stack[DistanceNode]) = { // rename to
    // maxChildDist
    var max = 0.0;
    for (i <- 0 until v.length) {

      var n = v(i)
      if (max < n.dist(n.dist.length - 1).floatValue()) { // v[i].dist.last())
        max = n.dist(n.dist.length - 1).floatValue(); // v[i].dist.last();
      }
    }
    max;
  }

  class DistanceNode {

    /**
     * The last distance is to the current reference point (potential current
     * parent). The previous ones are to reference points that were previously
     * looked at (all potential ancestors).
     */
    var dist: Stack[Double] = null;

    /** The index of the instance represented by this node. */
    var idx = 0;

    /**
     * Returns the instance represent by this DistanceNode.
     *
     * @return The instance represented by this node.
     */
    def q() {
      //  return m_Instances.instance(idx);
      // return instance 
    }
  }

}