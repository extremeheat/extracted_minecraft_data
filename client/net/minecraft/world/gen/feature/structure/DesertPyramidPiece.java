package net.minecraft.world.gen.feature.structure;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class DesertPyramidPiece extends ScatteredStructurePiece {
   private final boolean[] field_202598_e = new boolean[4];

   public static void func_202597_ad_() {
      StructureIO.func_143031_a(DesertPyramidPiece.class, "TeDP");
   }

   public DesertPyramidPiece() {
      super();
   }

   public DesertPyramidPiece(Random var1, int var2, int var3) {
      super(var1, var2, 64, var3, 21, 15, 21);
   }

   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("hasPlacedChest0", this.field_202598_e[0]);
      var1.func_74757_a("hasPlacedChest1", this.field_202598_e[1]);
      var1.func_74757_a("hasPlacedChest2", this.field_202598_e[2]);
      var1.func_74757_a("hasPlacedChest3", this.field_202598_e[3]);
   }

   protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
      super.func_143011_b(var1, var2);
      this.field_202598_e[0] = var1.func_74767_n("hasPlacedChest0");
      this.field_202598_e[1] = var1.func_74767_n("hasPlacedChest1");
      this.field_202598_e[2] = var1.func_74767_n("hasPlacedChest2");
      this.field_202598_e[3] = var1.func_74767_n("hasPlacedChest3");
   }

   public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
      this.func_175804_a(var1, var3, 0, -4, 0, this.field_202581_a - 1, 0, this.field_202583_c - 1, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);

      int var5;
      for(var5 = 1; var5 <= 9; ++var5) {
         this.func_175804_a(var1, var3, var5, var5, var5, this.field_202581_a - 1 - var5, var5, this.field_202583_c - 1 - var5, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
         this.func_175804_a(var1, var3, var5 + 1, var5, var5 + 1, this.field_202581_a - 2 - var5, var5, this.field_202583_c - 2 - var5, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      }

      for(var5 = 0; var5 < this.field_202581_a; ++var5) {
         for(int var6 = 0; var6 < this.field_202583_c; ++var6) {
            boolean var7 = true;
            this.func_175808_b(var1, Blocks.field_150322_A.func_176223_P(), var5, -5, var6, var3);
         }
      }

      IBlockState var13 = (IBlockState)Blocks.field_150372_bz.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH);
      IBlockState var14 = (IBlockState)Blocks.field_150372_bz.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.SOUTH);
      IBlockState var15 = (IBlockState)Blocks.field_150372_bz.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.EAST);
      IBlockState var8 = (IBlockState)Blocks.field_150372_bz.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.WEST);
      this.func_175804_a(var1, var3, 0, 0, 0, 4, 9, 4, Blocks.field_150322_A.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175804_a(var1, var3, 1, 10, 1, 3, 10, 3, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
      this.func_175811_a(var1, var13, 2, 10, 0, var3);
      this.func_175811_a(var1, var14, 2, 10, 4, var3);
      this.func_175811_a(var1, var15, 0, 10, 2, var3);
      this.func_175811_a(var1, var8, 4, 10, 2, var3);
      this.func_175804_a(var1, var3, this.field_202581_a - 5, 0, 0, this.field_202581_a - 1, 9, 4, Blocks.field_150322_A.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175804_a(var1, var3, this.field_202581_a - 4, 10, 1, this.field_202581_a - 2, 10, 3, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
      this.func_175811_a(var1, var13, this.field_202581_a - 3, 10, 0, var3);
      this.func_175811_a(var1, var14, this.field_202581_a - 3, 10, 4, var3);
      this.func_175811_a(var1, var15, this.field_202581_a - 5, 10, 2, var3);
      this.func_175811_a(var1, var8, this.field_202581_a - 1, 10, 2, var3);
      this.func_175804_a(var1, var3, 8, 0, 0, 12, 4, 4, Blocks.field_150322_A.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175804_a(var1, var3, 9, 1, 0, 11, 3, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), 9, 1, 1, var3);
      this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), 9, 2, 1, var3);
      this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), 9, 3, 1, var3);
      this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), 10, 3, 1, var3);
      this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), 11, 3, 1, var3);
      this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), 11, 2, 1, var3);
      this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), 11, 1, 1, var3);
      this.func_175804_a(var1, var3, 4, 1, 1, 8, 3, 3, Blocks.field_150322_A.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175804_a(var1, var3, 4, 1, 2, 8, 2, 2, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175804_a(var1, var3, 12, 1, 1, 16, 3, 3, Blocks.field_150322_A.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175804_a(var1, var3, 12, 1, 2, 16, 2, 2, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175804_a(var1, var3, 5, 4, 5, this.field_202581_a - 6, 4, this.field_202583_c - 6, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
      this.func_175804_a(var1, var3, 9, 4, 9, 11, 4, 11, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175804_a(var1, var3, 8, 1, 8, 8, 3, 8, Blocks.field_196585_ak.func_176223_P(), Blocks.field_196585_ak.func_176223_P(), false);
      this.func_175804_a(var1, var3, 12, 1, 8, 12, 3, 8, Blocks.field_196585_ak.func_176223_P(), Blocks.field_196585_ak.func_176223_P(), false);
      this.func_175804_a(var1, var3, 8, 1, 12, 8, 3, 12, Blocks.field_196585_ak.func_176223_P(), Blocks.field_196585_ak.func_176223_P(), false);
      this.func_175804_a(var1, var3, 12, 1, 12, 12, 3, 12, Blocks.field_196585_ak.func_176223_P(), Blocks.field_196585_ak.func_176223_P(), false);
      this.func_175804_a(var1, var3, 1, 1, 5, 4, 4, 11, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
      this.func_175804_a(var1, var3, this.field_202581_a - 5, 1, 5, this.field_202581_a - 2, 4, 11, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
      this.func_175804_a(var1, var3, 6, 7, 9, 6, 7, 11, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
      this.func_175804_a(var1, var3, this.field_202581_a - 7, 7, 9, this.field_202581_a - 7, 7, 11, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
      this.func_175804_a(var1, var3, 5, 5, 9, 5, 7, 11, Blocks.field_196585_ak.func_176223_P(), Blocks.field_196585_ak.func_176223_P(), false);
      this.func_175804_a(var1, var3, this.field_202581_a - 6, 5, 9, this.field_202581_a - 6, 7, 11, Blocks.field_196585_ak.func_176223_P(), Blocks.field_196585_ak.func_176223_P(), false);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 5, 5, 10, var3);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 5, 6, 10, var3);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 6, 6, 10, var3);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), this.field_202581_a - 6, 5, 10, var3);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), this.field_202581_a - 6, 6, 10, var3);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), this.field_202581_a - 7, 6, 10, var3);
      this.func_175804_a(var1, var3, 2, 4, 4, 2, 6, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175804_a(var1, var3, this.field_202581_a - 3, 4, 4, this.field_202581_a - 3, 6, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175811_a(var1, var13, 2, 4, 5, var3);
      this.func_175811_a(var1, var13, 2, 3, 4, var3);
      this.func_175811_a(var1, var13, this.field_202581_a - 3, 4, 5, var3);
      this.func_175811_a(var1, var13, this.field_202581_a - 3, 3, 4, var3);
      this.func_175804_a(var1, var3, 1, 1, 3, 2, 2, 3, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
      this.func_175804_a(var1, var3, this.field_202581_a - 3, 1, 3, this.field_202581_a - 2, 2, 3, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
      this.func_175811_a(var1, Blocks.field_150322_A.func_176223_P(), 1, 1, 2, var3);
      this.func_175811_a(var1, Blocks.field_150322_A.func_176223_P(), this.field_202581_a - 2, 1, 2, var3);
      this.func_175811_a(var1, Blocks.field_196640_bx.func_176223_P(), 1, 2, 2, var3);
      this.func_175811_a(var1, Blocks.field_196640_bx.func_176223_P(), this.field_202581_a - 2, 2, 2, var3);
      this.func_175811_a(var1, var8, 2, 1, 2, var3);
      this.func_175811_a(var1, var15, this.field_202581_a - 3, 1, 2, var3);
      this.func_175804_a(var1, var3, 4, 3, 5, 4, 3, 17, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
      this.func_175804_a(var1, var3, this.field_202581_a - 5, 3, 5, this.field_202581_a - 5, 3, 17, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
      this.func_175804_a(var1, var3, 3, 1, 5, 4, 2, 16, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175804_a(var1, var3, this.field_202581_a - 6, 1, 5, this.field_202581_a - 5, 2, 16, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);

      int var9;
      for(var9 = 5; var9 <= 17; var9 += 2) {
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), 4, 1, var9, var3);
         this.func_175811_a(var1, Blocks.field_196583_aj.func_176223_P(), 4, 2, var9, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), this.field_202581_a - 5, 1, var9, var3);
         this.func_175811_a(var1, Blocks.field_196583_aj.func_176223_P(), this.field_202581_a - 5, 2, var9, var3);
      }

      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 10, 0, 7, var3);
      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 10, 0, 8, var3);
      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 9, 0, 9, var3);
      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 11, 0, 9, var3);
      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 8, 0, 10, var3);
      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 12, 0, 10, var3);
      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 7, 0, 10, var3);
      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 13, 0, 10, var3);
      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 9, 0, 11, var3);
      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 11, 0, 11, var3);
      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 10, 0, 12, var3);
      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 10, 0, 13, var3);
      this.func_175811_a(var1, Blocks.field_196797_fz.func_176223_P(), 10, 0, 10, var3);

      for(var9 = 0; var9 <= this.field_202581_a - 1; var9 += this.field_202581_a - 1) {
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9, 2, 1, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9, 2, 3, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9, 3, 1, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 3, 2, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9, 3, 3, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 4, 1, var3);
         this.func_175811_a(var1, Blocks.field_196583_aj.func_176223_P(), var9, 4, 2, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 4, 3, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9, 5, 1, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 5, 2, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9, 5, 3, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 6, 1, var3);
         this.func_175811_a(var1, Blocks.field_196583_aj.func_176223_P(), var9, 6, 2, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 6, 3, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 7, 1, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 7, 2, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 7, 3, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9, 8, 1, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9, 8, 2, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9, 8, 3, var3);
      }

      for(var9 = 2; var9 <= this.field_202581_a - 3; var9 += this.field_202581_a - 3 - 2) {
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9 - 1, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9 + 1, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9 - 1, 3, 0, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 3, 0, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9 + 1, 3, 0, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9 - 1, 4, 0, var3);
         this.func_175811_a(var1, Blocks.field_196583_aj.func_176223_P(), var9, 4, 0, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9 + 1, 4, 0, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9 - 1, 5, 0, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 5, 0, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9 + 1, 5, 0, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9 - 1, 6, 0, var3);
         this.func_175811_a(var1, Blocks.field_196583_aj.func_176223_P(), var9, 6, 0, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9 + 1, 6, 0, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9 - 1, 7, 0, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9, 7, 0, var3);
         this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), var9 + 1, 7, 0, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9 - 1, 8, 0, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9, 8, 0, var3);
         this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), var9 + 1, 8, 0, var3);
      }

      this.func_175804_a(var1, var3, 8, 4, 0, 12, 6, 0, Blocks.field_196585_ak.func_176223_P(), Blocks.field_196585_ak.func_176223_P(), false);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 8, 6, 0, var3);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 12, 6, 0, var3);
      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 9, 5, 0, var3);
      this.func_175811_a(var1, Blocks.field_196583_aj.func_176223_P(), 10, 5, 0, var3);
      this.func_175811_a(var1, Blocks.field_196778_fp.func_176223_P(), 11, 5, 0, var3);
      this.func_175804_a(var1, var3, 8, -14, 8, 12, -11, 12, Blocks.field_196585_ak.func_176223_P(), Blocks.field_196585_ak.func_176223_P(), false);
      this.func_175804_a(var1, var3, 8, -10, 8, 12, -10, 12, Blocks.field_196583_aj.func_176223_P(), Blocks.field_196583_aj.func_176223_P(), false);
      this.func_175804_a(var1, var3, 8, -9, 8, 12, -9, 12, Blocks.field_196585_ak.func_176223_P(), Blocks.field_196585_ak.func_176223_P(), false);
      this.func_175804_a(var1, var3, 8, -8, 8, 12, -1, 12, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
      this.func_175804_a(var1, var3, 9, -11, 9, 11, -1, 11, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175811_a(var1, Blocks.field_150456_au.func_176223_P(), 10, -11, 10, var3);
      this.func_175804_a(var1, var3, 9, -13, 9, 11, -13, 11, Blocks.field_150335_W.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 8, -11, 10, var3);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 8, -10, 10, var3);
      this.func_175811_a(var1, Blocks.field_196583_aj.func_176223_P(), 7, -10, 10, var3);
      this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), 7, -11, 10, var3);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 12, -11, 10, var3);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 12, -10, 10, var3);
      this.func_175811_a(var1, Blocks.field_196583_aj.func_176223_P(), 13, -10, 10, var3);
      this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), 13, -11, 10, var3);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 10, -11, 8, var3);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 10, -10, 8, var3);
      this.func_175811_a(var1, Blocks.field_196583_aj.func_176223_P(), 10, -10, 7, var3);
      this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), 10, -11, 7, var3);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 10, -11, 12, var3);
      this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 10, -10, 12, var3);
      this.func_175811_a(var1, Blocks.field_196583_aj.func_176223_P(), 10, -10, 13, var3);
      this.func_175811_a(var1, Blocks.field_196585_ak.func_176223_P(), 10, -11, 13, var3);
      Iterator var16 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var16.hasNext()) {
         EnumFacing var10 = (EnumFacing)var16.next();
         if (!this.field_202598_e[var10.func_176736_b()]) {
            int var11 = var10.func_82601_c() * 2;
            int var12 = var10.func_82599_e() * 2;
            this.field_202598_e[var10.func_176736_b()] = this.func_186167_a(var1, var3, var2, 10 + var11, -11, 10 + var12, LootTableList.field_186429_k);
         }
      }

      return true;
   }
}
