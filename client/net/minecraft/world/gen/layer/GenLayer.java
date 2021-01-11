package net.minecraft.world.gen.layer;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.ChunkProviderSettings;

public abstract class GenLayer {
   private long field_75907_b;
   protected GenLayer field_75909_a;
   private long field_75908_c;
   protected long field_75906_d;

   public static GenLayer[] func_180781_a(long var0, WorldType var2, String var3) {
      GenLayerIsland var4 = new GenLayerIsland(1L);
      GenLayerFuzzyZoom var13 = new GenLayerFuzzyZoom(2000L, var4);
      GenLayerAddIsland var14 = new GenLayerAddIsland(1L, var13);
      GenLayerZoom var15 = new GenLayerZoom(2001L, var14);
      var14 = new GenLayerAddIsland(2L, var15);
      var14 = new GenLayerAddIsland(50L, var14);
      var14 = new GenLayerAddIsland(70L, var14);
      GenLayerRemoveTooMuchOcean var16 = new GenLayerRemoveTooMuchOcean(2L, var14);
      GenLayerAddSnow var17 = new GenLayerAddSnow(2L, var16);
      var14 = new GenLayerAddIsland(3L, var17);
      GenLayerEdge var18 = new GenLayerEdge(2L, var14, GenLayerEdge.Mode.COOL_WARM);
      var18 = new GenLayerEdge(2L, var18, GenLayerEdge.Mode.HEAT_ICE);
      var18 = new GenLayerEdge(3L, var18, GenLayerEdge.Mode.SPECIAL);
      var15 = new GenLayerZoom(2002L, var18);
      var15 = new GenLayerZoom(2003L, var15);
      var14 = new GenLayerAddIsland(4L, var15);
      GenLayerAddMushroomIsland var20 = new GenLayerAddMushroomIsland(5L, var14);
      GenLayerDeepOcean var23 = new GenLayerDeepOcean(4L, var20);
      GenLayer var26 = GenLayerZoom.func_75915_a(1000L, var23, 0);
      ChunkProviderSettings var5 = null;
      int var6 = 4;
      int var7 = var6;
      if (var2 == WorldType.field_180271_f && var3.length() > 0) {
         var5 = ChunkProviderSettings.Factory.func_177865_a(var3).func_177864_b();
         var6 = var5.field_177780_G;
         var7 = var5.field_177788_H;
      }

      if (var2 == WorldType.field_77135_d) {
         var6 = 6;
      }

      GenLayer var8 = GenLayerZoom.func_75915_a(1000L, var26, 0);
      GenLayerRiverInit var19 = new GenLayerRiverInit(100L, var8);
      GenLayerBiome var9 = new GenLayerBiome(200L, var26, var2, var3);
      GenLayer var21 = GenLayerZoom.func_75915_a(1000L, var9, 2);
      GenLayerBiomeEdge var24 = new GenLayerBiomeEdge(1000L, var21);
      GenLayer var10 = GenLayerZoom.func_75915_a(1000L, var19, 2);
      GenLayerHills var27 = new GenLayerHills(1000L, var24, var10);
      var8 = GenLayerZoom.func_75915_a(1000L, var19, 2);
      var8 = GenLayerZoom.func_75915_a(1000L, var8, var7);
      GenLayerRiver var22 = new GenLayerRiver(1L, var8);
      GenLayerSmooth var25 = new GenLayerSmooth(1000L, var22);
      Object var28 = new GenLayerRareBiome(1001L, var27);

      for(int var11 = 0; var11 < var6; ++var11) {
         var28 = new GenLayerZoom((long)(1000 + var11), (GenLayer)var28);
         if (var11 == 0) {
            var28 = new GenLayerAddIsland(3L, (GenLayer)var28);
         }

         if (var11 == 1 || var6 == 1) {
            var28 = new GenLayerShore(1000L, (GenLayer)var28);
         }
      }

      GenLayerSmooth var29 = new GenLayerSmooth(1000L, (GenLayer)var28);
      GenLayerRiverMix var30 = new GenLayerRiverMix(100L, var29, var25);
      GenLayerVoronoiZoom var12 = new GenLayerVoronoiZoom(10L, var30);
      var30.func_75905_a(var0);
      var12.func_75905_a(var0);
      return new GenLayer[]{var30, var12, var30};
   }

