package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RecipesCrafting {
   public RecipesCrafting() {
      super();
   }

   public void func_77589_a(CraftingManager var1) {
      var1.func_92103_a(new ItemStack(Blocks.field_150486_ae), "###", "# #", "###", '#', Blocks.field_150344_f);
      var1.func_92103_a(new ItemStack(Blocks.field_150447_bR), "#-", '#', Blocks.field_150486_ae, '-', Blocks.field_150479_bC);
      var1.func_92103_a(new ItemStack(Blocks.field_150477_bB), "###", "#E#", "###", '#', Blocks.field_150343_Z, 'E', Items.field_151061_bv);
      var1.func_92103_a(new ItemStack(Blocks.field_150460_al), "###", "# #", "###", '#', Blocks.field_150347_e);
      var1.func_92103_a(new ItemStack(Blocks.field_150462_ai), "##", "##", '#', Blocks.field_150344_f);
      var1.func_92103_a(new ItemStack(Blocks.field_150322_A), "##", "##", '#', new ItemStack(Blocks.field_150354_m, 1, 0));
      var1.func_92103_a(new ItemStack(Blocks.field_150322_A, 4, 2), "##", "##", '#', Blocks.field_150322_A);
      var1.func_92103_a(new ItemStack(Blocks.field_150322_A, 1, 1), "#", "#", '#', new ItemStack(Blocks.field_150333_U, 1, 1));
      var1.func_92103_a(new ItemStack(Blocks.field_150371_ca, 1, 1), "#", "#", '#', new ItemStack(Blocks.field_150333_U, 1, 7));
      var1.func_92103_a(new ItemStack(Blocks.field_150371_ca, 2, 2), "#", "#", '#', new ItemStack(Blocks.field_150371_ca, 1, 0));
      var1.func_92103_a(new ItemStack(Blocks.field_150417_aV, 4), "##", "##", '#', Blocks.field_150348_b);
      var1.func_92103_a(new ItemStack(Blocks.field_150411_aY, 16), "###", "###", '#', Items.field_151042_j);
      var1.func_92103_a(new ItemStack(Blocks.field_150410_aZ, 16), "###", "###", '#', Blocks.field_150359_w);
      var1.func_92103_a(new ItemStack(Blocks.field_150379_bu, 1), " R ", "RGR", " R ", 'R', Items.field_151137_ax, 'G', Blocks.field_150426_aN);
      var1.func_92103_a(
         new ItemStack(Blocks.field_150461_bJ, 1), "GGG", "GSG", "OOO", 'G', Blocks.field_150359_w, 'S', Items.field_151156_bN, 'O', Blocks.field_150343_Z
      );
      var1.func_92103_a(new ItemStack(Blocks.field_150385_bj, 1), "NN", "NN", 'N', Items.field_151130_bT);
   }
}
