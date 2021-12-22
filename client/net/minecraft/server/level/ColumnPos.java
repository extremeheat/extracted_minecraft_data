package net.minecraft.server.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;

public class ColumnPos {
   private static final long COORD_BITS = 32L;
   private static final long COORD_MASK = 4294967295L;
   private static final int HASH_A = 1664525;
   private static final int HASH_C = 1013904223;
   private static final int HASH_Z_XOR = -559038737;
   // $FF: renamed from: x int
   public final int field_143;
   // $FF: renamed from: z int
   public final int field_144;

   public ColumnPos(int var1, int var2) {
      super();
      this.field_143 = var1;
      this.field_144 = var2;
   }

   public ColumnPos(BlockPos var1) {
      super();
      this.field_143 = var1.getX();
      this.field_144 = var1.getZ();
   }

   public ChunkPos toChunkPos() {
      return new ChunkPos(SectionPos.blockToSectionCoord(this.field_143), SectionPos.blockToSectionCoord(this.field_144));
   }

   public long toLong() {
      return asLong(this.field_143, this.field_144);
   }

   public static long asLong(int var0, int var1) {
      return (long)var0 & 4294967295L | ((long)var1 & 4294967295L) << 32;
   }

   public String toString() {
      return "[" + this.field_143 + ", " + this.field_144 + "]";
   }

   public int hashCode() {
      int var1 = 1664525 * this.field_143 + 1013904223;
      int var2 = 1664525 * (this.field_144 ^ -559038737) + 1013904223;
      return var1 ^ var2;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ColumnPos)) {
         return false;
      } else {
         ColumnPos var2 = (ColumnPos)var1;
         return this.field_143 == var2.field_143 && this.field_144 == var2.field_144;
      }
   }
}
