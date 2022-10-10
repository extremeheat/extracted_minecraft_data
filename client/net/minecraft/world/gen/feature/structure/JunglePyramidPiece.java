package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class JunglePyramidPiece extends ScatteredStructurePiece {
   private boolean field_202586_e;
   private boolean field_202587_f;
   private boolean field_202588_g;
   private boolean field_202589_h;
   private static final JunglePyramidPiece.MossStoneSelector field_202590_i = new JunglePyramidPiece.MossStoneSelector();

   public static void func_202585_af_() {
      StructureIO.func_143031_a(JunglePyramidPiece.class, "TeJP");
   }

   public JunglePyramidPiece() {
      super();
   }

   public JunglePyramidPiece(Random var1, int var2, int var3) {
      super(var1, var2, 64, var3, 12, 10, 15);
   }

   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("placedMainChest", this.field_202586_e);
      var1.func_74757_a("placedHiddenChest", this.field_202587_f);
      var1.func_74757_a("placedTrap1", this.field_202588_g);
      var1.func_74757_a("placedTrap2", this.field_202589_h);
   }

   protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
      super.func_143011_b(var1, var2);
      this.field_202586_e = var1.func_74767_n("placedMainChest");
      this.field_202587_f = var1.func_74767_n("placedHiddenChest");
      this.field_202588_g = var1.func_74767_n("placedTrap1");
      this.field_202589_h = var1.func_74767_n("placedTrap2");
   }

   public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
      if (!this.func_202580_a(var1, var3, 0)) {
         return false;
      } else {
         this.func_74882_a(var1, var3, 0, -4, 0, this.field_202581_a - 1, 0, this.field_202583_c - 1, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 2, 1, 2, 9, 2, 2, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 2, 1, 12, 9, 2, 12, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 2, 1, 3, 2, 2, 11, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 9, 1, 3, 9, 2, 11, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 1, 3, 1, 10, 6, 1, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 1, 3, 13, 10, 6, 13, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 1, 3, 2, 1, 6, 12, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 10, 3, 2, 10, 6, 12, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 2, 3, 2, 9, 3, 12, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 2, 6, 2, 9, 6, 12, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 3, 7, 3, 8, 7, 11, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 4, 8, 4, 7, 8, 10, false, var2, field_202590_i);
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

         int var5;
         for(var5 = 0; var5 <= 14; var5 += 14) {
            this.func_74882_a(var1, var3, 2, 4, var5, 2, 5, var5, false, var2, field_202590_i);
            this.func_74882_a(var1, var3, 4, 4, var5, 4, 5, var5, false, var2, field_202590_i);
            this.func_74882_a(var1, var3, 7, 4, var5, 7, 5, var5, false, var2, field_202590_i);
            this.func_74882_a(var1, var3, 9, 4, var5, 9, 5, var5, false, var2, field_202590_i);
         }

         this.func_74882_a(var1, var3, 5, 6, 0, 6, 6, 0, false, var2, field_202590_i);

         for(var5 = 0; var5 <= 11; var5 += 11) {
            for(int var6 = 2; var6 <= 12; var6 += 2) {
               this.func_74882_a(var1, var3, var5, 4, var6, var5, 5, var6, false, var2, field_202590_i);
            }

            this.func_74882_a(var1, var3, var5, 6, 5, var5, 6, 5, false, var2, field_202590_i);
            this.func_74882_a(var1, var3, var5, 6, 9, var5, 6, 9, false, var2, field_202590_i);
         }

         this.func_74882_a(var1, var3, 2, 7, 2, 2, 9, 2, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 9, 7, 2, 9, 9, 2, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 2, 7, 12, 2, 9, 12, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 9, 7, 12, 9, 9, 12, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 4, 9, 4, 4, 9, 4, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 7, 9, 4, 7, 9, 4, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 4, 9, 10, 4, 9, 10, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 7, 9, 10, 7, 9, 10, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 5, 9, 7, 6, 9, 7, false, var2, field_202590_i);
         IBlockState var11 = (IBlockState)Blocks.field_196659_cl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.EAST);
         IBlockState var12 = (IBlockState)Blocks.field_196659_cl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.WEST);
         IBlockState var7 = (IBlockState)Blocks.field_196659_cl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.SOUTH);
         IBlockState var8 = (IBlockState)Blocks.field_196659_cl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH);
         this.func_175811_a(var1, var8, 5, 9, 6, var3);
         this.func_175811_a(var1, var8, 6, 9, 6, var3);
         this.func_175811_a(var1, var7, 5, 9, 8, var3);
         this.func_175811_a(var1, var7, 6, 9, 8, var3);
         this.func_175811_a(var1, var8, 4, 0, 0, var3);
         this.func_175811_a(var1, var8, 5, 0, 0, var3);
         this.func_175811_a(var1, var8, 6, 0, 0, var3);
         this.func_175811_a(var1, var8, 7, 0, 0, var3);
         this.func_175811_a(var1, var8, 4, 1, 8, var3);
         this.func_175811_a(var1, var8, 4, 2, 9, var3);
         this.func_175811_a(var1, var8, 4, 3, 10, var3);
         this.func_175811_a(var1, var8, 7, 1, 8, var3);
         this.func_175811_a(var1, var8, 7, 2, 9, var3);
         this.func_175811_a(var1, var8, 7, 3, 10, var3);
         this.func_74882_a(var1, var3, 4, 1, 9, 4, 1, 9, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 7, 1, 9, 7, 1, 9, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 4, 1, 10, 7, 2, 10, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 5, 4, 5, 6, 4, 5, false, var2, field_202590_i);
         this.func_175811_a(var1, var11, 4, 4, 5, var3);
         this.func_175811_a(var1, var12, 7, 4, 5, var3);

         int var9;
         for(var9 = 0; var9 < 4; ++var9) {
            this.func_175811_a(var1, var7, 5, 0 - var9, 6 + var9, var3);
            this.func_175811_a(var1, var7, 6, 0 - var9, 6 + var9, var3);
            this.func_74878_a(var1, var3, 5, 0 - var9, 7 + var9, 6, 0 - var9, 9 + var9);
         }

         this.func_74878_a(var1, var3, 1, -3, 12, 10, -1, 13);
         this.func_74878_a(var1, var3, 1, -3, 1, 3, -1, 13);
         this.func_74878_a(var1, var3, 1, -3, 1, 9, -1, 5);

         for(var9 = 1; var9 <= 13; var9 += 2) {
            this.func_74882_a(var1, var3, 1, -3, var9, 1, -2, var9, false, var2, field_202590_i);
         }

         for(var9 = 2; var9 <= 12; var9 += 2) {
            this.func_74882_a(var1, var3, 1, -1, var9, 3, -1, var9, false, var2, field_202590_i);
         }

         this.func_74882_a(var1, var3, 2, -2, 1, 5, -2, 1, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 7, -2, 1, 9, -2, 1, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 6, -3, 1, 6, -3, 1, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 6, -1, 1, 6, -1, 1, false, var2, field_202590_i);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150479_bC.func_176223_P().func_206870_a(BlockTripWireHook.field_176264_a, EnumFacing.EAST)).func_206870_a(BlockTripWireHook.field_176265_M, true), 1, -3, 8, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150479_bC.func_176223_P().func_206870_a(BlockTripWireHook.field_176264_a, EnumFacing.WEST)).func_206870_a(BlockTripWireHook.field_176265_M, true), 4, -3, 8, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)((IBlockState)Blocks.field_150473_bD.func_176223_P().func_206870_a(BlockTripWire.field_176291_P, true)).func_206870_a(BlockTripWire.field_176292_R, true)).func_206870_a(BlockTripWire.field_176294_M, true), 2, -3, 8, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)((IBlockState)Blocks.field_150473_bD.func_176223_P().func_206870_a(BlockTripWire.field_176291_P, true)).func_206870_a(BlockTripWire.field_176292_R, true)).func_206870_a(BlockTripWire.field_176294_M, true), 3, -3, 8, var3);
         IBlockState var13 = (IBlockState)((IBlockState)Blocks.field_150488_af.func_176223_P().func_206870_a(BlockRedstoneWire.field_176348_a, RedstoneSide.SIDE)).func_206870_a(BlockRedstoneWire.field_176349_M, RedstoneSide.SIDE);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150488_af.func_176223_P().func_206870_a(BlockRedstoneWire.field_176349_M, RedstoneSide.SIDE), 5, -3, 7, var3);
         this.func_175811_a(var1, var13, 5, -3, 6, var3);
         this.func_175811_a(var1, var13, 5, -3, 5, var3);
         this.func_175811_a(var1, var13, 5, -3, 4, var3);
         this.func_175811_a(var1, var13, 5, -3, 3, var3);
         this.func_175811_a(var1, var13, 5, -3, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150488_af.func_176223_P().func_206870_a(BlockRedstoneWire.field_176348_a, RedstoneSide.SIDE)).func_206870_a(BlockRedstoneWire.field_176350_N, RedstoneSide.SIDE), 5, -3, 1, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150488_af.func_176223_P().func_206870_a(BlockRedstoneWire.field_176347_b, RedstoneSide.SIDE), 4, -3, 1, var3);
         this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 3, -3, 1, var3);
         if (!this.field_202588_g) {
            this.field_202588_g = this.func_189419_a(var1, var3, var2, 3, -2, 1, EnumFacing.NORTH, LootTableList.field_189420_m);
         }

         this.func_175811_a(var1, (IBlockState)Blocks.field_150395_bd.func_176223_P().func_206870_a(BlockVine.field_176279_N, true), 3, -2, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150479_bC.func_176223_P().func_206870_a(BlockTripWireHook.field_176264_a, EnumFacing.NORTH)).func_206870_a(BlockTripWireHook.field_176265_M, true), 7, -3, 1, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150479_bC.func_176223_P().func_206870_a(BlockTripWireHook.field_176264_a, EnumFacing.SOUTH)).func_206870_a(BlockTripWireHook.field_176265_M, true), 7, -3, 5, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)((IBlockState)Blocks.field_150473_bD.func_176223_P().func_206870_a(BlockTripWire.field_176296_O, true)).func_206870_a(BlockTripWire.field_176289_Q, true)).func_206870_a(BlockTripWire.field_176294_M, true), 7, -3, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)((IBlockState)Blocks.field_150473_bD.func_176223_P().func_206870_a(BlockTripWire.field_176296_O, true)).func_206870_a(BlockTripWire.field_176289_Q, true)).func_206870_a(BlockTripWire.field_176294_M, true), 7, -3, 3, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)((IBlockState)Blocks.field_150473_bD.func_176223_P().func_206870_a(BlockTripWire.field_176296_O, true)).func_206870_a(BlockTripWire.field_176289_Q, true)).func_206870_a(BlockTripWire.field_176294_M, true), 7, -3, 4, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150488_af.func_176223_P().func_206870_a(BlockRedstoneWire.field_176347_b, RedstoneSide.SIDE), 8, -3, 6, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150488_af.func_176223_P().func_206870_a(BlockRedstoneWire.field_176350_N, RedstoneSide.SIDE)).func_206870_a(BlockRedstoneWire.field_176349_M, RedstoneSide.SIDE), 9, -3, 6, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150488_af.func_176223_P().func_206870_a(BlockRedstoneWire.field_176348_a, RedstoneSide.SIDE)).func_206870_a(BlockRedstoneWire.field_176349_M, RedstoneSide.UP), 9, -3, 5, var3);
         this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 9, -3, 4, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150488_af.func_176223_P().func_206870_a(BlockRedstoneWire.field_176348_a, RedstoneSide.SIDE), 9, -2, 4, var3);
         if (!this.field_202589_h) {
            this.field_202589_h = this.func_189419_a(var1, var3, var2, 9, -2, 3, EnumFacing.WEST, LootTableList.field_189420_m);
         }

         this.func_175811_a(var1, (IBlockState)Blocks.field_150395_bd.func_176223_P().func_206870_a(BlockVine.field_176278_M, true), 8, -1, 3, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150395_bd.func_176223_P().func_206870_a(BlockVine.field_176278_M, true), 8, -2, 3, var3);
         if (!this.field_202586_e) {
            this.field_202586_e = this.func_186167_a(var1, var3, var2, 8, -3, 3, LootTableList.field_186430_l);
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
         this.func_74882_a(var1, var3, 9, -1, 1, 9, -1, 5, false, var2, field_202590_i);
         this.func_74878_a(var1, var3, 8, -3, 8, 10, -1, 10);
         this.func_175811_a(var1, Blocks.field_196702_dl.func_176223_P(), 8, -2, 11, var3);
         this.func_175811_a(var1, Blocks.field_196702_dl.func_176223_P(), 9, -2, 11, var3);
         this.func_175811_a(var1, Blocks.field_196702_dl.func_176223_P(), 10, -2, 11, var3);
         IBlockState var10 = (IBlockState)((IBlockState)Blocks.field_150442_at.func_176223_P().func_206870_a(BlockLever.field_185512_D, EnumFacing.NORTH)).func_206870_a(BlockLever.field_196366_M, AttachFace.WALL);
         this.func_175811_a(var1, var10, 8, -2, 12, var3);
         this.func_175811_a(var1, var10, 9, -2, 12, var3);
         this.func_175811_a(var1, var10, 10, -2, 12, var3);
         this.func_74882_a(var1, var3, 8, -3, 8, 8, -3, 10, false, var2, field_202590_i);
         this.func_74882_a(var1, var3, 10, -3, 8, 10, -3, 10, false, var2, field_202590_i);
         this.func_175811_a(var1, Blocks.field_150341_Y.func_176223_P(), 10, -2, 9, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150488_af.func_176223_P().func_206870_a(BlockRedstoneWire.field_176348_a, RedstoneSide.SIDE), 8, -2, 9, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150488_af.func_176223_P().func_206870_a(BlockRedstoneWire.field_176349_M, RedstoneSide.SIDE), 8, -2, 10, var3);
         this.func_175811_a(var1, Blocks.field_150488_af.func_176223_P(), 10, -1, 9, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150320_F.func_176223_P().func_206870_a(BlockPistonBase.field_176387_N, EnumFacing.UP), 9, -2, 8, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150320_F.func_176223_P().func_206870_a(BlockPistonBase.field_176387_N, EnumFacing.WEST), 10, -2, 8, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150320_F.func_176223_P().func_206870_a(BlockPistonBase.field_176387_N, EnumFacing.WEST), 10, -1, 8, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_196633_cV.func_176223_P().func_206870_a(BlockRedstoneRepeater.field_185512_D, EnumFacing.NORTH), 10, -2, 10, var3);
         if (!this.field_202587_f) {
            this.field_202587_f = this.func_186167_a(var1, var3, var2, 9, -3, 10, LootTableList.field_186430_l);
         }

         return true;
      }
   }

   static class MossStoneSelector extends StructurePiece.BlockSelector {
      private MossStoneSelector() {
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
      MossStoneSelector(Object var1) {
         this();
      }
   }
}
