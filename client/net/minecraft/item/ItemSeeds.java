package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSeeds extends Item {
   private Block field_150925_a;
   private Block field_77838_b;

   public ItemSeeds(Block var1, Block var2) {
      super();
      this.field_150925_a = var1;
      this.field_77838_b = var2;
      this.func_77637_a(CreativeTabs.field_78035_l);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var5 != EnumFacing.UP) {
         return false;
      } else if (!var2.func_175151_a(var4.func_177972_a(var5), var5, var1)) {
         return false;
      } else if (var3.func_180495_p(var4).func_177230_c() == this.field_77838_b && var3.func_175623_d(var4.func_177984_a())) {
         var3.func_175656_a(var4.func_177984_a(), this.field_150925_a.func_176223_P());
         --var1.field_77994_a;
         return true;
      } else {
         return false;
      }
   }
}
