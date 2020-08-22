package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkBiomeContainer implements BiomeManager.NoiseBiomeSource {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int WIDTH_BITS = (int)Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;
   private static final int HEIGHT_BITS = (int)Math.round(Math.log(256.0D) / Math.log(2.0D)) - 2;
   public static final int BIOMES_SIZE;
   public static final int HORIZONTAL_MASK;
   public static final int VERTICAL_MASK;
   private final Biome[] biomes;

   public ChunkBiomeContainer(Biome[] var1) {
      this.biomes = var1;
   }

   private ChunkBiomeContainer() {
      this(new Biome[BIOMES_SIZE]);
   }

   public ChunkBiomeContainer(FriendlyByteBuf var1) {
      this();

      for(int var2 = 0; var2 < this.biomes.length; ++var2) {
         int var3 = var1.readInt();
         Biome var4 = (Biome)Registry.BIOME.byId(var3);
         if (var4 == null) {
            LOGGER.warn("Received invalid biome id: " + var3);
            this.biomes[var2] = Biomes.PLAINS;
         } else {
            this.biomes[var2] = var4;
         }
      }

   }

   public ChunkBiomeContainer(ChunkPos var1, BiomeSource var2) {
      this();
      int var3 = var1.getMinBlockX() >> 2;
      int var4 = var1.getMinBlockZ() >> 2;

      for(int var5 = 0; var5 < this.biomes.length; ++var5) {
         int var6 = var5 & HORIZONTAL_MASK;
         int var7 = var5 >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK;
         int var8 = var5 >> WIDTH_BITS & HORIZONTAL_MASK;
         this.biomes[var5] = var2.getNoiseBiome(var3 + var6, var7, var4 + var8);
      }

   }

   public ChunkBiomeContainer(ChunkPos var1, BiomeSource var2, @Nullable int[] var3) {
      this();
      int var4 = var1.getMinBlockX() >> 2;
      int var5 = var1.getMinBlockZ() >> 2;
      int var6;
      int var7;
      int var8;
      int var9;
      if (var3 != null) {
         for(var6 = 0; var6 < var3.length; ++var6) {
            this.biomes[var6] = (Biome)Registry.BIOME.byId(var3[var6]);
            if (this.biomes[var6] == null) {
               var7 = var6 & HORIZONTAL_MASK;
               var8 = var6 >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK;
               var9 = var6 >> WIDTH_BITS & HORIZONTAL_MASK;
               this.biomes[var6] = var2.getNoiseBiome(var4 + var7, var8, var5 + var9);
            }
         }
      } else {
         for(var6 = 0; var6 < this.biomes.length; ++var6) {
            var7 = var6 & HORIZONTAL_MASK;
            var8 = var6 >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK;
            var9 = var6 >> WIDTH_BITS & HORIZONTAL_MASK;
            this.biomes[var6] = var2.getNoiseBiome(var4 + var7, var8, var5 + var9);
         }
      }

   }

   public int[] writeBiomes() {
      int[] var1 = new int[this.biomes.length];

      for(int var2 = 0; var2 < this.biomes.length; ++var2) {
         var1[var2] = Registry.BIOME.getId(this.biomes[var2]);
      }

      return var1;
   }

   public void write(FriendlyByteBuf var1) {
      Biome[] var2 = this.biomes;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Biome var5 = var2[var4];
         var1.writeInt(Registry.BIOME.getId(var5));
      }

   }

   public ChunkBiomeContainer copy() {
      return new ChunkBiomeContainer((Biome[])this.biomes.clone());
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
