package search.speeder
import scala.collection.mutable.Stack
class CNode() {

  //    private  final long serialVersionUID = 1808760031169036512L;

  /** Index of the instance represented by this node in the index array. */
  var index = -1
  /** The distance of the furthest descendant of the node. */
  // The maximum distance to any grandchild.
   var max_dist: Double = 0;

  /** The distance to the nodes parent. */
  var parent_dist: Double = 0; // The distance to the parent.

  /** The children of the node. */
   var children: Stack[CNode] = null;

  /** The number of children node has. */
  var num_children = 0; // The number of children.

  /** The min i that makes base^i &lt;= max_dist. */
   var scale = 0; // Essentially, an upper bound on the distance to any

  def p() {

    // return the Instance
    // m_Instances.instance(idx);
  }

  def this(i: Int, md: Double, pd: Double, childs: Stack[CNode], numChildren: Int, s: Int) = {
    this
  }

  /**
   * Returns whether if the node is a leaf or not.
   *
   * @return true if the node is a leaf node.
   */
  def isLeaf() = {
    num_children == 0;
  }

}

class DistanceNode {

  /**
   * The last distance is to the current reference point (potential current
   * parent). The previous ones are to reference points that were previously
   * looked at (all potential ancestors).
   */
  var dist: Stack[Double] = null;

  /** The index of the instance represented by this node. */
  var idx: Int = -1;

  /**
   * Returns the instance represent by this DistanceNode.
   *
   * @return The instance represented by this node.
   */
  def q() ={
    // return instance  
    // return m_Instances.instance(idx);
    new Instance()
  }

}