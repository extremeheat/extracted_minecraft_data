package net.minecraft.world.level.levelgen.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ConfiguredWorldCarver {
   public final WorldCarver worldCarver;
   public final CarverConfiguration config;

   public ConfiguredWorldCarver(WorldCarver var1, CarverConfiguration var2) {
      this.worldCarver = var1;
      this.config = var2;
   }

   public boolean isStartChunk(Random var1, int var2, int var3) {
      return this.worldCarver.isStartChunk(var1, var2, var3, this.config);
   }

   public boolean carve(ChunkAccess var1, Function var2, Random var3, int var4, int var5, int var6, int var7, int var8, BitSet var9) {
      return this.worldCarver.carve(var1, var2, var3, var4, var5, var6, var7, var8, var9, this.config);
   }
}
