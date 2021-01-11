package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCauldron extends Block {
   public static final PropertyInteger field_176591_a = PropertyInteger.func_177719_a("level", 0, 3);

   public BlockCauldron() {
      super(Material.field_151573_f, MapColor.field_151665_m);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176591_a, 0));
   }

   public void func_180638_a(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      float var7 = 0.125F;
      this.func_149676_a(0.0F, 0.0F, 0.0F, var7, 1.0F, 1.0F);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var7);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      this.func_149676_a(1.0F - var7, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      this.func_149676_a(0.0F, 0.0F, 1.0F - var7, 1.0F, 1.0F, 1.0F);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      this.func_149683_g();
   }

   public void func_149683_g() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public void func_180634_a(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      int var5 = (Integer)var3.func_177229_b(field_176591_a);
      float var6 = (float)var2.func_177956_o() + (6.0F + (float)(3 * var5)) / 16.0F;
      if (!var1.field_72995_K && var4.func_70027_ad() && var5 > 0 && var4.func_174813_aQ().field_72338_b <= (double)var6) {
         var4.func_70066_B();
         this.func_176590_a(var1, var2, var3, var5 - 1);
      }

   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_72995_K) {
         return true;
      } else {
         ItemStack var9 = var4.field_71071_by.func_70448_g();
         if (var9 == null) {
            return true;
         } else {
            int var10 = (Integer)var3.func_177229_b(field_176591_a);
            Item var11 = var9.func_77973_b();
            if (var11 == Items.field_151131_as) {
               if (var10 < 3) {
                  if (!var4.field_71075_bZ.field_75098_d) {
                     var4.field_71071_by.func_70299_a(var4.field_71071_by.field_70461_c, new ItemStack(Items.field_151133_ar));
                  }

                  var4.func_71029_a(StatList.field_181725_I);
                  this.func_176590_a(var1, var2, var3, 3);
               }

               return true;
            } else {
               ItemStack var13;
               if (var11 == Items.field_151069_bo) {
                  if (var10 > 0) {
                     if (!var4.field_71075_bZ.field_75098_d) {
                        var13 = new ItemStack(Items.field_151068_bn, 1, 0);
                        if (!var4.field_71071_by.func_70441_a(var13)) {
                           var1.func_72838_d(new EntityItem(var1, (double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 1.5D, (double)var2.func_177952_p() + 0.5D, var13));
                        } else if (var4 instanceof EntityPlayerMP) {
                           ((EntityPlayerMP)var4).func_71120_a(var4.field_71069_bz);
                        }

                        var4.func_71029_a(StatList.field_181726_J);
                        --var9.field_77994_a;
                        if (var9.field_77994_a <= 0) {
                           var4.field_71071_by.func_70299_a(var4.field_71071_by.field_70461_c, (ItemStack)null);
                        }
                     }

                     this.func_176590_a(var1, var2, var3, var10 - 1);
                  }

                  return true;
               } else {
                  if (var10 > 0 && var11 instanceof ItemArmor) {
                     ItemArmor var12 = (ItemArmor)var11;
                     if (var12.func_82812_d() == ItemArmor.ArmorMaterial.LEATHER && var12.func_82816_b_(var9)) {
                        var12.func_82815_c(var9);
                        this.func_176590_a(var1, var2, var3, var10 - 1);
                        var4.func_71029_a(StatList.field_181727_K);
                        return true;
                     }
                  }

                  if (var10 > 0 && var11 instanceof ItemBanner && TileEntityBanner.func_175113_c(var9) > 0) {
                     var13 = var9.func_77946_l();
                     var13.field_77994_a = 1;
                     TileEntityBanner.func_175117_e(var13);
                     if (var9.field_77994_a <= 1 && !var4.field_71075_bZ.field_75098_d) {
                        var4.field_71071_by.func_70299_a(var4.field_71071_by.field_70461_c, var13);
                     } else {
                        if (!var4.field_71071_by.func_70441_a(var13)) {
                           var1.func_72838_d(new EntityItem(var1, (double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 1.5D, (double)var2.func_177952_p() + 0.5D, var13));
                        } else if (var4 instanceof EntityPlayerMP) {
                           ((EntityPlayerMP)var4).func_71120_a(var4.field_71069_bz);
                        }

                        var4.func_71029_a(StatList.field_181728_L);
                        if (!var4.field_71075_bZ.field_75098_d) {
                           --var9.field_77994_a;
                        }
                     }

                     if (!var4.field_71075_bZ.field_75098_d) {
                        this.func_176590_a(var1, var2, var3, var10 - 1);
                     }

                     return true;
                  } else {
                     return false;
                  }
               }
            }
         }
      }
   }

   public void func_176590_a(World var1, BlockPos var2, IBlockState var3, int var4) {
      var1.func_180501_a(var2, var3.func_177226_a(field_176591_a, MathHelper.func_76125_a(var4, 0, 3)), 2);
      var1.func_175666_e(var2, this);
   }

   public void func_176224_k(World var1, BlockPos var2) {
      if (var1.field_73012_v.nextInt(20) == 1) {
         IBlockState var3 = var1.func_180495_p(var2);
         if ((Integer)var3.func_177229_b(field_176591_a) < 3) {
            var1.func_180501_a(var2, var3.func_177231_a(field_176591_a), 2);
         }

      }
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151066_bu;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_151066_bu;
   }

   public boolean func_149740_M() {
      return true;
   }

   public int func_180641_l(World var1, BlockPos var2) {
      return (Integer)var1.func_180495_p(var2).func_177229_b(field_176591_a);
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176591_a, var1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176591_a);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176591_a});
   }
}
