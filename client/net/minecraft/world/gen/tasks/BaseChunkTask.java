package net.minecraft.world.gen.tasks;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.IChunkGenerator;

public class BaseChunkTask extends ChunkTask {
   public BaseChunkTask() {
      super();
   }

   protected ChunkPrimer func_202840_a(ChunkStatus var1, World var2, IChunkGenerator<?> var3, ChunkPrimer[] var4, int var5, int var6) {
      ChunkPrimer var7 = var4[var4.length / 2];
      var3.func_202088_a(var7);
      return var7;
   }
}
