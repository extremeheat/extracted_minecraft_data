package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemBucket extends Item {
   private Block field_77876_a;

   public ItemBucket(Block var1) {
      super();
      this.field_77777_bU = 1;
      this.field_77876_a = var1;
      this.func_77637_a(CreativeTabs.field_78026_f);
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      boolean var4 = this.field_77876_a == Blocks.field_150350_a;
      MovingObjectPosition var5 = this.func_77621_a(var2, var3, var4);
      if (var5 == null) {
         return var1;
      } else {
         if (var5.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos var6 = var5.func_178782_a();
            if (!var2.func_175660_a(var3, var6)) {
               return var1;
            }

            if (var4) {
               if (!var3.func_175151_a(var6.func_177972_a(var5.field_178784_b), var5.field_178784_b, var1)) {
                  return var1;
               }

               IBlockState var7 = var2.func_180495_p(var6);
               Material var8 = var7.func_177230_c().func_149688_o();
               if (var8 == Material.field_151586_h && (Integer)var7.func_177229_b(BlockLiquid.field_176367_b) == 0) {
                  var2.func_175698_g(var6);
                  var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
                  return this.func_150910_a(var1, var3, Items.field_151131_as);
               }

               if (var8 == Material.field_151587_i && (Integer)var7.func_177229_b(BlockLiquid.field_176367_b) == 0) {
                  var2.func_175698_g(var6);
                  var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
                  return this.func_150910_a(var1, var3, Items.field_151129_at);
               }
            } else {
               if (this.field_77876_a == Blocks.field_150350_a) {
                  return new ItemStack(Items.field_151133_ar);
               }

               BlockPos var9 = var6.func_177972_a(var5.field_178784_b);
               if (!var3.func_175151_a(var9, var5.field_178784_b, var1)) {
                  return var1;
               }

               if (this.func_180616_a(var2, var9) && !var3.field_71075_bZ.field_75098_d) {
                  var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
                  return new ItemStack(Items.field_151133_ar);
               }
            }
         }

         return var1;
      }
   }

   private ItemStack func_150910_a(ItemStack var1, EntityPlayer var2, Item var3) {
      if (var2.field_71075_bZ.field_75098_d) {
         return var1;
      } else if (--var1.field_77994_a <= 0) {
         return new ItemStack(var3);
      } else {
         if (!var2.field_71071_by.func_70441_a(new ItemStack(var3))) {
            var2.func_71019_a(new ItemStack(var3, 1, 0), false);
         }

         return var1;
      }
   }

   public boolean func_180616_a(World var1, BlockPos var2) {
      if (this.field_77876_a == Blocks.field_150350_a) {
         return false;
      } else {
         Material var3 = var1.func_180495_p(var2).func_177230_c().func_149688_o();
         boolean var4 = !var3.func_76220_a();
         if (!var1.func_175623_d(var2) && !var4) {
            return false;
         } else {
            if (var1.field_73011_w.func_177500_n() && this.field_77876_a == Blocks.field_150358_i) {
               int var5 = var2.func_177958_n();
               int var6 = var2.func_177956_o();
               int var7 = var2.func_177952_p();
               var1.func_72908_a((double)((float)var5 + 0.5F), (double)((float)var6 + 0.5F), (double)((float)var7 + 0.5F), "random.fizz", 0.5F, 2.6F + (var1.field_73012_v.nextFloat() - var1.field_73012_v.nextFloat()) * 0.8F);

               for(int var8 = 0; var8 < 8; ++var8) {
                  var1.func_175688_a(EnumParticleTypes.SMOKE_LARGE, (double)var5 + Math.random(), (double)var6 + Math.random(), (double)var7 + Math.random(), 0.0D, 0.0D, 0.0D);
               }
            } else {
               if (!var1.field_72995_K && var4 && !var3.func_76224_d()) {
                  var1.func_175655_b(var2, true);
               }

               var1.func_180501_a(var2, this.field_77876_a.func_176223_P(), 3);
            }

            return true;
         }
      }
   }
}
