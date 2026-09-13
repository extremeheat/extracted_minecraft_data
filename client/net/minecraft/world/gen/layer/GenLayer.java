package net.minecraft.world.gen.layer;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;

public abstract class GenLayer {
   private long field_75907_b;
   protected GenLayer field_75909_a;
   private long field_75908_c;
   protected long field_75906_d;

   public static GenLayer[] func_75901_a(long var0, WorldType var2) {
      boolean var3 = false;
      GenLayerIsland var4 = new GenLayerIsland(1L);
      GenLayerFuzzyZoom var11 = new GenLayerFuzzyZoom(2000L, var4);
      GenLayerAddIsland var12 = new GenLayerAddIsland(1L, var11);
      GenLayerZoom var13 = new GenLayerZoom(2001L, var12);
      GenLayerAddIsland var14 = new GenLayerAddIsland(2L, var13);
      GenLayerAddIsland var15 = new GenLayerAddIsland(50L, var14);
      GenLayerAddIsland var16 = new GenLayerAddIsland(70L, var15);
      GenLayerRemoveTooMuchOcean var17 = new GenLayerRemoveTooMuchOcean(2L, var16);
      GenLayerAddSnow var18 = new GenLayerAddSnow(2L, var17);
      GenLayerAddIsland var19 = new GenLayerAddIsland(3L, var18);
      GenLayerEdge var20 = new GenLayerEdge(2L, var19, GenLayerEdge$Mode.COOL_WARM);
      GenLayerEdge var21 = new GenLayerEdge(2L, var20, GenLayerEdge$Mode.HEAT_ICE);
      GenLayerEdge var22 = new GenLayerEdge(3L, var21, GenLayerEdge$Mode.SPECIAL);
      GenLayerZoom var23 = new GenLayerZoom(2002L, var22);
      GenLayerZoom var24 = new GenLayerZoom(2003L, var23);
      GenLayerAddIsland var25 = new GenLayerAddIsland(4L, var24);
      GenLayerAddMushroomIsland var26 = new GenLayerAddMushroomIsland(5L, var25);
      GenLayerDeepOcean var27 = new GenLayerDeepOcean(4L, var26);
      GenLayer var28 = GenLayerZoom.func_75915_a(1000L, var27, 0);
      byte var5 = 4;
      if (var2 == WorldType.field_77135_d) {
         var5 = 6;
      }

      if (var3) {
         var5 = 4;
      }

      GenLayer var6 = GenLayerZoom.func_75915_a(1000L, var28, 0);
      GenLayerRiverInit var29 = new GenLayerRiverInit(100L, var6);
      Object var7 = new GenLayerBiome(200L, var28, var2);
      if (!var3) {
         GenLayer var34 = GenLayerZoom.func_75915_a(1000L, (GenLayer)var7, 2);
         var7 = new GenLayerBiomeEdge(1000L, var34);
      }

      GenLayer var8 = GenLayerZoom.func_75915_a(1000L, var29, 2);
      GenLayerHills var35 = new GenLayerHills(1000L, (GenLayer)var7, var8);
      var6 = GenLayerZoom.func_75915_a(1000L, var29, 2);
      var6 = GenLayerZoom.func_75915_a(1000L, var6, var5);
      GenLayerRiver var32 = new GenLayerRiver(1L, var6);
      GenLayerSmooth var33 = new GenLayerSmooth(1000L, var32);
      var35 = new GenLayerRareBiome(1001L, var35);

      for(int var9 = 0; var9 < var5; ++var9) {
         var35 = new GenLayerZoom((long)(1000 + var9), var35);
         if (var9 == 0) {
            var35 = new GenLayerAddIsland(3L, var35);
         }

         if (var9 == 1) {
            var35 = new GenLayerShore(1000L, var35);
         }
      }

      GenLayerSmooth var37 = new GenLayerSmooth(1000L, var35);
      GenLayerRiverMix var38 = new GenLayerRiverMix(100L, var37, var33);
      GenLayerVoronoiZoom var10 = new GenLayerVoronoiZoom(10L, var38);
      var38.func_75905_a(var0);
      var10.func_75905_a(var0);
      return new GenLayer[]{var38, var10, var38};
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
         try {
            return BiomeGenBase.func_150568_d(var0) != null && BiomeGenBase.func_150568_d(var1) != null
               ? BiomeGenBase.func_150568_d(var0).func_150569_a(BiomeGenBase.func_150568_d(var1))
               : false;
         } catch (Throwable var5) {
            CrashReport var3 = CrashReport.func_85055_a(var5, "Comparing biomes");
            CrashReportCategory var4 = var3.func_85058_a("Biomes being compared");
            var4.func_71507_a("Biome A ID", var0);
            var4.func_71507_a("Biome B ID", var1);
            var4.func_71500_a("Biome A", new GenLayer$1(var0));
            var4.func_71500_a("Biome B", new GenLayer$2(var1));
            throw new ReportedException(var3);
         }
      } else {
         return var1 == BiomeGenBase.field_150607_aa.field_76756_M || var1 == BiomeGenBase.field_150608_ab.field_76756_M;
      }
   }

   protected static boolean func_151618_b(int var0) {
      return var0 == BiomeGenBase.field_76771_b.field_76756_M
         || var0 == BiomeGenBase.field_150575_M.field_76756_M
         || var0 == BiomeGenBase.field_76776_l.field_76756_M;
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
