package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ItemShears extends Item {
   public ItemShears() {
      super();
      this.func_77625_d(1);
      this.func_77656_e(238);
      this.func_77637_a(CreativeTabs.field_78040_i);
   }

   public boolean func_179218_a(ItemStack var1, World var2, Block var3, BlockPos var4, EntityLivingBase var5) {
      if (var3.func_149688_o() != Material.field_151584_j && var3 != Blocks.field_150321_G && var3 != Blocks.field_150329_H && var3 != Blocks.field_150395_bd && var3 != Blocks.field_150473_bD && var3 != Blocks.field_150325_L) {
         return super.func_179218_a(var1, var2, var3, var4, var5);
      } else {
         var1.func_77972_a(1, var5);
         return true;
      }
   }

   public boolean func_150897_b(Block var1) {
      return var1 == Blocks.field_150321_G || var1 == Blocks.field_150488_af || var1 == Blocks.field_150473_bD;
   }

   public float func_150893_a(ItemStack var1, Block var2) {
      if (var2 != Blocks.field_150321_G && var2.func_149688_o() != Material.field_151584_j) {
         return var2 == Blocks.field_150325_L ? 5.0F : super.func_150893_a(var1, var2);
      } else {
         return 15.0F;
      }
   }
}
