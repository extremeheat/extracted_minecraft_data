package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multisets;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;

public class ItemMap extends ItemMapBase {
   protected ItemMap() {
      super();
      this.func_77627_a(true);
   }

   public static MapData func_150912_a(int var0, World var1) {
      String var2 = "map_" + var0;
      MapData var3 = (MapData)var1.func_72943_a(MapData.class, var2);
      if (var3 == null) {
         var3 = new MapData(var2);
         var1.func_72823_a(var2, var3);
      }

      return var3;
   }

   public MapData func_77873_a(ItemStack var1, World var2) {
      String var3 = "map_" + var1.func_77960_j();
      MapData var4 = (MapData)var2.func_72943_a(MapData.class, var3);
      if (var4 == null && !var2.field_72995_K) {
         var1.func_77964_b(var2.func_72841_b("map"));
         var3 = "map_" + var1.func_77960_j();
         var4 = new MapData(var3);
         var4.field_76197_d = 3;
         var4.func_176054_a((double)var2.func_72912_H().func_76079_c(), (double)var2.func_72912_H().func_76074_e(), var4.field_76197_d);
         var4.field_76200_c = (byte)var2.field_73011_w.func_177502_q();
         var4.func_76185_a();
         var2.func_72823_a(var3, var4);
      }

      return var4;
   }

