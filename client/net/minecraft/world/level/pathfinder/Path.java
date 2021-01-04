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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class Path {
   private final List<Node> nodes;
   private Node[] openSet = new Node[0];
   private Node[] closedSet = new Node[0];
   private Set<Target> targetNodes;
   private int index;
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

   public void next() {
      ++this.index;
   }

   public boolean isDone() {
      return this.index >= this.nodes.size();
   }

   @Nullable
   public Node last() {
      return !this.nodes.isEmpty() ? (Node)this.nodes.get(this.nodes.size() - 1) : null;
   }

   public Node get(int var1) {
      return (Node)this.nodes.get(var1);
   }

   public List<Node> getNodes() {
      return this.nodes;
   }

   public void truncate(int var1) {
      if (this.nodes.size() > var1) {
         this.nodes.subList(var1, this.nodes.size()).clear();
      }

   }

   public void set(int var1, Node var2) {
      this.nodes.set(var1, var2);
   }

   public int getSize() {
      return this.nodes.size();
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int var1) {
      this.index = var1;
   }

   public Vec3 getPos(Entity var1, int var2) {
      Node var3 = (Node)this.nodes.get(var2);
      double var4 = (double)var3.x + (double)((int)(var1.getBbWidth() + 1.0F)) * 0.5D;
      double var6 = (double)var3.y;
      double var8 = (double)var3.z + (double)((int)(var1.getBbWidth() + 1.0F)) * 0.5D;
      return new Vec3(var4, var6, var8);
   }

   public Vec3 currentPos(Entity var1) {
      return this.getPos(var1, this.index);
   }

   public Vec3 currentPos() {
      Node var1 = (Node)this.nodes.get(this.index);
      return new Vec3((double)var1.x, (double)var1.y, (double)var1.z);
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

   public Node[] getOpenSet() {
      return this.openSet;
   }

   public Node[] getClosedSet() {
      return this.closedSet;
   }

   public static Path createFromStream(FriendlyByteBuf var0) {
      boolean var1 = var0.readBoolean();
      int var2 = var0.readInt();
      int var3 = var0.readInt();
      HashSet var4 = Sets.newHashSet();

      for(int var5 = 0; var5 < var3; ++var5) {
         var4.add(Target.createFromStream(var0));
      }

      BlockPos var12 = new BlockPos(var0.readInt(), var0.readInt(), var0.readInt());
      ArrayList var6 = Lists.newArrayList();
      int var7 = var0.readInt();

      for(int var8 = 0; var8 < var7; ++var8) {
         var6.add(Node.createFromStream(var0));
      }

      Node[] var13 = new Node[var0.readInt()];

      for(int var9 = 0; var9 < var13.length; ++var9) {
         var13[var9] = Node.createFromStream(var0);
      }

      Node[] var14 = new Node[var0.readInt()];

      for(int var10 = 0; var10 < var14.length; ++var10) {
         var14[var10] = Node.createFromStream(var0);
      }

      Path var11 = new Path(var6, var12, var1);
      var11.openSet = var13;
      var11.closedSet = var14;
      var11.targetNodes = var4;
      var11.index = var2;
      return var11;
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
