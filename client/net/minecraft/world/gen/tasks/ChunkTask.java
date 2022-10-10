package net.minecraft.world.gen.tasks;

import java.util.Map;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.IChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChunkTask {
   private static final Logger field_202841_a = LogManager.getLogger();

   public ChunkTask() {
      super();
   }

   protected ChunkPrimer[] func_202838_a(ChunkStatus var1, int var2, int var3, Map<ChunkPos, ChunkPrimer> var4) {
      int var5 = var1.func_202128_c();
      ChunkPrimer[] var6 = new ChunkPrimer[(1 + 2 * var5) * (1 + 2 * var5)];
      int var7 = 0;

      for(int var8 = -var5; var8 <= var5; ++var8) {
         for(int var9 = -var5; var9 <= var5; ++var9) {
            ChunkPrimer var10 = (ChunkPrimer)var4.get(new ChunkPos(var2 + var9, var3 + var8));
            var10.func_207739_b(var1.func_207794_f());
            var6[var7++] = var10;
         }
      }

      return var6;
   }

   public ChunkPrimer func_202839_a(ChunkStatus var1, World var2, IChunkGenerator<?> var3, Map<ChunkPos, ChunkPrimer> var4, int var5, int var6) {
      ChunkPrimer[] var7 = this.func_202838_a(var1, var5, var6, var4);
      return this.func_202840_a(var1, var2, var3, var7, var5, var6);
   }

   protected abstract ChunkPrimer func_202840_a(ChunkStatus var1, World var2, IChunkGenerator<?> var3, ChunkPrimer[] var4, int var5, int var6);
}
