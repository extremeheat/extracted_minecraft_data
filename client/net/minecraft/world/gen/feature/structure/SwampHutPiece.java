package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SwampHutPiece extends ScatteredStructurePiece {
   private boolean field_202596_e;

   public static void func_202595_b() {
      StructureIO.func_143031_a(SwampHutPiece.class, "TeSH");
   }

   public SwampHutPiece() {
      super();
   }

   public SwampHutPiece(Random var1, int var2, int var3) {
      super(var1, var2, 64, var3, 7, 7, 9);
   }

   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("Witch", this.field_202596_e);
   }

   protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
      super.func_143011_b(var1, var2);
      this.field_202596_e = var1.func_74767_n("Witch");
   }

   public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
      if (!this.func_202580_a(var1, var3, 0)) {
         return false;
      } else {
         this.func_175804_a(var1, var3, 1, 1, 1, 5, 1, 7, Blocks.field_196664_o.func_176223_P(), Blocks.field_196664_o.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 4, 2, 5, 4, 7, Blocks.field_196664_o.func_176223_P(), Blocks.field_196664_o.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 1, 0, 4, 1, 0, Blocks.field_196664_o.func_176223_P(), Blocks.field_196664_o.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 2, 2, 3, 3, 2, Blocks.field_196664_o.func_176223_P(), Blocks.field_196664_o.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 3, 1, 3, 6, Blocks.field_196664_o.func_176223_P(), Blocks.field_196664_o.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 2, 3, 5, 3, 6, Blocks.field_196664_o.func_176223_P(), Blocks.field_196664_o.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 2, 7, 4, 3, 7, Blocks.field_196664_o.func_176223_P(), Blocks.field_196664_o.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 2, 1, 3, 2, Blocks.field_196617_K.func_176223_P(), Blocks.field_196617_K.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 0, 2, 5, 3, 2, Blocks.field_196617_K.func_176223_P(), Blocks.field_196617_K.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 7, 1, 3, 7, Blocks.field_196617_K.func_176223_P(), Blocks.field_196617_K.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 0, 7, 5, 3, 7, Blocks.field_196617_K.func_176223_P(), Blocks.field_196617_K.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 2, 3, 2, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 3, 3, 7, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 1, 3, 4, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 5, 3, 4, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 5, 3, 5, var3);
         this.func_175811_a(var1, Blocks.field_196756_ey.func_176223_P(), 1, 3, 5, var3);
         this.func_175811_a(var1, Blocks.field_150462_ai.func_176223_P(), 3, 2, 6, var3);
         this.func_175811_a(var1, Blocks.field_150383_bp.func_176223_P(), 4, 2, 6, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 1, 2, 1, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 5, 2, 1, var3);
         IBlockState var5 = (IBlockState)Blocks.field_150485_bF.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH);
         IBlockState var6 = (IBlockState)Blocks.field_150485_bF.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.EAST);
         IBlockState var7 = (IBlockState)Blocks.field_150485_bF.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.WEST);
         IBlockState var8 = (IBlockState)Blocks.field_150485_bF.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.SOUTH);
         this.func_175804_a(var1, var3, 0, 4, 1, 6, 4, 1, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 4, 2, 0, 4, 7, var6, var6, false);
         this.func_175804_a(var1, var3, 6, 4, 2, 6, 4, 7, var7, var7, false);
         this.func_175804_a(var1, var3, 0, 4, 8, 6, 4, 8, var8, var8, false);
         this.func_175811_a(var1, (IBlockState)var5.func_206870_a(BlockStairs.field_176310_M, StairsShape.OUTER_RIGHT), 0, 4, 1, var3);
         this.func_175811_a(var1, (IBlockState)var5.func_206870_a(BlockStairs.field_176310_M, StairsShape.OUTER_LEFT), 6, 4, 1, var3);
         this.func_175811_a(var1, (IBlockState)var8.func_206870_a(BlockStairs.field_176310_M, StairsShape.OUTER_LEFT), 0, 4, 8, var3);
         this.func_175811_a(var1, (IBlockState)var8.func_206870_a(BlockStairs.field_176310_M, StairsShape.OUTER_RIGHT), 6, 4, 8, var3);

         int var9;
         int var10;
         for(var9 = 2; var9 <= 7; var9 += 5) {
            for(var10 = 1; var10 <= 5; var10 += 4) {
               this.func_175808_b(var1, Blocks.field_196617_K.func_176223_P(), var10, -1, var9, var3);
            }
         }

         if (!this.field_202596_e) {
            var9 = this.func_74865_a(2, 5);
            var10 = this.func_74862_a(2);
            int var11 = this.func_74873_b(2, 5);
            if (var3.func_175898_b(new BlockPos(var9, var10, var11))) {
               this.field_202596_e = true;
               EntityWitch var12 = new EntityWitch(var1.func_201672_e());
               var12.func_110163_bv();
               var12.func_70012_b((double)var9 + 0.5D, (double)var10, (double)var11 + 0.5D, 0.0F, 0.0F);
               var12.func_204210_a(var1.func_175649_E(new BlockPos(var9, var10, var11)), (IEntityLivingData)null, (NBTTagCompound)null);
               var1.func_72838_d(var12);
            }
         }

         return true;
      }
   }
}