   public GenLayer(long var1) {
      super();
      this.field_75906_d = var1;
      this.field_75906_d *= this.field_75906_d * 6364136223846793005L + 1442695040888963407L;
      this.field_75906_d += var1;
      this.field_75906_d *= this.field_75906_d * 6364136223846793005L + 1442695040888963407L;
      this.field_75906_d += var1;
      this.field_75906_d *= this.field_75906_d * 6364136223846793005L + 1442695040888963407L;
      this.field_75906_d += var1;
   }

   public void func_75905_a(long var1) {
      this.field_75907_b = var1;
      if (this.field_75909_a != null) {
         this.field_75909_a.func_75905_a(var1);
      }

      this.field_75907_b *= this.field_75907_b * 6364136223846793005L + 1442695040888963407L;
      this.field_75907_b += this.field_75906_d;
      this.field_75907_b *= this.field_75907_b * 6364136223846793005L + 1442695040888963407L;
      this.field_75907_b += this.field_75906_d;
      this.field_75907_b *= this.field_75907_b * 6364136223846793005L + 1442695040888963407L;
      this.field_75907_b += this.field_75906_d;
   }

   public void func_75903_a(long var1, long var3) {
      this.field_75908_c = this.field_75907_b;
      this.field_75908_c *= this.field_75908_c * 6364136223846793005L + 1442695040888963407L;
      this.field_75908_c += var1;
      this.field_75908_c *= this.field_75908_c * 6364136223846793005L + 1442695040888963407L;
      this.field_75908_c += var3;
      this.field_75908_c *= this.field_75908_c * 6364136223846793005L + 1442695040888963407L;
      this.field_75908_c += var1;
      this.field_75908_c *= this.field_75908_c * 6364136223846793005L + 1442695040888963407L;
      this.field_75908_c += var3;
   }

   protected int func_75902_a(int var1) {
      int var2 = (int)((this.field_75908_c >> 24) % (long)var1);
      if (var2 < 0) {
         var2 += var1;
      }

      this.field_75908_c *= this.field_75908_c * 6364136223846793005L + 1442695040888963407L;
      this.field_75908_c += this.field_75907_b;
      return var2;
   }

   public abstract int[] func_75904_a(int var1, int var2, int var3, int var4);

   protected static boolean func_151616_a(int var0, int var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != BiomeGenBase.field_150607_aa.field_76756_M && var0 != BiomeGenBase.field_150608_ab.field_76756_M) {
         final BiomeGenBase var2 = BiomeGenBase.func_150568_d(var0);
         final BiomeGenBase var3 = BiomeGenBase.func_150568_d(var1);

         try {
            return var2 != null && var3 != null ? var2.func_150569_a(var3) : false;
         } catch (Throwable var7) {
            CrashReport var5 = CrashReport.func_85055_a(var7, "Comparing biomes");
            CrashReportCategory var6 = var5.func_85058_a("Biomes being compared");
            var6.func_71507_a("Biome A ID", var0);
            var6.func_71507_a("Biome B ID", var1);
            var6.func_71500_a("Biome A", new Callable<String>() {
               public String call() throws Exception {
                  return String.valueOf(var2);
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            var6.func_71500_a("Biome B", new Callable<String>() {
               public String call() throws Exception {
                  return String.valueOf(var3);
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            throw new ReportedException(var5);
         }
      } else {
         return var1 == BiomeGenBase.field_150607_aa.field_76756_M || var1 == BiomeGenBase.field_150608_ab.field_76756_M;
      }
   }

   protected static boolean func_151618_b(int var0) {
      return var0 == BiomeGenBase.field_76771_b.field_76756_M || var0 == BiomeGenBase.field_150575_M.field_76756_M || var0 == BiomeGenBase.field_76776_l.field_76756_M;
   }

   protected int func_151619_a(int... var1) {
      return var1[this.func_75902_a(var1.length)];
   }

   protected int func_151617_b(int var1, int var2, int var3, int var4) {
      if (var2 == var3 && var3 == var4) {
         return var2;
      } else if (var1 == var2 && var1 == var3) {
         return var1;
      } else if (var1 == var2 && var1 == var4) {
         return var1;
      } else if (var1 == var3 && var1 == var4) {
         return var1;
      } else if (var1 == var2 && var3 != var4) {
         return var1;
      } else if (var1 == var3 && var2 != var4) {
         return var1;
      } else if (var1 == var4 && var2 != var3) {
         return var1;
      } else if (var2 == var3 && var1 != var4) {
         return var2;
      } else if (var2 == var4 && var1 != var3) {
         return var2;
      } else {
         return var3 == var4 && var1 != var2 ? var3 : this.func_151619_a(var1, var2, var3, var4);
      }
   }
}
