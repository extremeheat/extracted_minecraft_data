package net.minecraft.world.gen.feature.structure;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class MineshaftStructure extends Structure<MineshaftConfig> {
   public MineshaftStructure() {
      super();
   }

   protected boolean func_202372_a(IChunkGenerator<?> var1, Random var2, int var3, int var4) {
      ((SharedSeedRandom)var2).func_202425_c(var1.func_202089_c(), var3, var4);
      Biome var5 = var1.func_202090_b().func_180300_a(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9), Biomes.field_180279_ad);
      if (var1.func_202094_a(var5, Feature.field_202329_g)) {
         MineshaftConfig var6 = (MineshaftConfig)var1.func_202087_b(var5, Feature.field_202329_g);
         double var7 = var6.field_202439_a;
         return var2.nextDouble() < var7;
      } else {
         return false;
      }
   }

   protected boolean func_202365_a(IWorld var1) {
      return var1.func_72912_H().func_76089_r();
   }

   protected StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5) {
      Biome var6 = var2.func_202090_b().func_180300_a(new BlockPos((var4 << 4) + 9, 0, (var5 << 4) + 9), Biomes.field_180279_ad);
      return new MineshaftStructure.Start(var1, var2, var3, var4, var5, var6);
   }

   protected String func_143025_a() {
      return "Mineshaft";
   }

   public int func_202367_b() {
      return 8;
   }

   public static class Start extends StructureStart {
      private MineshaftStructure.Type field_202507_c;

      public Start() {
         super();
      }

      public Start(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5, Biome var6) {
         super(var4, var5, var6, var3, var1.func_72905_C());
         MineshaftConfig var7 = (MineshaftConfig)var2.func_202087_b(var6, Feature.field_202329_g);
         this.field_202507_c = var7.field_202440_b;
         MineshaftPieces.Room var8 = new MineshaftPieces.Room(0, var3, (var4 << 4) + 2, (var5 << 4) + 2, this.field_202507_c);
         this.field_75075_a.add(var8);
         var8.func_74861_a(var8, this.field_75075_a, var3);
         this.func_202500_a(var1);
         if (var7.field_202440_b == MineshaftStructure.Type.MESA) {
            boolean var9 = true;
            int var10 = var1.func_181545_F() - this.field_75074_b.field_78894_e + this.field_75074_b.func_78882_c() / 2 - -5;
            this.field_75074_b.func_78886_a(0, var10, 0);
            Iterator var11 = this.field_75075_a.iterator();

            while(var11.hasNext()) {
               StructurePiece var12 = (StructurePiece)var11.next();
               var12.func_181138_a(0, var10, 0);
            }
         } else {
            this.func_75067_a(var1, var3, 10);
         }

      }
   }

   public static enum Type {
      NORMAL,
      MESA;

      private Type() {
      }

      public static MineshaftStructure.Type func_189910_a(int var0) {
         return var0 >= 0 && var0 < values().length ? values()[var0] : NORMAL;
      }
   }
}
