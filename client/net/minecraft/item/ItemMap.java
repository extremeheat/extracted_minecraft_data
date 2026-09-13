package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multisets;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapData$MapInfo;

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
         int var5 = 128 * (1 << var4.field_76197_d);
         var4.field_76201_a = Math.round((float)var2.func_72912_H().func_76079_c() / (float)var5) * var5;
         var4.field_76199_b = Math.round((float)(var2.func_72912_H().func_76074_e() / var5)) * var5;
         var4.field_76200_c = (byte)var2.field_73011_w.field_76574_g;
         var4.func_76185_a();
         var2.func_72823_a(var3, var4);
      }

      return var4;
   }

   public void func_77872_a(World var1, Entity var2, MapData var3) {
      if (var1.field_73011_w.field_76574_g == var3.field_76200_c && var2 instanceof EntityPlayer) {
         int var4 = 1 << var3.field_76197_d;
         int var5 = var3.field_76201_a;
         int var6 = var3.field_76199_b;
         int var7 = MathHelper.func_76128_c(var2.field_70165_t - (double)var5) / var4 + 64;
         int var8 = MathHelper.func_76128_c(var2.field_70161_v - (double)var6) / var4 + 64;
         int var9 = 128 / var4;
         if (var1.field_73011_w.field_76576_e) {
            var9 /= 2;
         }

         MapData$MapInfo var10 = var3.func_82568_a((EntityPlayer)var2);
         ++var10.field_82569_d;

         for(int var11 = var7 - var9 + 1; var11 < var7 + var9; ++var11) {
            if ((var11 & 15) == (var10.field_82569_d & 15)) {
               int var12 = 255;
               int var13 = 0;
               double var14 = 0.0;

               for(int var16 = var8 - var9 - 1; var16 < var8 + var9; ++var16) {
                  if (var11 >= 0 && var16 >= -1 && var11 < 128 && var16 < 128) {
                     int var17 = var11 - var7;
                     int var18 = var16 - var8;
                     boolean var19 = var17 * var17 + var18 * var18 > (var9 - 2) * (var9 - 2);
                     int var20 = (var5 / var4 + var11 - 64) * var4;
                     int var21 = (var6 / var4 + var16 - 64) * var4;
                     HashMultiset var22 = HashMultiset.create();
                     Chunk var23 = var1.func_72938_d(var20, var21);
                     if (!var23.func_76621_g()) {
                        int var24 = var20 & 15;
                        int var25 = var21 & 15;
                        int var26 = 0;
                        double var27 = 0.0;
                        if (var1.field_73011_w.field_76576_e) {
                           int var29 = var20 + var21 * 231871;
                           var29 = var29 * var29 * 31287121 + var29 * 11;
                           if ((var29 >> 20 & 1) == 0) {
                              var22.add(Blocks.field_150346_d.func_149728_f(0), 10);
                           } else {
                              var22.add(Blocks.field_150348_b.func_149728_f(0), 100);
                           }

                           var27 = 100.0;
                        } else {
                           for(int var38 = 0; var38 < var4; ++var38) {
                              for(int var30 = 0; var30 < var4; ++var30) {
                                 int var31 = var23.func_76611_b(var38 + var24, var30 + var25) + 1;
                                 Block var32 = Blocks.field_150350_a;
                                 int var33 = 0;
                                 if (var31 > 1) {
                                    do {
                                       var32 = var23.func_150810_a(var38 + var24, --var31, var30 + var25);
                                       var33 = var23.func_76628_c(var38 + var24, var31, var30 + var25);
                                    } while(var32.func_149728_f(var33) == MapColor.field_151660_b && var31 > 0);

                                    if (var31 > 0 && var32.func_149688_o().func_76224_d()) {
                                       int var34 = var31 - 1;

                                       Block var35;
                                       do {
                                          var35 = var23.func_150810_a(var38 + var24, var34--, var30 + var25);
                                          ++var26;
                                       } while(var34 > 0 && var35.func_149688_o().func_76224_d());
                                    }
                                 }

                                 var27 += (double)var31 / (double)(var4 * var4);
                                 var22.add(var32.func_149728_f(var33));
                              }
                           }
                        }

                        var26 /= var4 * var4;
                        double var39 = (var27 - var14) * 4.0 / (double)(var4 + 4) + ((double)(var11 + var16 & 1) - 0.5) * 0.4;
                        byte var41 = 1;
                        if (var39 > 0.6) {
                           var41 = 2;
                        }

                        if (var39 < -0.6) {
                           var41 = 0;
                        }

                        MapColor var42 = (MapColor)Iterables.getFirst(Multisets.copyHighestCountFirst(var22), MapColor.field_151660_b);
                        if (var42 == MapColor.field_151662_n) {
                           var39 = (double)var26 * 0.1 + (double)(var11 + var16 & 1) * 0.2;
                           var41 = 1;
                           if (var39 < 0.5) {
                              var41 = 2;
                           }

                           if (var39 > 0.9) {
                              var41 = 0;
                           }
                        }

                        var14 = var27;
                        if (var16 >= 0 && var17 * var17 + var18 * var18 < var9 * var9 && (!var19 || (var11 + var16 & 1) != 0)) {
                           byte var43 = var3.field_76198_e[var11 + var16 * 128];
                           byte var44 = (byte)(var42.field_76290_q * 4 + var41);
                           if (var43 != var44) {
                              if (var12 > var16) {
                                 var12 = var16;
                              }

                              if (var13 < var16) {
                                 var13 = var16;
                              }

                              var3.field_76198_e[var11 + var16 * 128] = var44;
                           }
                        }
                     }
                  }
               }

               if (var12 <= var13) {
                  var3.func_76194_a(var11, var12, var13);
               }
            }
         }
      }
   }

   @Override
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

   @Override
   public Packet func_150911_c(ItemStack var1, World var2, EntityPlayer var3) {
      byte[] var4 = this.func_77873_a(var1, var2).func_76193_a(var1, var2, var3);
      return var4 == null ? null : new S34PacketMaps(var1.func_77960_j(), var4);
   }

   @Override
   public void func_77622_d(ItemStack var1, World var2, EntityPlayer var3) {
      if (var1.func_77942_o() && var1.func_77978_p().func_74767_n("map_is_scaling")) {
         MapData var4 = Items.field_151098_aY.func_77873_a(var1, var2);
         var1.func_77964_b(var2.func_72841_b("map"));
         MapData var5 = new MapData("map_" + var1.func_77960_j());
         var5.field_76197_d = (byte)(var4.field_76197_d + 1);
         if (var5.field_76197_d > 4) {
            var5.field_76197_d = 4;
         }

         var5.field_76201_a = var4.field_76201_a;
         var5.field_76199_b = var4.field_76199_b;
         var5.field_76200_c = var4.field_76200_c;
         var5.func_76185_a();
         var2.func_72823_a("map_" + var1.func_77960_j(), var5);
      }
   }

   @Override
   public void func_77624_a(ItemStack var1, EntityPlayer var2, List var3, boolean var4) {
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
