package net.minecraft.item.crafting;

import net.minecraft.block.BlockColored;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesDyes {
   public RecipesDyes() {
      super();
   }

   public void func_77607_a(CraftingManager var1) {
      for(int var2 = 0; var2 < 16; ++var2) {
         var1.func_77596_b(
            new ItemStack(Blocks.field_150325_L, 1, BlockColored.func_150031_c(var2)),
            new ItemStack(Items.field_151100_aR, 1, var2),
            new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 0)
         );
         var1.func_92103_a(
            new ItemStack(Blocks.field_150406_ce, 8, BlockColored.func_150031_c(var2)),
            "###",
            "#X#",
            "###",
            '#',
            new ItemStack(Blocks.field_150405_ch),
            'X',
            new ItemStack(Items.field_151100_aR, 1, var2)
         );
         var1.func_92103_a(
            new ItemStack(Blocks.field_150399_cn, 8, BlockColored.func_150031_c(var2)),
            "###",
            "#X#",
            "###",
            '#',
            new ItemStack(Blocks.field_150359_w),
            'X',
            new ItemStack(Items.field_151100_aR, 1, var2)
         );
         var1.func_92103_a(new ItemStack(Blocks.field_150397_co, 16, var2), "###", "###", '#', new ItemStack(Blocks.field_150399_cn, 1, var2));
      }

      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 11), new ItemStack(Blocks.field_150327_N, 1, 0));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 1), new ItemStack(Blocks.field_150328_O, 1, 0));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 3, 15), Items.field_151103_aS);
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 9), new ItemStack(Items.field_151100_aR, 1, 1), new ItemStack(Items.field_151100_aR, 1, 15));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 14), new ItemStack(Items.field_151100_aR, 1, 1), new ItemStack(Items.field_151100_aR, 1, 11));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 10), new ItemStack(Items.field_151100_aR, 1, 2), new ItemStack(Items.field_151100_aR, 1, 15));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 8), new ItemStack(Items.field_151100_aR, 1, 0), new ItemStack(Items.field_151100_aR, 1, 15));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 7), new ItemStack(Items.field_151100_aR, 1, 8), new ItemStack(Items.field_151100_aR, 1, 15));
      var1.func_77596_b(
         new ItemStack(Items.field_151100_aR, 3, 7),
         new ItemStack(Items.field_151100_aR, 1, 0),
         new ItemStack(Items.field_151100_aR, 1, 15),
         new ItemStack(Items.field_151100_aR, 1, 15)
      );
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 12), new ItemStack(Items.field_151100_aR, 1, 4), new ItemStack(Items.field_151100_aR, 1, 15));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 6), new ItemStack(Items.field_151100_aR, 1, 4), new ItemStack(Items.field_151100_aR, 1, 2));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 5), new ItemStack(Items.field_151100_aR, 1, 4), new ItemStack(Items.field_151100_aR, 1, 1));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 13), new ItemStack(Items.field_151100_aR, 1, 5), new ItemStack(Items.field_151100_aR, 1, 9));
      var1.func_77596_b(
         new ItemStack(Items.field_151100_aR, 3, 13),
         new ItemStack(Items.field_151100_aR, 1, 4),
         new ItemStack(Items.field_151100_aR, 1, 1),
         new ItemStack(Items.field_151100_aR, 1, 9)
      );
      var1.func_77596_b(
         new ItemStack(Items.field_151100_aR, 4, 13),
         new ItemStack(Items.field_151100_aR, 1, 4),
         new ItemStack(Items.field_151100_aR, 1, 1),
         new ItemStack(Items.field_151100_aR, 1, 1),
         new ItemStack(Items.field_151100_aR, 1, 15)
      );
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 12), new ItemStack(Blocks.field_150328_O, 1, 1));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 13), new ItemStack(Blocks.field_150328_O, 1, 2));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 7), new ItemStack(Blocks.field_150328_O, 1, 3));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 1), new ItemStack(Blocks.field_150328_O, 1, 4));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 14), new ItemStack(Blocks.field_150328_O, 1, 5));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 7), new ItemStack(Blocks.field_150328_O, 1, 6));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 9), new ItemStack(Blocks.field_150328_O, 1, 7));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 7), new ItemStack(Blocks.field_150328_O, 1, 8));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 11), new ItemStack(Blocks.field_150398_cm, 1, 0));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 13), new ItemStack(Blocks.field_150398_cm, 1, 1));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 1), new ItemStack(Blocks.field_150398_cm, 1, 4));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 9), new ItemStack(Blocks.field_150398_cm, 1, 5));

      for(int var3 = 0; var3 < 16; ++var3) {
         var1.func_92103_a(new ItemStack(Blocks.field_150404_cg, 3, var3), "##", '#', new ItemStack(Blocks.field_150325_L, 1, var3));
      }
   }
}
