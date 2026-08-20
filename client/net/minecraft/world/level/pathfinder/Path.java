package net.minecraft.world.level.pathfinder;

import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class Path {
   public static final StreamCodec<ByteBuf, Path> DEBUG_STREAM_CODEC;
   private final List<Node> nodes;
   private @Nullable DebugData debugData;
   private int nextNodeIndex;
   private final BlockPos target;
   private final float distToTarget;
   private final boolean reached;

   public Path(final List<Node> nodes, final BlockPos target, final boolean reached) {
      super();
      this.nodes = nodes;
      this.target = target;
      this.distToTarget = nodes.isEmpty() ? 3.4028235E38F : ((Node)this.nodes.get(this.nodes.size() - 1)).distanceManhattan(this.target);
      this.reached = reached;
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

   public @Nullable Node getEndNode() {
      return !this.nodes.isEmpty() ? (Node)this.nodes.get(this.nodes.size() - 1) : null;
   }

   public Node getNode(final int i) {
      return (Node)this.nodes.get(i);
   }

   public void truncateNodes(final int index) {
      if (this.nodes.size() > index) {
         this.nodes.subList(index, this.nodes.size()).clear();
      }

   }

   public void replaceNode(final int index, final Node replaceWith) {
      this.nodes.set(index, replaceWith);
   }

   public int getNodeCount() {
      return this.nodes.size();
   }

   public int getNextNodeIndex() {
      return this.nextNodeIndex;
   }

   public void setNextNodeIndex(final int nextNodeIndex) {
      this.nextNodeIndex = nextNodeIndex;
   }

   public Vec3 getEntityPosAtNode(final Entity entity, final int index) {
      Node node = (Node)this.nodes.get(index);
      double x = (double)node.x + (double)((int)(entity.getBbWidth() + 1.0F)) * 0.5;
      double y = (double)node.y;
      double z = (double)node.z + (double)((int)(entity.getBbWidth() + 1.0F)) * 0.5;
      return new Vec3(x, y, z);
   }

   public BlockPos getNodePos(final int index) {
      return ((Node)this.nodes.get(index)).asBlockPos();
   }

   public Vec3 getNextEntityPos(final Entity entity) {
      return this.getEntityPosAtNode(entity, this.nextNodeIndex);
   }

   public BlockPos getNextNodePos() {
      return ((Node)this.nodes.get(this.nextNodeIndex)).asBlockPos();
   }

   public Node getNextNode() {
      return (Node)this.nodes.get(this.nextNodeIndex);
   }

   public @Nullable Node getPreviousNode() {
      return this.nextNodeIndex > 0 ? (Node)this.nodes.get(this.nextNodeIndex - 1) : null;
   }

   public boolean sameAs(final @Nullable Path path) {
      return path != null && this.nodes.equals(path.nodes);
   }

   public boolean equals(final Object obj) {
      if (!(obj instanceof Path path)) {
         return false;
      } else {
         return this.nextNodeIndex == path.nextNodeIndex && this.debugData == path.debugData && this.reached == path.reached && this.target.equals(path.target) && this.nodes.equals(path.nodes);
      }
   }

   public int hashCode() {
      return this.nextNodeIndex + this.nodes.hashCode() * 31;
   }

   public boolean canReach() {
      return this.reached;
   }

   @VisibleForDebug
   void setDebug(final List<Node> openSet, final List<Node> closedSet, final Set<Target> targets) {
      this.debugData = new DebugData(openSet, closedSet, targets);
   }

   public @Nullable DebugData debugData() {
      return this.debugData;
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

   public Path copy() {
      Path result = new Path(this.nodes, this.target, this.reached);
      result.debugData = this.debugData;
      result.nextNodeIndex = this.nextNodeIndex;
      return result;
   }

   static {
      DEBUG_STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, (p) -> p.reached, ByteBufCodecs.INT, (p) -> p.nextNodeIndex, BlockPos.STREAM_CODEC, (p) -> p.target, Node.DEBUG_STREAM_CODEC.apply(ByteBufCodecs.list()), (p) -> p.nodes, Path.DebugData.STREAM_CODEC, (p) -> {
         if (p.debugData != null && !p.debugData.targetNodes.isEmpty()) {
            return p.debugData;
         } else {
            throw new IllegalStateException("Missing debug data");
         }
      }, (reached, nextNodeIndex, target, nodes, debugData) -> {
         Path path = new Path(nodes, target, reached);
         path.debugData = debugData;
         path.nextNodeIndex = nextNodeIndex;
         return path;
      });
   }

   public static record DebugData(List<Node> openSet, List<Node> closedSet, Set<Target> targetNodes) {
      public static final StreamCodec<ByteBuf, DebugData> STREAM_CODEC;

      public DebugData {
         super();
      }

      static {
         STREAM_CODEC = StreamCodec.composite(Node.DEBUG_STREAM_CODEC.apply(ByteBufCodecs.list()), DebugData::openSet, Node.DEBUG_STREAM_CODEC.apply(ByteBufCodecs.list()), DebugData::closedSet, Target.DEBUG_STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), DebugData::targetNodes, DebugData::new);
      }
   }
}
