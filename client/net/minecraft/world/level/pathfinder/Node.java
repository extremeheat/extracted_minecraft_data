package net.minecraft.world.level.pathfinder;

import com.mojang.datafixers.util.Function3;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Node {
   public static final StreamCodec<ByteBuf, Node> DEBUG_STREAM_CODEC = createDebugStreamCodec(Node::new);
   public final int x;
   public final int y;
   public final int z;
   private final int hash;
   public int heapIdx = -1;
   public float g;
   public float h;
   public float f;
   public @Nullable Node cameFrom;
   public boolean closed;
   public float walkedDistance;
   public float costMalus;
   public PathType type;

   protected static <N extends Node> StreamCodec<ByteBuf, N> createDebugStreamCodec(final Function3<Integer, Integer, Integer, N> factory) {
      return StreamCodec.composite(ByteBufCodecs.INT, (n) -> n.x, ByteBufCodecs.INT, (n) -> n.y, ByteBufCodecs.INT, (n) -> n.z, ByteBufCodecs.FLOAT, (n) -> n.walkedDistance, ByteBufCodecs.FLOAT, (n) -> n.costMalus, ByteBufCodecs.BOOL, (n) -> n.closed, PathType.STREAM_CODEC, (n) -> n.type, ByteBufCodecs.FLOAT, (n) -> n.f, (x, y, z, walkedDistance, costMalus, closed, type, f) -> {
         N node = (N)((Node)factory.apply(x, y, z));
         node.walkedDistance = walkedDistance;
         node.costMalus = costMalus;
         node.closed = closed;
         node.type = type;
         node.f = f;
         return node;
      });
   }

   public Node(final int x, final int y, final int z) {
      super();
      this.type = PathType.BLOCKED;
      this.x = x;
      this.y = y;
      this.z = z;
      this.hash = createHash(x, y, z);
   }

   public Node cloneAndMove(final int x, final int y, final int z) {
      Node node = new Node(x, y, z);
      node.heapIdx = this.heapIdx;
      node.g = this.g;
      node.h = this.h;
      node.f = this.f;
      node.cameFrom = this.cameFrom;
      node.closed = this.closed;
      node.walkedDistance = this.walkedDistance;
      node.costMalus = this.costMalus;
      node.type = this.type;
      return node;
   }

   public static int createHash(final int x, final int y, final int z) {
      return y & 255 | (x & 32767) << 8 | (z & 32767) << 24 | (x < 0 ? -2147483648 : 0) | (z < 0 ? '\u8000' : 0);
   }

   public float distanceTo(final Node to) {
      float xd = (float)(to.x - this.x);
      float yd = (float)(to.y - this.y);
      float zd = (float)(to.z - this.z);
      return Mth.sqrt(xd * xd + yd * yd + zd * zd);
   }

   public float distanceToXZ(final Node to) {
      float xd = (float)(to.x - this.x);
      float zd = (float)(to.z - this.z);
      return Mth.sqrt(xd * xd + zd * zd);
   }

   public float distanceTo(final BlockPos pos) {
      float xd = (float)(pos.getX() - this.x);
      float yd = (float)(pos.getY() - this.y);
      float zd = (float)(pos.getZ() - this.z);
      return Mth.sqrt(xd * xd + yd * yd + zd * zd);
   }

   public float distanceToSqr(final Node to) {
      float xd = (float)(to.x - this.x);
      float yd = (float)(to.y - this.y);
      float zd = (float)(to.z - this.z);
      return xd * xd + yd * yd + zd * zd;
   }

   public float distanceToSqr(final BlockPos pos) {
      float xd = (float)(pos.getX() - this.x);
      float yd = (float)(pos.getY() - this.y);
      float zd = (float)(pos.getZ() - this.z);
      return xd * xd + yd * yd + zd * zd;
   }

   public float distanceManhattan(final Node to) {
      float xd = (float)Math.abs(to.x - this.x);
      float yd = (float)Math.abs(to.y - this.y);
      float zd = (float)Math.abs(to.z - this.z);
      return xd + yd + zd;
   }

   public float distanceManhattan(final BlockPos pos) {
      float xd = (float)Math.abs(pos.getX() - this.x);
      float yd = (float)Math.abs(pos.getY() - this.y);
      float zd = (float)Math.abs(pos.getZ() - this.z);
      return xd + yd + zd;
   }

   public BlockPos asBlockPos() {
      return new BlockPos(this.x, this.y, this.z);
   }

   public Vec3 asVec3() {
      return new Vec3((double)this.x, (double)this.y, (double)this.z);
   }

   public boolean equals(final Object o) {
      if (!(o instanceof Node no)) {
         return false;
      } else {
         return this.hash == no.hash && this.x == no.x && this.y == no.y && this.z == no.z;
      }
   }

   public int hashCode() {
      return this.hash;
   }

   public boolean inOpenSet() {
      return this.heapIdx >= 0;
   }

   public String toString() {
      return "Node{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
   }
}
