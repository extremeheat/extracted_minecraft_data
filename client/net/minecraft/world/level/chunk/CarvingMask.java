package net.minecraft.world.level.chunk;

import java.util.BitSet;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class CarvingMask {
   private final int minY;
   private final BitSet mask;
   private CarvingMask.Mask additionalMask = (var0, var1x, var2x) -> {
      return false;
   };

   public CarvingMask(int var1, int var2) {
      super();
      this.minY = var2;
      this.mask = new BitSet(256 * var1);
   }

   public void setAdditionalMask(CarvingMask.Mask var1) {
      this.additionalMask = var1;
   }

   public CarvingMask(long[] var1, int var2) {
      super();
      this.minY = var2;
      this.mask = BitSet.valueOf(var1);
   }

   private int getIndex(int var1, int var2, int var3) {
      return var1 & 15 | (var3 & 15) << 4 | var2 - this.minY << 8;
   }

   public void set(int var1, int var2, int var3) {
      this.mask.set(this.getIndex(var1, var2, var3));
   }

   public boolean get(int var1, int var2, int var3) {
      return this.additionalMask.test(var1, var2, var3) || this.mask.get(this.getIndex(var1, var2, var3));
   }

   public Stream<BlockPos> stream(ChunkPos var1) {
      return this.mask.stream().mapToObj((var2) -> {
         int var3 = var2 & 15;
         int var4 = var2 >> 4 & 15;
         int var5 = var2 >> 8;
         return var1.getBlockAt(var3, var5 + this.minY, var4);
      });
   }

   public long[] toArray() {
      return this.mask.toLongArray();
   }

   public interface Mask {
      boolean test(int var1, int var2, int var3);
   }
}
