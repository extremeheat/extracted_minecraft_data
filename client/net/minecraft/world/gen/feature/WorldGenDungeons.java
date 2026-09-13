package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class WorldGenDungeons extends WorldGenerator {
   private static final WeightedRandomChestContent[] field_111189_a = new WeightedRandomChestContent[]{
      new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 10),
      new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 4, 10),
      new WeightedRandomChestContent(Items.field_151025_P, 0, 1, 1, 10),
      new WeightedRandomChestContent(Items.field_151015_O, 0, 1, 4, 10),
      new WeightedRandomChestContent(Items.field_151016_H, 0, 1, 4, 10),
      new WeightedRandomChestContent(Items.field_151007_F, 0, 1, 4, 10),
      new WeightedRandomChestContent(Items.field_151133_ar, 0, 1, 1, 10),
      new WeightedRandomChestContent(Items.field_151153_ao, 0, 1, 1, 1),
      new WeightedRandomChestContent(Items.field_151137_ax, 0, 1, 4, 10),
      new WeightedRandomChestContent(Items.field_151096_cd, 0, 1, 1, 10),
      new WeightedRandomChestContent(Items.field_151093_ce, 0, 1, 1, 10),
      new WeightedRandomChestContent(Items.field_151057_cb, 0, 1, 1, 10),
      new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 2),
      new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 1)
   };

   public WorldGenDungeons() {
      super();
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      byte var6 = 3;
      int var7 = var2.nextInt(2) + 2;
      int var8 = var2.nextInt(2) + 2;
      int var9 = 0;

      for(int var10 = var3 - var7 - 1; var10 <= var3 + var7 + 1; ++var10) {
         for(int var11 = var4 - 1; var11 <= var4 + var6 + 1; ++var11) {
            for(int var12 = var5 - var8 - 1; var12 <= var5 + var8 + 1; ++var12) {
               Material var13 = var1.func_147439_a(var10, var11, var12).func_149688_o();
               if (var11 == var4 - 1 && !var13.func_76220_a()) {
                  return false;
               }

               if (var11 == var4 + var6 + 1 && !var13.func_76220_a()) {
                  return false;
               }

               if ((var10 == var3 - var7 - 1 || var10 == var3 + var7 + 1 || var12 == var5 - var8 - 1 || var12 == var5 + var8 + 1)
                  && var11 == var4
                  && var1.func_147437_c(var10, var11, var12)
                  && var1.func_147437_c(var10, var11 + 1, var12)) {
                  ++var9;
               }
            }
         }
      }

      if (var9 >= 1 && var9 <= 5) {
         for(int var18 = var3 - var7 - 1; var18 <= var3 + var7 + 1; ++var18) {
            for(int var21 = var4 + var6; var21 >= var4 - 1; --var21) {
               for(int var23 = var5 - var8 - 1; var23 <= var5 + var8 + 1; ++var23) {
                  if (var18 != var3 - var7 - 1
                     && var21 != var4 - 1
                     && var23 != var5 - var8 - 1
                     && var18 != var3 + var7 + 1
                     && var21 != var4 + var6 + 1
                     && var23 != var5 + var8 + 1) {
                     var1.func_147468_f(var18, var21, var23);
                  } else if (var21 >= 0 && !var1.func_147439_a(var18, var21 - 1, var23).func_149688_o().func_76220_a()) {
                     var1.func_147468_f(var18, var21, var23);
                  } else if (var1.func_147439_a(var18, var21, var23).func_149688_o().func_76220_a()) {
                     if (var21 == var4 - 1 && var2.nextInt(4) != 0) {
                        var1.func_147465_d(var18, var21, var23, Blocks.field_150341_Y, 0, 2);
                     } else {
                        var1.func_147465_d(var18, var21, var23, Blocks.field_150347_e, 0, 2);
                     }
                  }
               }
            }
         }

         for(int var19 = 0; var19 < 2; ++var19) {
            for(int var22 = 0; var22 < 3; ++var22) {
               int var24 = var3 + var2.nextInt(var7 * 2 + 1) - var7;
               int var14 = var5 + var2.nextInt(var8 * 2 + 1) - var8;
               if (var1.func_147437_c(var24, var4, var14)) {
                  int var15 = 0;
                  if (var1.func_147439_a(var24 - 1, var4, var14).func_149688_o().func_76220_a()) {
                     ++var15;
                  }

                  if (var1.func_147439_a(var24 + 1, var4, var14).func_149688_o().func_76220_a()) {
                     ++var15;
                  }

                  if (var1.func_147439_a(var24, var4, var14 - 1).func_149688_o().func_76220_a()) {
                     ++var15;
                  }

                  if (var1.func_147439_a(var24, var4, var14 + 1).func_149688_o().func_76220_a()) {
                     ++var15;
                  }

                  if (var15 == 1) {
                     var1.func_147465_d(var24, var4, var14, Blocks.field_150486_ae, 0, 2);
                     WeightedRandomChestContent[] var16 = WeightedRandomChestContent.func_92080_a(field_111189_a, Items.field_151134_bR.func_92114_b(var2));
                     TileEntityChest var17 = (TileEntityChest)var1.func_147438_o(var24, var4, var14);
                     if (var17 != null) {
                        WeightedRandomChestContent.func_76293_a(var2, var16, var17, 8);
                     }
                     break;
                  }
               }
            }
         }

         var1.func_147465_d(var3, var4, var5, Blocks.field_150474_ac, 0, 2);
         TileEntityMobSpawner var20 = (TileEntityMobSpawner)var1.func_147438_o(var3, var4, var5);
         if (var20 != null) {
            var20.func_145881_a().func_98272_a(this.func_76543_b(var2));
         } else {
            System.err.println("Failed to fetch mob spawner entity at (" + var3 + ", " + var4 + ", " + var5 + ")");
         }

         return true;
      } else {
         return false;
      }
   }

   private String func_76543_b(Random var1) {
      int var2 = var1.nextInt(4);
      if (var2 == 0) {
         return "Skeleton";
      } else if (var2 == 1) {
         return "Zombie";
      } else if (var2 == 2) {
         return "Zombie";
      } else {
         return var2 == 3 ? "Spider" : "";
      }
   }
}
