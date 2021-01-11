package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class WorldChunkManager {
   private GenLayer field_76944_d;
   private GenLayer field_76945_e;
   private BiomeCache field_76942_f;
   private List<BiomeGenBase> field_76943_g;
   private String field_180301_f;

   protected WorldChunkManager() {
      super();
      this.field_76942_f = new BiomeCache(this);
      this.field_180301_f = "";
      this.field_76943_g = Lists.newArrayList();
      this.field_76943_g.add(BiomeGenBase.field_76767_f);
      this.field_76943_g.add(BiomeGenBase.field_76772_c);
      this.field_76943_g.add(BiomeGenBase.field_76768_g);
      this.field_76943_g.add(BiomeGenBase.field_76784_u);
      this.field_76943_g.add(BiomeGenBase.field_76785_t);
      this.field_76943_g.add(BiomeGenBase.field_76782_w);
      this.field_76943_g.add(BiomeGenBase.field_76792_x);
   }

   public WorldChunkManager(long var1, WorldType var3, String var4) {
      this();
      this.field_180301_f = var4;
      GenLayer[] var5 = GenLayer.func_180781_a(var1, var3, var4);
      this.field_76944_d = var5[0];
      this.field_76945_e = var5[1];
   }

   public WorldChunkManager(World var1) {
      this(var1.func_72905_C(), var1.func_72912_H().func_76067_t(), var1.func_72912_H().func_82571_y());
   }

   public List<BiomeGenBase> func_76932_a() {
      return this.field_76943_g;
   }

   public BiomeGenBase func_180631_a(BlockPos var1) {
      return this.func_180300_a(var1, (BiomeGenBase)null);
   }

   public BiomeGenBase func_180300_a(BlockPos var1, BiomeGenBase var2) {
      return this.field_76942_f.func_180284_a(var1.func_177958_n(), var1.func_177952_p(), var2);
   }

   public float[] func_76936_a(float[] var1, int var2, int var3, int var4, int var5) {
      IntCache.func_76446_a();
      if (var1 == null || var1.length < var4 * var5) {
         var1 = new float[var4 * var5];
      }

      int[] var6 = this.field_76945_e.func_75904_a(var2, var3, var4, var5);

      for(int var7 = 0; var7 < var4 * var5; ++var7) {
         try {
            float var8 = (float)BiomeGenBase.func_180276_a(var6[var7], BiomeGenBase.field_180279_ad).func_76744_g() / 65536.0F;
            if (var8 > 1.0F) {
               var8 = 1.0F;
            }

            var1[var7] = var8;
         } catch (Throwable var11) {
            CrashReport var9 = CrashReport.func_85055_a(var11, "Invalid Biome id");
            CrashReportCategory var10 = var9.func_85058_a("DownfallBlock");
            var10.func_71507_a("biome id", var7);
            var10.func_71507_a("downfalls[] size", var1.length);
            var10.func_71507_a("x", var2);
            var10.func_71507_a("z", var3);
            var10.func_71507_a("w", var4);
            var10.func_71507_a("h", var5);
            throw new ReportedException(var9);
         }
      }

      return var1;
   }

   public float func_76939_a(float var1, int var2) {
      return var1;
   }

   public BiomeGenBase[] func_76937_a(BiomeGenBase[] var1, int var2, int var3, int var4, int var5) {
      IntCache.func_76446_a();
      if (var1 == null || var1.length < var4 * var5) {
         var1 = new BiomeGenBase[var4 * var5];
      }

      int[] var6 = this.field_76944_d.func_75904_a(var2, var3, var4, var5);

      try {
         for(int var7 = 0; var7 < var4 * var5; ++var7) {
            var1[var7] = BiomeGenBase.func_180276_a(var6[var7], BiomeGenBase.field_180279_ad);
         }

         return var1;
      } catch (Throwable var10) {
         CrashReport var8 = CrashReport.func_85055_a(var10, "Invalid Biome id");
         CrashReportCategory var9 = var8.func_85058_a("RawBiomeBlock");
         var9.func_71507_a("biomes[] size", var1.length);
         var9.func_71507_a("x", var2);
         var9.func_71507_a("z", var3);
         var9.func_71507_a("w", var4);
         var9.func_71507_a("h", var5);
         throw new ReportedException(var8);
      }
   }

   public BiomeGenBase[] func_76933_b(BiomeGenBase[] var1, int var2, int var3, int var4, int var5) {
      return this.func_76931_a(var1, var2, var3, var4, var5, true);
   }

   public BiomeGenBase[] func_76931_a(BiomeGenBase[] var1, int var2, int var3, int var4, int var5, boolean var6) {
      IntCache.func_76446_a();
      if (var1 == null || var1.length < var4 * var5) {
         var1 = new BiomeGenBase[var4 * var5];
      }

      if (var6 && var4 == 16 && var5 == 16 && (var2 & 15) == 0 && (var3 & 15) == 0) {
         BiomeGenBase[] var9 = this.field_76942_f.func_76839_e(var2, var3);
         System.arraycopy(var9, 0, var1, 0, var4 * var5);
         return var1;
      } else {
         int[] var7 = this.field_76945_e.func_75904_a(var2, var3, var4, var5);

         for(int var8 = 0; var8 < var4 * var5; ++var8) {
            var1[var8] = BiomeGenBase.func_180276_a(var7[var8], BiomeGenBase.field_180279_ad);
         }

         return var1;
      }
   }

   public boolean func_76940_a(int var1, int var2, int var3, List<BiomeGenBase> var4) {
      IntCache.func_76446_a();
      int var5 = var1 - var3 >> 2;
      int var6 = var2 - var3 >> 2;
      int var7 = var1 + var3 >> 2;
      int var8 = var2 + var3 >> 2;
      int var9 = var7 - var5 + 1;
      int var10 = var8 - var6 + 1;
      int[] var11 = this.field_76944_d.func_75904_a(var5, var6, var9, var10);

      try {
         for(int var12 = 0; var12 < var9 * var10; ++var12) {
            BiomeGenBase var16 = BiomeGenBase.func_150568_d(var11[var12]);
            if (!var4.contains(var16)) {
               return false;
            }
         }

         return true;
      } catch (Throwable var15) {
         CrashReport var13 = CrashReport.func_85055_a(var15, "Invalid Biome id");
         CrashReportCategory var14 = var13.func_85058_a("Layer");
         var14.func_71507_a("Layer", this.field_76944_d.toString());
         var14.func_71507_a("x", var1);
         var14.func_71507_a("z", var2);
         var14.func_71507_a("radius", var3);
         var14.func_71507_a("allowed", var4);
         throw new ReportedException(var13);
      }
   }

   public BlockPos func_180630_a(int var1, int var2, int var3, List<BiomeGenBase> var4, Random var5) {
      IntCache.func_76446_a();
      int var6 = var1 - var3 >> 2;
      int var7 = var2 - var3 >> 2;
      int var8 = var1 + var3 >> 2;
      int var9 = var2 + var3 >> 2;
      int var10 = var8 - var6 + 1;
      int var11 = var9 - var7 + 1;
      int[] var12 = this.field_76944_d.func_75904_a(var6, var7, var10, var11);
      BlockPos var13 = null;
      int var14 = 0;

      for(int var15 = 0; var15 < var10 * var11; ++var15) {
         int var16 = var6 + var15 % var10 << 2;
         int var17 = var7 + var15 / var10 << 2;
         BiomeGenBase var18 = BiomeGenBase.func_150568_d(var12[var15]);
         if (var4.contains(var18) && (var13 == null || var5.nextInt(var14 + 1) == 0)) {
            var13 = new BlockPos(var16, 0, var17);
            ++var14;
         }
      }

      return var13;
   }

   public void func_76938_b() {
      this.field_76942_f.func_76838_a();
   }
}
