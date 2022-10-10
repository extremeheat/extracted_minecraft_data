package net.minecraft.world.biome.provider;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraft.world.gen.feature.structure.Structure;

public class EndBiomeProvider extends BiomeProvider {
   private final NoiseGeneratorSimplex field_201546_a;
   private final SharedSeedRandom field_201547_b;
   private final Biome[] field_205009_d;

   public EndBiomeProvider(EndBiomeProviderSettings var1) {
      super();
      this.field_205009_d = new Biome[]{Biomes.field_76779_k, Biomes.field_201938_R, Biomes.field_201937_Q, Biomes.field_201936_P, Biomes.field_201939_S};
      this.field_201547_b = new SharedSeedRandom(var1.func_205445_a());
      this.field_201547_b.func_202423_a(17292);
      this.field_201546_a = new NoiseGeneratorSimplex(this.field_201547_b);
   }

   @Nullable
   public Biome func_180300_a(BlockPos var1, @Nullable Biome var2) {
      return this.func_201545_a(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4);
   }

   private Biome func_201545_a(int var1, int var2) {
      if ((long)var1 * (long)var1 + (long)var2 * (long)var2 <= 4096L) {
         return Biomes.field_76779_k;
      } else {
         float var3 = this.func_201536_c(var1, var2, 1, 1);
         if (var3 > 40.0F) {
            return Biomes.field_201938_R;
         } else if (var3 >= 0.0F) {
            return Biomes.field_201937_Q;
         } else {
            return var3 < -20.0F ? Biomes.field_201936_P : Biomes.field_201939_S;
         }
      }
   }

   public Biome[] func_201535_a(int var1, int var2, int var3, int var4) {
      return this.func_201539_b(var1, var2, var3, var4);
   }

   public Biome[] func_201537_a(int var1, int var2, int var3, int var4, boolean var5) {
      Biome[] var6 = new Biome[var3 * var4];
      Long2ObjectOpenHashMap var7 = new Long2ObjectOpenHashMap();

      for(int var8 = 0; var8 < var3; ++var8) {
         for(int var9 = 0; var9 < var4; ++var9) {
            int var10 = var8 + var1 >> 4;
            int var11 = var9 + var2 >> 4;
            long var12 = ChunkPos.func_77272_a(var10, var11);
            Biome var14 = (Biome)var7.get(var12);
            if (var14 == null) {
               var14 = this.func_201545_a(var10, var11);
               var7.put(var12, var14);
            }

            var6[var8 + var9 * var3] = var14;
         }
      }

      return var6;
   }

   public Set<Biome> func_201538_a(int var1, int var2, int var3) {
      int var4 = var1 - var3 >> 2;
      int var5 = var2 - var3 >> 2;
      int var6 = var1 + var3 >> 2;
      int var7 = var2 + var3 >> 2;
      int var8 = var6 - var4 + 1;
      int var9 = var7 - var5 + 1;
      return Sets.newHashSet(this.func_201539_b(var4, var5, var8, var9));
   }

   @Nullable
   public BlockPos func_180630_a(int var1, int var2, int var3, List<Biome> var4, Random var5) {
      int var6 = var1 - var3 >> 2;
      int var7 = var2 - var3 >> 2;
      int var8 = var1 + var3 >> 2;
      int var9 = var2 + var3 >> 2;
      int var10 = var8 - var6 + 1;
      int var11 = var9 - var7 + 1;
      Biome[] var12 = this.func_201539_b(var6, var7, var10, var11);
      BlockPos var13 = null;
      int var14 = 0;

      for(int var15 = 0; var15 < var10 * var11; ++var15) {
         int var16 = var6 + var15 % var10 << 2;
         int var17 = var7 + var15 / var10 << 2;
         if (var4.contains(var12[var15])) {
            if (var13 == null || var5.nextInt(var14 + 1) == 0) {
               var13 = new BlockPos(var16, 0, var17);
            }

            ++var14;
         }
      }

      return var13;
   }

   public float func_201536_c(int var1, int var2, int var3, int var4) {
      float var5 = (float)(var1 * 2 + var3);
      float var6 = (float)(var2 * 2 + var4);
      float var7 = 100.0F - MathHelper.func_76129_c(var5 * var5 + var6 * var6) * 8.0F;
      var7 = MathHelper.func_76131_a(var7, -100.0F, 80.0F);

      for(int var8 = -12; var8 <= 12; ++var8) {
         for(int var9 = -12; var9 <= 12; ++var9) {
            long var10 = (long)(var1 + var8);
            long var12 = (long)(var2 + var9);
            if (var10 * var10 + var12 * var12 > 4096L && this.field_201546_a.func_151605_a((double)var10, (double)var12) < -0.8999999761581421D) {
               float var14 = (MathHelper.func_76135_e((float)var10) * 3439.0F + MathHelper.func_76135_e((float)var12) * 147.0F) % 13.0F + 9.0F;
               var5 = (float)(var3 - var8 * 2);
               var6 = (float)(var4 - var9 * 2);
               float var15 = 100.0F - MathHelper.func_76129_c(var5 * var5 + var6 * var6) * var14;
               var15 = MathHelper.func_76131_a(var15, -100.0F, 80.0F);
               var7 = Math.max(var7, var15);
            }
         }
      }

      return var7;
   }

   public boolean func_205004_a(Structure<?> var1) {
      return (Boolean)this.field_205005_a.computeIfAbsent(var1, (var1x) -> {
         Biome[] var2 = this.field_205009_d;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Biome var5 = var2[var4];
            if (var5.func_201858_a(var1x)) {
               return true;
            }
         }

         return false;
      });
   }

   public Set<IBlockState> func_205706_b() {
      if (this.field_205707_b.isEmpty()) {
         Biome[] var1 = this.field_205009_d;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Biome var4 = var1[var3];
            this.field_205707_b.add(var4.func_203944_q().func_204108_a());
         }
      }

      return this.field_205707_b;
   }
}
