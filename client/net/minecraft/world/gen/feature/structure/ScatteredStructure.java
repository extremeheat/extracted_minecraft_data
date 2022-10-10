package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.IFeatureConfig;

public abstract class ScatteredStructure<C extends IFeatureConfig> extends Structure<C> {
   public ScatteredStructure() {
      super();
   }

   protected ChunkPos func_211744_a(IChunkGenerator<?> var1, Random var2, int var3, int var4, int var5, int var6) {
      int var7 = this.func_204030_a(var1);
      int var8 = this.func_211745_b(var1);
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0 ? var9 - var7 + 1 : var9;
      int var12 = var10 < 0 ? var10 - var7 + 1 : var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((SharedSeedRandom)var2).func_202427_a(var1.func_202089_c(), var13, var14, this.func_202382_c());
      var13 *= var7;
      var14 *= var7;
      var13 += var2.nextInt(var7 - var8);
      var14 += var2.nextInt(var7 - var8);
      return new ChunkPos(var13, var14);
   }

   protected boolean func_202372_a(IChunkGenerator<?> var1, Random var2, int var3, int var4) {
      ChunkPos var5 = this.func_211744_a(var1, var2, var3, var4, 0, 0);
      if (var3 == var5.field_77276_a && var4 == var5.field_77275_b) {
         Biome var6 = var1.func_202090_b().func_180300_a(new BlockPos(var3 * 16 + 9, 0, var4 * 16 + 9), (Biome)null);
         if (var1.func_202094_a(var6, this)) {
            return true;
         }
      }

      return false;
   }

   protected int func_204030_a(IChunkGenerator<?> var1) {
      return var1.func_201496_a_().func_202177_g();
   }

   protected int func_211745_b(IChunkGenerator<?> var1) {
      return var1.func_201496_a_().func_211731_i();
   }

   protected boolean func_202365_a(IWorld var1) {
      return var1.func_72912_H().func_76089_r();
   }

   protected abstract StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5);

   protected abstract int func_202382_c();
}
