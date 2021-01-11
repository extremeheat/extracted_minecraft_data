package net.minecraft.item.crafting;

import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockStoneSlabNew;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
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
      var1.func_92103_a(new ItemStack(Blocks.field_150322_A), "##", "##", '#', new ItemStack(Blocks.field_150354_m, 1, BlockSand.EnumType.SAND.func_176688_a()));
      var1.func_92103_a(new ItemStack(Blocks.field_180395_cM), "##", "##", '#', new ItemStack(Blocks.field_150354_m, 1, BlockSand.EnumType.RED_SAND.func_176688_a()));
      var1.func_92103_a(new ItemStack(Blocks.field_150322_A, 4, BlockSandStone.EnumType.SMOOTH.func_176675_a()), "##", "##", '#', new ItemStack(Blocks.field_150322_A, 1, BlockSandStone.EnumType.DEFAULT.func_176675_a()));
      var1.func_92103_a(new ItemStack(Blocks.field_180395_cM, 4, BlockRedSandstone.EnumType.SMOOTH.func_176827_a()), "##", "##", '#', new ItemStack(Blocks.field_180395_cM, 1, BlockRedSandstone.EnumType.DEFAULT.func_176827_a()));
      var1.func_92103_a(new ItemStack(Blocks.field_150322_A, 1, BlockSandStone.EnumType.CHISELED.func_176675_a()), "#", "#", '#', new ItemStack(Blocks.field_150333_U, 1, BlockStoneSlab.EnumType.SAND.func_176624_a()));
      var1.func_92103_a(new ItemStack(Blocks.field_180395_cM, 1, BlockRedSandstone.EnumType.CHISELED.func_176827_a()), "#", "#", '#', new ItemStack(Blocks.field_180389_cP, 1, BlockStoneSlabNew.EnumType.RED_SANDSTONE.func_176915_a()));
      var1.func_92103_a(new ItemStack(Blocks.field_150371_ca, 1, BlockQuartz.EnumType.CHISELED.func_176796_a()), "#", "#", '#', new ItemStack(Blocks.field_150333_U, 1, BlockStoneSlab.EnumType.QUARTZ.func_176624_a()));
      var1.func_92103_a(new ItemStack(Blocks.field_150371_ca, 2, BlockQuartz.EnumType.LINES_Y.func_176796_a()), "#", "#", '#', new ItemStack(Blocks.field_150371_ca, 1, BlockQuartz.EnumType.DEFAULT.func_176796_a()));
      var1.func_92103_a(new ItemStack(Blocks.field_150417_aV, 4), "##", "##", '#', new ItemStack(Blocks.field_150348_b, 1, BlockStone.EnumType.STONE.func_176642_a()));
      var1.func_92103_a(new ItemStack(Blocks.field_150417_aV, 1, BlockStoneBrick.field_176252_O), "#", "#", '#', new ItemStack(Blocks.field_150333_U, 1, BlockStoneSlab.EnumType.SMOOTHBRICK.func_176624_a()));
      var1.func_77596_b(new ItemStack(Blocks.field_150417_aV, 1, BlockStoneBrick.field_176250_M), Blocks.field_150417_aV, Blocks.field_150395_bd);
      var1.func_77596_b(new ItemStack(Blocks.field_150341_Y, 1), Blocks.field_150347_e, Blocks.field_150395_bd);
      var1.func_92103_a(new ItemStack(Blocks.field_150411_aY, 16), "###", "###", '#', Items.field_151042_j);
      var1.func_92103_a(new ItemStack(Blocks.field_150410_aZ, 16), "###", "###", '#', Blocks.field_150359_w);
      var1.func_92103_a(new ItemStack(Blocks.field_150379_bu, 1), " R ", "RGR", " R ", 'R', Items.field_151137_ax, 'G', Blocks.field_150426_aN);
      var1.func_92103_a(new ItemStack(Blocks.field_150461_bJ, 1), "GGG", "GSG", "OOO", 'G', Blocks.field_150359_w, 'S', Items.field_151156_bN, 'O', Blocks.field_150343_Z);
      var1.func_92103_a(new ItemStack(Blocks.field_150385_bj, 1), "NN", "NN", 'N', Items.field_151130_bT);
      var1.func_92103_a(new ItemStack(Blocks.field_150348_b, 2, BlockStone.EnumType.DIORITE.func_176642_a()), "CQ", "QC", 'C', Blocks.field_150347_e, 'Q', Items.field_151128_bU);
      var1.func_77596_b(new ItemStack(Blocks.field_150348_b, 1, BlockStone.EnumType.GRANITE.func_176642_a()), new ItemStack(Blocks.field_150348_b, 1, BlockStone.EnumType.DIORITE.func_176642_a()), Items.field_151128_bU);
      var1.func_77596_b(new ItemStack(Blocks.field_150348_b, 2, BlockStone.EnumType.ANDESITE.func_176642_a()), new ItemStack(Blocks.field_150348_b, 1, BlockStone.EnumType.DIORITE.func_176642_a()), Blocks.field_150347_e);
      var1.func_92103_a(new ItemStack(Blocks.field_150346_d, 4, BlockDirt.DirtType.COARSE_DIRT.func_176925_a()), "DG", "GD", 'D', new ItemStack(Blocks.field_150346_d, 1, BlockDirt.DirtType.DIRT.func_176925_a()), 'G', Blocks.field_150351_n);
      var1.func_92103_a(new ItemStack(Blocks.field_150348_b, 4, BlockStone.EnumType.DIORITE_SMOOTH.func_176642_a()), "SS", "SS", 'S', new ItemStack(Blocks.field_150348_b, 1, BlockStone.EnumType.DIORITE.func_176642_a()));
      var1.func_92103_a(new ItemStack(Blocks.field_150348_b, 4, BlockStone.EnumType.GRANITE_SMOOTH.func_176642_a()), "SS", "SS", 'S', new ItemStack(Blocks.field_150348_b, 1, BlockStone.EnumType.GRANITE.func_176642_a()));
      var1.func_92103_a(new ItemStack(Blocks.field_150348_b, 4, BlockStone.EnumType.ANDESITE_SMOOTH.func_176642_a()), "SS", "SS", 'S', new ItemStack(Blocks.field_150348_b, 1, BlockStone.EnumType.ANDESITE.func_176642_a()));
      var1.func_92103_a(new ItemStack(Blocks.field_180397_cI, 1, BlockPrismarine.field_176331_b), "SS", "SS", 'S', Items.field_179562_cC);
      var1.func_92103_a(new ItemStack(Blocks.field_180397_cI, 1, BlockPrismarine.field_176333_M), "SSS", "SSS", "SSS", 'S', Items.field_179562_cC);
      var1.func_92103_a(new ItemStack(Blocks.field_180397_cI, 1, BlockPrismarine.field_176334_N), "SSS", "SIS", "SSS", 'S', Items.field_179562_cC, 'I', new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLACK.func_176767_b()));
      var1.func_92103_a(new ItemStack(Blocks.field_180398_cJ, 1, 0), "SCS", "CCC", "SCS", 'S', Items.field_179562_cC, 'C', Items.field_179563_cD);
   }
}
