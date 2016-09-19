package search.speeder


trait DistanceFunction {
   def distance(p :Instance, q :Instance, scala: Double) :Double 
}