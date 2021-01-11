package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenBigMushroom extends WorldGenerator {
   private Block field_76523_a;

   public WorldGenBigMushroom(Block var1) {
      super(true);
      this.field_76523_a = var1;
   }

   public WorldGenBigMushroom() {
      super(false);
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      if (this.field_76523_a == null) {
         this.field_76523_a = var2.nextBoolean() ? Blocks.field_150420_aW : Blocks.field_150419_aX;
      }

      int var4 = var2.nextInt(3) + 4;
      boolean var5 = true;
      if (var3.func_177956_o() >= 1 && var3.func_177956_o() + var4 + 1 < 256) {
         int var9;
         int var10;
         for(int var6 = var3.func_177956_o(); var6 <= var3.func_177956_o() + 1 + var4; ++var6) {
            byte var7 = 3;
            if (var6 <= var3.func_177956_o() + 3) {
               var7 = 0;
            }

            BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

            for(var9 = var3.func_177958_n() - var7; var9 <= var3.func_177958_n() + var7 && var5; ++var9) {
               for(var10 = var3.func_177952_p() - var7; var10 <= var3.func_177952_p() + var7 && var5; ++var10) {
                  if (var6 >= 0 && var6 < 256) {
                     Block var11 = var1.func_180495_p(var8.func_181079_c(var9, var6, var10)).func_177230_c();
                     if (var11.func_149688_o() != Material.field_151579_a && var11.func_149688_o() != Material.field_151584_j) {
                        var5 = false;
                     }
                  } else {
                     var5 = false;
                  }
               }
            }
         }

         if (!var5) {
            return false;
         } else {
            Block var19 = var1.func_180495_p(var3.func_177977_b()).func_177230_c();
            if (var19 != Blocks.field_150346_d && var19 != Blocks.field_150349_c && var19 != Blocks.field_150391_bh) {
               return false;
            } else {
               int var20 = var3.func_177956_o() + var4;
               if (this.field_76523_a == Blocks.field_150419_aX) {
                  var20 = var3.func_177956_o() + var4 - 3;
               }

               int var21;
               for(var21 = var20; var21 <= var3.func_177956_o() + var4; ++var21) {
                  var9 = 1;
                  if (var21 < var3.func_177956_o() + var4) {
                     ++var9;
                  }

                  if (this.field_76523_a == Blocks.field_150420_aW) {
                     var9 = 3;
                  }

                  var10 = var3.func_177958_n() - var9;
                  int var23 = var3.func_177958_n() + var9;
                  int var12 = var3.func_177952_p() - var9;
                  int var13 = var3.func_177952_p() + var9;

                  for(int var14 = var10; var14 <= var23; ++var14) {
                     for(int var15 = var12; var15 <= var13; ++var15) {
                        int var16 = 5;
                        if (var14 == var10) {
                           --var16;
                        } else if (var14 == var23) {
                           ++var16;
                        }

                        if (var15 == var12) {
                           var16 -= 3;
                        } else if (var15 == var13) {
                           var16 += 3;
                        }

                        BlockHugeMushroom.EnumType var17 = BlockHugeMushroom.EnumType.func_176895_a(var16);
                        if (this.field_76523_a == Blocks.field_150420_aW || var21 < var3.func_177956_o() + var4) {
                           if ((var14 == var10 || var14 == var23) && (var15 == var12 || var15 == var13)) {
                              continue;
                           }

                           if (var14 == var3.func_177958_n() - (var9 - 1) && var15 == var12) {
                              var17 = BlockHugeMushroom.EnumType.NORTH_WEST;
                           }

                           if (var14 == var10 && var15 == var3.func_177952_p() - (var9 - 1)) {
                              var17 = BlockHugeMushroom.EnumType.NORTH_WEST;
                           }

                           if (var14 == var3.func_177958_n() + (var9 - 1) && var15 == var12) {
                              var17 = BlockHugeMushroom.EnumType.NORTH_EAST;
                           }

                           if (var14 == var23 && var15 == var3.func_177952_p() - (var9 - 1)) {
                              var17 = BlockHugeMushroom.EnumType.NORTH_EAST;
                           }

                           if (var14 == var3.func_177958_n() - (var9 - 1) && var15 == var13) {
                              var17 = BlockHugeMushroom.EnumType.SOUTH_WEST;
                           }

                           if (var14 == var10 && var15 == var3.func_177952_p() + (var9 - 1)) {
                              var17 = BlockHugeMushroom.EnumType.SOUTH_WEST;
                           }

                           if (var14 == var3.func_177958_n() + (var9 - 1) && var15 == var13) {
                              var17 = BlockHugeMushroom.EnumType.SOUTH_EAST;
                           }

                           if (var14 == var23 && var15 == var3.func_177952_p() + (var9 - 1)) {
                              var17 = BlockHugeMushroom.EnumType.SOUTH_EAST;
                           }
                        }

                        if (var17 == BlockHugeMushroom.EnumType.CENTER && var21 < var3.func_177956_o() + var4) {
                           var17 = BlockHugeMushroom.EnumType.ALL_INSIDE;
                        }

                        if (var3.func_177956_o() >= var3.func_177956_o() + var4 - 1 || var17 != BlockHugeMushroom.EnumType.ALL_INSIDE) {
                           BlockPos var18 = new BlockPos(var14, var21, var15);
                           if (!var1.func_180495_p(var18).func_177230_c().func_149730_j()) {
                              this.func_175903_a(var1, var18, this.field_76523_a.func_176223_P().func_177226_a(BlockHugeMushroom.field_176380_a, var17));
                           }
                        }
                     }
                  }
               }

               for(var21 = 0; var21 < var4; ++var21) {
                  Block var22 = var1.func_180495_p(var3.func_177981_b(var21)).func_177230_c();
                  if (!var22.func_149730_j()) {
                     this.func_175903_a(var1, var3.func_177981_b(var21), this.field_76523_a.func_176223_P().func_177226_a(BlockHugeMushroom.field_176380_a, BlockHugeMushroom.EnumType.STEM));
                  }
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }
}
