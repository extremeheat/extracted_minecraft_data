package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ItemDye extends Item {
   public static final int[] field_150922_c = new int[]{1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320};

   public ItemDye() {
      super();
      this.func_77627_a(true);
      this.func_77656_e(0);
      this.func_77637_a(CreativeTabs.field_78035_l);
   }

   public String func_77667_c(ItemStack var1) {
      int var2 = var1.func_77960_j();
      return super.func_77658_a() + "." + EnumDyeColor.func_176766_a(var2).func_176762_d();
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (!var2.func_175151_a(var4.func_177972_a(var5), var5, var1)) {
         return false;
      } else {
         EnumDyeColor var9 = EnumDyeColor.func_176766_a(var1.func_77960_j());
         if (var9 == EnumDyeColor.WHITE) {
            if (func_179234_a(var1, var3, var4)) {
               if (!var3.field_72995_K) {
                  var3.func_175718_b(2005, var4, 0);
               }

               return true;
            }
         } else if (var9 == EnumDyeColor.BROWN) {
            IBlockState var10 = var3.func_180495_p(var4);
            Block var11 = var10.func_177230_c();
            if (var11 == Blocks.field_150364_r && var10.func_177229_b(BlockPlanks.field_176383_a) == BlockPlanks.EnumType.JUNGLE) {
               if (var5 == EnumFacing.DOWN) {
                  return false;
               }

               if (var5 == EnumFacing.UP) {
                  return false;
               }

               var4 = var4.func_177972_a(var5);
               if (var3.func_175623_d(var4)) {
                  IBlockState var12 = Blocks.field_150375_by.func_180642_a(var3, var4, var5, var6, var7, var8, 0, var2);
                  var3.func_180501_a(var4, var12, 2);
                  if (!var2.field_71075_bZ.field_75098_d) {
                     --var1.field_77994_a;
                  }
               }

               return true;
            }
         }

         return false;
      }
   }

   public static boolean func_179234_a(ItemStack var0, World var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      if (var3.func_177230_c() instanceof IGrowable) {
         IGrowable var4 = (IGrowable)var3.func_177230_c();
         if (var4.func_176473_a(var1, var2, var3, var1.field_72995_K)) {
            if (!var1.field_72995_K) {
               if (var4.func_180670_a(var1, var1.field_73012_v, var2, var3)) {
                  var4.func_176474_b(var1, var1.field_73012_v, var2, var3);
               }

               --var0.field_77994_a;
            }

            return true;
         }
      }

      return false;
   }

   public static void func_180617_a(World var0, BlockPos var1, int var2) {
      if (var2 == 0) {
         var2 = 15;
      }

      Block var3 = var0.func_180495_p(var1).func_177230_c();
      if (var3.func_149688_o() != Material.field_151579_a) {
         var3.func_180654_a(var0, var1);

         for(int var4 = 0; var4 < var2; ++var4) {
            double var5 = field_77697_d.nextGaussian() * 0.02D;
            double var7 = field_77697_d.nextGaussian() * 0.02D;
            double var9 = field_77697_d.nextGaussian() * 0.02D;
            var0.func_175688_a(EnumParticleTypes.VILLAGER_HAPPY, (double)((float)var1.func_177958_n() + field_77697_d.nextFloat()), (double)var1.func_177956_o() + (double)field_77697_d.nextFloat() * var3.func_149669_A(), (double)((float)var1.func_177952_p() + field_77697_d.nextFloat()), var5, var7, var9);
         }

      }
   }

   public boolean func_111207_a(ItemStack var1, EntityPlayer var2, EntityLivingBase var3) {
      if (var3 instanceof EntitySheep) {
         EntitySheep var4 = (EntitySheep)var3;
         EnumDyeColor var5 = EnumDyeColor.func_176766_a(var1.func_77960_j());
         if (!var4.func_70892_o() && var4.func_175509_cj() != var5) {
            var4.func_175512_b(var5);
            --var1.field_77994_a;
         }

         return true;
      } else {
         return false;
      }
   }

   public void func_150895_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for(int var4 = 0; var4 < 16; ++var4) {
         var3.add(new ItemStack(var1, 1, var4));
      }

   }
}
