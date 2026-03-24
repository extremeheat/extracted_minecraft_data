package net.minecraft.world.level.chunk;

import java.util.BitSet;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class CarvingMask {
   private final int minY;
   private final BitSet mask;
   private Mask additionalMask = (x, y, z) -> false;

   public CarvingMask(final int height, final int minY) {
      super();
      this.minY = minY;
      this.mask = new BitSet(256 * height);
   }

   public void setAdditionalMask(final Mask additionalMask) {
      this.additionalMask = additionalMask;
   }

   public CarvingMask(final long[] array, final int minY) {
      super();
      this.minY = minY;
      this.mask = BitSet.valueOf(array);
   }

   private int getIndex(final int x, final int y, final int z) {
      return x & 15 | (z & 15) << 4 | y - this.minY << 8;
   }

   public void set(final int x, final int y, final int z) {
      this.mask.set(this.getIndex(x, y, z));
   }

   public boolean get(final int x, final int y, final int z) {
      return this.additionalMask.test(x, y, z) || this.mask.get(this.getIndex(x, y, z));
   }

   public Stream<BlockPos> stream(final ChunkPos pos) {
      return this.mask.stream().mapToObj((i) -> {
         int x = i & 15;
         int z = i >> 4 & 15;
         int y = i >> 8;
         return pos.getBlockAt(x, y + this.minY, z);
      });
   }

   public long[] toArray() {
      return this.mask.toLongArray();
   }

   public interface Mask {
      boolean test(int x, int y, int z);
   }
}