   public void func_77872_a(World var1, Entity var2, MapData var3) {
      if (var1.field_73011_w.func_177502_q() == var3.field_76200_c && var2 instanceof EntityPlayer) {
         int var4 = 1 << var3.field_76197_d;
         int var5 = var3.field_76201_a;
         int var6 = var3.field_76199_b;
         int var7 = MathHelper.func_76128_c(var2.field_70165_t - (double)var5) / var4 + 64;
         int var8 = MathHelper.func_76128_c(var2.field_70161_v - (double)var6) / var4 + 64;
         int var9 = 128 / var4;
         if (var1.field_73011_w.func_177495_o()) {
            var9 /= 2;
         }

         MapData.MapInfo var10 = var3.func_82568_a((EntityPlayer)var2);
         ++var10.field_82569_d;
         boolean var11 = false;

         for(int var12 = var7 - var9 + 1; var12 < var7 + var9; ++var12) {
            if ((var12 & 15) == (var10.field_82569_d & 15) || var11) {
               var11 = false;
               double var13 = 0.0D;

               for(int var15 = var8 - var9 - 1; var15 < var8 + var9; ++var15) {
                  if (var12 >= 0 && var15 >= -1 && var12 < 128 && var15 < 128) {
                     int var16 = var12 - var7;
                     int var17 = var15 - var8;
                     boolean var18 = var16 * var16 + var17 * var17 > (var9 - 2) * (var9 - 2);
                     int var19 = (var5 / var4 + var12 - 64) * var4;
                     int var20 = (var6 / var4 + var15 - 64) * var4;
                     HashMultiset var21 = HashMultiset.create();
                     Chunk var22 = var1.func_175726_f(new BlockPos(var19, 0, var20));
                     if (!var22.func_76621_g()) {
                        int var23 = var19 & 15;
                        int var24 = var20 & 15;
                        int var25 = 0;
                        double var26 = 0.0D;
                        if (var1.field_73011_w.func_177495_o()) {
                           int var28 = var19 + var20 * 231871;
                           var28 = var28 * var28 * 31287121 + var28 * 11;
                           if ((var28 >> 20 & 1) == 0) {
                              var21.add(Blocks.field_150346_d.func_180659_g(Blocks.field_150346_d.func_176223_P().func_177226_a(BlockDirt.field_176386_a, BlockDirt.DirtType.DIRT)), 10);
                           } else {
                              var21.add(Blocks.field_150348_b.func_180659_g(Blocks.field_150348_b.func_176223_P().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.STONE)), 100);
                           }

                           var26 = 100.0D;
                        } else {
                           BlockPos.MutableBlockPos var35 = new BlockPos.MutableBlockPos();

                           for(int var29 = 0; var29 < var4; ++var29) {
                              for(int var30 = 0; var30 < var4; ++var30) {
                                 int var31 = var22.func_76611_b(var29 + var23, var30 + var24) + 1;
                                 IBlockState var32 = Blocks.field_150350_a.func_176223_P();
                                 if (var31 > 1) {
                                    do {
                                       --var31;
                                       var32 = var22.func_177435_g(var35.func_181079_c(var29 + var23, var31, var30 + var24));
                                    } while(var32.func_177230_c().func_180659_g(var32) == MapColor.field_151660_b && var31 > 0);

                                    if (var31 > 0 && var32.func_177230_c().func_149688_o().func_76224_d()) {
                                       int var33 = var31 - 1;

                                       Block var34;
                                       do {
                                          var34 = var22.func_177438_a(var29 + var23, var33--, var30 + var24);
                                          ++var25;
                                       } while(var33 > 0 && var34.func_149688_o().func_76224_d());
                                    }
                                 }

                                 var26 += (double)var31 / (double)(var4 * var4);
                                 var21.add(var32.func_177230_c().func_180659_g(var32));
                              }
                           }
                        }

                        var25 /= var4 * var4;
                        double var36 = (var26 - var13) * 4.0D / (double)(var4 + 4) + ((double)(var12 + var15 & 1) - 0.5D) * 0.4D;
                        byte var37 = 1;
                        if (var36 > 0.6D) {
                           var37 = 2;
                        }

                        if (var36 < -0.6D) {
                           var37 = 0;
                        }

                        MapColor var38 = (MapColor)Iterables.getFirst(Multisets.copyHighestCountFirst(var21), MapColor.field_151660_b);
                        if (var38 == MapColor.field_151662_n) {
                           var36 = (double)var25 * 0.1D + (double)(var12 + var15 & 1) * 0.2D;
                           var37 = 1;
                           if (var36 < 0.5D) {
                              var37 = 2;
                           }

                           if (var36 > 0.9D) {
                              var37 = 0;
                           }
                        }

                        var13 = var26;
                        if (var15 >= 0 && var16 * var16 + var17 * var17 < var9 * var9 && (!var18 || (var12 + var15 & 1) != 0)) {
                           byte var39 = var3.field_76198_e[var12 + var15 * 128];
                           byte var40 = (byte)(var38.field_76290_q * 4 + var37);
                           if (var39 != var40) {
                              var3.field_76198_e[var12 + var15 * 128] = var40;
                              var3.func_176053_a(var12, var15);
                              var11 = true;
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   public void func_77663_a(ItemStack var1, World var2, Entity var3, int var4, boolean var5) {
      if (!var2.field_72995_K) {
         MapData var6 = this.func_77873_a(var1, var2);
         if (var3 instanceof EntityPlayer) {
            EntityPlayer var7 = (EntityPlayer)var3;
            var6.func_76191_a(var7, var1);
         }

         if (var5) {
            this.func_77872_a(var2, var3, var6);
         }

      }
   }

   public Packet func_150911_c(ItemStack var1, World var2, EntityPlayer var3) {
      return this.func_77873_a(var1, var2).func_176052_a(var1, var2, var3);
   }

   public void func_77622_d(ItemStack var1, World var2, EntityPlayer var3) {
      if (var1.func_77942_o() && var1.func_77978_p().func_74767_n("map_is_scaling")) {
         MapData var4 = Items.field_151098_aY.func_77873_a(var1, var2);
         var1.func_77964_b(var2.func_72841_b("map"));
         MapData var5 = new MapData("map_" + var1.func_77960_j());
         var5.field_76197_d = (byte)(var4.field_76197_d + 1);
         if (var5.field_76197_d > 4) {
            var5.field_76197_d = 4;
         }

         var5.func_176054_a((double)var4.field_76201_a, (double)var4.field_76199_b, var5.field_76197_d);
         var5.field_76200_c = var4.field_76200_c;
         var5.func_76185_a();
         var2.func_72823_a("map_" + var1.func_77960_j(), var5);
      }

   }

   public void func_77624_a(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
      MapData var5 = this.func_77873_a(var1, var2.field_70170_p);
      if (var4) {
         if (var5 == null) {
            var3.add("Unknown map");
         } else {
            var3.add("Scaling at 1:" + (1 << var5.field_76197_d));
            var3.add("(Level " + var5.field_76197_d + "/" + 4 + ")");
         }
      }

   }
}
