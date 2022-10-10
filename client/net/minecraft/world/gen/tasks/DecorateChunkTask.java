package net.minecraft.world.gen.tasks;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;

public class DecorateChunkTask extends ChunkTask {
   public DecorateChunkTask() {
      super();
   }

   protected ChunkPrimer func_202840_a(ChunkStatus var1, World var2, IChunkGenerator<?> var3, ChunkPrimer[] var4, int var5, int var6) {
      var3.func_202092_b(new WorldGenRegion(var4, var1.func_202128_c() * 2 + 1, var1.func_202128_c() * 2 + 1, var5, var6, var2));
      ChunkPrimer var7 = var4[var4.length / 2];
      var7.func_201574_a(ChunkStatus.DECORATED);
      return var7;
   }
}
