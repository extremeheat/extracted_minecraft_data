package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class TheEndBiomeSource extends BiomeSource {
   public static final Codec<TheEndBiomeSource> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter((var0x) -> {
         return var0x.biomes;
      }), Codec.LONG.fieldOf("seed").stable().forGetter((var0x) -> {
         return var0x.seed;
      })).apply(var0, var0.stable(TheEndBiomeSource::new));
   });
   private final SimplexNoise islandNoise;
   private final Registry<Biome> biomes;
   private final long seed;
   private final Biome end;
   private final Biome highlands;
   private final Biome midlands;
   private final Biome islands;
   private final Biome barrens;

   public TheEndBiomeSource(Registry<Biome> var1, long var2) {
      this(var1, var2, (Biome)var1.getOrThrow(Biomes.THE_END), (Biome)var1.getOrThrow(Biomes.END_HIGHLANDS), (Biome)var1.getOrThrow(Biomes.END_MIDLANDS), (Biome)var1.getOrThrow(Biomes.SMALL_END_ISLANDS), (Biome)var1.getOrThrow(Biomes.END_BARRENS));
   }

   private TheEndBiomeSource(Registry<Biome> var1, long var2, Biome var4, Biome var5, Biome var6, Biome var7, Biome var8) {
      super((List)ImmutableList.of(var4, var5, var6, var7, var8));
      this.biomes = var1;
      this.seed = var2;
      this.end = var4;
      this.highlands = var5;
      this.midlands = var6;
      this.islands = var7;
      this.barrens = var8;
      WorldgenRandom var9 = new WorldgenRandom(var2);
      var9.consumeCount(17292);
      this.islandNoise = new SimplexNoise(var9);
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public BiomeSource withSeed(long var1) {
      return new TheEndBiomeSource(this.biomes, var1, this.end, this.highlands, this.midlands, this.islands, this.barrens);
   }

   public Biome getNoiseBiome(int var1, int var2, int var3) {
      int var4 = var1 >> 2;
      int var5 = var3 >> 2;
      if ((long)var4 * (long)var4 + (long)var5 * (long)var5 <= 4096L) {
         return this.end;
      } else {
         float var6 = getHeightValue(this.islandNoise, var4 * 2 + 1, var5 * 2 + 1);
         if (var6 > 40.0F) {
            return this.highlands;
         } else if (var6 >= 0.0F) {
            return this.midlands;
         } else {
            return var6 < -20.0F ? this.islands : this.barrens;
         }
      }
   }

   public boolean stable(long var1) {
      return this.seed == var1;
   }

   public static float getHeightValue(SimplexNoise var0, int var1, int var2) {
      int var3 = var1 / 2;
      int var4 = var2 / 2;
      int var5 = var1 % 2;
      int var6 = var2 % 2;
      float var7 = 100.0F - Mth.sqrt((float)(var1 * var1 + var2 * var2)) * 8.0F;
      var7 = Mth.clamp(var7, -100.0F, 80.0F);

      for(int var8 = -12; var8 <= 12; ++var8) {
         for(int var9 = -12; var9 <= 12; ++var9) {
            long var10 = (long)(var3 + var8);
            long var12 = (long)(var4 + var9);
            if (var10 * var10 + var12 * var12 > 4096L && var0.getValue((double)var10, (double)var12) < -0.8999999761581421D) {
               float var14 = (Mth.abs((float)var10) * 3439.0F + Mth.abs((float)var12) * 147.0F) % 13.0F + 9.0F;
               float var15 = (float)(var5 - var8 * 2);
               float var16 = (float)(var6 - var9 * 2);
               float var17 = 100.0F - Mth.sqrt(var15 * var15 + var16 * var16) * var14;
               var17 = Mth.clamp(var17, -100.0F, 80.0F);
               var7 = Math.max(var7, var17);
            }
         }
      }

      return var7;
   }
}
