package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class Node {
   // $FF: renamed from: x int
   public final int field_333;
   // $FF: renamed from: y int
   public final int field_334;
   // $FF: renamed from: z int
   public final int field_335;
   private final int hash;
   public int heapIdx = -1;
   // $FF: renamed from: g float
   public float field_336;
   // $FF: renamed from: h float
   public float field_337;
   // $FF: renamed from: f float
   public float field_338;
   @Nullable
   public Node cameFrom;
   public boolean closed;
   public float walkedDistance;
   public float costMalus;
   public BlockPathTypes type;

   public Node(int var1, int var2, int var3) {
      super();
      this.type = BlockPathTypes.BLOCKED;
      this.field_333 = var1;
      this.field_334 = var2;
      this.field_335 = var3;
      this.hash = createHash(var1, var2, var3);
   }

   public Node cloneAndMove(int var1, int var2, int var3) {
      Node var4 = new Node(var1, var2, var3);
      var4.heapIdx = this.heapIdx;
      var4.field_336 = this.field_336;
      var4.field_337 = this.field_337;
      var4.field_338 = this.field_338;
      var4.cameFrom = this.cameFrom;
      var4.closed = this.closed;
      var4.walkedDistance = this.walkedDistance;
      var4.costMalus = this.costMalus;
      var4.type = this.type;
      return var4;
   }

   public static int createHash(int var0, int var1, int var2) {
      return var1 & 255 | (var0 & 32767) << 8 | (var2 & 32767) << 24 | (var0 < 0 ? -2147483648 : 0) | (var2 < 0 ? '\u8000' : 0);
   }

   public float distanceTo(Node var1) {
      float var2 = (float)(var1.field_333 - this.field_333);
      float var3 = (float)(var1.field_334 - this.field_334);
      float var4 = (float)(var1.field_335 - this.field_335);
      return Mth.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public float distanceTo(BlockPos var1) {
      float var2 = (float)(var1.getX() - this.field_333);
      float var3 = (float)(var1.getY() - this.field_334);
      float var4 = (float)(var1.getZ() - this.field_335);
      return Mth.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public float distanceToSqr(Node var1) {
      float var2 = (float)(var1.field_333 - this.field_333);
      float var3 = (float)(var1.field_334 - this.field_334);
      float var4 = (float)(var1.field_335 - this.field_335);
      return var2 * var2 + var3 * var3 + var4 * var4;
   }

   public float distanceToSqr(BlockPos var1) {
      float var2 = (float)(var1.getX() - this.field_333);
      float var3 = (float)(var1.getY() - this.field_334);
      float var4 = (float)(var1.getZ() - this.field_335);
      return var2 * var2 + var3 * var3 + var4 * var4;
   }

   public float distanceManhattan(Node var1) {
      float var2 = (float)Math.abs(var1.field_333 - this.field_333);
      float var3 = (float)Math.abs(var1.field_334 - this.field_334);
      float var4 = (float)Math.abs(var1.field_335 - this.field_335);
      return var2 + var3 + var4;
   }

   public float distanceManhattan(BlockPos var1) {
      float var2 = (float)Math.abs(var1.getX() - this.field_333);
      float var3 = (float)Math.abs(var1.getY() - this.field_334);
      float var4 = (float)Math.abs(var1.getZ() - this.field_335);
      return var2 + var3 + var4;
   }

   public BlockPos asBlockPos() {
      return new BlockPos(this.field_333, this.field_334, this.field_335);
   }

   public Vec3 asVec3() {
      return new Vec3((double)this.field_333, (double)this.field_334, (double)this.field_335);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Node)) {
         return false;
      } else {
         Node var2 = (Node)var1;
         return this.hash == var2.hash && this.field_333 == var2.field_333 && this.field_334 == var2.field_334 && this.field_335 == var2.field_335;
      }
   }

   public int hashCode() {
      return this.hash;
   }

   public boolean inOpenSet() {
      return this.heapIdx >= 0;
   }

   public String toString() {
      return "Node{x=" + this.field_333 + ", y=" + this.field_334 + ", z=" + this.field_335 + "}";
   }

   public void writeToStream(FriendlyByteBuf var1) {
      var1.writeInt(this.field_333);
      var1.writeInt(this.field_334);
      var1.writeInt(this.field_335);
      var1.writeFloat(this.walkedDistance);
      var1.writeFloat(this.costMalus);
      var1.writeBoolean(this.closed);
      var1.writeInt(this.type.ordinal());
      var1.writeFloat(this.field_338);
   }

   public static Node createFromStream(FriendlyByteBuf var0) {
      Node var1 = new Node(var0.readInt(), var0.readInt(), var0.readInt());
      var1.walkedDistance = var0.readFloat();
      var1.costMalus = var0.readFloat();
      var1.closed = var0.readBoolean();
      var1.type = BlockPathTypes.values()[var0.readInt()];
      var1.field_338 = var0.readFloat();
      return var1;
   }
}
