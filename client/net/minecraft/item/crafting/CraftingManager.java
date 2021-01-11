package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockStoneSlabNew;
import net.minecraft.block.BlockWall;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CraftingManager {
   private static final CraftingManager field_77598_a = new CraftingManager();
   private final List<IRecipe> field_77597_b = Lists.newArrayList();

   public static CraftingManager func_77594_a() {
      return field_77598_a;
   }

   private CraftingManager() {
      super();
      (new RecipesTools()).func_77586_a(this);
      (new RecipesWeapons()).func_77583_a(this);
      (new RecipesIngots()).func_77590_a(this);
      (new RecipesFood()).func_77608_a(this);
      (new RecipesCrafting()).func_77589_a(this);
      (new RecipesArmor()).func_77609_a(this);
      (new RecipesDyes()).func_77607_a(this);
      this.field_77597_b.add(new RecipesArmorDyes());
      this.field_77597_b.add(new RecipeBookCloning());
      this.field_77597_b.add(new RecipesMapCloning());
      this.field_77597_b.add(new RecipesMapExtending());
      this.field_77597_b.add(new RecipeFireworks());
      this.field_77597_b.add(new RecipeRepairItem());
      (new RecipesBanners()).func_179534_a(this);
      this.func_92103_a(new ItemStack(Items.field_151121_aF, 3), "###", '#', Items.field_151120_aE);
      this.func_77596_b(new ItemStack(Items.field_151122_aG, 1), Items.field_151121_aF, Items.field_151121_aF, Items.field_151121_aF, Items.field_151116_aA);
      this.func_77596_b(new ItemStack(Items.field_151099_bA, 1), Items.field_151122_aG, new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLACK.func_176767_b()), Items.field_151008_G);
      this.func_92103_a(new ItemStack(Blocks.field_180407_aO, 3), "W#W", "W#W", '#', Items.field_151055_y, 'W', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.OAK.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_180404_aQ, 3), "W#W", "W#W", '#', Items.field_151055_y, 'W', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.BIRCH.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_180408_aP, 3), "W#W", "W#W", '#', Items.field_151055_y, 'W', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.SPRUCE.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_180403_aR, 3), "W#W", "W#W", '#', Items.field_151055_y, 'W', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.JUNGLE.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_180405_aT, 3), "W#W", "W#W", '#', Items.field_151055_y, 'W', new ItemStack(Blocks.field_150344_f, 1, 4 + BlockPlanks.EnumType.ACACIA.func_176839_a() - 4));
      this.func_92103_a(new ItemStack(Blocks.field_180406_aS, 3), "W#W", "W#W", '#', Items.field_151055_y, 'W', new ItemStack(Blocks.field_150344_f, 1, 4 + BlockPlanks.EnumType.DARK_OAK.func_176839_a() - 4));
      this.func_92103_a(new ItemStack(Blocks.field_150463_bK, 6, BlockWall.EnumType.NORMAL.func_176657_a()), "###", "###", '#', Blocks.field_150347_e);
      this.func_92103_a(new ItemStack(Blocks.field_150463_bK, 6, BlockWall.EnumType.MOSSY.func_176657_a()), "###", "###", '#', Blocks.field_150341_Y);
      this.func_92103_a(new ItemStack(Blocks.field_150386_bk, 6), "###", "###", '#', Blocks.field_150385_bj);
      this.func_92103_a(new ItemStack(Blocks.field_180390_bo, 1), "#W#", "#W#", '#', Items.field_151055_y, 'W', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.OAK.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_180392_bq, 1), "#W#", "#W#", '#', Items.field_151055_y, 'W', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.BIRCH.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_180391_bp, 1), "#W#", "#W#", '#', Items.field_151055_y, 'W', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.SPRUCE.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_180386_br, 1), "#W#", "#W#", '#', Items.field_151055_y, 'W', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.JUNGLE.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_180387_bt, 1), "#W#", "#W#", '#', Items.field_151055_y, 'W', new ItemStack(Blocks.field_150344_f, 1, 4 + BlockPlanks.EnumType.ACACIA.func_176839_a() - 4));
      this.func_92103_a(new ItemStack(Blocks.field_180385_bs, 1), "#W#", "#W#", '#', Items.field_151055_y, 'W', new ItemStack(Blocks.field_150344_f, 1, 4 + BlockPlanks.EnumType.DARK_OAK.func_176839_a() - 4));
      this.func_92103_a(new ItemStack(Blocks.field_150421_aI, 1), "###", "#X#", "###", '#', Blocks.field_150344_f, 'X', Items.field_151045_i);
      this.func_92103_a(new ItemStack(Items.field_151058_ca, 2), "~~ ", "~O ", "  ~", '~', Items.field_151007_F, 'O', Items.field_151123_aH);
      this.func_92103_a(new ItemStack(Blocks.field_150323_B, 1), "###", "#X#", "###", '#', Blocks.field_150344_f, 'X', Items.field_151137_ax);
      this.func_92103_a(new ItemStack(Blocks.field_150342_X, 1), "###", "XXX", "###", '#', Blocks.field_150344_f, 'X', Items.field_151122_aG);
      this.func_92103_a(new ItemStack(Blocks.field_150433_aE, 1), "##", "##", '#', Items.field_151126_ay);
      this.func_92103_a(new ItemStack(Blocks.field_150431_aC, 6), "###", '#', Blocks.field_150433_aE);
      this.func_92103_a(new ItemStack(Blocks.field_150435_aG, 1), "##", "##", '#', Items.field_151119_aD);
      this.func_92103_a(new ItemStack(Blocks.field_150336_V, 1), "##", "##", '#', Items.field_151118_aC);
      this.func_92103_a(new ItemStack(Blocks.field_150426_aN, 1), "##", "##", '#', Items.field_151114_aO);
      this.func_92103_a(new ItemStack(Blocks.field_150371_ca, 1), "##", "##", '#', Items.field_151128_bU);
      this.func_92103_a(new ItemStack(Blocks.field_150325_L, 1), "##", "##", '#', Items.field_151007_F);
      this.func_92103_a(new ItemStack(Blocks.field_150335_W, 1), "X#X", "#X#", "X#X", 'X', Items.field_151016_H, '#', Blocks.field_150354_m);
      this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, BlockStoneSlab.EnumType.COBBLESTONE.func_176624_a()), "###", '#', Blocks.field_150347_e);
      this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, BlockStoneSlab.EnumType.STONE.func_176624_a()), "###", '#', new ItemStack(Blocks.field_150348_b, BlockStone.EnumType.STONE.func_176642_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, BlockStoneSlab.EnumType.SAND.func_176624_a()), "###", '#', Blocks.field_150322_A);
      this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, BlockStoneSlab.EnumType.BRICK.func_176624_a()), "###", '#', Blocks.field_150336_V);
      this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, BlockStoneSlab.EnumType.SMOOTHBRICK.func_176624_a()), "###", '#', Blocks.field_150417_aV);
      this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, BlockStoneSlab.EnumType.NETHERBRICK.func_176624_a()), "###", '#', Blocks.field_150385_bj);
      this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, BlockStoneSlab.EnumType.QUARTZ.func_176624_a()), "###", '#', Blocks.field_150371_ca);
      this.func_92103_a(new ItemStack(Blocks.field_180389_cP, 6, BlockStoneSlabNew.EnumType.RED_SANDSTONE.func_176915_a()), "###", '#', Blocks.field_180395_cM);
      this.func_92103_a(new ItemStack(Blocks.field_150376_bx, 6, 0), "###", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.OAK.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150376_bx, 6, BlockPlanks.EnumType.BIRCH.func_176839_a()), "###", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.BIRCH.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150376_bx, 6, BlockPlanks.EnumType.SPRUCE.func_176839_a()), "###", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.SPRUCE.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150376_bx, 6, BlockPlanks.EnumType.JUNGLE.func_176839_a()), "###", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.JUNGLE.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150376_bx, 6, 4 + BlockPlanks.EnumType.ACACIA.func_176839_a() - 4), "###", '#', new ItemStack(Blocks.field_150344_f, 1, 4 + BlockPlanks.EnumType.ACACIA.func_176839_a() - 4));
      this.func_92103_a(new ItemStack(Blocks.field_150376_bx, 6, 4 + BlockPlanks.EnumType.DARK_OAK.func_176839_a() - 4), "###", '#', new ItemStack(Blocks.field_150344_f, 1, 4 + BlockPlanks.EnumType.DARK_OAK.func_176839_a() - 4));
      this.func_92103_a(new ItemStack(Blocks.field_150468_ap, 3), "# #", "###", "# #", '#', Items.field_151055_y);
      this.func_92103_a(new ItemStack(Items.field_179570_aq, 3), "##", "##", "##", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.OAK.func_176839_a()));
      this.func_92103_a(new ItemStack(Items.field_179569_ar, 3), "##", "##", "##", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.SPRUCE.func_176839_a()));
      this.func_92103_a(new ItemStack(Items.field_179568_as, 3), "##", "##", "##", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.BIRCH.func_176839_a()));
      this.func_92103_a(new ItemStack(Items.field_179567_at, 3), "##", "##", "##", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.JUNGLE.func_176839_a()));
      this.func_92103_a(new ItemStack(Items.field_179572_au, 3), "##", "##", "##", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.ACACIA.func_176839_a()));
      this.func_92103_a(new ItemStack(Items.field_179571_av, 3), "##", "##", "##", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.DARK_OAK.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150415_aT, 2), "###", "###", '#', Blocks.field_150344_f);
      this.func_92103_a(new ItemStack(Items.field_151139_aw, 3), "##", "##", "##", '#', Items.field_151042_j);
      this.func_92103_a(new ItemStack(Blocks.field_180400_cw, 1), "##", "##", '#', Items.field_151042_j);
      this.func_92103_a(new ItemStack(Items.field_151155_ap, 3), "###", "###", " X ", '#', Blocks.field_150344_f, 'X', Items.field_151055_y);
      this.func_92103_a(new ItemStack(Items.field_151105_aU, 1), "AAA", "BEB", "CCC", 'A', Items.field_151117_aB, 'B', Items.field_151102_aT, 'C', Items.field_151015_O, 'E', Items.field_151110_aK);
      this.func_92103_a(new ItemStack(Items.field_151102_aT, 1), "#", '#', Items.field_151120_aE);
      this.func_92103_a(new ItemStack(Blocks.field_150344_f, 4, BlockPlanks.EnumType.OAK.func_176839_a()), "#", '#', new ItemStack(Blocks.field_150364_r, 1, BlockPlanks.EnumType.OAK.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150344_f, 4, BlockPlanks.EnumType.SPRUCE.func_176839_a()), "#", '#', new ItemStack(Blocks.field_150364_r, 1, BlockPlanks.EnumType.SPRUCE.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150344_f, 4, BlockPlanks.EnumType.BIRCH.func_176839_a()), "#", '#', new ItemStack(Blocks.field_150364_r, 1, BlockPlanks.EnumType.BIRCH.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150344_f, 4, BlockPlanks.EnumType.JUNGLE.func_176839_a()), "#", '#', new ItemStack(Blocks.field_150364_r, 1, BlockPlanks.EnumType.JUNGLE.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150344_f, 4, 4 + BlockPlanks.EnumType.ACACIA.func_176839_a() - 4), "#", '#', new ItemStack(Blocks.field_150363_s, 1, BlockPlanks.EnumType.ACACIA.func_176839_a() - 4));
      this.func_92103_a(new ItemStack(Blocks.field_150344_f, 4, 4 + BlockPlanks.EnumType.DARK_OAK.func_176839_a() - 4), "#", '#', new ItemStack(Blocks.field_150363_s, 1, BlockPlanks.EnumType.DARK_OAK.func_176839_a() - 4));
      this.func_92103_a(new ItemStack(Items.field_151055_y, 4), "#", "#", '#', Blocks.field_150344_f);
      this.func_92103_a(new ItemStack(Blocks.field_150478_aa, 4), "X", "#", 'X', Items.field_151044_h, '#', Items.field_151055_y);
      this.func_92103_a(new ItemStack(Blocks.field_150478_aa, 4), "X", "#", 'X', new ItemStack(Items.field_151044_h, 1, 1), '#', Items.field_151055_y);
      this.func_92103_a(new ItemStack(Items.field_151054_z, 4), "# #", " # ", '#', Blocks.field_150344_f);
      this.func_92103_a(new ItemStack(Items.field_151069_bo, 3), "# #", " # ", '#', Blocks.field_150359_w);
      this.func_92103_a(new ItemStack(Blocks.field_150448_aq, 16), "X X", "X#X", "X X", 'X', Items.field_151042_j, '#', Items.field_151055_y);
      this.func_92103_a(new ItemStack(Blocks.field_150318_D, 6), "X X", "X#X", "XRX", 'X', Items.field_151043_k, 'R', Items.field_151137_ax, '#', Items.field_151055_y);
      this.func_92103_a(new ItemStack(Blocks.field_150408_cc, 6), "XSX", "X#X", "XSX", 'X', Items.field_151042_j, '#', Blocks.field_150429_aA, 'S', Items.field_151055_y);
      this.func_92103_a(new ItemStack(Blocks.field_150319_E, 6), "X X", "X#X", "XRX", 'X', Items.field_151042_j, 'R', Items.field_151137_ax, '#', Blocks.field_150456_au);
      this.func_92103_a(new ItemStack(Items.field_151143_au, 1), "# #", "###", '#', Items.field_151042_j);
      this.func_92103_a(new ItemStack(Items.field_151066_bu, 1), "# #", "# #", "###", '#', Items.field_151042_j);
      this.func_92103_a(new ItemStack(Items.field_151067_bt, 1), " B ", "###", '#', Blocks.field_150347_e, 'B', Items.field_151072_bj);
      this.func_92103_a(new ItemStack(Blocks.field_150428_aP, 1), "A", "B", 'A', Blocks.field_150423_aK, 'B', Blocks.field_150478_aa);
      this.func_92103_a(new ItemStack(Items.field_151108_aI, 1), "A", "B", 'A', Blocks.field_150486_ae, 'B', Items.field_151143_au);
      this.func_92103_a(new ItemStack(Items.field_151109_aJ, 1), "A", "B", 'A', Blocks.field_150460_al, 'B', Items.field_151143_au);
      this.func_92103_a(new ItemStack(Items.field_151142_bV, 1), "A", "B", 'A', Blocks.field_150335_W, 'B', Items.field_151143_au);
      this.func_92103_a(new ItemStack(Items.field_151140_bW, 1), "A", "B", 'A', Blocks.field_150438_bZ, 'B', Items.field_151143_au);
      this.func_92103_a(new ItemStack(Items.field_151124_az, 1), "# #", "###", '#', Blocks.field_150344_f);
      this.func_92103_a(new ItemStack(Items.field_151133_ar, 1), "# #", " # ", '#', Items.field_151042_j);
      this.func_92103_a(new ItemStack(Items.field_151162_bE, 1), "# #", " # ", '#', Items.field_151118_aC);
      this.func_77596_b(new ItemStack(Items.field_151033_d, 1), new ItemStack(Items.field_151042_j, 1), new ItemStack(Items.field_151145_ak, 1));
      this.func_92103_a(new ItemStack(Items.field_151025_P, 1), "###", '#', Items.field_151015_O);
      this.func_92103_a(new ItemStack(Blocks.field_150476_ad, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.OAK.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150487_bG, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.BIRCH.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150485_bF, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.SPRUCE.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150481_bH, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.field_150344_f, 1, BlockPlanks.EnumType.JUNGLE.func_176839_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150400_ck, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.field_150344_f, 1, 4 + BlockPlanks.EnumType.ACACIA.func_176839_a() - 4));
      this.func_92103_a(new ItemStack(Blocks.field_150401_cl, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.field_150344_f, 1, 4 + BlockPlanks.EnumType.DARK_OAK.func_176839_a() - 4));
      this.func_92103_a(new ItemStack(Items.field_151112_aM, 1), "  #", " #X", "# X", '#', Items.field_151055_y, 'X', Items.field_151007_F);
      this.func_92103_a(new ItemStack(Items.field_151146_bM, 1), "# ", " X", '#', Items.field_151112_aM, 'X', Items.field_151172_bF);
      this.func_92103_a(new ItemStack(Blocks.field_150446_ar, 4), "#  ", "## ", "###", '#', Blocks.field_150347_e);
      this.func_92103_a(new ItemStack(Blocks.field_150389_bf, 4), "#  ", "## ", "###", '#', Blocks.field_150336_V);
      this.func_92103_a(new ItemStack(Blocks.field_150390_bg, 4), "#  ", "## ", "###", '#', Blocks.field_150417_aV);
      this.func_92103_a(new ItemStack(Blocks.field_150387_bl, 4), "#  ", "## ", "###", '#', Blocks.field_150385_bj);
      this.func_92103_a(new ItemStack(Blocks.field_150372_bz, 4), "#  ", "## ", "###", '#', Blocks.field_150322_A);
      this.func_92103_a(new ItemStack(Blocks.field_180396_cN, 4), "#  ", "## ", "###", '#', Blocks.field_180395_cM);
      this.func_92103_a(new ItemStack(Blocks.field_150370_cb, 4), "#  ", "## ", "###", '#', Blocks.field_150371_ca);
      this.func_92103_a(new ItemStack(Items.field_151159_an, 1), "###", "#X#", "###", '#', Items.field_151055_y, 'X', Blocks.field_150325_L);
      this.func_92103_a(new ItemStack(Items.field_151160_bD, 1), "###", "#X#", "###", '#', Items.field_151055_y, 'X', Items.field_151116_aA);
      this.func_92103_a(new ItemStack(Items.field_151153_ao, 1, 0), "###", "#X#", "###", '#', Items.field_151043_k, 'X', Items.field_151034_e);
      this.func_92103_a(new ItemStack(Items.field_151153_ao, 1, 1), "###", "#X#", "###", '#', Blocks.field_150340_R, 'X', Items.field_151034_e);
      this.func_92103_a(new ItemStack(Items.field_151150_bK, 1, 0), "###", "#X#", "###", '#', Items.field_151074_bl, 'X', Items.field_151172_bF);
      this.func_92103_a(new ItemStack(Items.field_151060_bw, 1), "###", "#X#", "###", '#', Items.field_151074_bl, 'X', Items.field_151127_ba);
      this.func_92103_a(new ItemStack(Blocks.field_150442_at, 1), "X", "#", '#', Blocks.field_150347_e, 'X', Items.field_151055_y);
      this.func_92103_a(new ItemStack(Blocks.field_150479_bC, 2), "I", "S", "#", '#', Blocks.field_150344_f, 'S', Items.field_151055_y, 'I', Items.field_151042_j);
      this.func_92103_a(new ItemStack(Blocks.field_150429_aA, 1), "X", "#", '#', Items.field_151055_y, 'X', Items.field_151137_ax);
      this.func_92103_a(new ItemStack(Items.field_151107_aW, 1), "#X#", "III", '#', Blocks.field_150429_aA, 'X', Items.field_151137_ax, 'I', new ItemStack(Blocks.field_150348_b, 1, BlockStone.EnumType.STONE.func_176642_a()));
      this.func_92103_a(new ItemStack(Items.field_151132_bS, 1), " # ", "#X#", "III", '#', Blocks.field_150429_aA, 'X', Items.field_151128_bU, 'I', new ItemStack(Blocks.field_150348_b, 1, BlockStone.EnumType.STONE.func_176642_a()));
      this.func_92103_a(new ItemStack(Items.field_151113_aN, 1), " # ", "#X#", " # ", '#', Items.field_151043_k, 'X', Items.field_151137_ax);
      this.func_92103_a(new ItemStack(Items.field_151111_aL, 1), " # ", "#X#", " # ", '#', Items.field_151042_j, 'X', Items.field_151137_ax);
      this.func_92103_a(new ItemStack(Items.field_151148_bJ, 1), "###", "#X#", "###", '#', Items.field_151121_aF, 'X', Items.field_151111_aL);
      this.func_92103_a(new ItemStack(Blocks.field_150430_aB, 1), "#", '#', new ItemStack(Blocks.field_150348_b, 1, BlockStone.EnumType.STONE.func_176642_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150471_bO, 1), "#", '#', Blocks.field_150344_f);
      this.func_92103_a(new ItemStack(Blocks.field_150456_au, 1), "##", '#', new ItemStack(Blocks.field_150348_b, 1, BlockStone.EnumType.STONE.func_176642_a()));
      this.func_92103_a(new ItemStack(Blocks.field_150452_aw, 1), "##", '#', Blocks.field_150344_f);
      this.func_92103_a(new ItemStack(Blocks.field_150443_bT, 1), "##", '#', Items.field_151042_j);
      this.func_92103_a(new ItemStack(Blocks.field_150445_bS, 1), "##", '#', Items.field_151043_k);
      this.func_92103_a(new ItemStack(Blocks.field_150367_z, 1), "###", "#X#", "#R#", '#', Blocks.field_150347_e, 'X', Items.field_151031_f, 'R', Items.field_151137_ax);
      this.func_92103_a(new ItemStack(Blocks.field_150409_cd, 1), "###", "# #", "#R#", '#', Blocks.field_150347_e, 'R', Items.field_151137_ax);
      this.func_92103_a(new ItemStack(Blocks.field_150331_J, 1), "TTT", "#X#", "#R#", '#', Blocks.field_150347_e, 'X', Items.field_151042_j, 'R', Items.field_151137_ax, 'T', Blocks.field_150344_f);
      this.func_92103_a(new ItemStack(Blocks.field_150320_F, 1), "S", "P", 'S', Items.field_151123_aH, 'P', Blocks.field_150331_J);
      this.func_92103_a(new ItemStack(Items.field_151104_aV, 1), "###", "XXX", '#', Blocks.field_150325_L, 'X', Blocks.field_150344_f);
      this.func_92103_a(new ItemStack(Blocks.field_150381_bn, 1), " B ", "D#D", "###", '#', Blocks.field_150343_Z, 'B', Items.field_151122_aG, 'D', Items.field_151045_i);
      this.func_92103_a(new ItemStack(Blocks.field_150467_bQ, 1), "III", " i ", "iii", 'I', Blocks.field_150339_S, 'i', Items.field_151042_j);
      this.func_92103_a(new ItemStack(Items.field_151116_aA), "##", "##", '#', Items.field_179555_bs);
      this.func_77596_b(new ItemStack(Items.field_151061_bv, 1), Items.field_151079_bi, Items.field_151065_br);
      this.func_77596_b(new ItemStack(Items.field_151059_bz, 3), Items.field_151016_H, Items.field_151065_br, Items.field_151044_h);
      this.func_77596_b(new ItemStack(Items.field_151059_bz, 3), Items.field_151016_H, Items.field_151065_br, new ItemStack(Items.field_151044_h, 1, 1));
      this.func_92103_a(new ItemStack(Blocks.field_150453_bW), "GGG", "QQQ", "WWW", 'G', Blocks.field_150359_w, 'Q', Items.field_151128_bU, 'W', Blocks.field_150376_bx);
      this.func_92103_a(new ItemStack(Blocks.field_150438_bZ), "I I", "ICI", " I ", 'I', Items.field_151042_j, 'C', Blocks.field_150486_ae);
      this.func_92103_a(new ItemStack(Items.field_179565_cj, 1), "///", " / ", "/_/", '/', Items.field_151055_y, '_', new ItemStack(Blocks.field_150333_U, 1, BlockStoneSlab.EnumType.STONE.func_176624_a()));
      Collections.sort(this.field_77597_b, new Comparator<IRecipe>() {
         public int compare(IRecipe var1, IRecipe var2) {
            if (var1 instanceof ShapelessRecipes && var2 instanceof ShapedRecipes) {
               return 1;
            } else if (var2 instanceof ShapelessRecipes && var1 instanceof ShapedRecipes) {
               return -1;
            } else if (var2.func_77570_a() < var1.func_77570_a()) {
               return -1;
            } else {
               return var2.func_77570_a() > var1.func_77570_a() ? 1 : 0;
            }
         }

         // $FF: synthetic method
         public int compare(Object var1, Object var2) {
            return this.compare((IRecipe)var1, (IRecipe)var2);
         }
      });
   }

   public ShapedRecipes func_92103_a(ItemStack var1, Object... var2) {
      String var3 = "";
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      if (var2[var4] instanceof String[]) {
         String[] var11 = (String[])((String[])var2[var4++]);

         for(int var8 = 0; var8 < var11.length; ++var8) {
            String var9 = var11[var8];
            ++var6;
            var5 = var9.length();
            var3 = var3 + var9;
         }
      } else {
         while(var2[var4] instanceof String) {
            String var7 = (String)var2[var4++];
            ++var6;
            var5 = var7.length();
            var3 = var3 + var7;
         }
      }

      HashMap var12;
      for(var12 = Maps.newHashMap(); var4 < var2.length; var4 += 2) {
         Character var13 = (Character)var2[var4];
         ItemStack var15 = null;
         if (var2[var4 + 1] instanceof Item) {
            var15 = new ItemStack((Item)var2[var4 + 1]);
         } else if (var2[var4 + 1] instanceof Block) {
            var15 = new ItemStack((Block)var2[var4 + 1], 1, 32767);
         } else if (var2[var4 + 1] instanceof ItemStack) {
            var15 = (ItemStack)var2[var4 + 1];
         }

         var12.put(var13, var15);
      }

      ItemStack[] var14 = new ItemStack[var5 * var6];

      for(int var16 = 0; var16 < var5 * var6; ++var16) {
         char var10 = var3.charAt(var16);
         if (var12.containsKey(var10)) {
            var14[var16] = ((ItemStack)var12.get(var10)).func_77946_l();
         } else {
            var14[var16] = null;
         }
      }

      ShapedRecipes var17 = new ShapedRecipes(var5, var6, var14, var1);
      this.field_77597_b.add(var17);
      return var17;
   }

   public void func_77596_b(ItemStack var1, Object... var2) {
      ArrayList var3 = Lists.newArrayList();
      Object[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Object var7 = var4[var6];
         if (var7 instanceof ItemStack) {
            var3.add(((ItemStack)var7).func_77946_l());
         } else if (var7 instanceof Item) {
            var3.add(new ItemStack((Item)var7));
         } else {
            if (!(var7 instanceof Block)) {
               throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + var7.getClass().getName() + "!");
            }

            var3.add(new ItemStack((Block)var7));
         }
      }

      this.field_77597_b.add(new ShapelessRecipes(var1, var3));
   }

   public void func_180302_a(IRecipe var1) {
      this.field_77597_b.add(var1);
   }

   public ItemStack func_82787_a(InventoryCrafting var1, World var2) {
      Iterator var3 = this.field_77597_b.iterator();

      IRecipe var4;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         var4 = (IRecipe)var3.next();
      } while(!var4.func_77569_a(var1, var2));

      return var4.func_77572_b(var1);
   }

   public ItemStack[] func_180303_b(InventoryCrafting var1, World var2) {
      Iterator var3 = this.field_77597_b.iterator();

      while(var3.hasNext()) {
         IRecipe var4 = (IRecipe)var3.next();
         if (var4.func_77569_a(var1, var2)) {
            return var4.func_179532_b(var1);
         }
      }

      ItemStack[] var5 = new ItemStack[var1.func_70302_i_()];

      for(int var6 = 0; var6 < var5.length; ++var6) {
         var5[var6] = var1.func_70301_a(var6);
      }

      return var5;
   }

   public List<IRecipe> func_77592_b() {
      return this.field_77597_b;
   }
}
