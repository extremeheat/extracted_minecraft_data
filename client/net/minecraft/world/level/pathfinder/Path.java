package net.minecraft.world.level.pathfinder;

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
   @Nullable
   private Path.DebugData debugData;
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
      this.nextNodeIndex++;
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
         for (int var2 = 0; var2 < this.nodes.size(); var2++) {
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
      this.debugData = new Path.DebugData(var1, var2, var3);
   }

   @Nullable
   public Path.DebugData debugData() {
      return this.debugData;
   }

   public void writeToStream(FriendlyByteBuf var1) {
      if (this.debugData != null && !this.debugData.targetNodes.isEmpty()) {
         var1.writeBoolean(this.reached);
         var1.writeInt(this.nextNodeIndex);
         var1.writeBlockPos(this.target);
         var1.writeCollection(this.nodes, (var0, var1x) -> var1x.writeToStream(var0));
         this.debugData.write(var1);
      }
   }

   public static Path createFromStream(FriendlyByteBuf var0) {
      boolean var1 = var0.readBoolean();
      int var2 = var0.readInt();
      BlockPos var3 = var0.readBlockPos();
      List var4 = var0.readList(Node::createFromStream);
      Path.DebugData var5 = Path.DebugData.read(var0);
      Path var6 = new Path(var4, var3, var1);
      var6.debugData = var5;
      var6.nextNodeIndex = var2;
      return var6;
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

   static Node[] readNodeArray(FriendlyByteBuf var0) {
      Node[] var1 = new Node[var0.readVarInt()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = Node.createFromStream(var0);
      }

      return var1;
   }

   static void writeNodeArray(FriendlyByteBuf var0, Node[] var1) {
      var0.writeVarInt(var1.length);

      for (Node var5 : var1) {
         var5.writeToStream(var0);
      }
   }

   public Path copy() {
      Path var1 = new Path(this.nodes, this.target, this.reached);
      var1.debugData = this.debugData;
      var1.nextNodeIndex = this.nextNodeIndex;
      return var1;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
