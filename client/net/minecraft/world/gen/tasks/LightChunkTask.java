package net.minecraft.world.gen.tasks;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.lighting.BlockLightEngine;
import net.minecraft.world.lighting.SkyLightEngine;

public class LightChunkTask extends ChunkTask {
   public LightChunkTask() {
      super();
   }

   protected ChunkPrimer func_202840_a(ChunkStatus var1, World var2, IChunkGenerator<?> var3, ChunkPrimer[] var4, int var5, int var6) {
      ChunkPrimer var7 = var4[var4.length / 2];
      WorldGenRegion var8 = new WorldGenRegion(var4, var1.func_202128_c() * 2 + 1, var1.func_202128_c() * 2 + 1, var5, var6, var2);
      var7.func_201588_a(Heightmap.Type.LIGHT_BLOCKING);
      if (var8.func_201675_m().func_191066_m()) {
         (new SkyLightEngine()).func_202675_a(var8, var7);
      }

      (new BlockLightEngine()).func_202677_a(var8, var7);
      var7.func_201574_a(ChunkStatus.LIGHTED);
      return var7;
   }
}
