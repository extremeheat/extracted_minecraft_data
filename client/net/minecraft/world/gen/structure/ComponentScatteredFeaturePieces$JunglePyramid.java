package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.block.BlockLever;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class ComponentScatteredFeaturePieces$JunglePyramid extends ComponentScatteredFeaturePieces$Feature {
   private boolean field_74947_h;
   private boolean field_74948_i;
   private boolean field_74945_j;
   private boolean field_74946_k;
   private static final WeightedRandomChestContent[] field_74943_l = new WeightedRandomChestContent[]{
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
   private static final WeightedRandomChestContent[] field_74944_m = new WeightedRandomChestContent[]{
      new WeightedRandomChestContent(Items.field_151032_g, 0, 2, 7, 30)
   };
   private static ComponentScatteredFeaturePieces$JunglePyramid$Stones field_74942_n = new ComponentScatteredFeaturePieces$JunglePyramid$Stones(null);

   public ComponentScatteredFeaturePieces$JunglePyramid() {
      super();
   }

   public ComponentScatteredFeaturePieces$JunglePyramid(Random var1, int var2, int var3) {
      super(var1, var2, 64, var3, 12, 10, 15);
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("placedMainChest", this.field_74947_h);
      var1.func_74757_a("placedHiddenChest", this.field_74948_i);
      var1.func_74757_a("placedTrap1", this.field_74945_j);
      var1.func_74757_a("placedTrap2", this.field_74946_k);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_74947_h = var1.func_74767_n("placedMainChest");
      this.field_74948_i = var1.func_74767_n("placedHiddenChest");
      this.field_74945_j = var1.func_74767_n("placedTrap1");
      this.field_74946_k = var1.func_74767_n("placedTrap2");
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (!this.func_74935_a(var1, var3, 0)) {
         return false;
      } else {
         int var4 = this.func_151555_a(Blocks.field_150446_ar, 3);
         int var5 = this.func_151555_a(Blocks.field_150446_ar, 2);
         int var6 = this.func_151555_a(Blocks.field_150446_ar, 0);
         int var7 = this.func_151555_a(Blocks.field_150446_ar, 1);
         this.func_74882_a(var1, var3, 0, -4, 0, this.field_74939_a - 1, 0, this.field_74938_c - 1, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 2, 1, 2, 9, 2, 2, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 2, 1, 12, 9, 2, 12, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 2, 1, 3, 2, 2, 11, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 9, 1, 3, 9, 2, 11, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 1, 3, 1, 10, 6, 1, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 1, 3, 13, 10, 6, 13, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 1, 3, 2, 1, 6, 12, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 10, 3, 2, 10, 6, 12, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 2, 3, 2, 9, 3, 12, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 2, 6, 2, 9, 6, 12, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 3, 7, 3, 8, 7, 11, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 4, 8, 4, 7, 8, 10, false, var2, field_74942_n);
         this.func_74878_a(var1, var3, 3, 1, 3, 8, 2, 11);
         this.func_74878_a(var1, var3, 4, 3, 6, 7, 3, 9);
         this.func_74878_a(var1, var3, 2, 4, 2, 9, 5, 12);
         this.func_74878_a(var1, var3, 4, 6, 5, 7, 6, 9);
         this.func_74878_a(var1, var3, 5, 7, 6, 6, 7, 8);
         this.func_74878_a(var1, var3, 5, 1, 2, 6, 2, 2);
         this.func_74878_a(var1, var3, 5, 2, 12, 6, 2, 12);
         this.func_74878_a(var1, var3, 5, 5, 1, 6, 5, 1);
         this.func_74878_a(var1, var3, 5, 5, 13, 6, 5, 13);
         this.func_151550_a(var1, Blocks.field_150350_a, 0, 1, 5, 5, var3);
         this.func_151550_a(var1, Blocks.field_150350_a, 0, 10, 5, 5, var3);
         this.func_151550_a(var1, Blocks.field_150350_a, 0, 1, 5, 9, var3);
         this.func_151550_a(var1, Blocks.field_150350_a, 0, 10, 5, 9, var3);

         for(int var8 = 0; var8 <= 14; var8 += 14) {
            this.func_74882_a(var1, var3, 2, 4, var8, 2, 5, var8, false, var2, field_74942_n);
            this.func_74882_a(var1, var3, 4, 4, var8, 4, 5, var8, false, var2, field_74942_n);
            this.func_74882_a(var1, var3, 7, 4, var8, 7, 5, var8, false, var2, field_74942_n);
            this.func_74882_a(var1, var3, 9, 4, var8, 9, 5, var8, false, var2, field_74942_n);
         }

         this.func_74882_a(var1, var3, 5, 6, 0, 6, 6, 0, false, var2, field_74942_n);

         for(int var10 = 0; var10 <= 11; var10 += 11) {
            for(int var9 = 2; var9 <= 12; var9 += 2) {
               this.func_74882_a(var1, var3, var10, 4, var9, var10, 5, var9, false, var2, field_74942_n);
            }

            this.func_74882_a(var1, var3, var10, 6, 5, var10, 6, 5, false, var2, field_74942_n);
            this.func_74882_a(var1, var3, var10, 6, 9, var10, 6, 9, false, var2, field_74942_n);
         }

         this.func_74882_a(var1, var3, 2, 7, 2, 2, 9, 2, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 9, 7, 2, 9, 9, 2, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 2, 7, 12, 2, 9, 12, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 9, 7, 12, 9, 9, 12, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 4, 9, 4, 4, 9, 4, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 7, 9, 4, 7, 9, 4, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 4, 9, 10, 4, 9, 10, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 7, 9, 10, 7, 9, 10, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 5, 9, 7, 6, 9, 7, false, var2, field_74942_n);
         this.func_151550_a(var1, Blocks.field_150446_ar, var4, 5, 9, 6, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var4, 6, 9, 6, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var5, 5, 9, 8, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var5, 6, 9, 8, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var4, 4, 0, 0, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var4, 5, 0, 0, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var4, 6, 0, 0, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var4, 7, 0, 0, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var4, 4, 1, 8, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var4, 4, 2, 9, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var4, 4, 3, 10, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var4, 7, 1, 8, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var4, 7, 2, 9, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var4, 7, 3, 10, var3);
         this.func_74882_a(var1, var3, 4, 1, 9, 4, 1, 9, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 7, 1, 9, 7, 1, 9, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 4, 1, 10, 7, 2, 10, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 5, 4, 5, 6, 4, 5, false, var2, field_74942_n);
         this.func_151550_a(var1, Blocks.field_150446_ar, var6, 4, 4, 5, var3);
         this.func_151550_a(var1, Blocks.field_150446_ar, var7, 7, 4, 5, var3);

         for(int var11 = 0; var11 < 4; ++var11) {
            this.func_151550_a(var1, Blocks.field_150446_ar, var5, 5, 0 - var11, 6 + var11, var3);
            this.func_151550_a(var1, Blocks.field_150446_ar, var5, 6, 0 - var11, 6 + var11, var3);
            this.func_74878_a(var1, var3, 5, 0 - var11, 7 + var11, 6, 0 - var11, 9 + var11);
         }

         this.func_74878_a(var1, var3, 1, -3, 12, 10, -1, 13);
         this.func_74878_a(var1, var3, 1, -3, 1, 3, -1, 13);
         this.func_74878_a(var1, var3, 1, -3, 1, 9, -1, 5);

         for(int var12 = 1; var12 <= 13; var12 += 2) {
            this.func_74882_a(var1, var3, 1, -3, var12, 1, -2, var12, false, var2, field_74942_n);
         }

         for(int var13 = 2; var13 <= 12; var13 += 2) {
            this.func_74882_a(var1, var3, 1, -1, var13, 3, -1, var13, false, var2, field_74942_n);
         }

         this.func_74882_a(var1, var3, 2, -2, 1, 5, -2, 1, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 7, -2, 1, 9, -2, 1, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 6, -3, 1, 6, -3, 1, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 6, -1, 1, 6, -1, 1, false, var2, field_74942_n);
         this.func_151550_a(var1, Blocks.field_150479_bC, this.func_151555_a(Blocks.field_150479_bC, 3) | 4, 1, -3, 8, var3);
         this.func_151550_a(var1, Blocks.field_150479_bC, this.func_151555_a(Blocks.field_150479_bC, 1) | 4, 4, -3, 8, var3);
         this.func_151550_a(var1, Blocks.field_150473_bD, 4, 2, -3, 8, var3);
         this.func_151550_a(var1, Blocks.field_150473_bD, 4, 3, -3, 8, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 5, -3, 7, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 5, -3, 6, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 5, -3, 5, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 5, -3, 4, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 5, -3, 3, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 5, -3, 2, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 5, -3, 1, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 4, -3, 1, var3);
         this.func_151550_a(var1, Blocks.field_150341_Y, 0, 3, -3, 1, var3);
         if (!this.field_74945_j) {
            this.field_74945_j = this.func_74869_a(var1, var3, var2, 3, -2, 1, 2, field_74944_m, 2);
         }

         this.func_151550_a(var1, Blocks.field_150395_bd, 15, 3, -2, 2, var3);
         this.func_151550_a(var1, Blocks.field_150479_bC, this.func_151555_a(Blocks.field_150479_bC, 2) | 4, 7, -3, 1, var3);
         this.func_151550_a(var1, Blocks.field_150479_bC, this.func_151555_a(Blocks.field_150479_bC, 0) | 4, 7, -3, 5, var3);
         this.func_151550_a(var1, Blocks.field_150473_bD, 4, 7, -3, 2, var3);
         this.func_151550_a(var1, Blocks.field_150473_bD, 4, 7, -3, 3, var3);
         this.func_151550_a(var1, Blocks.field_150473_bD, 4, 7, -3, 4, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 8, -3, 6, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 9, -3, 6, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 9, -3, 5, var3);
         this.func_151550_a(var1, Blocks.field_150341_Y, 0, 9, -3, 4, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 9, -2, 4, var3);
         if (!this.field_74946_k) {
            this.field_74946_k = this.func_74869_a(var1, var3, var2, 9, -2, 3, 4, field_74944_m, 2);
         }

         this.func_151550_a(var1, Blocks.field_150395_bd, 15, 8, -1, 3, var3);
         this.func_151550_a(var1, Blocks.field_150395_bd, 15, 8, -2, 3, var3);
         if (!this.field_74947_h) {
            this.field_74947_h = this.func_74879_a(
               var1,
               var3,
               var2,
               8,
               -3,
               3,
               WeightedRandomChestContent.func_92080_a(field_74943_l, Items.field_151134_bR.func_92114_b(var2)),
               2 + var2.nextInt(5)
            );
         }

         this.func_151550_a(var1, Blocks.field_150341_Y, 0, 9, -3, 2, var3);
         this.func_151550_a(var1, Blocks.field_150341_Y, 0, 8, -3, 1, var3);
         this.func_151550_a(var1, Blocks.field_150341_Y, 0, 4, -3, 5, var3);
         this.func_151550_a(var1, Blocks.field_150341_Y, 0, 5, -2, 5, var3);
         this.func_151550_a(var1, Blocks.field_150341_Y, 0, 5, -1, 5, var3);
         this.func_151550_a(var1, Blocks.field_150341_Y, 0, 6, -3, 5, var3);
         this.func_151550_a(var1, Blocks.field_150341_Y, 0, 7, -2, 5, var3);
         this.func_151550_a(var1, Blocks.field_150341_Y, 0, 7, -1, 5, var3);
         this.func_151550_a(var1, Blocks.field_150341_Y, 0, 8, -3, 5, var3);
         this.func_74882_a(var1, var3, 9, -1, 1, 9, -1, 5, false, var2, field_74942_n);
         this.func_74878_a(var1, var3, 8, -3, 8, 10, -1, 10);
         this.func_151550_a(var1, Blocks.field_150417_aV, 3, 8, -2, 11, var3);
         this.func_151550_a(var1, Blocks.field_150417_aV, 3, 9, -2, 11, var3);
         this.func_151550_a(var1, Blocks.field_150417_aV, 3, 10, -2, 11, var3);
         this.func_151550_a(var1, Blocks.field_150442_at, BlockLever.func_149819_b(this.func_151555_a(Blocks.field_150442_at, 2)), 8, -2, 12, var3);
         this.func_151550_a(var1, Blocks.field_150442_at, BlockLever.func_149819_b(this.func_151555_a(Blocks.field_150442_at, 2)), 9, -2, 12, var3);
         this.func_151550_a(var1, Blocks.field_150442_at, BlockLever.func_149819_b(this.func_151555_a(Blocks.field_150442_at, 2)), 10, -2, 12, var3);
         this.func_74882_a(var1, var3, 8, -3, 8, 8, -3, 10, false, var2, field_74942_n);
         this.func_74882_a(var1, var3, 10, -3, 8, 10, -3, 10, false, var2, field_74942_n);
         this.func_151550_a(var1, Blocks.field_150341_Y, 0, 10, -2, 9, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 8, -2, 9, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 8, -2, 10, var3);
         this.func_151550_a(var1, Blocks.field_150488_af, 0, 10, -1, 9, var3);
         this.func_151550_a(var1, Blocks.field_150320_F, 1, 9, -2, 8, var3);
         this.func_151550_a(var1, Blocks.field_150320_F, this.func_151555_a(Blocks.field_150320_F, 4), 10, -2, 8, var3);
         this.func_151550_a(var1, Blocks.field_150320_F, this.func_151555_a(Blocks.field_150320_F, 4), 10, -1, 8, var3);
         this.func_151550_a(var1, Blocks.field_150413_aR, this.func_151555_a(Blocks.field_150413_aR, 2), 10, -2, 10, var3);
         if (!this.field_74948_i) {
            this.field_74948_i = this.func_74879_a(
               var1,
               var3,
               var2,
               9,
               -3,
               10,
               WeightedRandomChestContent.func_92080_a(field_74943_l, Items.field_151134_bR.func_92114_b(var2)),
               2 + var2.nextInt(5)
            );
         }

         return true;
      }
   }
}
