package net.minecraft.world.level.levelgen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ConfiguredWorldCarver<WC extends CarverConfiguration> {
   public final WorldCarver<WC> worldCarver;
   public final WC config;

   public ConfiguredWorldCarver(WorldCarver<WC> var1, WC var2) {
      super();
      this.worldCarver = var1;
      this.config = var2;
   }

   public boolean isStartChunk(Random var1, int var2, int var3) {
      return this.worldCarver.isStartChunk(var1, var2, var3, this.config);
   }

   public boolean carve(ChunkAccess var1, Random var2, int var3, int var4, int var5, int var6, int var7, BitSet var8) {
      return this.worldCarver.carve(var1, var2, var3, var4, var5, var6, var7, var8, this.config);
   }
}
