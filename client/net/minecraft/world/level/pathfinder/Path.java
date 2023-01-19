package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class Path {
   private final List<Node> nodes;
   private Node[] openSet = new Node[0];
   private Node[] closedSet = new Node[0];
   @Nullable
   private Set<Target> targetNodes;
   private int nextNodeIndex;
   private final BlockPos target;
   private final float distToTarget;
   private final boolean reached;

   public Path(List<Node> var1, BlockPos var2, boolean var3) {
      super();
      this.nodes = var1;
      this.target = var2;
      this.distToTarget = var1.isEmpty() ? 3.4028235E38F : this.nodes.get(this.nodes.size() - 1).distanceManhattan(this.target);
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
      return !this.nodes.isEmpty() ? this.nodes.get(this.nodes.size() - 1) : null;
   }

   public Node getNode(int var1) {
      return this.nodes.get(var1);
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
      Node var3 = this.nodes.get(var2);
      double var4 = (double)var3.x + (double)((int)(var1.getBbWidth() + 1.0F)) * 0.5;
      double var6 = (double)var3.y;
      double var8 = (double)var3.z + (double)((int)(var1.getBbWidth() + 1.0F)) * 0.5;
      return new Vec3(var4, var6, var8);
   }

   public BlockPos getNodePos(int var1) {
      return this.nodes.get(var1).asBlockPos();
   }

   public Vec3 getNextEntityPos(Entity var1) {
      return this.getEntityPosAtNode(var1, this.nextNodeIndex);
   }

   public BlockPos getNextNodePos() {
      return this.nodes.get(this.nextNodeIndex).asBlockPos();
   }

   public Node getNextNode() {
      return this.nodes.get(this.nextNodeIndex);
   }

   @Nullable
   public Node getPreviousNode() {
      return this.nextNodeIndex > 0 ? this.nodes.get(this.nextNodeIndex - 1) : null;
   }

   public boolean sameAs(@Nullable Path var1) {
      if (var1 == null) {
         return false;
      } else if (var1.nodes.size() != this.nodes.size()) {
         return false;
      } else {
         for(int var2 = 0; var2 < this.nodes.size(); ++var2) {
            Node var3 = this.nodes.get(var2);
            Node var4 = var1.nodes.get(var2);
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

   @VisibleForDebug
   void setDebug(Node[] var1, Node[] var2, Set<Target> var3) {
      this.openSet = var1;
      this.closedSet = var2;
      this.targetNodes = var3;
   }

   @VisibleForDebug
   public Node[] getOpenSet() {
      return this.openSet;
   }

   @VisibleForDebug
   public Node[] getClosedSet() {
      return this.closedSet;
   }

   public void writeToStream(FriendlyByteBuf var1) {
      if (this.targetNodes != null && !this.targetNodes.isEmpty()) {
         var1.writeBoolean(this.reached);
         var1.writeInt(this.nextNodeIndex);
         var1.writeInt(this.targetNodes.size());
         this.targetNodes.forEach(var1x -> var1x.writeToStream(var1));
         var1.writeInt(this.target.getX());
         var1.writeInt(this.target.getY());
         var1.writeInt(this.target.getZ());
         var1.writeInt(this.nodes.size());

         for(Node var3 : this.nodes) {
            var3.writeToStream(var1);
         }

         var1.writeInt(this.openSet.length);

         for(Node var5 : this.openSet) {
            var5.writeToStream(var1);
         }

         var1.writeInt(this.closedSet.length);

         for(Node var11 : this.closedSet) {
            var11.writeToStream(var1);
         }
      }
   }

   public static Path createFromStream(FriendlyByteBuf var0) {
      boolean var1 = var0.readBoolean();
      int var2 = var0.readInt();
      int var3 = var0.readInt();
      HashSet var4 = Sets.newHashSet();

      for(int var5 = 0; var5 < var3; ++var5) {
         var4.add(Target.createFromStream(var0));
      }

      BlockPos var11 = new BlockPos(var0.readInt(), var0.readInt(), var0.readInt());
      ArrayList var6 = Lists.newArrayList();
      int var7 = var0.readInt();

      for(int var8 = 0; var8 < var7; ++var8) {
         var6.add(Node.createFromStream(var0));
      }

      Node[] var12 = new Node[var0.readInt()];

      for(int var9 = 0; var9 < var12.length; ++var9) {
         var12[var9] = Node.createFromStream(var0);
      }

      Node[] var13 = new Node[var0.readInt()];

      for(int var10 = 0; var10 < var13.length; ++var10) {
         var13[var10] = Node.createFromStream(var0);
      }

      Path var14 = new Path(var6, var11, var1);
      var14.openSet = var12;
      var14.closedSet = var13;
      var14.targetNodes = var4;
      var14.nextNodeIndex = var2;
      return var14;
   }

   @Override
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
