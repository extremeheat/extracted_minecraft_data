package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Direction;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class ComponentScatteredFeaturePieces$DesertPyramid extends ComponentScatteredFeaturePieces$Feature {
   private boolean[] field_74940_h = new boolean[4];
   private static final WeightedRandomChestContent[] field_74941_i = new WeightedRandomChestContent[]{
      new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 3, 3),
      new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10),
      new WeightedRandomChestContent(Items.field_151043_k, 0, 2, 7, 15),
      new WeightedRandomChestContent(Items.field_151166_bC, 0, 1, 3, 2),
      new WeightedRandomChestContent(Items.field_151103_aS, 0, 4, 6, 20),
      new WeightedRandomChestContent(Items.field_151078_bh, 0, 3, 7, 16),
      new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 3),
      new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 1),
      new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 1),
      new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 1)
   };

   public ComponentScatteredFeaturePieces$DesertPyramid() {
      super();
   }

   public ComponentScatteredFeaturePieces$DesertPyramid(Random var1, int var2, int var3) {
      super(var1, var2, 64, var3, 21, 15, 21);
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("hasPlacedChest0", this.field_74940_h[0]);
      var1.func_74757_a("hasPlacedChest1", this.field_74940_h[1]);
      var1.func_74757_a("hasPlacedChest2", this.field_74940_h[2]);
      var1.func_74757_a("hasPlacedChest3", this.field_74940_h[3]);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_74940_h[0] = var1.func_74767_n("hasPlacedChest0");
      this.field_74940_h[1] = var1.func_74767_n("hasPlacedChest1");
      this.field_74940_h[2] = var1.func_74767_n("hasPlacedChest2");
      this.field_74940_h[3] = var1.func_74767_n("hasPlacedChest3");
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      this.func_151549_a(var1, var3, 0, -4, 0, this.field_74939_a - 1, 0, this.field_74938_c - 1, Blocks.field_150322_A, Blocks.field_150322_A, false);

      for(int var4 = 1; var4 <= 9; ++var4) {
         this.func_151549_a(
            var1,
            var3,
            var4,
            var4,
            var4,
            this.field_74939_a - 1 - var4,
            var4,
            this.field_74938_c - 1 - var4,
            Blocks.field_150322_A,
            Blocks.field_150322_A,
            false
         );
         this.func_151549_a(
            var1,
            var3,
            var4 + 1,
            var4,
            var4 + 1,
            this.field_74939_a - 2 - var4,
            var4,
            this.field_74938_c - 2 - var4,
            Blocks.field_150350_a,
            Blocks.field_150350_a,
            false
         );
      }

      for(int var13 = 0; var13 < this.field_74939_a; ++var13) {
         for(int var5 = 0; var5 < this.field_74938_c; ++var5) {
            byte var6 = -5;
            this.func_151554_b(var1, Blocks.field_150322_A, 0, var13, var6, var5, var3);
         }
      }

      int var14 = this.func_151555_a(Blocks.field_150372_bz, 3);
      int var15 = this.func_151555_a(Blocks.field_150372_bz, 2);
      int var16 = this.func_151555_a(Blocks.field_150372_bz, 0);
      int var7 = this.func_151555_a(Blocks.field_150372_bz, 1);
      byte var8 = 1;
      byte var9 = 11;
      this.func_151549_a(var1, var3, 0, 0, 0, 4, 9, 4, Blocks.field_150322_A, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 1, 10, 1, 3, 10, 3, Blocks.field_150322_A, Blocks.field_150322_A, false);
      this.func_151550_a(var1, Blocks.field_150372_bz, var14, 2, 10, 0, var3);
      this.func_151550_a(var1, Blocks.field_150372_bz, var15, 2, 10, 4, var3);
      this.func_151550_a(var1, Blocks.field_150372_bz, var16, 0, 10, 2, var3);
      this.func_151550_a(var1, Blocks.field_150372_bz, var7, 4, 10, 2, var3);
      this.func_151549_a(var1, var3, this.field_74939_a - 5, 0, 0, this.field_74939_a - 1, 9, 4, Blocks.field_150322_A, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, this.field_74939_a - 4, 10, 1, this.field_74939_a - 2, 10, 3, Blocks.field_150322_A, Blocks.field_150322_A, false);
      this.func_151550_a(var1, Blocks.field_150372_bz, var14, this.field_74939_a - 3, 10, 0, var3);
      this.func_151550_a(var1, Blocks.field_150372_bz, var15, this.field_74939_a - 3, 10, 4, var3);
      this.func_151550_a(var1, Blocks.field_150372_bz, var16, this.field_74939_a - 5, 10, 2, var3);
      this.func_151550_a(var1, Blocks.field_150372_bz, var7, this.field_74939_a - 1, 10, 2, var3);
      this.func_151549_a(var1, var3, 8, 0, 0, 12, 4, 4, Blocks.field_150322_A, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 9, 1, 0, 11, 3, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151550_a(var1, Blocks.field_150322_A, 2, 9, 1, 1, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 2, 9, 2, 1, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 2, 9, 3, 1, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 2, 10, 3, 1, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 2, 11, 3, 1, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 2, 11, 2, 1, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 2, 11, 1, 1, var3);
      this.func_151549_a(var1, var3, 4, 1, 1, 8, 3, 3, Blocks.field_150322_A, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 4, 1, 2, 8, 2, 2, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 12, 1, 1, 16, 3, 3, Blocks.field_150322_A, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 12, 1, 2, 16, 2, 2, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 5, 4, 5, this.field_74939_a - 6, 4, this.field_74938_c - 6, Blocks.field_150322_A, Blocks.field_150322_A, false);
      this.func_151549_a(var1, var3, 9, 4, 9, 11, 4, 11, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151556_a(var1, var3, 8, 1, 8, 8, 3, 8, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
      this.func_151556_a(var1, var3, 12, 1, 8, 12, 3, 8, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
      this.func_151556_a(var1, var3, 8, 1, 12, 8, 3, 12, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
      this.func_151556_a(var1, var3, 12, 1, 12, 12, 3, 12, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
      this.func_151549_a(var1, var3, 1, 1, 5, 4, 4, 11, Blocks.field_150322_A, Blocks.field_150322_A, false);
      this.func_151549_a(var1, var3, this.field_74939_a - 5, 1, 5, this.field_74939_a - 2, 4, 11, Blocks.field_150322_A, Blocks.field_150322_A, false);
      this.func_151549_a(var1, var3, 6, 7, 9, 6, 7, 11, Blocks.field_150322_A, Blocks.field_150322_A, false);
      this.func_151549_a(var1, var3, this.field_74939_a - 7, 7, 9, this.field_74939_a - 7, 7, 11, Blocks.field_150322_A, Blocks.field_150322_A, false);
      this.func_151556_a(var1, var3, 5, 5, 9, 5, 7, 11, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
      this.func_151556_a(var1, var3, this.field_74939_a - 6, 5, 9, this.field_74939_a - 6, 7, 11, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 5, 5, 10, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 5, 6, 10, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 6, 6, 10, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, this.field_74939_a - 6, 5, 10, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, this.field_74939_a - 6, 6, 10, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, this.field_74939_a - 7, 6, 10, var3);
      this.func_151549_a(var1, var3, 2, 4, 4, 2, 6, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, this.field_74939_a - 3, 4, 4, this.field_74939_a - 3, 6, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151550_a(var1, Blocks.field_150372_bz, var14, 2, 4, 5, var3);
      this.func_151550_a(var1, Blocks.field_150372_bz, var14, 2, 3, 4, var3);
      this.func_151550_a(var1, Blocks.field_150372_bz, var14, this.field_74939_a - 3, 4, 5, var3);
      this.func_151550_a(var1, Blocks.field_150372_bz, var14, this.field_74939_a - 3, 3, 4, var3);
      this.func_151549_a(var1, var3, 1, 1, 3, 2, 2, 3, Blocks.field_150322_A, Blocks.field_150322_A, false);
      this.func_151549_a(var1, var3, this.field_74939_a - 3, 1, 3, this.field_74939_a - 2, 2, 3, Blocks.field_150322_A, Blocks.field_150322_A, false);
      this.func_151550_a(var1, Blocks.field_150372_bz, 0, 1, 1, 2, var3);
      this.func_151550_a(var1, Blocks.field_150372_bz, 0, this.field_74939_a - 2, 1, 2, var3);
      this.func_151550_a(var1, Blocks.field_150333_U, 1, 1, 2, 2, var3);
      this.func_151550_a(var1, Blocks.field_150333_U, 1, this.field_74939_a - 2, 2, 2, var3);
      this.func_151550_a(var1, Blocks.field_150372_bz, var7, 2, 1, 2, var3);
      this.func_151550_a(var1, Blocks.field_150372_bz, var16, this.field_74939_a - 3, 1, 2, var3);
      this.func_151549_a(var1, var3, 4, 3, 5, 4, 3, 18, Blocks.field_150322_A, Blocks.field_150322_A, false);
      this.func_151549_a(var1, var3, this.field_74939_a - 5, 3, 5, this.field_74939_a - 5, 3, 17, Blocks.field_150322_A, Blocks.field_150322_A, false);
      this.func_151549_a(var1, var3, 3, 1, 5, 4, 2, 16, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, this.field_74939_a - 6, 1, 5, this.field_74939_a - 5, 2, 16, Blocks.field_150350_a, Blocks.field_150350_a, false);

      for(int var10 = 5; var10 <= 17; var10 += 2) {
         this.func_151550_a(var1, Blocks.field_150322_A, 2, 4, 1, var10, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 1, 4, 2, var10, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, this.field_74939_a - 5, 1, var10, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 1, this.field_74939_a - 5, 2, var10, var3);
      }

      this.func_151550_a(var1, Blocks.field_150325_L, var8, 10, 0, 7, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var8, 10, 0, 8, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var8, 9, 0, 9, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var8, 11, 0, 9, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var8, 8, 0, 10, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var8, 12, 0, 10, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var8, 7, 0, 10, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var8, 13, 0, 10, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var8, 9, 0, 11, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var8, 11, 0, 11, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var8, 10, 0, 12, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var8, 10, 0, 13, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var9, 10, 0, 10, var3);

      for(int var17 = 0; var17 <= this.field_74939_a - 1; var17 += this.field_74939_a - 1) {
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var17, 2, 1, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var17, 2, 2, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var17, 2, 3, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var17, 3, 1, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var17, 3, 2, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var17, 3, 3, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var17, 4, 1, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 1, var17, 4, 2, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var17, 4, 3, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var17, 5, 1, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var17, 5, 2, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var17, 5, 3, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var17, 6, 1, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 1, var17, 6, 2, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var17, 6, 3, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var17, 7, 1, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var17, 7, 2, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var17, 7, 3, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var17, 8, 1, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var17, 8, 2, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var17, 8, 3, var3);
      }

      for(int var18 = 2; var18 <= this.field_74939_a - 3; var18 += this.field_74939_a - 3 - 2) {
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var18 - 1, 2, 0, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var18, 2, 0, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var18 + 1, 2, 0, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var18 - 1, 3, 0, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var18, 3, 0, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var18 + 1, 3, 0, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var18 - 1, 4, 0, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 1, var18, 4, 0, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var18 + 1, 4, 0, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var18 - 1, 5, 0, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var18, 5, 0, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var18 + 1, 5, 0, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var18 - 1, 6, 0, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 1, var18, 6, 0, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var18 + 1, 6, 0, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var18 - 1, 7, 0, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var18, 7, 0, var3);
         this.func_151550_a(var1, Blocks.field_150325_L, var8, var18 + 1, 7, 0, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var18 - 1, 8, 0, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var18, 8, 0, var3);
         this.func_151550_a(var1, Blocks.field_150322_A, 2, var18 + 1, 8, 0, var3);
      }

      this.func_151556_a(var1, var3, 8, 4, 0, 12, 6, 0, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 8, 6, 0, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 12, 6, 0, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var8, 9, 5, 0, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 1, 10, 5, 0, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, var8, 11, 5, 0, var3);
      this.func_151556_a(var1, var3, 8, -14, 8, 12, -11, 12, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
      this.func_151556_a(var1, var3, 8, -10, 8, 12, -10, 12, Blocks.field_150322_A, 1, Blocks.field_150322_A, 1, false);
      this.func_151556_a(var1, var3, 8, -9, 8, 12, -9, 12, Blocks.field_150322_A, 2, Blocks.field_150322_A, 2, false);
      this.func_151549_a(var1, var3, 8, -8, 8, 12, -1, 12, Blocks.field_150322_A, Blocks.field_150322_A, false);
      this.func_151549_a(var1, var3, 9, -11, 9, 11, -1, 11, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151550_a(var1, Blocks.field_150456_au, 0, 10, -11, 10, var3);
      this.func_151549_a(var1, var3, 9, -13, 9, 11, -13, 11, Blocks.field_150335_W, Blocks.field_150350_a, false);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 8, -11, 10, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 8, -10, 10, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 1, 7, -10, 10, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 2, 7, -11, 10, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 12, -11, 10, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 12, -10, 10, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 1, 13, -10, 10, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 2, 13, -11, 10, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 10, -11, 8, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 10, -10, 8, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 1, 10, -10, 7, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 2, 10, -11, 7, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 10, -11, 12, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 10, -10, 12, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 1, 10, -10, 13, var3);
      this.func_151550_a(var1, Blocks.field_150322_A, 2, 10, -11, 13, var3);

      for(int var19 = 0; var19 < 4; ++var19) {
         if (!this.field_74940_h[var19]) {
            int var11 = Direction.field_71583_a[var19] * 2;
            int var12 = Direction.field_71581_b[var19] * 2;
            this.field_74940_h[var19] = this.func_74879_a(
               var1,
               var3,
               var2,
               10 + var11,
               -11,
               10 + var12,
               WeightedRandomChestContent.func_92080_a(field_74941_i, Items.field_151134_bR.func_92114_b(var2)),
               2 + var2.nextInt(5)
            );
         }
      }

      return true;
   }
}
