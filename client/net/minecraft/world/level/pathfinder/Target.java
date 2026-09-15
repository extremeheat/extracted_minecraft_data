package net.minecraft.world.level.pathfinder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class Target extends Node {
   public static final StreamCodec<ByteBuf, Target> DEBUG_STREAM_CODEC = createDebugStreamCodec(Target::new);
   private float bestHeuristic = 3.4028235E38F;
   private Node bestNode;
   private boolean reached;

   public Target(final Node node) {
      super(node.x, node.y, node.z);
   }

   public Target(final int x, final int y, final int z) {
      super(x, y, z);
   }

   public void updateBest(final float heuristic, final Node node) {
      if (heuristic < this.bestHeuristic) {
         this.bestHeuristic = heuristic;
         this.bestNode = node;
      }

   }

   public Node getBestNode() {
      return this.bestNode;
   }

   public void setReached() {
      this.reached = true;
   }

   public boolean isReached() {
      return this.reached;
   }
}
