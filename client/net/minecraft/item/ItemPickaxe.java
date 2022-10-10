package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class ItemPickaxe extends ItemTool {
   private static final Set<Block> field_150915_c;

   protected ItemPickaxe(IItemTier var1, int var2, float var3, Item.Properties var4) {
      super((float)var2, var3, var1, field_150915_c, var4);
   }

   public boolean func_150897_b(IBlockState var1) {
      Block var2 = var1.func_177230_c();
      int var3 = this.func_200891_e().func_200925_d();
      if (var2 == Blocks.field_150343_Z) {
         return var3 == 3;
      } else if (var2 != Blocks.field_150484_ah && var2 != Blocks.field_150482_ag && var2 != Blocks.field_150412_bA && var2 != Blocks.field_150475_bE && var2 != Blocks.field_150340_R && var2 != Blocks.field_150352_o && var2 != Blocks.field_150450_ax) {
         if (var2 != Blocks.field_150339_S && var2 != Blocks.field_150366_p && var2 != Blocks.field_150368_y && var2 != Blocks.field_150369_x) {
            Material var4 = var1.func_185904_a();
            return var4 == Material.field_151576_e || var4 == Material.field_151573_f || var4 == Material.field_151574_g;
         } else {
            return var3 >= 1;
         }
      } else {
         return var3 >= 2;
      }
   }

   public float func_150893_a(ItemStack var1, IBlockState var2) {
      Material var3 = var2.func_185904_a();
      return var3 != Material.field_151573_f && var3 != Material.field_151574_g && var3 != Material.field_151576_e ? super.func_150893_a(var1, var2) : this.field_77864_a;
   }

   static {
      field_150915_c = Sets.newHashSet(new Block[]{Blocks.field_150408_cc, Blocks.field_150365_q, Blocks.field_150347_e, Blocks.field_150319_E, Blocks.field_150484_ah, Blocks.field_150482_ag, Blocks.field_196552_aC, Blocks.field_150340_R, Blocks.field_150352_o, Blocks.field_150432_aD, Blocks.field_150339_S, Blocks.field_150366_p, Blocks.field_150368_y, Blocks.field_150369_x, Blocks.field_150341_Y, Blocks.field_150424_aL, Blocks.field_150403_cj, Blocks.field_205164_gk, Blocks.field_150448_aq, Blocks.field_150450_ax, Blocks.field_150322_A, Blocks.field_196583_aj, Blocks.field_196585_ak, Blocks.field_196798_hA, Blocks.field_196799_hB, Blocks.field_180395_cM, Blocks.field_150348_b, Blocks.field_196650_c, Blocks.field_196652_d, Blocks.field_196654_e, Blocks.field_196655_f, Blocks.field_196656_g, Blocks.field_196657_h, Blocks.field_150333_U, Blocks.field_196640_bx, Blocks.field_196643_by, Blocks.field_196646_bz, Blocks.field_196571_bA, Blocks.field_196573_bB, Blocks.field_196575_bC, Blocks.field_196576_bD, Blocks.field_196578_bE, Blocks.field_185771_cX, Blocks.field_196581_bI, Blocks.field_196582_bJ, Blocks.field_196580_bH, Blocks.field_196579_bG, Blocks.field_150430_aB, Blocks.field_150456_au});
   }
}
