package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemLilyPad extends ItemColored {
   public ItemLilyPad(Block var1) {
      super(var1, false);
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      MovingObjectPosition var4 = this.func_77621_a(var2, var3, true);
      if (var4 == null) {
         return var1;
      } else {
         if (var4.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos var5 = var4.func_178782_a();
            if (!var2.func_175660_a(var3, var5)) {
               return var1;
            }

            if (!var3.func_175151_a(var5.func_177972_a(var4.field_178784_b), var4.field_178784_b, var1)) {
               return var1;
            }

            BlockPos var6 = var5.func_177984_a();
            IBlockState var7 = var2.func_180495_p(var5);
            if (var7.func_177230_c().func_149688_o() == Material.field_151586_h && (Integer)var7.func_177229_b(BlockLiquid.field_176367_b) == 0 && var2.func_175623_d(var6)) {
               var2.func_175656_a(var6, Blocks.field_150392_bi.func_176223_P());
               if (!var3.field_71075_bZ.field_75098_d) {
                  --var1.field_77994_a;
               }

               var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
            }
         }

         return var1;
      }
   }

   public int func_82790_a(ItemStack var1, int var2) {
      return Blocks.field_150392_bi.func_180644_h(Blocks.field_150392_bi.func_176203_a(var1.func_77960_j()));
   }
}
