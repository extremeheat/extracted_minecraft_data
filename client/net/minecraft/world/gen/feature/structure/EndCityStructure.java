package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class EndCityStructure extends Structure<EndCityConfig> {
   public EndCityStructure() {
      super();
   }

   protected ChunkPos func_211744_a(IChunkGenerator<?> var1, Random var2, int var3, int var4, int var5, int var6) {
      int var7 = var1.func_201496_a_().func_202178_h();
      int var8 = var1.func_201496_a_().func_211728_o();
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0 ? var9 - var7 + 1 : var9;
      int var12 = var10 < 0 ? var10 - var7 + 1 : var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((SharedSeedRandom)var2).func_202427_a(var1.func_202089_c(), var13, var14, 10387313);
      var13 *= var7;
      var14 *= var7;
      var13 += (var2.nextInt(var7 - var8) + var2.nextInt(var7 - var8)) / 2;
      var14 += (var2.nextInt(var7 - var8) + var2.nextInt(var7 - var8)) / 2;
      return new ChunkPos(var13, var14);
   }

   protected boolean func_202372_a(IChunkGenerator<?> var1, Random var2, int var3, int var4) {
      ChunkPos var5 = this.func_211744_a(var1, var2, var3, var4, 0, 0);
      if (var3 == var5.field_77276_a && var4 == var5.field_77275_b) {
         Biome var6 = var1.func_202090_b().func_180300_a(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9), Biomes.field_180279_ad);
         if (!var1.func_202094_a(var6, Feature.field_202338_p)) {
            return false;
         } else {
            int var7 = func_191070_b(var3, var4, var1);
            return var7 >= 60;
         }
      } else {
         return false;
      }
   }

   protected boolean func_202365_a(IWorld var1) {
      return var1.func_72912_H().func_76089_r();
   }

   protected StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5) {
      Biome var6 = var2.func_202090_b().func_180300_a(new BlockPos((var4 << 4) + 9, 0, (var5 << 4) + 9), Biomes.field_180279_ad);
      return new EndCityStructure.Start(var1, var2, var3, var4, var5, var6);
   }

   protected String func_143025_a() {
      return "EndCity";
   }

   public int func_202367_b() {
      return 9;
   }

   private static int func_191070_b(int var0, int var1, IChunkGenerator<?> var2) {
      Random var3 = new Random((long)(var0 + var1 * 10387313));
      Rotation var4 = Rotation.values()[var3.nextInt(Rotation.values().length)];
      ChunkPrimer var5 = new ChunkPrimer(new ChunkPos(var0, var1), UpgradeData.field_196994_a);
      var2.func_202088_a(var5);
      byte var6 = 5;
      byte var7 = 5;
      if (var4 == Rotation.CLOCKWISE_90) {
         var6 = -5;
      } else if (var4 == Rotation.CLOCKWISE_180) {
         var6 = -5;
         var7 = -5;
      } else if (var4 == Rotation.COUNTERCLOCKWISE_90) {
         var7 = -5;
      }

      int var8 = var5.func_201576_a(Heightmap.Type.MOTION_BLOCKING, 7, 7);
      int var9 = var5.func_201576_a(Heightmap.Type.MOTION_BLOCKING, 7, 7 + var7);
      int var10 = var5.func_201576_a(Heightmap.Type.MOTION_BLOCKING, 7 + var6, 7);
      int var11 = var5.func_201576_a(Heightmap.Type.MOTION_BLOCKING, 7 + var6, 7 + var7);
      return Math.min(Math.min(var8, var9), Math.min(var10, var11));
   }

   public static class Start extends StructureStart {
      private boolean field_186163_c;

      public Start() {
         super();
      }

      public Start(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5, Biome var6) {
         super(var4, var5, var6, var3, var1.func_72905_C());
         Rotation var7 = Rotation.values()[var3.nextInt(Rotation.values().length)];
         int var8 = EndCityStructure.func_191070_b(var4, var5, var2);
         if (var8 < 60) {
            this.field_186163_c = false;
         } else {
            BlockPos var9 = new BlockPos(var4 * 16 + 8, var8, var5 * 16 + 8);
            EndCityPieces.func_191087_a(var1.func_72860_G().func_186340_h(), var9, var7, this.field_75075_a, var3);
            this.func_202500_a(var1);
            this.field_186163_c = true;
         }
      }

      public boolean func_75069_d() {
         return this.field_186163_c;
      }
   }
}
