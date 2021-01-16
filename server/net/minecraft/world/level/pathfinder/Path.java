package net.minecraft.world.level.pathfinder;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class Path {
   private final List<Node> nodes;
   private Node[] openSet = new Node[0];
   private Node[] closedSet = new Node[0];
   private int nextNodeIndex;
   private final BlockPos target;
   private final float distToTarget;
   private final boolean reached;

   public Path(List<Node> var1, BlockPos var2, boolean var3) {
      super();
      this.nodes = var1;
      this.target = var2;
      this.distToTarget = var1.isEmpty() ? 3.4028235E38F : ((Node)this.nodes.get(this.nodes.size() - 1)).distanceManhattan(this.target);
      this.reached = var3;
   }

   public void advance() {
      ++this.nextNodeIndex;
   }

   public boolean notStarted() {
      return this.nextNodeIndex <= 0;
   }

   public boolean isDone() {
      return this.nextNodeIndex >= this.nodes.size();
   }

   @Nullable
   public Node getEndNode() {
      return !this.nodes.isEmpty() ? (Node)this.nodes.get(this.nodes.size() - 1) : null;
   }

   public Node getNode(int var1) {
      return (Node)this.nodes.get(var1);
   }

   public void truncateNodes(int var1) {
      if (this.nodes.size() > var1) {
         this.nodes.subList(var1, this.nodes.size()).clear();
      }

   }

   public void replaceNode(int var1, Node var2) {
      this.nodes.set(var1, var2);
   }

   public int getNodeCount() {
      return this.nodes.size();
   }

   public int getNextNodeIndex() {
      return this.nextNodeIndex;
   }

   public void setNextNodeIndex(int var1) {
      this.nextNodeIndex = var1;
   }

   public Vec3 getEntityPosAtNode(Entity var1, int var2) {
      Node var3 = (Node)this.nodes.get(var2);
      double var4 = (double)var3.x + (double)((int)(var1.getBbWidth() + 1.0F)) * 0.5D;
      double var6 = (double)var3.y;
      double var8 = (double)var3.z + (double)((int)(var1.getBbWidth() + 1.0F)) * 0.5D;
      return new Vec3(var4, var6, var8);
   }

   public BlockPos getNodePos(int var1) {
      return ((Node)this.nodes.get(var1)).asBlockPos();
   }

   public Vec3 getNextEntityPos(Entity var1) {
      return this.getEntityPosAtNode(var1, this.nextNodeIndex);
   }

   public BlockPos getNextNodePos() {
      return ((Node)this.nodes.get(this.nextNodeIndex)).asBlockPos();
   }

   public Node getNextNode() {
      return (Node)this.nodes.get(this.nextNodeIndex);
   }

   @Nullable
   public Node getPreviousNode() {
      return this.nextNodeIndex > 0 ? (Node)this.nodes.get(this.nextNodeIndex - 1) : null;
   }

   public boolean sameAs(@Nullable Path var1) {
      if (var1 == null) {
         return false;
      } else if (var1.nodes.size() != this.nodes.size()) {
         return false;
      } else {
         for(int var2 = 0; var2 < this.nodes.size(); ++var2) {
            Node var3 = (Node)this.nodes.get(var2);
            Node var4 = (Node)var1.nodes.get(var2);
            if (var3.x != var4.x || var3.y != var4.y || var3.z != var4.z) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean canReach() {
      return this.reached;
   }

   public String toString() {
      return "Path(length=" + this.nodes.size() + ")";
   }

   public BlockPos getTarget() {
      return this.target;
   }

   public float getDistToTarget() {
      return this.distToTarget;
   }
}
