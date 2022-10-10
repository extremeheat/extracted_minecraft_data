package net.minecraft.world.biome.provider;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.storage.WorldInfo;

public class OverworldBiomeProvider extends BiomeProvider {
   private final BiomeCache field_201542_b = new BiomeCache(this);
   private final GenLayer field_201543_c;
   private final GenLayer field_201544_d;
   private final Biome[] field_205007_e;

   public OverworldBiomeProvider(OverworldBiomeProviderSettings var1) {
      super();
      this.field_205007_e = new Biome[]{Biomes.field_76771_b, Biomes.field_76772_c, Biomes.field_76769_d, Biomes.field_76770_e, Biomes.field_76767_f, Biomes.field_76768_g, Biomes.field_76780_h, Biomes.field_76781_i, Biomes.field_76776_l, Biomes.field_76777_m, Biomes.field_76774_n, Biomes.field_76775_o, Biomes.field_76789_p, Biomes.field_76788_q, Biomes.field_76787_r, Biomes.field_76786_s, Biomes.field_76785_t, Biomes.field_76784_u, Biomes.field_76783_v, Biomes.field_76782_w, Biomes.field_76792_x, Biomes.field_150574_L, Biomes.field_150575_M, Biomes.field_150576_N, Biomes.field_150577_O, Biomes.field_150583_P, Biomes.field_150582_Q, Biomes.field_150585_R, Biomes.field_150584_S, Biomes.field_150579_T, Biomes.field_150578_U, Biomes.field_150581_V, Biomes.field_150580_W, Biomes.field_150588_X, Biomes.field_150587_Y, Biomes.field_150589_Z, Biomes.field_150607_aa, Biomes.field_150608_ab, Biomes.field_203614_T, Biomes.field_203615_U, Biomes.field_203616_V, Biomes.field_203617_W, Biomes.field_203618_X, Biomes.field_203619_Y, Biomes.field_203620_Z, Biomes.field_185441_Q, Biomes.field_185442_R, Biomes.field_185443_S, Biomes.field_185444_T, Biomes.field_150590_f, Biomes.field_150599_m, Biomes.field_185445_W, Biomes.field_185446_X, Biomes.field_185447_Y, Biomes.field_185448_Z, Biomes.field_185429_aa, Biomes.field_185430_ab, Biomes.field_185431_ac, Biomes.field_185432_ad, Biomes.field_185433_ae, Biomes.field_185434_af, Biomes.field_185435_ag, Biomes.field_185436_ah, Biomes.field_185437_ai, Biomes.field_185438_aj, Biomes.field_185439_ak};
      WorldInfo var2 = var1.func_205440_a();
      OverworldGenSettings var3 = var1.func_205442_b();
      GenLayer[] var4 = LayerUtil.func_202824_a(var2.func_76063_b(), var2.func_76067_t(), var3);
      this.field_201543_c = var4[0];
      this.field_201544_d = var4[1];
   }

   @Nullable
   public Biome func_180300_a(BlockPos var1, @Nullable Biome var2) {
      return this.field_201542_b.func_180284_a(var1.func_177958_n(), var1.func_177952_p(), var2);
   }

   public Biome[] func_201535_a(int var1, int var2, int var3, int var4) {
      return this.field_201543_c.func_202833_a(var1, var2, var3, var4, Biomes.field_180279_ad);
   }

   public Biome[] func_201537_a(int var1, int var2, int var3, int var4, boolean var5) {
      return var5 && var3 == 16 && var4 == 16 && (var1 & 15) == 0 && (var2 & 15) == 0 ? this.field_201542_b.func_76839_e(var1, var2) : this.field_201544_d.func_202833_a(var1, var2, var3, var4, Biomes.field_180279_ad);
   }

   public Set<Biome> func_201538_a(int var1, int var2, int var3) {
      int var4 = var1 - var3 >> 2;
      int var5 = var2 - var3 >> 2;
      int var6 = var1 + var3 >> 2;
      int var7 = var2 + var3 >> 2;
      int var8 = var6 - var4 + 1;
      int var9 = var7 - var5 + 1;
      HashSet var10 = Sets.newHashSet();
      Collections.addAll(var10, this.field_201543_c.func_202833_a(var4, var5, var8, var9, (Biome)null));
      return var10;
   }

   @Nullable
   public BlockPos func_180630_a(int var1, int var2, int var3, List<Biome> var4, Random var5) {
      int var6 = var1 - var3 >> 2;
      int var7 = var2 - var3 >> 2;
      int var8 = var1 + var3 >> 2;
      int var9 = var2 + var3 >> 2;
      int var10 = var8 - var6 + 1;
      int var11 = var9 - var7 + 1;
      Biome[] var12 = this.field_201543_c.func_202833_a(var6, var7, var10, var11, (Biome)null);
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

   public boolean func_205004_a(Structure<?> var1) {
      return (Boolean)this.field_205005_a.computeIfAbsent(var1, (var1x) -> {
         Biome[] var2 = this.field_205007_e;
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
         Biome[] var1 = this.field_205007_e;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Biome var4 = var1[var3];
            this.field_205707_b.add(var4.func_203944_q().func_204108_a());
         }
      }

      return this.field_205707_b;
   }

   public void func_73660_a() {
      this.field_201542_b.func_76838_a();
   }
}
