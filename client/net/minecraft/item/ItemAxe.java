package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class ItemAxe extends ItemTool {
   private static final Set<Block> field_150917_c;

   protected ItemAxe(Item.ToolMaterial var1) {
      super(3.0F, var1, field_150917_c);
   }

   public float func_150893_a(ItemStack var1, Block var2) {
      return var2.func_149688_o() != Material.field_151575_d && var2.func_149688_o() != Material.field_151585_k && var2.func_149688_o() != Material.field_151582_l ? super.func_150893_a(var1, var2) : this.field_77864_a;
   }

   static {
      field_150917_c = Sets.newHashSet(new Block[]{Blocks.field_150344_f, Blocks.field_150342_X, Blocks.field_150364_r, Blocks.field_150363_s, Blocks.field_150486_ae, Blocks.field_150423_aK, Blocks.field_150428_aP, Blocks.field_150440_ba, Blocks.field_150468_ap});
   }
}
