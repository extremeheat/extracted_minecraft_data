package net.minecraft.world.level;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public class ChunkPos {
   private static final int SAFETY_MARGIN = 1056;
   public static final long INVALID_CHUNK_POS = asLong(1875066, 1875066);
   public static final ChunkPos ZERO = new ChunkPos(0, 0);
   private static final long COORD_BITS = 32L;
   private static final long COORD_MASK = 4294967295L;
   private static final int REGION_BITS = 5;
   private static final int REGION_MASK = 31;
   // $FF: renamed from: x int
   public final int field_504;
   // $FF: renamed from: z int
   public final int field_505;
   private static final int HASH_A = 1664525;
   private static final int HASH_C = 1013904223;
   private static final int HASH_Z_XOR = -559038737;

   public ChunkPos(int var1, int var2) {
      super();
      this.field_504 = var1;
      this.field_505 = var2;
   }

   public ChunkPos(BlockPos var1) {
      super();
      this.field_504 = SectionPos.blockToSectionCoord(var1.getX());
      this.field_505 = SectionPos.blockToSectionCoord(var1.getZ());
   }

   public ChunkPos(long var1) {
      super();
      this.field_504 = (int)var1;
      this.field_505 = (int)(var1 >> 32);
   }

   public long toLong() {
      return asLong(this.field_504, this.field_505);
   }

   public static long asLong(int var0, int var1) {
      return (long)var0 & 4294967295L | ((long)var1 & 4294967295L) << 32;
   }

   public static long asLong(BlockPos var0) {
      return asLong(SectionPos.blockToSectionCoord(var0.getX()), SectionPos.blockToSectionCoord(var0.getZ()));
   }

   public static int getX(long var0) {
      return (int)(var0 & 4294967295L);
   }

   public static int getZ(long var0) {
      return (int)(var0 >>> 32 & 4294967295L);
   }

   public int hashCode() {
      int var1 = 1664525 * this.field_504 + 1013904223;
      int var2 = 1664525 * (this.field_505 ^ -559038737) + 1013904223;
      return var1 ^ var2;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ChunkPos)) {
         return false;
      } else {
         ChunkPos var2 = (ChunkPos)var1;
         return this.field_504 == var2.field_504 && this.field_505 == var2.field_505;
      }
   }

   public int getMiddleBlockX() {
      return this.getBlockX(8);
   }

   public int getMiddleBlockZ() {
      return this.getBlockZ(8);
   }

   public int getMinBlockX() {
      return SectionPos.sectionToBlockCoord(this.field_504);
   }

   public int getMinBlockZ() {
      return SectionPos.sectionToBlockCoord(this.field_505);
   }

   public int getMaxBlockX() {
      return this.getBlockX(15);
   }

   public int getMaxBlockZ() {
      return this.getBlockZ(15);
   }

   public int getRegionX() {
      return this.field_504 >> 5;
   }

   public int getRegionZ() {
      return this.field_505 >> 5;
   }

   public int getRegionLocalX() {
      return this.field_504 & 31;
   }

   public int getRegionLocalZ() {
      return this.field_505 & 31;
   }

   public BlockPos getBlockAt(int var1, int var2, int var3) {
      return new BlockPos(this.getBlockX(var1), var2, this.getBlockZ(var3));
   }

   public int getBlockX(int var1) {
      return SectionPos.sectionToBlockCoord(this.field_504, var1);
   }

   public int getBlockZ(int var1) {
      return SectionPos.sectionToBlockCoord(this.field_505, var1);
   }

   public BlockPos getMiddleBlockPosition(int var1) {
      return new BlockPos(this.getMiddleBlockX(), var1, this.getMiddleBlockZ());
   }

   public String toString() {
      return "[" + this.field_504 + ", " + this.field_505 + "]";
   }

   public BlockPos getWorldPosition() {
      return new BlockPos(this.getMinBlockX(), 0, this.getMinBlockZ());
   }

   public int getChessboardDistance(ChunkPos var1) {
      return Math.max(Math.abs(this.field_504 - var1.field_504), Math.abs(this.field_505 - var1.field_505));
   }

   public static Stream<ChunkPos> rangeClosed(ChunkPos var0, int var1) {
      return rangeClosed(new ChunkPos(var0.field_504 - var1, var0.field_505 - var1), new ChunkPos(var0.field_504 + var1, var0.field_505 + var1));
   }

   public static Stream<ChunkPos> rangeClosed(final ChunkPos var0, final ChunkPos var1) {
      int var2 = Math.abs(var0.field_504 - var1.field_504) + 1;
      int var3 = Math.abs(var0.field_505 - var1.field_505) + 1;
      final int var4 = var0.field_504 < var1.field_504 ? 1 : -1;
      final int var5 = var0.field_505 < var1.field_505 ? 1 : -1;
      return StreamSupport.stream(new AbstractSpliterator<ChunkPos>((long)(var2 * var3), 64) {
         @Nullable
         private ChunkPos pos;

         public boolean tryAdvance(Consumer<? super ChunkPos> var1x) {
            if (this.pos == null) {
               this.pos = var0;
            } else {
               int var2 = this.pos.field_504;
               int var3 = this.pos.field_505;
               if (var2 == var1.field_504) {
                  if (var3 == var1.field_505) {
                     return false;
                  }

                  this.pos = new ChunkPos(var0.field_504, var3 + var5);
               } else {
                  this.pos = new ChunkPos(var2 + var4, var3);
               }
            }

            var1x.accept(this.pos);
            return true;
         }
      }, false);
   }
}
