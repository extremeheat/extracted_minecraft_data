package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.IdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkBiomeContainer implements BiomeManager.NoiseBiomeSource {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int WIDTH_BITS = (int)Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;
   private static final int HEIGHT_BITS = (int)Math.round(Math.log(256.0D) / Math.log(2.0D)) - 2;
   public static final int BIOMES_SIZE;
   public static final int HORIZONTAL_MASK;
   public static final int VERTICAL_MASK;
   private final IdMap<Biome> biomeRegistry;
   private final Biome[] biomes;

   public ChunkBiomeContainer(IdMap<Biome> var1, Biome[] var2) {
      super();
      this.biomeRegistry = var1;
      this.biomes = var2;
   }

   private ChunkBiomeContainer(IdMap<Biome> var1) {
      this(var1, new Biome[BIOMES_SIZE]);
   }

   public ChunkBiomeContainer(IdMap<Biome> var1, int[] var2) {
      this(var1);

      for(int var3 = 0; var3 < this.biomes.length; ++var3) {
         int var4 = var2[var3];
         Biome var5 = (Biome)var1.byId(var4);
         if (var5 == null) {
            LOGGER.warn("Received invalid biome id: {}", var4);
            this.biomes[var3] = (Biome)var1.byId(0);
         } else {
            this.biomes[var3] = var5;
         }
      }

   }

   public ChunkBiomeContainer(IdMap<Biome> var1, ChunkPos var2, BiomeSource var3) {
      this(var1);
      int var4 = var2.getMinBlockX() >> 2;
      int var5 = var2.getMinBlockZ() >> 2;

      for(int var6 = 0; var6 < this.biomes.length; ++var6) {
         int var7 = var6 & HORIZONTAL_MASK;
         int var8 = var6 >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK;
         int var9 = var6 >> WIDTH_BITS & HORIZONTAL_MASK;
         this.biomes[var6] = var3.getNoiseBiome(var4 + var7, var8, var5 + var9);
      }

   }

   public ChunkBiomeContainer(IdMap<Biome> var1, ChunkPos var2, BiomeSource var3, @Nullable int[] var4) {
      this(var1);
      int var5 = var2.getMinBlockX() >> 2;
      int var6 = var2.getMinBlockZ() >> 2;
      int var7;
      int var8;
      int var9;
      int var10;
      if (var4 != null) {
         for(var7 = 0; var7 < var4.length; ++var7) {
            this.biomes[var7] = (Biome)var1.byId(var4[var7]);
            if (this.biomes[var7] == null) {
               var8 = var7 & HORIZONTAL_MASK;
               var9 = var7 >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK;
               var10 = var7 >> WIDTH_BITS & HORIZONTAL_MASK;
               this.biomes[var7] = var3.getNoiseBiome(var5 + var8, var9, var6 + var10);
            }
         }
      } else {
         for(var7 = 0; var7 < this.biomes.length; ++var7) {
            var8 = var7 & HORIZONTAL_MASK;
            var9 = var7 >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK;
            var10 = var7 >> WIDTH_BITS & HORIZONTAL_MASK;
            this.biomes[var7] = var3.getNoiseBiome(var5 + var8, var9, var6 + var10);
         }
      }

   }

   public int[] writeBiomes() {
      int[] var1 = new int[this.biomes.length];

      for(int var2 = 0; var2 < this.biomes.length; ++var2) {
         var1[var2] = this.biomeRegistry.getId(this.biomes[var2]);
      }

      return var1;
   }

   public Biome getNoiseBiome(int var1, int var2, int var3) {
      int var4 = var1 & HORIZONTAL_MASK;
      int var5 = Mth.clamp(var2, 0, VERTICAL_MASK);
      int var6 = var3 & HORIZONTAL_MASK;
      return this.biomes[var5 << WIDTH_BITS + WIDTH_BITS | var6 << WIDTH_BITS | var4];
   }

   static {
      BIOMES_SIZE = 1 << WIDTH_BITS + WIDTH_BITS + HEIGHT_BITS;
      HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1;
      VERTICAL_MASK = (1 << HEIGHT_BITS) - 1;
   }
}
