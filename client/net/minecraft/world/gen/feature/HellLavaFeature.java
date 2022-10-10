package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class HellLavaFeature extends Feature<HellLavaConfig> {
   private static final IBlockState field_205173_a;

   public HellLavaFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, HellLavaConfig var5) {
      if (var1.func_180495_p(var4.func_177984_a()) != field_205173_a) {
         return false;
      } else if (!var1.func_180495_p(var4).func_196958_f() && var1.func_180495_p(var4) != field_205173_a) {
         return false;
      } else {
         int var6 = 0;
         if (var1.func_180495_p(var4.func_177976_e()) == field_205173_a) {
            ++var6;
         }

         if (var1.func_180495_p(var4.func_177974_f()) == field_205173_a) {
            ++var6;
         }

         if (var1.func_180495_p(var4.func_177978_c()) == field_205173_a) {
            ++var6;
         }

         if (var1.func_180495_p(var4.func_177968_d()) == field_205173_a) {
            ++var6;
         }

         if (var1.func_180495_p(var4.func_177977_b()) == field_205173_a) {
            ++var6;
         }

         int var7 = 0;
         if (var1.func_175623_d(var4.func_177976_e())) {
            ++var7;
         }

         if (var1.func_175623_d(var4.func_177974_f())) {
            ++var7;
         }

         if (var1.func_175623_d(var4.func_177978_c())) {
            ++var7;
         }

         if (var1.func_175623_d(var4.func_177968_d())) {
            ++var7;
         }

         if (var1.func_175623_d(var4.func_177977_b())) {
            ++var7;
         }

         if (!var5.field_202437_a && var6 == 4 && var7 == 1 || var6 == 5) {
            var1.func_180501_a(var4, Blocks.field_150353_l.func_176223_P(), 2);
            var1.func_205219_F_().func_205360_a(var4, Fluids.field_204547_b, 0);
         }

         return true;
      }
   }

   static {
      field_205173_a = Blocks.field_150424_aL.func_176223_P();
   }
}
