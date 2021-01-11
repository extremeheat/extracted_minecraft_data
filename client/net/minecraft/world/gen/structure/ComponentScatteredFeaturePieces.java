package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class ComponentScatteredFeaturePieces {
   public static void func_143045_a() {
      MapGenStructureIO.func_143031_a(ComponentScatteredFeaturePieces.DesertPyramid.class, "TeDP");
      MapGenStructureIO.func_143031_a(ComponentScatteredFeaturePieces.JunglePyramid.class, "TeJP");
      MapGenStructureIO.func_143031_a(ComponentScatteredFeaturePieces.SwampHut.class, "TeSH");
   }

   public static class SwampHut extends ComponentScatteredFeaturePieces.Feature {
      private boolean field_82682_h;

      public SwampHut() {
         super();
      }

      public SwampHut(Random var1, int var2, int var3) {
         super(var1, var2, 64, var3, 7, 7, 9);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Witch", this.field_82682_h);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_82682_h = var1.func_74767_n("Witch");
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (!this.func_74935_a(var1, var3, 0)) {
            return false;
         } else {
            this.func_175804_a(var1, var3, 1, 1, 1, 5, 1, 7, Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), false);
            this.func_175804_a(var1, var3, 1, 4, 2, 5, 4, 7, Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), false);
            this.func_175804_a(var1, var3, 2, 1, 0, 4, 1, 0, Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), false);
            this.func_175804_a(var1, var3, 2, 2, 2, 3, 3, 2, Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), false);
            this.func_175804_a(var1, var3, 1, 2, 3, 1, 3, 6, Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), false);
            this.func_175804_a(var1, var3, 5, 2, 3, 5, 3, 6, Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), false);
            this.func_175804_a(var1, var3, 2, 2, 7, 4, 3, 7, Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), Blocks.field_150344_f.func_176203_a(BlockPlanks.EnumType.SPRUCE.func_176839_a()), false);
            this.func_175804_a(var1, var3, 1, 0, 2, 1, 3, 2, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
            this.func_175804_a(var1, var3, 5, 0, 2, 5, 3, 2, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
            this.func_175804_a(var1, var3, 1, 0, 7, 1, 3, 7, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
            this.func_175804_a(var1, var3, 5, 0, 7, 5, 3, 7, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 2, 3, 2, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 3, 3, 7, var3);
            this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 1, 3, 4, var3);
            this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 5, 3, 4, var3);
            this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 5, 3, 5, var3);
            this.func_175811_a(var1, Blocks.field_150457_bL.func_176223_P().func_177226_a(BlockFlowerPot.field_176443_b, BlockFlowerPot.EnumFlowerType.MUSHROOM_RED), 1, 3, 5, var3);
            this.func_175811_a(var1, Blocks.field_150462_ai.func_176223_P(), 3, 2, 6, var3);
            this.func_175811_a(var1, Blocks.field_150383_bp.func_176223_P(), 4, 2, 6, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 1, 2, 1, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 5, 2, 1, var3);
            int var4 = this.func_151555_a(Blocks.field_150476_ad, 3);
            int var5 = this.func_151555_a(Blocks.field_150476_ad, 1);
            int var6 = this.func_151555_a(Blocks.field_150476_ad, 0);
            int var7 = this.func_151555_a(Blocks.field_150476_ad, 2);
            this.func_175804_a(var1, var3, 0, 4, 1, 6, 4, 1, Blocks.field_150485_bF.func_176203_a(var4), Blocks.field_150485_bF.func_176203_a(var4), false);
            this.func_175804_a(var1, var3, 0, 4, 2, 0, 4, 7, Blocks.field_150485_bF.func_176203_a(var6), Blocks.field_150485_bF.func_176203_a(var6), false);
            this.func_175804_a(var1, var3, 6, 4, 2, 6, 4, 7, Blocks.field_150485_bF.func_176203_a(var5), Blocks.field_150485_bF.func_176203_a(var5), false);
            this.func_175804_a(var1, var3, 0, 4, 8, 6, 4, 8, Blocks.field_150485_bF.func_176203_a(var7), Blocks.field_150485_bF.func_176203_a(var7), false);

            int var8;
            int var9;
            for(var8 = 2; var8 <= 7; var8 += 5) {
               for(var9 = 1; var9 <= 5; var9 += 4) {
                  this.func_175808_b(var1, Blocks.field_150364_r.func_176223_P(), var9, -1, var8, var3);
               }
            }

            if (!this.field_82682_h) {
               var8 = this.func_74865_a(2, 5);
               var9 = this.func_74862_a(2);
               int var10 = this.func_74873_b(2, 5);
               if (var3.func_175898_b(new BlockPos(var8, var9, var10))) {
                  this.field_82682_h = true;
                  EntityWitch var11 = new EntityWitch(var1);
                  var11.func_70012_b((double)var8 + 0.5D, (double)var9, (double)var10 + 0.5D, 0.0F, 0.0F);
                  var11.func_180482_a(var1.func_175649_E(new BlockPos(var8, var9, var10)), (IEntityLivingData)null);
                  var1.func_72838_d(var11);
               }
            }

            return true;
         }
      }
   }

   public static class JunglePyramid extends ComponentScatteredFeaturePieces.Feature {
      private boolean field_74947_h;
      private boolean field_74948_i;
      private boolean field_74945_j;
      private boolean field_74946_k;
      private static final List<WeightedRandomChestContent> field_175816_i;
      private static final List<WeightedRandomChestContent> field_175815_j;
      private static ComponentScatteredFeaturePieces.JunglePyramid.Stones field_74942_n;

      public JunglePyramid() {
         super();
      }

      public JunglePyramid(Random var1, int var2, int var3) {
         super(var1, var2, 64, var3, 12, 10, 15);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("placedMainChest", this.field_74947_h);
         var1.func_74757_a("placedHiddenChest", this.field_74948_i);
         var1.func_74757_a("placedTrap1", this.field_74945_j);
         var1.func_74757_a("placedTrap2", this.field_74946_k);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_74947_h = var1.func_74767_n("placedMainChest");
         this.field_74948_i = var1.func_74767_n("placedHiddenChest");
         this.field_74945_j = var1.func_74767_n("placedTrap1");
         this.field_74946_k = var1.func_74767_n("placedTrap2");
      }

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
            this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 1, 5, 5, var3);
            this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 10, 5, 5, var3);
            this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 1, 5, 9, var3);
            this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 10, 5, 9, var3);

            int var8;
            for(var8 = 0; var8 <= 14; var8 += 14) {
               this.func_74882_a(var1, var3, 2, 4, var8, 2, 5, var8, false, var2, field_74942_n);
               this.func_74882_a(var1, var3, 4, 4, var8, 4, 5, var8, false, var2, field_74942_n);
               this.func_74882_a(var1, var3, 7, 4, var8, 7, 5, var8, false, var2, field_74942_n);
               this.func_74882_a(var1, var3, 9, 4, var8, 9, 5, var8, false, var2, field_74942_n);
            }

            this.func_74882_a(var1, var3, 5, 6, 0, 6, 6, 0, false, var2, field_74942_n);

            for(var8 = 0; var8 <= 11; var8 += 11) {
               for(int var9 = 2; var9 <= 12; var9 += 2) {
                  this.func_74882_a(var1, var3, var8, 4, var9, var8, 5, var9, false, var2, field_74942_n);
               }

               this.func_74882_a(var1, var3, var8, 6, 5, var8, 6, 5, false, var2, field_74942_n);
               this.func_74882_a(var1, var3, var8, 6, 9, var8, 6, 9, false, var2, field_74942_n);
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
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 5, 9, 6, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 6, 9, 6, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var5), 5, 9, 8, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var5), 6, 9, 8, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 4, 0, 0, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 5, 0, 0, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 6, 0, 0, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 7, 0, 0, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 4, 1, 8, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 4, 2, 9, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 4, 3, 10, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 7, 1, 8, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 7, 2, 9, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 7, 3, 10, var3);
            this.func_74882_a(var1, var3, 4, 1, 9, 4, 1, 9, false, var2, field_74942_n);
            this.func_74882_a(var1, var3, 7, 1, 9, 7, 1, 9, false, var2, field_74942_n);
            this.func_74882_a(var1, var3, 4, 1, 10, 7, 2, 10, false, var2, field_74942_n);
            this.func_74882_a(var1, var3, 5, 4, 5, 6, 4, 5, false, var2, field_74942_n);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var6), 4, 4, 5, var3);
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var7), 7, 4, 5, var3);

            for(var8 = 0; var8 < 4; ++var8) {
               this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var5), 5, 0 - var8, 6 + var8, var3);
               this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var5), 6, 0 - var8, 6 + var8, var3);
               this.func_74878_a(var1, var3, 5, 0 - var8, 7 + var8, 6, 0 - var8, 9 + var8);
            }

            this.func_74878_a(var1, var3, 1, -3, 12, 10, -1, 13);
            this.func_74878_a(var1, var3, 1, -3, 1, 3, -1, 13);
            this.func_74878_a(var1, var3, 1, -3, 1, 9, -1, 5);

            for(var8 = 1; var8 <= 13; var8 += 2) {
               this.func_74882_a(var1, var3, 1, -3, var8, 1, -2, var8, false, var2, field_74942_n);
            }

            for(var8 = 2; var8 <= 12; var8 += 2) {
               this.func_74882_a(var1, var3, 1, -1, var8, 3, -1, var8, false, var2, field_74942_n);
            }

            this.func_74882_a(var1, var3, 2, -2, 1, 5, -2, 1, false, var2, field_74942_n);
            this.func_74882_a(var1, var3, 7, -2, 1, 9, -2, 1, false, var2, field_74942_n);
            this.func_74882_a(var1, var3, 6, -3, 1, 6, -3, 1, false, var2, field_74942_n);
            this.func_74882_a(var1, var3, 6, -1, 1, 6, -1, 1, false, var2, field_74942_n);
            this.func_175811_a(var1, Blocks.field_150479_bC.func_176203_a(this.func_151555_a(Blocks.field_150479_bC, EnumFacing.EAST.func_176736_b())).func_177226_a(BlockTripWireHook.field_176265_M, true), 1, -3, 8, var3);
            this.func_175811_a(var1, Blocks.field_150479_bC.func_176203_a(this.func_151555_a(Blocks.field_150479_bC, EnumFacing.WEST.func_176736_b())).func_177226_a(BlockTripWireHook.field_176265_M, true), 4, -3, 8, var3);
            this.func_175811_a(var1, Blocks.field_150473_bD.func_176223_P().func_177226_a(BlockTripWire.field_176294_M, true), 2, -3, 8, var3);
            this.func_175811_a(var1, Blocks.field_150473_bD.func_176223_P().func_177226_a(BlockTripWire.field_176294_M, true), 3, -3, 8, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 5, -3, 7, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 5, -3, 6, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 5, -3, 5, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 5, -3, 4, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 5, -3, 3, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 5, -3, 2, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 5, -3, 1, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 4, -3, 1, var3);
            this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 3, -3, 1, var3);
            if (!this.field_74945_j) {
               this.field_74945_j = this.func_175806_a(var1, var3, var2, 3, -2, 1, EnumFacing.NORTH.func_176745_a(), field_175815_j, 2);
            }

            this.func_175811_a(var1, Blocks.field_150395_bd.func_176203_a(15), 3, -2, 2, var3);
            this.func_175811_a(var1, Blocks.field_150479_bC.func_176203_a(this.func_151555_a(Blocks.field_150479_bC, EnumFacing.NORTH.func_176736_b())).func_177226_a(BlockTripWireHook.field_176265_M, true), 7, -3, 1, var3);
            this.func_175811_a(var1, Blocks.field_150479_bC.func_176203_a(this.func_151555_a(Blocks.field_150479_bC, EnumFacing.SOUTH.func_176736_b())).func_177226_a(BlockTripWireHook.field_176265_M, true), 7, -3, 5, var3);
            this.func_175811_a(var1, Blocks.field_150473_bD.func_176223_P().func_177226_a(BlockTripWire.field_176294_M, true), 7, -3, 2, var3);
            this.func_175811_a(var1, Blocks.field_150473_bD.func_176223_P().func_177226_a(BlockTripWire.field_176294_M, true), 7, -3, 3, var3);
            this.func_175811_a(var1, Blocks.field_150473_bD.func_176223_P().func_177226_a(BlockTripWire.field_176294_M, true), 7, -3, 4, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 8, -3, 6, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 9, -3, 6, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 9, -3, 5, var3);
            this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 9, -3, 4, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 9, -2, 4, var3);
            if (!this.field_74946_k) {
               this.field_74946_k = this.func_175806_a(var1, var3, var2, 9, -2, 3, EnumFacing.WEST.func_176745_a(), field_175815_j, 2);
            }

            this.func_175811_a(var1, Blocks.field_150395_bd.func_176203_a(15), 8, -1, 3, var3);
            this.func_175811_a(var1, Blocks.field_150395_bd.func_176203_a(15), 8, -2, 3, var3);
            if (!this.field_74947_h) {
               this.field_74947_h = this.func_180778_a(var1, var3, var2, 8, -3, 3, WeightedRandomChestContent.func_177629_a(field_175816_i, Items.field_151134_bR.func_92114_b(var2)), 2 + var2.nextInt(5));
            }

            this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 9, -3, 2, var3);
            this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 8, -3, 1, var3);
            this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 4, -3, 5, var3);
            this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 5, -2, 5, var3);
            this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 5, -1, 5, var3);
            this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 6, -3, 5, var3);
            this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 7, -2, 5, var3);
            this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 7, -1, 5, var3);
            this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 8, -3, 5, var3);
            this.func_74882_a(var1, var3, 9, -1, 1, 9, -1, 5, false, var2, field_74942_n);
            this.func_74878_a(var1, var3, 8, -3, 8, 10, -1, 10);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176203_a(BlockStoneBrick.field_176252_O), 8, -2, 11, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176203_a(BlockStoneBrick.field_176252_O), 9, -2, 11, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176203_a(BlockStoneBrick.field_176252_O), 10, -2, 11, var3);
            this.func_175811_a(var1, Blocks.field_150442_at.func_176203_a(BlockLever.func_176357_a(EnumFacing.func_82600_a(this.func_151555_a(Blocks.field_150442_at, EnumFacing.NORTH.func_176745_a())))), 8, -2, 12, var3);
            this.func_175811_a(var1, Blocks.field_150442_at.func_176203_a(BlockLever.func_176357_a(EnumFacing.func_82600_a(this.func_151555_a(Blocks.field_150442_at, EnumFacing.NORTH.func_176745_a())))), 9, -2, 12, var3);
            this.func_175811_a(var1, Blocks.field_150442_at.func_176203_a(BlockLever.func_176357_a(EnumFacing.func_82600_a(this.func_151555_a(Blocks.field_150442_at, EnumFacing.NORTH.func_176745_a())))), 10, -2, 12, var3);
            this.func_74882_a(var1, var3, 8, -3, 8, 8, -3, 10, false, var2, field_74942_n);
            this.func_74882_a(var1, var3, 10, -3, 8, 10, -3, 10, false, var2, field_74942_n);
            this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 10, -2, 9, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 8, -2, 9, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 8, -2, 10, var3);
            this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 10, -1, 9, var3);
            this.func_175811_a(var1, Blocks.field_150320_F.func_176203_a(EnumFacing.UP.func_176745_a()), 9, -2, 8, var3);
            this.func_175811_a(var1, Blocks.field_150320_F.func_176203_a(this.func_151555_a(Blocks.field_150320_F, EnumFacing.WEST.func_176745_a())), 10, -2, 8, var3);
            this.func_175811_a(var1, Blocks.field_150320_F.func_176203_a(this.func_151555_a(Blocks.field_150320_F, EnumFacing.WEST.func_176745_a())), 10, -1, 8, var3);
            this.func_175811_a(var1, Blocks.field_150413_aR.func_176203_a(this.func_151555_a(Blocks.field_150413_aR, EnumFacing.NORTH.func_176736_b())), 10, -2, 10, var3);
            if (!this.field_74948_i) {
               this.field_74948_i = this.func_180778_a(var1, var3, var2, 9, -3, 10, WeightedRandomChestContent.func_177629_a(field_175816_i, Items.field_151134_bR.func_92114_b(var2)), 2 + var2.nextInt(5));
            }

            return true;
         }
      }

      static {
         field_175816_i = Lists.newArrayList(new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 3, 3), new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10), new WeightedRandomChestContent(Items.field_151043_k, 0, 2, 7, 15), new WeightedRandomChestContent(Items.field_151166_bC, 0, 1, 3, 2), new WeightedRandomChestContent(Items.field_151103_aS, 0, 4, 6, 20), new WeightedRandomChestContent(Items.field_151078_bh, 0, 3, 7, 16), new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 3), new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 1)});
         field_175815_j = Lists.newArrayList(new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151032_g, 0, 2, 7, 30)});
         field_74942_n = new ComponentScatteredFeaturePieces.JunglePyramid.Stones();
      }

      static class Stones extends StructureComponent.BlockSelector {
         private Stones() {
            super();
         }

         public void func_75062_a(Random var1, int var2, int var3, int var4, boolean var5) {
            if (var1.nextFloat() < 0.4F) {
               this.field_151562_a = Blocks.field_150347_e.func_176223_P();
            } else {
               this.field_151562_a = Blocks.field_150341_Y.func_176223_P();
            }

         }

         // $FF: synthetic method
         Stones(Object var1) {
            this();
         }
      }
   }

   public static class DesertPyramid extends ComponentScatteredFeaturePieces.Feature {
      private boolean[] field_74940_h = new boolean[4];
      private static final List<WeightedRandomChestContent> field_74941_i;

      public DesertPyramid() {
         super();
      }

      public DesertPyramid(Random var1, int var2, int var3) {
         super(var1, var2, 64, var3, 21, 15, 21);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("hasPlacedChest0", this.field_74940_h[0]);
         var1.func_74757_a("hasPlacedChest1", this.field_74940_h[1]);
         var1.func_74757_a("hasPlacedChest2", this.field_74940_h[2]);
         var1.func_74757_a("hasPlacedChest3", this.field_74940_h[3]);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_74940_h[0] = var1.func_74767_n("hasPlacedChest0");
         this.field_74940_h[1] = var1.func_74767_n("hasPlacedChest1");
         this.field_74940_h[2] = var1.func_74767_n("hasPlacedChest2");
         this.field_74940_h[3] = var1.func_74767_n("hasPlacedChest3");
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_175804_a(var1, var3, 0, -4, 0, this.field_74939_a - 1, 0, this.field_74938_c - 1, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);

         int var4;
         for(var4 = 1; var4 <= 9; ++var4) {
            this.func_175804_a(var1, var3, var4, var4, var4, this.field_74939_a - 1 - var4, var4, this.field_74938_c - 1 - var4, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
            this.func_175804_a(var1, var3, var4 + 1, var4, var4 + 1, this.field_74939_a - 2 - var4, var4, this.field_74938_c - 2 - var4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         }

         int var5;
         for(var4 = 0; var4 < this.field_74939_a; ++var4) {
            for(var5 = 0; var5 < this.field_74938_c; ++var5) {
               byte var6 = -5;
               this.func_175808_b(var1, Blocks.field_150322_A.func_176223_P(), var4, var6, var5, var3);
            }
         }

         var4 = this.func_151555_a(Blocks.field_150372_bz, 3);
         var5 = this.func_151555_a(Blocks.field_150372_bz, 2);
         int var14 = this.func_151555_a(Blocks.field_150372_bz, 0);
         int var7 = this.func_151555_a(Blocks.field_150372_bz, 1);
         int var8 = ~EnumDyeColor.ORANGE.func_176767_b() & 15;
         int var9 = ~EnumDyeColor.BLUE.func_176767_b() & 15;
         this.func_175804_a(var1, var3, 0, 0, 0, 4, 9, 4, Blocks.field_150322_A.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 10, 1, 3, 10, 3, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var4), 2, 10, 0, var3);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var5), 2, 10, 4, var3);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var14), 0, 10, 2, var3);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var7), 4, 10, 2, var3);
         this.func_175804_a(var1, var3, this.field_74939_a - 5, 0, 0, this.field_74939_a - 1, 9, 4, Blocks.field_150322_A.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, this.field_74939_a - 4, 10, 1, this.field_74939_a - 2, 10, 3, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var4), this.field_74939_a - 3, 10, 0, var3);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var5), this.field_74939_a - 3, 10, 4, var3);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var14), this.field_74939_a - 5, 10, 2, var3);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var7), this.field_74939_a - 1, 10, 2, var3);
         this.func_175804_a(var1, var3, 8, 0, 0, 12, 4, 4, Blocks.field_150322_A.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 1, 0, 11, 3, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), 9, 1, 1, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), 9, 2, 1, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), 9, 3, 1, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), 10, 3, 1, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), 11, 3, 1, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), 11, 2, 1, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), 11, 1, 1, var3);
         this.func_175804_a(var1, var3, 4, 1, 1, 8, 3, 3, Blocks.field_150322_A.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 1, 2, 8, 2, 2, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 12, 1, 1, 16, 3, 3, Blocks.field_150322_A.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 12, 1, 2, 16, 2, 2, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 4, 5, this.field_74939_a - 6, 4, this.field_74938_c - 6, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 4, 9, 11, 4, 11, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 1, 8, 8, 3, 8, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), false);
         this.func_175804_a(var1, var3, 12, 1, 8, 12, 3, 8, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), false);
         this.func_175804_a(var1, var3, 8, 1, 12, 8, 3, 12, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), false);
         this.func_175804_a(var1, var3, 12, 1, 12, 12, 3, 12, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), false);
         this.func_175804_a(var1, var3, 1, 1, 5, 4, 4, 11, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
         this.func_175804_a(var1, var3, this.field_74939_a - 5, 1, 5, this.field_74939_a - 2, 4, 11, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 7, 9, 6, 7, 11, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
         this.func_175804_a(var1, var3, this.field_74939_a - 7, 7, 9, this.field_74939_a - 7, 7, 11, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 5, 9, 5, 7, 11, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), false);
         this.func_175804_a(var1, var3, this.field_74939_a - 6, 5, 9, this.field_74939_a - 6, 7, 11, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), false);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 5, 5, 10, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 5, 6, 10, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 6, 6, 10, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), this.field_74939_a - 6, 5, 10, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), this.field_74939_a - 6, 6, 10, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), this.field_74939_a - 7, 6, 10, var3);
         this.func_175804_a(var1, var3, 2, 4, 4, 2, 6, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, this.field_74939_a - 3, 4, 4, this.field_74939_a - 3, 6, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var4), 2, 4, 5, var3);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var4), 2, 3, 4, var3);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var4), this.field_74939_a - 3, 4, 5, var3);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var4), this.field_74939_a - 3, 3, 4, var3);
         this.func_175804_a(var1, var3, 1, 1, 3, 2, 2, 3, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
         this.func_175804_a(var1, var3, this.field_74939_a - 3, 1, 3, this.field_74939_a - 2, 2, 3, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176223_P(), 1, 1, 2, var3);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176223_P(), this.field_74939_a - 2, 1, 2, var3);
         this.func_175811_a(var1, Blocks.field_150333_U.func_176203_a(BlockStoneSlab.EnumType.SAND.func_176624_a()), 1, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150333_U.func_176203_a(BlockStoneSlab.EnumType.SAND.func_176624_a()), this.field_74939_a - 2, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var7), 2, 1, 2, var3);
         this.func_175811_a(var1, Blocks.field_150372_bz.func_176203_a(var14), this.field_74939_a - 3, 1, 2, var3);
         this.func_175804_a(var1, var3, 4, 3, 5, 4, 3, 18, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
         this.func_175804_a(var1, var3, this.field_74939_a - 5, 3, 5, this.field_74939_a - 5, 3, 17, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 1, 5, 4, 2, 16, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, this.field_74939_a - 6, 1, 5, this.field_74939_a - 5, 2, 16, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);

         int var10;
         for(var10 = 5; var10 <= 17; var10 += 2) {
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), 4, 1, var10, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.CHISELED.func_176675_a()), 4, 2, var10, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), this.field_74939_a - 5, 1, var10, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.CHISELED.func_176675_a()), this.field_74939_a - 5, 2, var10, var3);
         }

         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 10, 0, 7, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 10, 0, 8, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 9, 0, 9, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 11, 0, 9, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 8, 0, 10, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 12, 0, 10, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 7, 0, 10, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 13, 0, 10, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 9, 0, 11, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 11, 0, 11, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 10, 0, 12, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 10, 0, 13, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var9), 10, 0, 10, var3);

         for(var10 = 0; var10 <= this.field_74939_a - 1; var10 += this.field_74939_a - 1) {
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10, 2, 1, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 2, 2, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10, 2, 3, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10, 3, 1, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 3, 2, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10, 3, 3, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 4, 1, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.CHISELED.func_176675_a()), var10, 4, 2, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 4, 3, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10, 5, 1, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 5, 2, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10, 5, 3, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 6, 1, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.CHISELED.func_176675_a()), var10, 6, 2, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 6, 3, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 7, 1, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 7, 2, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 7, 3, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10, 8, 1, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10, 8, 2, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10, 8, 3, var3);
         }

         for(var10 = 2; var10 <= this.field_74939_a - 3; var10 += this.field_74939_a - 3 - 2) {
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10 - 1, 2, 0, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 2, 0, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10 + 1, 2, 0, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10 - 1, 3, 0, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 3, 0, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10 + 1, 3, 0, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10 - 1, 4, 0, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.CHISELED.func_176675_a()), var10, 4, 0, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10 + 1, 4, 0, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10 - 1, 5, 0, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 5, 0, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10 + 1, 5, 0, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10 - 1, 6, 0, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.CHISELED.func_176675_a()), var10, 6, 0, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10 + 1, 6, 0, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10 - 1, 7, 0, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10, 7, 0, var3);
            this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), var10 + 1, 7, 0, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10 - 1, 8, 0, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10, 8, 0, var3);
            this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), var10 + 1, 8, 0, var3);
         }

         this.func_175804_a(var1, var3, 8, 4, 0, 12, 6, 0, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), false);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 8, 6, 0, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 12, 6, 0, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 9, 5, 0, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.CHISELED.func_176675_a()), 10, 5, 0, var3);
         this.func_175811_a(var1, Blocks.field_150406_ce.func_176203_a(var8), 11, 5, 0, var3);
         this.func_175804_a(var1, var3, 8, -14, 8, 12, -11, 12, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), false);
         this.func_175804_a(var1, var3, 8, -10, 8, 12, -10, 12, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.CHISELED.func_176675_a()), Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.CHISELED.func_176675_a()), false);
         this.func_175804_a(var1, var3, 8, -9, 8, 12, -9, 12, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), false);
         this.func_175804_a(var1, var3, 8, -8, 8, 12, -1, 12, Blocks.field_150322_A.func_176223_P(), Blocks.field_150322_A.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, -11, 9, 11, -1, 11, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150456_au.func_176223_P(), 10, -11, 10, var3);
         this.func_175804_a(var1, var3, 9, -13, 9, 11, -13, 11, Blocks.field_150335_W.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 8, -11, 10, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 8, -10, 10, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.CHISELED.func_176675_a()), 7, -10, 10, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), 7, -11, 10, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 12, -11, 10, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 12, -10, 10, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.CHISELED.func_176675_a()), 13, -10, 10, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), 13, -11, 10, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 10, -11, 8, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 10, -10, 8, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.CHISELED.func_176675_a()), 10, -10, 7, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), 10, -11, 7, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 10, -11, 12, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 10, -10, 12, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.CHISELED.func_176675_a()), 10, -10, 13, var3);
         this.func_175811_a(var1, Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a()), 10, -11, 13, var3);
         Iterator var15 = EnumFacing.Plane.HORIZONTAL.iterator();

         while(var15.hasNext()) {
            EnumFacing var11 = (EnumFacing)var15.next();
            if (!this.field_74940_h[var11.func_176736_b()]) {
               int var12 = var11.func_82601_c() * 2;
               int var13 = var11.func_82599_e() * 2;
               this.field_74940_h[var11.func_176736_b()] = this.func_180778_a(var1, var3, var2, 10 + var12, -11, 10 + var13, WeightedRandomChestContent.func_177629_a(field_74941_i, Items.field_151134_bR.func_92114_b(var2)), 2 + var2.nextInt(5));
            }
         }

         return true;
      }

      static {
         field_74941_i = Lists.newArrayList(new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 3, 3), new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10), new WeightedRandomChestContent(Items.field_151043_k, 0, 2, 7, 15), new WeightedRandomChestContent(Items.field_151166_bC, 0, 1, 3, 2), new WeightedRandomChestContent(Items.field_151103_aS, 0, 4, 6, 20), new WeightedRandomChestContent(Items.field_151078_bh, 0, 3, 7, 16), new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 3), new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 1)});
      }
   }

   abstract static class Feature extends StructureComponent {
      protected int field_74939_a;
      protected int field_74937_b;
      protected int field_74938_c;
      protected int field_74936_d = -1;

      public Feature() {
         super();
      }

      protected Feature(Random var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         super(0);
         this.field_74939_a = var5;
         this.field_74937_b = var6;
         this.field_74938_c = var7;
         this.field_74885_f = EnumFacing.Plane.HORIZONTAL.func_179518_a(var1);
         switch(this.field_74885_f) {
         case NORTH:
         case SOUTH:
            this.field_74887_e = new StructureBoundingBox(var2, var3, var4, var2 + var5 - 1, var3 + var6 - 1, var4 + var7 - 1);
            break;
         default:
            this.field_74887_e = new StructureBoundingBox(var2, var3, var4, var2 + var7 - 1, var3 + var6 - 1, var4 + var5 - 1);
         }

      }

      protected void func_143012_a(NBTTagCompound var1) {
         var1.func_74768_a("Width", this.field_74939_a);
         var1.func_74768_a("Height", this.field_74937_b);
         var1.func_74768_a("Depth", this.field_74938_c);
         var1.func_74768_a("HPos", this.field_74936_d);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         this.field_74939_a = var1.func_74762_e("Width");
         this.field_74937_b = var1.func_74762_e("Height");
         this.field_74938_c = var1.func_74762_e("Depth");
         this.field_74936_d = var1.func_74762_e("HPos");
      }

      protected boolean func_74935_a(World var1, StructureBoundingBox var2, int var3) {
         if (this.field_74936_d >= 0) {
            return true;
         } else {
            int var4 = 0;
            int var5 = 0;
            BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

            for(int var7 = this.field_74887_e.field_78896_c; var7 <= this.field_74887_e.field_78892_f; ++var7) {
               for(int var8 = this.field_74887_e.field_78897_a; var8 <= this.field_74887_e.field_78893_d; ++var8) {
                  var6.func_181079_c(var8, 64, var7);
                  if (var2.func_175898_b(var6)) {
                     var4 += Math.max(var1.func_175672_r(var6).func_177956_o(), var1.field_73011_w.func_76557_i());
                     ++var5;
                  }
               }
            }

            if (var5 == 0) {
               return false;
            } else {
               this.field_74936_d = var4 / var5;
               this.field_74887_e.func_78886_a(0, this.field_74936_d - this.field_74887_e.field_78895_b + var3, 0);
               return true;
            }
         }
      }
   }
}
