package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.MapData;

public class ItemMap extends ItemMapBase {
   public ItemMap(Item.Properties var1) {
      super(var1);
   }

   public static ItemStack func_195952_a(World var0, int var1, int var2, byte var3, boolean var4, boolean var5) {
      ItemStack var6 = new ItemStack(Items.field_151098_aY);
      func_195951_a(var6, var0, var1, var2, var3, var4, var5, var0.field_73011_w.func_186058_p());
      return var6;
   }

   @Nullable
   public static MapData func_195950_a(ItemStack var0, World var1) {
      MapData var2 = func_195953_a(var1, "map_" + func_195949_f(var0));
      if (var2 == null && !var1.field_72995_K) {
         var2 = func_195951_a(var0, var1, var1.func_72912_H().func_76079_c(), var1.func_72912_H().func_76074_e(), 3, false, false, var1.field_73011_w.func_186058_p());
      }

      return var2;
   }

   public static int func_195949_f(ItemStack var0) {
      NBTTagCompound var1 = var0.func_77978_p();
      return var1 != null && var1.func_150297_b("map", 99) ? var1.func_74762_e("map") : 0;
   }

   private static MapData func_195951_a(ItemStack var0, World var1, int var2, int var3, int var4, boolean var5, boolean var6, DimensionType var7) {
      int var8 = var1.func_212410_a(DimensionType.OVERWORLD, "map");
      MapData var9 = new MapData("map_" + var8);
      var9.func_212440_a(var2, var3, var4, var5, var6, var7);
      var1.func_212409_a(DimensionType.OVERWORLD, var9.func_195925_e(), var9);
      var0.func_196082_o().func_74768_a("map", var8);
      return var9;
   }

   @Nullable
   public static MapData func_195953_a(IWorld var0, String var1) {
      return (MapData)var0.func_212411_a(DimensionType.OVERWORLD, MapData::new, var1);
   }

