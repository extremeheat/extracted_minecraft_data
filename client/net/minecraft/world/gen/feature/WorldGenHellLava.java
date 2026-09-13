package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenHellLava extends WorldGenerator {
   private Block field_150553_a;
   private boolean field_94524_b;

   public WorldGenHellLava(Block var1, boolean var2) {
      super();
      this.field_150553_a = var1;
      this.field_94524_b = var2;
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      if (var1.func_147439_a(var3, var4 + 1, var5) != Blocks.field_150424_aL) {
         return false;
      } else if (var1.func_147439_a(var3, var4, var5).func_149688_o() != Material.field_151579_a
         && var1.func_147439_a(var3, var4, var5) != Blocks.field_150424_aL) {
         return false;
      } else {
         int var6 = 0;
         if (var1.func_147439_a(var3 - 1, var4, var5) == Blocks.field_150424_aL) {
            ++var6;
         }

         if (var1.func_147439_a(var3 + 1, var4, var5) == Blocks.field_150424_aL) {
            ++var6;
         }

         if (var1.func_147439_a(var3, var4, var5 - 1) == Blocks.field_150424_aL) {
            ++var6;
         }

         if (var1.func_147439_a(var3, var4, var5 + 1) == Blocks.field_150424_aL) {
            ++var6;
         }

         if (var1.func_147439_a(var3, var4 - 1, var5) == Blocks.field_150424_aL) {
            ++var6;
         }

         int var7 = 0;
         if (var1.func_147437_c(var3 - 1, var4, var5)) {
            ++var7;
         }

         if (var1.func_147437_c(var3 + 1, var4, var5)) {
            ++var7;
         }

         if (var1.func_147437_c(var3, var4, var5 - 1)) {
            ++var7;
         }

         if (var1.func_147437_c(var3, var4, var5 + 1)) {
            ++var7;
         }

         if (var1.func_147437_c(var3, var4 - 1, var5)) {
            ++var7;
         }

         if (!this.field_94524_b && var6 == 4 && var7 == 1 || var6 == 5) {
            var1.func_147465_d(var3, var4, var5, this.field_150553_a, 0, 2);
            var1.field_72999_e = true;
            this.field_150553_a.func_149674_a(var1, var3, var4, var5, var2);
            var1.field_72999_e = false;
         }

         return true;
      }
   }
}
