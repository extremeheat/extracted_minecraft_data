package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class WorldGeneratorBonusChest extends WorldGenerator {
   private final WeightedRandomChestContent[] field_76546_a;
   private final int field_76545_b;

   public WorldGeneratorBonusChest(WeightedRandomChestContent[] var1, int var2) {
      super();
      this.field_76546_a = var1;
      this.field_76545_b = var2;
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      Block var6;
      while(
         ((var6 = var1.func_147439_a(var3, var4, var5)).func_149688_o() == Material.field_151579_a || var6.func_149688_o() == Material.field_151584_j)
            && var4 > 1
      ) {
         --var4;
      }

      if (var4 < 1) {
         return false;
      } else {
         ++var4;

         for(int var7 = 0; var7 < 4; ++var7) {
            int var8 = var3 + var2.nextInt(4) - var2.nextInt(4);
            int var9 = var4 + var2.nextInt(3) - var2.nextInt(3);
            int var10 = var5 + var2.nextInt(4) - var2.nextInt(4);
            if (var1.func_147437_c(var8, var9, var10) && World.func_147466_a(var1, var8, var9 - 1, var10)) {
               var1.func_147465_d(var8, var9, var10, Blocks.field_150486_ae, 0, 2);
               TileEntityChest var11 = (TileEntityChest)var1.func_147438_o(var8, var9, var10);
               if (var11 != null && var11 != null) {
                  WeightedRandomChestContent.func_76293_a(var2, this.field_76546_a, var11, this.field_76545_b);
               }

               if (var1.func_147437_c(var8 - 1, var9, var10) && World.func_147466_a(var1, var8 - 1, var9 - 1, var10)) {
                  var1.func_147465_d(var8 - 1, var9, var10, Blocks.field_150478_aa, 0, 2);
               }

               if (var1.func_147437_c(var8 + 1, var9, var10) && World.func_147466_a(var1, var8 - 1, var9 - 1, var10)) {
                  var1.func_147465_d(var8 + 1, var9, var10, Blocks.field_150478_aa, 0, 2);
               }

               if (var1.func_147437_c(var8, var9, var10 - 1) && World.func_147466_a(var1, var8 - 1, var9 - 1, var10)) {
                  var1.func_147465_d(var8, var9, var10 - 1, Blocks.field_150478_aa, 0, 2);
               }

               if (var1.func_147437_c(var8, var9, var10 + 1) && World.func_147466_a(var1, var8 - 1, var9 - 1, var10)) {
                  var1.func_147465_d(var8, var9, var10 + 1, Blocks.field_150478_aa, 0, 2);
               }

               return true;
            }
         }

         return false;
      }
   }
}