   public void func_77872_a(World var1, Entity var2, MapData var3) {
      if (var1.field_73011_w.func_186058_p() == var3.field_76200_c && var2 instanceof EntityPlayer) {
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
                     LinkedHashMultiset var21 = LinkedHashMultiset.create();
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
                              var21.add(Blocks.field_150346_d.func_176223_P().func_185909_g(var1, BlockPos.field_177992_a), 10);
                           } else {
                              var21.add(Blocks.field_150348_b.func_176223_P().func_185909_g(var1, BlockPos.field_177992_a), 100);
                           }

                           var26 = 100.0D;
                        } else {
                           BlockPos.MutableBlockPos var35 = new BlockPos.MutableBlockPos();

                           for(int var29 = 0; var29 < var4; ++var29) {
                              for(int var30 = 0; var30 < var4; ++var30) {
                                 int var31 = var22.func_201576_a(Heightmap.Type.WORLD_SURFACE, var29 + var23, var30 + var24) + 1;
                                 IBlockState var32;
                                 if (var31 <= 1) {
                                    var32 = Blocks.field_150357_h.func_176223_P();
                                 } else {
                                    do {
                                       --var31;
                                       var32 = var22.func_186032_a(var29 + var23, var31, var30 + var24);
                                       var35.func_181079_c((var22.field_76635_g << 4) + var29 + var23, var31, (var22.field_76647_h << 4) + var30 + var24);
                                    } while(var32.func_185909_g(var1, var35) == MaterialColor.field_151660_b && var31 > 0);

                                    if (var31 > 0 && !var32.func_204520_s().func_206888_e()) {
                                       int var33 = var31 - 1;

                                       IBlockState var34;
                                       do {
                                          var34 = var22.func_186032_a(var29 + var23, var33--, var30 + var24);
                                          ++var25;
                                       } while(var33 > 0 && !var34.func_204520_s().func_206888_e());

                                       var32 = this.func_211698_a(var1, var32, var35);
                                    }
                                 }

                                 var3.func_204268_a(var1, (var22.field_76635_g << 4) + var29 + var23, (var22.field_76647_h << 4) + var30 + var24);
                                 var26 += (double)var31 / (double)(var4 * var4);
                                 var21.add(var32.func_185909_g(var1, var35));
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

                        MaterialColor var38 = (MaterialColor)Iterables.getFirst(Multisets.copyHighestCountFirst(var21), MaterialColor.field_151660_b);
                        if (var38 == MaterialColor.field_151662_n) {
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

   private IBlockState func_211698_a(World var1, IBlockState var2, BlockPos var3) {
      IFluidState var4 = var2.func_204520_s();
      return !var4.func_206888_e() && !Block.func_208061_a(var2.func_196952_d(var1, var3), EnumFacing.UP) ? var4.func_206883_i() : var2;
   }

   private static boolean func_195954_a(Biome[] var0, int var1, int var2, int var3) {
      return var0[var2 * var1 + var3 * var1 * 128 * var1].func_185355_j() >= 0.0F;
   }

   public static void func_190905_a(World var0, ItemStack var1) {
      MapData var2 = func_195950_a(var1, var0);
      if (var2 != null) {
         if (var0.field_73011_w.func_186058_p() == var2.field_76200_c) {
            int var3 = 1 << var2.field_76197_d;
            int var4 = var2.field_76201_a;
            int var5 = var2.field_76199_b;
            Biome[] var6 = var0.func_72863_F().func_201711_g().func_202090_b().func_201537_a((var4 / var3 - 64) * var3, (var5 / var3 - 64) * var3, 128 * var3, 128 * var3, false);

            for(int var7 = 0; var7 < 128; ++var7) {
               for(int var8 = 0; var8 < 128; ++var8) {
                  if (var7 > 0 && var8 > 0 && var7 < 127 && var8 < 127) {
                     Biome var9 = var6[var7 * var3 + var8 * var3 * 128 * var3];
                     int var10 = 8;
                     if (func_195954_a(var6, var3, var7 - 1, var8 - 1)) {
                        --var10;
                     }

                     if (func_195954_a(var6, var3, var7 - 1, var8 + 1)) {
                        --var10;
                     }

                     if (func_195954_a(var6, var3, var7 - 1, var8)) {
                        --var10;
                     }

                     if (func_195954_a(var6, var3, var7 + 1, var8 - 1)) {
                        --var10;
                     }

                     if (func_195954_a(var6, var3, var7 + 1, var8 + 1)) {
                        --var10;
                     }

                     if (func_195954_a(var6, var3, var7 + 1, var8)) {
                        --var10;
                     }

                     if (func_195954_a(var6, var3, var7, var8 - 1)) {
                        --var10;
                     }

                     if (func_195954_a(var6, var3, var7, var8 + 1)) {
                        --var10;
                     }

                     int var11 = 3;
                     MaterialColor var12 = MaterialColor.field_151660_b;
                     if (var9.func_185355_j() < 0.0F) {
                        var12 = MaterialColor.field_151676_q;
                        if (var10 > 7 && var8 % 2 == 0) {
                           var11 = (var7 + (int)(MathHelper.func_76126_a((float)var8 + 0.0F) * 7.0F)) / 8 % 5;
                           if (var11 == 3) {
                              var11 = 1;
                           } else if (var11 == 4) {
                              var11 = 0;
                           }
                        } else if (var10 > 7) {
                           var12 = MaterialColor.field_151660_b;
                        } else if (var10 > 5) {
                           var11 = 1;
                        } else if (var10 > 3) {
                           var11 = 0;
                        } else if (var10 > 1) {
                           var11 = 0;
                        }
                     } else if (var10 > 0) {
                        var12 = MaterialColor.field_151650_B;
                        if (var10 > 3) {
                           var11 = 1;
                        } else {
                           var11 = 3;
                        }
                     }

                     if (var12 != MaterialColor.field_151660_b) {
                        var2.field_76198_e[var7 + var8 * 128] = (byte)(var12.field_76290_q * 4 + var11);
                        var2.func_176053_a(var7, var8);
                     }
                  }
               }
            }

         }
      }
   }

   public void func_77663_a(ItemStack var1, World var2, Entity var3, int var4, boolean var5) {
      if (!var2.field_72995_K) {
         MapData var6 = func_195950_a(var1, var2);
         if (var3 instanceof EntityPlayer) {
            EntityPlayer var7 = (EntityPlayer)var3;
            var6.func_76191_a(var7, var1);
         }

         if (var5 || var3 instanceof EntityPlayer && ((EntityPlayer)var3).func_184592_cb() == var1) {
            this.func_77872_a(var2, var3, var6);
         }

      }
   }

   @Nullable
   public Packet<?> func_150911_c(ItemStack var1, World var2, EntityPlayer var3) {
      return func_195950_a(var1, var2).func_176052_a(var1, var2, var3);
   }

   public void func_77622_d(ItemStack var1, World var2, EntityPlayer var3) {
      NBTTagCompound var4 = var1.func_77978_p();
      if (var4 != null && var4.func_150297_b("map_scale_direction", 99)) {
         func_185063_a(var1, var2, var4.func_74762_e("map_scale_direction"));
         var4.func_82580_o("map_scale_direction");
      }

   }

   protected static void func_185063_a(ItemStack var0, World var1, int var2) {
      MapData var3 = func_195950_a(var0, var1);
      if (var3 != null) {
         func_195951_a(var0, var1, var3.field_76201_a, var3.field_76199_b, MathHelper.func_76125_a(var3.field_76197_d + var2, 0, 4), var3.field_186210_e, var3.field_191096_f, var3.field_76200_c);
      }

   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      if (var4.func_194127_a()) {
         MapData var5 = var2 == null ? null : func_195950_a(var1, var2);
         if (var5 != null) {
            var3.add((new TextComponentTranslation("filled_map.id", new Object[]{func_195949_f(var1)})).func_211708_a(TextFormatting.GRAY));
            var3.add((new TextComponentTranslation("filled_map.scale", new Object[]{1 << var5.field_76197_d})).func_211708_a(TextFormatting.GRAY));
            var3.add((new TextComponentTranslation("filled_map.level", new Object[]{var5.field_76197_d, 4})).func_211708_a(TextFormatting.GRAY));
         } else {
            var3.add((new TextComponentTranslation("filled_map.unknown", new Object[0])).func_211708_a(TextFormatting.GRAY));
         }
      }

   }

   public static int func_190907_h(ItemStack var0) {
      NBTTagCompound var1 = var0.func_179543_a("display");
      if (var1 != null && var1.func_150297_b("MapColor", 99)) {
         int var2 = var1.func_74762_e("MapColor");
         return -16777216 | var2 & 16777215;
      } else {
         return -12173266;
      }
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      IBlockState var2 = var1.func_195991_k().func_180495_p(var1.func_195995_a());
      if (var2.func_203425_a(BlockTags.field_202897_p)) {
         if (!var1.field_196006_g.field_72995_K) {
            MapData var3 = func_195950_a(var1.func_195996_i(), var1.func_195991_k());
            var3.func_204269_a(var1.func_195991_k(), var1.func_195995_a());
         }

         return EnumActionResult.SUCCESS;
      } else {
         return super.func_195939_a(var1);
      }
   }
}
