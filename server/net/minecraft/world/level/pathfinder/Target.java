package net.minecraft.world.level.pathfinder;

public class Target extends Node {
   private float bestHeuristic = 3.4028235E38F;
   private Node bestNode;
   private boolean reached;

   public Target(Node var1) {
      super(var1.x, var1.y, var1.z);
   }

   public void updateBest(float var1, Node var2) {
      if (var1 < this.bestHeuristic) {
         this.bestHeuristic = var1;
         this.bestNode = var2;
      }

   }

   public Node getBestNode() {
      return this.bestNode;
   }

   public void setReached() {
      this.reached = true;
   }
}
