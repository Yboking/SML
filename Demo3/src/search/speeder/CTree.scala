package search.speeder
import scala.collection.mutable.Stack

import scala.collection.mutable.ListBuffer

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
  def dist_of_scale(s: Int) = {
    Math.pow(m_Base, s);
  }

  /**
   * Finds the scale/level of a given value. I.e. the "i" in base^i.
   *
   * @param d the value whose scale/level is to be determined.
   * @return the scale/level of the given value.
   */
  def get_scale(d: Double) = {
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

  /**
   * Splits a given point_set into near and far based on the given scale/level.
   * All points with distance > base^max_scale would be moved to far set. In
   * other words, all those points that are not covered by the next child ball
   * of a point p (ball made of the same point p but of smaller radius at the
   * next lower level) are removed from the supplied current point_set and put
   * into far_set.
   *
   * @param point_set The supplied set from which all far points would be
   *          removed.
   * @param far_set The set in which all far points having distance >
   *          base^max_scale would be put into.
   * @param max_scale The given scale based on which the distances of points are
   *          judged to be far or near.
   */
  def split(point_set: Stack[DistanceNode],
            far_set: Stack[DistanceNode], max_scale: Int) {
    var new_index = 0;
    var fmax = dist_of_scale(max_scale);
    for (i <- 0 until point_set.length) {

      var n = point_set(i);
      if (n.dist(n.dist.length - 1).toDouble <= fmax) {
        new_index += 1
        point_set.updated(new_index, point_set(i))
        //        point_set.set(new_index++, point_set.element(i));
      } else {
        far_set.push(point_set(i)); // point_set[i]);
      }

    }

    val l: ListBuffer[DistanceNode] = ListBuffer[DistanceNode]()

    for (i <- 0 until new_index) {
      l += point_set(i)
    }
    //    List<DistanceNode> l = new java.util.LinkedList<DistanceNode>();
    //    for (int i = 0; i < new_index; i++) {
    //      l.add(point_set.element(i));
    //    }
    // removing all and adding only the near points
    point_set.clear();
    //    point_set.addAll(l); // point_set.index=new_index;
    point_set.pushAll(l)
  }

  val df = new DistanceFunction {
    def distance(p: Instance, q: Instance, scala: Double): Double = {
      0.0
    }
  }

  /**
   * Moves all the points in point_set covered by (the ball of) new_point into
   * new_point_set, based on the given scale/level.
   *
   * @param point_set The supplied set of instances from which all points
   *          covered by new_point will be removed.
   * @param new_point_set The set in which all points covered by new_point will
   *          be put into.
   * @param new_point The given new point.
   * @param max_scale The scale based on which distances are judged (radius of
   *          cover ball is calculated).
   */
  def dist_split(point_set: Stack[DistanceNode],
                 new_point_set: Stack[DistanceNode], new_point: DistanceNode, max_scale: Int) {
    var new_index = 0;
    var fmax = dist_of_scale(max_scale);

    for (i <- 0 until point_set.length) {

      var new_d = Math.sqrt(df.distance(new_point.q(),
        point_set(i).q(), fmax * fmax));
      if (new_d <= fmax) {
        point_set(i).dist.push(new_d);
        new_point_set.push(point_set(i));
      } else {

        point_set.update(new_index, point_set(i))
        new_index += 1
        //        point_set.set(new_index++, point_set.element(i));
      }
    }
    val l = ListBuffer[DistanceNode]();

    for (i <- 0 until new_index) {
      l += point_set(i)
    }

    point_set.clear();
    //    point_set.addAll(l);
    point_set.pushAll(l)
  }

  /**
   * Builds the tree on the given set of instances. P.S.: For internal use only.
   * Outside classes should call setInstances().
   *
   * @param insts The instances on which to build the cover tree.
   * @throws Exception If the supplied set of Instances is empty, or if there
   *           are missing values.
   */
  def buildCoverTree(insts: Instances) {
    if (insts.numInstances() == 0) {
      throw new Exception(
        "CoverTree: Empty set of instances. Cannot build tree.");
    }
    //    checkMissing(insts);
    //    if (m_EuclideanDistance == null) {
    //      m_DistanceFunction = m_EuclideanDistance = new EuclideanDistance(insts);
    //    } else {
    //      m_EuclideanDistance.setInstances(insts);
    //    }

    val point_set = new Stack[DistanceNode]()
    val consumed_set = new Stack[DistanceNode]()
    //    Stack<DistanceNode> point_set = nDistanceNodeew Stack<DistanceNode>();
    //    Stack<DistanceNode> consumed_set = new Stack<DistanceNode>();

    val point_p = insts.instance(0);
    val p_idx = 0
    var max_dist = -1.0
    var dist = 0.0;

    for (i <- 1 until insts.numInstances()) {

      var temp = new DistanceNode();
      temp.dist = new Stack[Double]();
      dist = Math.sqrt(df.distance(point_p, insts.instance(i), Double.PositiveInfinity));
      if (dist > max_dist) {
        max_dist = dist;
        insts.instance(i);
      }
      temp.dist.push(dist);
      temp.idx = i;
      point_set.push(temp);

    }

    max_dist = max_set(point_set);
    m_Root = batch_insert(p_idx, get_scale(max_dist), get_scale(max_dist),
      point_set, consumed_set);
  }

  /**
   * Creates a cover tree recursively using batch insert method.
   *
   * @param p The index of the instance from which to create the first node. All
   *          other points will be inserted beneath this node for p.
   * @param max_scale The current scale/level where the node is to be created
   *          (Also determines the radius of the cover balls created at this
   *          level).
   * @param top_scale The max scale in the whole tree.
   * @param point_set The set of unprocessed points from which child nodes need
   *          to be created.
   * @param consumed_set The set of processed points from which child nodes have
   *          already been created. This would be used to find the radius of the
   *          cover ball of p.
   * @return the node of cover tree created with p.
   */
  def batch_insert(p: Int, max_scale: Int, // current
                   // scale/level
                   top_scale: Int, // max scale/level for this dataset
                   point_set: Stack[DistanceNode], // set of points that are nearer to p
                   // [will also contain returned unused
                   // points]
                   consumed_set: Stack[DistanceNode]) // to return the set of points that have
                   // been used to calc. max_dist to a
                   // descendent
                   // Stack<Stack<DistanceNode>> stack)  //may not be needed
                   : CNode = {
    if (point_set.length == 0) {
      val leaf = new_leaf(p);
      m_NumNodes += 1; // incrementing node count
      m_NumLeaves += 1; // incrementing leaves count
      return leaf;
    } else {
      val max_dist = max_set(point_set); // O(|point_set|) the max dist
      // in point_set to point "p".
      val next_scale = Math.min(max_scale - 1, get_scale(max_dist));
      if (next_scale == Integer.MIN_VALUE) { // We have points with distance
        // 0. if max_dist is 0.

        val children = new Stack[CNode]()
        //        Stack<CoverTreeNode> children = new Stack<CoverTreeNode>();
        var leaf = new_leaf(p);
        children.push(leaf);
        m_NumLeaves += 1;
        m_NumNodes += 1; // incrementing node and leaf count
        while (point_set.length > 0) {
          val tmpnode = point_set.pop();
          //          DistanceNode tmpnode = point_set.pop();
          leaf = new_leaf(tmpnode.idx);
          children.push(leaf);
          m_NumLeaves += 1;
          m_NumNodes += 1; // incrementing node and leaf count
          consumed_set.push(tmpnode);
        }
        var n = new_node(p); // make a new node out of p and assign
        m_NumNodes += 1; // incrementing node count
        n.scale = 100; // A magic number meant to be larger than all scales.
        n.max_dist = 0; // since all points have distance 0 to p
        n.num_children = children.length;
        n.children = children;
        return n;
      } else {
        //        Stack<DistanceNode> far = new Stack<DistanceNode>();
        var far = new Stack[DistanceNode]();
        split(point_set, far, max_scale); // O(|point_set|)

        val child = batch_insert(p, next_scale, top_scale, point_set,
          consumed_set);

        if (point_set.length == 0) { // not creating any node in this
          // recursive call
          // push(stack,point_set);
          /**
           *  to do replace all far  !!!!!!!!!!!!!!!!!!!!
           */
          //point_set.replaceAllBy(far); // point_set=far;
          return child;
        } else {
          var n = new_node(p);
          m_NumNodes += 1; // incrementing node count

          var children = new Stack[CNode]();
          children.push(child);

          while (point_set.length != 0) { // O(|point_set| * num_children)
            val new_point_set = new Stack[DistanceNode]();
            val new_consumed_set = new Stack[DistanceNode]();
            var tmpnode = point_set.pop();
            var new_dist = tmpnode.dist.last;
            consumed_set.push(tmpnode);

            // putting points closer to new_point into new_point_set (and
            // removing them from point_set)
            dist_split(point_set, new_point_set, tmpnode, max_scale); // O(|point_saet|)
            // putting points closer to new_point into new_point_set (and
            // removing them from far)
            dist_split(far, new_point_set, tmpnode, max_scale); // O(|far|)

            var new_child = batch_insert(tmpnode.idx, next_scale,
              top_scale, new_point_set, new_consumed_set);
            new_child.parent_dist = new_dist;

            children.push(new_child);

            // putting the unused points from new_point_set back into
            // point_set and far
            var fmax = dist_of_scale(max_scale);
            tmpnode = null;

            for (i <- 0 until new_point_set.length) {

              tmpnode = new_point_set(i);
              tmpnode.dist.pop();
              if (tmpnode.dist.last <= fmax) {
                point_set.push(tmpnode);
              } else {
                far.push(tmpnode);
              }

            }
            // putting the points consumed while recursing for new_point
            // into consumed_set
            tmpnode = null;

            for (i <- 0 until new_consumed_set.length) {
              tmpnode = new_consumed_set(i);
              tmpnode.dist.pop();
              consumed_set.push(tmpnode);

            }
          } // end while(point_size.size!=0)

          /**
           *  to do replace far !!!!!!!!!!!!!!!!!!!!
           *
           */
          // point_set.replaceAllBy(far); // point_set=far;
          n.scale = top_scale - max_scale;
          n.max_dist = max_set(consumed_set);
          n.num_children = children.length;
          n.children = children;
          return n;
        } // end else if(pointset!=0)
      } // end else if(next_scale != -214....
    } // end else if(pointset!=0)
  }

}

class Instance {

}

class Instances {
  var num_Of_Instance = 0
  def numInstances() = {

    num_Of_Instance
  }
  def instance(index: Int) = {
    new Instance
  }
}
