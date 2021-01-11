package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSeedFood extends ItemFood {
   private Block field_150908_b;
   private Block field_82809_c;

   public ItemSeedFood(int var1, float var2, Block var3, Block var4) {
      super(var1, var2, false);
      this.field_150908_b = var3;
      this.field_82809_c = var4;
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var5 != EnumFacing.UP) {
         return false;
      } else if (!var2.func_175151_a(var4.func_177972_a(var5), var5, var1)) {
         return false;
      } else if (var3.func_180495_p(var4).func_177230_c() == this.field_82809_c && var3.func_175623_d(var4.func_177984_a())) {
         var3.func_175656_a(var4.func_177984_a(), this.field_150908_b.func_176223_P());
         --var1.field_77994_a;
         return true;
      } else {
         return false;
      }
   }
}
