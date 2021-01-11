package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class RecipesIngots {
   private Object[][] field_77591_a;

   public RecipesIngots() {
      super();
      this.field_77591_a = new Object[][]{{Blocks.field_150340_R, new ItemStack(Items.field_151043_k, 9)}, {Blocks.field_150339_S, new ItemStack(Items.field_151042_j, 9)}, {Blocks.field_150484_ah, new ItemStack(Items.field_151045_i, 9)}, {Blocks.field_150475_bE, new ItemStack(Items.field_151166_bC, 9)}, {Blocks.field_150368_y, new ItemStack(Items.field_151100_aR, 9, EnumDyeColor.BLUE.func_176767_b())}, {Blocks.field_150451_bX, new ItemStack(Items.field_151137_ax, 9)}, {Blocks.field_150402_ci, new ItemStack(Items.field_151044_h, 9, 0)}, {Blocks.field_150407_cf, new ItemStack(Items.field_151015_O, 9)}, {Blocks.field_180399_cE, new ItemStack(Items.field_151123_aH, 9)}};
   }

   public void func_77590_a(CraftingManager var1) {
      for(int var2 = 0; var2 < this.field_77591_a.length; ++var2) {
         Block var3 = (Block)this.field_77591_a[var2][0];
         ItemStack var4 = (ItemStack)this.field_77591_a[var2][1];
         var1.func_92103_a(new ItemStack(var3), "###", "###", "###", '#', var4);
         var1.func_92103_a(var4, "#", '#', var3);
      }

      var1.func_92103_a(new ItemStack(Items.field_151043_k), "###", "###", "###", '#', Items.field_151074_bl);
      var1.func_92103_a(new ItemStack(Items.field_151074_bl, 9), "#", '#', Items.field_151043_k);
   }
}
