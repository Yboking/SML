package search.speeder

class ResultHeap(maxSize: Int) {
 
  if(maxSize % 2 == 0){
    maxSize +=1 
  }
  
  
  var elements = new Arrray[Element](maxSize + 1) // == myheap 
      elements(0) = new Element(-1);
  def peek(): Element={
    elements(1)
  }
  
   def size() ={
        elements(0).index;
    }
   
   def get()={
     if (elements(0).index == 0){
      throw new Exception("empty exception") 
     }
     
     
      MyHeapElement r = elements[1];
      m_heap[1] = m_heap[m_heap[0].index];
      m_heap[0].index--;
      downheap();
      return r;
   }
}

class Element(index: Int){
  
  
}



 
   
    public MyHeapElement get() throws Exception {
      if (m_heap[0].index == 0) {
        throw new Exception("No elements present in the heap");
      }
      MyHeapElement r = m_heap[1];
      m_heap[1] = m_heap[m_heap[0].index];
      m_heap[0].index--;
      downheap();
      return r;
    }

    /**
     * adds the distance value to the heap.
     * 
     * @param d the distance value
     * @throws Exception if the heap gets too large
     */
    public void put(double d) throws Exception {
      if ((m_heap[0].index + 1) > (m_heap.length - 1)) {
        throw new Exception("the number of elements cannot exceed the "
          + "initially set maximum limit");
      }
      m_heap[0].index++;
      m_heap[m_heap[0].index] = new MyHeapElement(d);
      upheap();
    }

    /**
     * Puts an element by substituting it in place of the top most element.
     * 
     * @param d The distance value.
     * @throws Exception If distance is smaller than that of the head element.
     */
    public void putBySubstitute(double d) throws Exception {
      MyHeapElement head = get();
      put(d);
      if (head.distance == m_heap[1].distance) {
        putKthNearest(head.distance);
      } else if (head.distance > m_heap[1].distance) {
        m_KthNearest = null;
        m_KthNearestSize = 0;
        initSize = 10;
      } else if (head.distance < m_heap[1].distance) {
        throw new Exception("The substituted element is greater than the "
          + "head element. put() should have been called "
          + "in place of putBySubstitute()");
      }
    }

    /** the kth nearest ones. */
    MyHeapElement m_KthNearest[] = null;

    /** The number of kth nearest elements. */
    int m_KthNearestSize = 0;

    /** the initial size of the heap. */
    int initSize = 10;

    /**
     * returns the number of k nearest.
     * 
     * @return the number of k nearest
     * @see #m_KthNearestSize
     */
    public int noOfKthNearest() {
      return m_KthNearestSize;
    }

    /**
     * Stores kth nearest elements (if there are more than one).
     * 
     * @param d the distance
     */
    public void putKthNearest(double d) {
      if (m_KthNearest == null) {
        m_KthNearest = new MyHeapElement[initSize];
      }
      if (m_KthNearestSize >= m_KthNearest.length) {
        initSize += initSize;
        MyHeapElement temp[] = new MyHeapElement[initSize];
        System.arraycopy(m_KthNearest, 0, temp, 0, m_KthNearest.length);
        m_KthNearest = temp;
      }
      m_KthNearest[m_KthNearestSize++] = new MyHeapElement(d);
    }

    /**
     * returns the kth nearest element or null if none there.
     * 
     * @return the kth nearest element
     */
    public MyHeapElement getKthNearest() {
      if (m_KthNearestSize == 0) {
        return null;
      }
      m_KthNearestSize--;
      return m_KthNearest[m_KthNearestSize];
    }

    /**
     * performs upheap operation for the heap to maintian its properties.
     */
    protected void upheap() {
      int i = m_heap[0].index;
      MyHeapElement temp;
      while (i > 1 && m_heap[i].distance > m_heap[i / 2].distance) {
        temp = m_heap[i];
        m_heap[i] = m_heap[i / 2];
        i = i / 2;
        m_heap[i] = temp; // this is i/2 done here to avoid another division.
      }
    }

    /**
     * performs downheap operation for the heap to maintian its properties.
     */
    protected void downheap() {
      int i = 1;
      MyHeapElement temp;
      while (((2 * i) <= m_heap[0].index && m_heap[i].distance < m_heap[2 * i].distance)
        || ((2 * i + 1) <= m_heap[0].index && m_heap[i].distance < m_heap[2 * i + 1].distance)) {
        if ((2 * i + 1) <= m_heap[0].index) {
          if (m_heap[2 * i].distance > m_heap[2 * i + 1].distance) {
            temp = m_heap[i];
            m_heap[i] = m_heap[2 * i];
            i = 2 * i;
            m_heap[i] = temp;
          } else {
            temp = m_heap[i];
            m_heap[i] = m_heap[2 * i + 1];
            i = 2 * i + 1;
            m_heap[i] = temp;
          }
        } else {
          temp = m_heap[i];
          m_heap[i] = m_heap[2 * i];
          i = 2 * i;
          m_heap[i] = temp;
        }
      }
    }

    /**
     * returns the total size.
     * 
     * @return the total size
     */
    public int totalSize() {
      return size() + noOfKthNearest();
    }

    /**
     * Returns the revision string.
     * 
     * @return the revision
     */
    @Override
    public String getRevision() {
      return RevisionUtils.extract("$Revision: 10203 $");
    }
  }