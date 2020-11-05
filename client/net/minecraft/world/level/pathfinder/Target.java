package net.minecraft.world.level.pathfinder;

import net.minecraft.network.FriendlyByteBuf;

public class Target extends Node {
   private float bestHeuristic = 3.4028235E38F;
   private Node bestNode;
   private boolean reached;

   public Target(Node var1) {
      super(var1.x, var1.y, var1.z);
   }

   public Target(int var1, int var2, int var3) {
      super(var1, var2, var3);
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

   public static Target createFromStream(FriendlyByteBuf var0) {
      Target var1 = new Target(var0.readInt(), var0.readInt(), var0.readInt());
      var1.walkedDistance = var0.readFloat();
      var1.costMalus = var0.readFloat();
      var1.closed = var0.readBoolean();
      var1.type = BlockPathTypes.values()[var0.readInt()];
      var1.f = var0.readFloat();
      return var1;
   }
}
