package net.minecraft.item.crafting;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesDyes {
   public RecipesDyes() {
      super();
   }

   public void func_77607_a(CraftingManager var1) {
      int var2;
      for(var2 = 0; var2 < 16; ++var2) {
         var1.func_77596_b(new ItemStack(Blocks.field_150325_L, 1, var2), new ItemStack(Items.field_151100_aR, 1, 15 - var2), new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 0));
         var1.func_92103_a(new ItemStack(Blocks.field_150406_ce, 8, 15 - var2), "###", "#X#", "###", '#', new ItemStack(Blocks.field_150405_ch), 'X', new ItemStack(Items.field_151100_aR, 1, var2));
         var1.func_92103_a(new ItemStack(Blocks.field_150399_cn, 8, 15 - var2), "###", "#X#", "###", '#', new ItemStack(Blocks.field_150359_w), 'X', new ItemStack(Items.field_151100_aR, 1, var2));
         var1.func_92103_a(new ItemStack(Blocks.field_150397_co, 16, var2), "###", "###", '#', new ItemStack(Blocks.field_150399_cn, 1, var2));
      }

      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.YELLOW.func_176767_b()), new ItemStack(Blocks.field_150327_N, 1, BlockFlower.EnumFlowerType.DANDELION.func_176968_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.RED.func_176767_b()), new ItemStack(Blocks.field_150328_O, 1, BlockFlower.EnumFlowerType.POPPY.func_176968_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 3, EnumDyeColor.WHITE.func_176767_b()), Items.field_151103_aS);
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, EnumDyeColor.PINK.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.RED.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.WHITE.func_176767_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, EnumDyeColor.ORANGE.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.RED.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.YELLOW.func_176767_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, EnumDyeColor.LIME.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.GREEN.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.WHITE.func_176767_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, EnumDyeColor.GRAY.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLACK.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.WHITE.func_176767_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, EnumDyeColor.SILVER.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.GRAY.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.WHITE.func_176767_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 3, EnumDyeColor.SILVER.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLACK.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.WHITE.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.WHITE.func_176767_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, EnumDyeColor.LIGHT_BLUE.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.WHITE.func_176767_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, EnumDyeColor.CYAN.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.GREEN.func_176767_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, EnumDyeColor.PURPLE.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.RED.func_176767_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, EnumDyeColor.MAGENTA.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.PURPLE.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.PINK.func_176767_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 3, EnumDyeColor.MAGENTA.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.RED.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.PINK.func_176767_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 4, EnumDyeColor.MAGENTA.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.RED.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.RED.func_176767_b()), new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.WHITE.func_176767_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.LIGHT_BLUE.func_176767_b()), new ItemStack(Blocks.field_150328_O, 1, BlockFlower.EnumFlowerType.BLUE_ORCHID.func_176968_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.MAGENTA.func_176767_b()), new ItemStack(Blocks.field_150328_O, 1, BlockFlower.EnumFlowerType.ALLIUM.func_176968_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.SILVER.func_176767_b()), new ItemStack(Blocks.field_150328_O, 1, BlockFlower.EnumFlowerType.HOUSTONIA.func_176968_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.RED.func_176767_b()), new ItemStack(Blocks.field_150328_O, 1, BlockFlower.EnumFlowerType.RED_TULIP.func_176968_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.ORANGE.func_176767_b()), new ItemStack(Blocks.field_150328_O, 1, BlockFlower.EnumFlowerType.ORANGE_TULIP.func_176968_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.SILVER.func_176767_b()), new ItemStack(Blocks.field_150328_O, 1, BlockFlower.EnumFlowerType.WHITE_TULIP.func_176968_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.PINK.func_176767_b()), new ItemStack(Blocks.field_150328_O, 1, BlockFlower.EnumFlowerType.PINK_TULIP.func_176968_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.SILVER.func_176767_b()), new ItemStack(Blocks.field_150328_O, 1, BlockFlower.EnumFlowerType.OXEYE_DAISY.func_176968_b()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, EnumDyeColor.YELLOW.func_176767_b()), new ItemStack(Blocks.field_150398_cm, 1, BlockDoublePlant.EnumPlantType.SUNFLOWER.func_176936_a()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, EnumDyeColor.MAGENTA.func_176767_b()), new ItemStack(Blocks.field_150398_cm, 1, BlockDoublePlant.EnumPlantType.SYRINGA.func_176936_a()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, EnumDyeColor.RED.func_176767_b()), new ItemStack(Blocks.field_150398_cm, 1, BlockDoublePlant.EnumPlantType.ROSE.func_176936_a()));
      var1.func_77596_b(new ItemStack(Items.field_151100_aR, 2, EnumDyeColor.PINK.func_176767_b()), new ItemStack(Blocks.field_150398_cm, 1, BlockDoublePlant.EnumPlantType.PAEONIA.func_176936_a()));

      for(var2 = 0; var2 < 16; ++var2) {
         var1.func_92103_a(new ItemStack(Blocks.field_150404_cg, 3, var2), "##", '#', new ItemStack(Blocks.field_150325_L, 1, var2));
      }

   }
}
