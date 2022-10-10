package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class BuriedTreasureStructure extends Structure<BuriedTreasureConfig> {
   public BuriedTreasureStructure() {
      super();
   }

   protected boolean func_202372_a(IChunkGenerator<?> var1, Random var2, int var3, int var4) {
      Biome var5 = var1.func_202090_b().func_180300_a(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9), (Biome)null);
      if (var1.func_202094_a(var5, Feature.field_204292_r)) {
         ((SharedSeedRandom)var2).func_202427_a(var1.func_202089_c(), var3, var4, 10387320);
         BuriedTreasureConfig var6 = (BuriedTreasureConfig)var1.func_202087_b(var5, Feature.field_204292_r);
         return var2.nextFloat() < var6.field_204293_a;
      } else {
         return false;
      }
   }

   protected boolean func_202365_a(IWorld var1) {
      return var1.func_72912_H().func_76089_r();
   }

   protected StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5) {
      Biome var6 = var2.func_202090_b().func_180300_a(new BlockPos((var4 << 4) + 9, 0, (var5 << 4) + 9), (Biome)null);
      return new BuriedTreasureStructure.Start(var1, var2, var3, var4, var5, var6);
   }

   protected String func_143025_a() {
      return "Buried_Treasure";
   }

   public int func_202367_b() {
      return 1;
   }

   public static class Start extends StructureStart {
      public Start() {
         super();
      }

      public Start(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5, Biome var6) {
         super(var4, var5, var6, var3, var1.func_72905_C());
         int var7 = var4 * 16;
         int var8 = var5 * 16;
         BlockPos var9 = new BlockPos(var7 + 9, 90, var8 + 9);
         this.field_75075_a.add(new BuriedTreasurePieces.Piece(var9));
         this.func_202500_a(var1);
      }

      public BlockPos func_204294_a() {
         return new BlockPos((this.field_143024_c << 4) + 9, 0, (this.field_143023_d << 4) + 9);
      }
   }
}
