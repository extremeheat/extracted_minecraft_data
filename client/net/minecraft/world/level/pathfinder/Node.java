package net.minecraft.world.level.pathfinder;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

public class Node {
   public final int x;
   public final int y;
   public final int z;
   private final int hash;
   public int heapIdx = -1;
   public float g;
   public float h;
   public float f;
   public Node cameFrom;
   public boolean closed;
   public float walkedDistance;
   public float costMalus;
   public BlockPathTypes type;

   public Node(int var1, int var2, int var3) {
      super();
      this.type = BlockPathTypes.BLOCKED;
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.hash = createHash(var1, var2, var3);
   }

   public Node cloneMove(int var1, int var2, int var3) {
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
      return var1 & 255 | (var0 & 32767) << 8 | (var2 & 32767) << 24 | (var0 < 0 ? -2147483648 : 0) | (var2 < 0 ? '\u8000' : 0);
   }

   public float distanceTo(Node var1) {
      float var2 = (float)(var1.x - this.x);
      float var3 = (float)(var1.y - this.y);
      float var4 = (float)(var1.z - this.z);
      return Mth.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public float distanceToSqr(Node var1) {
      float var2 = (float)(var1.x - this.x);
      float var3 = (float)(var1.y - this.y);
      float var4 = (float)(var1.z - this.z);
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

   public boolean equals(Object var1) {
      if (!(var1 instanceof Node)) {
         return false;
      } else {
         Node var2 = (Node)var1;
         return this.hash == var2.hash && this.x == var2.x && this.y == var2.y && this.z == var2.z;
      }
   }

   public int hashCode() {
      return this.hash;
   }

   public boolean inOpenSet() {
      return this.heapIdx >= 0;
   }

   public String toString() {
      return "Node{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
   }

   public static Node createFromStream(FriendlyByteBuf var0) {
      Node var1 = new Node(var0.readInt(), var0.readInt(), var0.readInt());
      var1.walkedDistance = var0.readFloat();
      var1.costMalus = var0.readFloat();
      var1.closed = var0.readBoolean();
      var1.type = BlockPathTypes.values()[var0.readInt()];
      var1.f = var0.readFloat();
      return var1;
   }
}
