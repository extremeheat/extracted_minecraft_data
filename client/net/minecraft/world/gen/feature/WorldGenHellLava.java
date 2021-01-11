package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenHellLava extends WorldGenerator {
   private final Block field_150553_a;
   private final boolean field_94524_b;

   public WorldGenHellLava(Block var1, boolean var2) {
      super();
      this.field_150553_a = var1;
      this.field_94524_b = var2;
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      if (var1.func_180495_p(var3.func_177984_a()).func_177230_c() != Blocks.field_150424_aL) {
         return false;
      } else if (var1.func_180495_p(var3).func_177230_c().func_149688_o() != Material.field_151579_a && var1.func_180495_p(var3).func_177230_c() != Blocks.field_150424_aL) {
         return false;
      } else {
         int var4 = 0;
         if (var1.func_180495_p(var3.func_177976_e()).func_177230_c() == Blocks.field_150424_aL) {
            ++var4;
         }

         if (var1.func_180495_p(var3.func_177974_f()).func_177230_c() == Blocks.field_150424_aL) {
            ++var4;
         }

         if (var1.func_180495_p(var3.func_177978_c()).func_177230_c() == Blocks.field_150424_aL) {
            ++var4;
         }

         if (var1.func_180495_p(var3.func_177968_d()).func_177230_c() == Blocks.field_150424_aL) {
            ++var4;
         }

         if (var1.func_180495_p(var3.func_177977_b()).func_177230_c() == Blocks.field_150424_aL) {
            ++var4;
         }

         int var5 = 0;
         if (var1.func_175623_d(var3.func_177976_e())) {
            ++var5;
         }

         if (var1.func_175623_d(var3.func_177974_f())) {
            ++var5;
         }

         if (var1.func_175623_d(var3.func_177978_c())) {
            ++var5;
         }

         if (var1.func_175623_d(var3.func_177968_d())) {
            ++var5;
         }

         if (var1.func_175623_d(var3.func_177977_b())) {
            ++var5;
         }

         if (!this.field_94524_b && var4 == 4 && var5 == 1 || var4 == 5) {
            var1.func_180501_a(var3, this.field_150553_a.func_176223_P(), 2);
            var1.func_175637_a(this.field_150553_a, var3, var2);
         }

         return true;
      }
   }
}
