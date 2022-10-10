package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class StrongholdStructure extends Structure<StrongholdConfig> {
   private boolean field_75056_f;
   private ChunkPos[] field_75057_g;
   private long field_202387_av;

   public StrongholdStructure() {
      super();
   }

   protected boolean func_202372_a(IChunkGenerator<?> var1, Random var2, int var3, int var4) {
      if (this.field_202387_av != var1.func_202089_c()) {
         this.func_202386_c();
      }

      if (!this.field_75056_f) {
         this.func_202385_a(var1);
         this.field_75056_f = true;
      }

      ChunkPos[] var5 = this.field_75057_g;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         ChunkPos var8 = var5[var7];
         if (var3 == var8.field_77276_a && var4 == var8.field_77275_b) {
            return true;
         }
      }

      return false;
   }

   private void func_202386_c() {
      this.field_75056_f = false;
      this.field_75057_g = null;
   }

   protected boolean func_202365_a(IWorld var1) {
      return var1.func_72912_H().func_76089_r();
   }

   protected StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5) {
      Biome var6 = var2.func_202090_b().func_180300_a(new BlockPos((var4 << 4) + 9, 0, (var5 << 4) + 9), Biomes.field_180279_ad);
      byte var7 = 0;
      int var10 = var7 + 1;

      StrongholdStructure.Start var8;
      for(var8 = new StrongholdStructure.Start(var1, var3, var4, var5, var6, var7); var8.func_186161_c().isEmpty() || ((StrongholdPieces.Stairs2)var8.func_186161_c().get(0)).field_75025_b == null; var8 = new StrongholdStructure.Start(var1, var3, var4, var5, var6, var10++)) {
      }

      return var8;
   }

   protected String func_143025_a() {
      return "Stronghold";
   }

   public int func_202367_b() {
      return 8;
   }

   @Nullable
   public BlockPos func_211405_a(World var1, IChunkGenerator<? extends IChunkGenSettings> var2, BlockPos var3, int var4, boolean var5) {
      if (!var2.func_202090_b().func_205004_a(this)) {
         return null;
      } else {
         if (this.field_202387_av != var1.func_72905_C()) {
            this.func_202386_c();
         }

         if (!this.field_75056_f) {
            this.func_202385_a(var2);
            this.field_75056_f = true;
         }

         BlockPos var6 = null;
         BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos(0, 0, 0);
         double var8 = 1.7976931348623157E308D;
         ChunkPos[] var10 = this.field_75057_g;
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            ChunkPos var13 = var10[var12];
            var7.func_181079_c((var13.field_77276_a << 4) + 8, 32, (var13.field_77275_b << 4) + 8);
            double var14 = var7.func_177951_i(var3);
            if (var6 == null) {
               var6 = new BlockPos(var7);
               var8 = var14;
            } else if (var14 < var8) {
               var6 = new BlockPos(var7);
               var8 = var14;
            }
         }

         return var6;
      }
   }

   private void func_202385_a(IChunkGenerator<?> var1) {
      this.field_202387_av = var1.func_202089_c();
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = IRegistry.field_212624_m.iterator();

      while(var3.hasNext()) {
         Biome var4 = (Biome)var3.next();
         if (var4 != null && var1.func_202094_a(var4, Feature.field_202335_m)) {
            var2.add(var4);
         }
      }

      int var21 = var1.func_201496_a_().func_202172_d();
      int var22 = var1.func_201496_a_().func_202176_e();
      int var5 = var1.func_201496_a_().func_202175_f();
      this.field_75057_g = new ChunkPos[var22];
      int var6 = 0;
      Long2ObjectMap var7 = var1.func_203224_a(this);
      synchronized(var7) {
         ObjectIterator var9 = var7.values().iterator();

         while(true) {
            if (!var9.hasNext()) {
               break;
            }

            StructureStart var10 = (StructureStart)var9.next();
            if (var6 < this.field_75057_g.length) {
               this.field_75057_g[var6++] = new ChunkPos(var10.func_143019_e(), var10.func_143018_f());
            }
         }
      }

      Random var8 = new Random();
      var8.setSeed(var1.func_202089_c());
      double var23 = var8.nextDouble() * 3.141592653589793D * 2.0D;
      int var11 = var7.size();
      if (var11 < this.field_75057_g.length) {
         int var12 = 0;
         int var13 = 0;

         for(int var14 = 0; var14 < this.field_75057_g.length; ++var14) {
            double var15 = (double)(4 * var21 + var21 * var13 * 6) + (var8.nextDouble() - 0.5D) * (double)var21 * 2.5D;
            int var17 = (int)Math.round(Math.cos(var23) * var15);
            int var18 = (int)Math.round(Math.sin(var23) * var15);
            BlockPos var19 = var1.func_202090_b().func_180630_a((var17 << 4) + 8, (var18 << 4) + 8, 112, var2, var8);
            if (var19 != null) {
               var17 = var19.func_177958_n() >> 4;
               var18 = var19.func_177952_p() >> 4;
            }

            if (var14 >= var11) {
               this.field_75057_g[var14] = new ChunkPos(var17, var18);
            }

            var23 += 6.283185307179586D / (double)var5;
            ++var12;
            if (var12 == var5) {
               ++var13;
               var12 = 0;
               var5 += 2 * var5 / (var13 + 1);
               var5 = Math.min(var5, this.field_75057_g.length - var14);
               var23 += var8.nextDouble() * 3.141592653589793D * 2.0D;
            }
         }
      }

   }

   public static class Start extends StructureStart {
      public Start() {
         super();
      }

      public Start(IWorld var1, SharedSeedRandom var2, int var3, int var4, Biome var5, int var6) {
         super(var3, var4, var5, var2, var1.func_72905_C() + (long)var6);
         StrongholdPieces.func_75198_a();
         StrongholdPieces.Stairs2 var7 = new StrongholdPieces.Stairs2(0, var2, (var3 << 4) + 2, (var4 << 4) + 2);
         this.field_75075_a.add(var7);
         var7.func_74861_a(var7, this.field_75075_a, var2);
         List var8 = var7.field_75026_c;

         while(!var8.isEmpty()) {
            int var9 = var2.nextInt(var8.size());
            StructurePiece var10 = (StructurePiece)var8.remove(var9);
            var10.func_74861_a(var7, this.field_75075_a, var2);
         }

         this.func_202500_a(var1);
         this.func_75067_a(var1, var2, 10);
      }
   }
}
