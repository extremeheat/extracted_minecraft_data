package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemHoe extends Item {
   protected Item.ToolMaterial field_77843_a;

   public ItemHoe(Item.ToolMaterial var1) {
      super();
      this.field_77843_a = var1;
      this.field_77777_bU = 1;
      this.func_77656_e(var1.func_77997_a());
      this.func_77637_a(CreativeTabs.field_78040_i);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (!var2.func_175151_a(var4.func_177972_a(var5), var5, var1)) {
         return false;
      } else {
         IBlockState var9 = var3.func_180495_p(var4);
         Block var10 = var9.func_177230_c();
         if (var5 != EnumFacing.DOWN && var3.func_180495_p(var4.func_177984_a()).func_177230_c().func_149688_o() == Material.field_151579_a) {
            if (var10 == Blocks.field_150349_c) {
               return this.func_179232_a(var1, var2, var3, var4, Blocks.field_150458_ak.func_176223_P());
            }

            if (var10 == Blocks.field_150346_d) {
               switch((BlockDirt.DirtType)var9.func_177229_b(BlockDirt.field_176386_a)) {
               case DIRT:
                  return this.func_179232_a(var1, var2, var3, var4, Blocks.field_150458_ak.func_176223_P());
               case COARSE_DIRT:
                  return this.func_179232_a(var1, var2, var3, var4, Blocks.field_150346_d.func_176223_P().func_177226_a(BlockDirt.field_176386_a, BlockDirt.DirtType.DIRT));
               }
            }
         }

         return false;
      }
   }

   protected boolean func_179232_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, IBlockState var5) {
      var3.func_72908_a((double)((float)var4.func_177958_n() + 0.5F), (double)((float)var4.func_177956_o() + 0.5F), (double)((float)var4.func_177952_p() + 0.5F), var5.func_177230_c().field_149762_H.func_150498_e(), (var5.func_177230_c().field_149762_H.func_150497_c() + 1.0F) / 2.0F, var5.func_177230_c().field_149762_H.func_150494_d() * 0.8F);
      if (var3.field_72995_K) {
         return true;
      } else {
         var3.func_175656_a(var4, var5);
         var1.func_77972_a(1, var2);
         return true;
      }
   }

   public boolean func_77662_d() {
      return true;
   }

   public String func_77842_f() {
      return this.field_77843_a.toString();
   }
}
