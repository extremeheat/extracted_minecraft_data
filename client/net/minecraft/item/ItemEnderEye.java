package net.minecraft.item;

import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemEnderEye extends Item {
   public ItemEnderEye() {
      super();
      this.func_77637_a(CreativeTabs.field_78026_f);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      IBlockState var9 = var3.func_180495_p(var4);
      if (var2.func_175151_a(var4.func_177972_a(var5), var5, var1) && var9.func_177230_c() == Blocks.field_150378_br && !(Boolean)var9.func_177229_b(BlockEndPortalFrame.field_176507_b)) {
         if (var3.field_72995_K) {
            return true;
         } else {
            var3.func_180501_a(var4, var9.func_177226_a(BlockEndPortalFrame.field_176507_b, true), 2);
            var3.func_175666_e(var4, Blocks.field_150378_br);
            --var1.field_77994_a;

            for(int var10 = 0; var10 < 16; ++var10) {
               double var11 = (double)((float)var4.func_177958_n() + (5.0F + field_77697_d.nextFloat() * 6.0F) / 16.0F);
               double var13 = (double)((float)var4.func_177956_o() + 0.8125F);
               double var15 = (double)((float)var4.func_177952_p() + (5.0F + field_77697_d.nextFloat() * 6.0F) / 16.0F);
               double var17 = 0.0D;
               double var19 = 0.0D;
               double var21 = 0.0D;
               var3.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, var11, var13, var15, var17, var19, var21);
            }

            EnumFacing var23 = (EnumFacing)var9.func_177229_b(BlockEndPortalFrame.field_176508_a);
            int var24 = 0;
            int var12 = 0;
            boolean var25 = false;
            boolean var14 = true;
            EnumFacing var26 = var23.func_176746_e();

            for(int var16 = -2; var16 <= 2; ++var16) {
               BlockPos var28 = var4.func_177967_a(var26, var16);
               IBlockState var18 = var3.func_180495_p(var28);
               if (var18.func_177230_c() == Blocks.field_150378_br) {
                  if (!(Boolean)var18.func_177229_b(BlockEndPortalFrame.field_176507_b)) {
                     var14 = false;
                     break;
                  }

                  var12 = var16;
                  if (!var25) {
                     var24 = var16;
                     var25 = true;
                  }
               }
            }

            if (var14 && var12 == var24 + 2) {
               BlockPos var27 = var4.func_177967_a(var23, 4);

               int var29;
               for(var29 = var24; var29 <= var12; ++var29) {
                  BlockPos var30 = var27.func_177967_a(var26, var29);
                  IBlockState var32 = var3.func_180495_p(var30);
                  if (var32.func_177230_c() != Blocks.field_150378_br || !(Boolean)var32.func_177229_b(BlockEndPortalFrame.field_176507_b)) {
                     var14 = false;
                     break;
                  }
               }

               int var31;
               BlockPos var33;
               for(var29 = var24 - 1; var29 <= var12 + 1; var29 += 4) {
                  var27 = var4.func_177967_a(var26, var29);

                  for(var31 = 1; var31 <= 3; ++var31) {
                     var33 = var27.func_177967_a(var23, var31);
                     IBlockState var20 = var3.func_180495_p(var33);
                     if (var20.func_177230_c() != Blocks.field_150378_br || !(Boolean)var20.func_177229_b(BlockEndPortalFrame.field_176507_b)) {
                        var14 = false;
                        break;
                     }
                  }
               }

               if (var14) {
                  for(var29 = var24; var29 <= var12; ++var29) {
                     var27 = var4.func_177967_a(var26, var29);

                     for(var31 = 1; var31 <= 3; ++var31) {
                        var33 = var27.func_177967_a(var23, var31);
                        var3.func_180501_a(var33, Blocks.field_150384_bq.func_176223_P(), 2);
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      MovingObjectPosition var4 = this.func_77621_a(var2, var3, false);
      if (var4 != null && var4.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK && var2.func_180495_p(var4.func_178782_a()).func_177230_c() == Blocks.field_150378_br) {
         return var1;
      } else {
         if (!var2.field_72995_K) {
            BlockPos var5 = var2.func_180499_a("Stronghold", new BlockPos(var3));
            if (var5 != null) {
               EntityEnderEye var6 = new EntityEnderEye(var2, var3.field_70165_t, var3.field_70163_u, var3.field_70161_v);
               var6.func_180465_a(var5);
               var2.func_72838_d(var6);
               var2.func_72956_a(var3, "random.bow", 0.5F, 0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F));
               var2.func_180498_a((EntityPlayer)null, 1002, new BlockPos(var3), 0);
               if (!var3.field_71075_bZ.field_75098_d) {
                  --var1.field_77994_a;
               }

               var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
            }
         }

         return var1;
      }
   }
}
