package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class Node {
   public final int x;
   public final int y;
   public final int z;
   private final int hash;
   public int heapIdx = -1;
   public float g;
   public float h;
   public float f;
   @Nullable
   public Node cameFrom;
   public boolean closed;
   public float walkedDistance;
   public float costMalus;
   public BlockPathTypes type = BlockPathTypes.BLOCKED;

   public Node(int var1, int var2, int var3) {
      super();
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.hash = createHash(var1, var2, var3);
   }

   public Node cloneAndMove(int var1, int var2, int var3) {
      Node var4 = new Node(var1, var2, var3);
      var4.heapIdx = this.heapIdx;
      var4.g = this.g;
      var4.h = this.h;
      var4.f = this.f;
      var4.cameFrom = this.cameFrom;
      var4.closed = this.closed;
      var4.walkedDistance = this.walkedDistance;
      var4.costMalus = this.costMalus;
      var4.type = this.type;
      return var4;
   }

   public static int createHash(int var0, int var1, int var2) {
      return var1 & 0xFF | (var0 & 32767) << 8 | (var2 & 32767) << 24 | (var0 < 0 ? -2147483648 : 0) | (var2 < 0 ? 32768 : 0);
   }

   public float distanceTo(Node var1) {
      float var2 = (float)(var1.x - this.x);
      float var3 = (float)(var1.y - this.y);
      float var4 = (float)(var1.z - this.z);
      return Mth.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public float distanceToXZ(Node var1) {
      float var2 = (float)(var1.x - this.x);
      float var3 = (float)(var1.z - this.z);
      return Mth.sqrt(var2 * var2 + var3 * var3);
   }

   public float distanceTo(BlockPos var1) {
      float var2 = (float)(var1.getX() - this.x);
      float var3 = (float)(var1.getY() - this.y);
      float var4 = (float)(var1.getZ() - this.z);
      return Mth.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public float distanceToSqr(Node var1) {
      float var2 = (float)(var1.x - this.x);
      float var3 = (float)(var1.y - this.y);
      float var4 = (float)(var1.z - this.z);
      return var2 * var2 + var3 * var3 + var4 * var4;
   }

   public float distanceToSqr(BlockPos var1) {
      float var2 = (float)(var1.getX() - this.x);
      float var3 = (float)(var1.getY() - this.y);
      float var4 = (float)(var1.getZ() - this.z);
      return var2 * var2 + var3 * var3 + var4 * var4;
   }

   public float distanceManhattan(Node var1) {
      float var2 = (float)Math.abs(var1.x - this.x);
      float var3 = (float)Math.abs(var1.y - this.y);
      float var4 = (float)Math.abs(var1.z - this.z);
      return var2 + var3 + var4;
   }

   public float distanceManhattan(BlockPos var1) {
      float var2 = (float)Math.abs(var1.getX() - this.x);
      float var3 = (float)Math.abs(var1.getY() - this.y);
      float var4 = (float)Math.abs(var1.getZ() - this.z);
      return var2 + var3 + var4;
   }

   public BlockPos asBlockPos() {
      return new BlockPos(this.x, this.y, this.z);
   }

   public Vec3 asVec3() {
      return new Vec3((double)this.x, (double)this.y, (double)this.z);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof Node)) {
         return false;
      } else {
         Node var2 = (Node)var1;
         return this.hash == var2.hash && this.x == var2.x && this.y == var2.y && this.z == var2.z;
      }
   }

   @Override
   public int hashCode() {
      return this.hash;
   }

   public boolean inOpenSet() {
      return this.heapIdx >= 0;
   }

   @Override
   public String toString() {
      return "Node{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
   }

   public void writeToStream(FriendlyByteBuf var1) {
      var1.writeInt(this.x);
      var1.writeInt(this.y);
      var1.writeInt(this.z);
      var1.writeFloat(this.walkedDistance);
      var1.writeFloat(this.costMalus);
      var1.writeBoolean(this.closed);
      var1.writeEnum(this.type);
      var1.writeFloat(this.f);
   }

   public static Node createFromStream(FriendlyByteBuf var0) {
      Node var1 = new Node(var0.readInt(), var0.readInt(), var0.readInt());
      readContents(var0, var1);
      return var1;
   }

   protected static void readContents(FriendlyByteBuf var0, Node var1) {
      var1.walkedDistance = var0.readFloat();
      var1.costMalus = var0.readFloat();
      var1.closed = var0.readBoolean();
      var1.type = var0.readEnum(BlockPathTypes.class);
      var1.f = var0.readFloat();
   }
}
