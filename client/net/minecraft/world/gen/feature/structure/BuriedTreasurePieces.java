package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class BuriedTreasurePieces {
   public static void func_204296_a() {
      StructureIO.func_143031_a(BuriedTreasurePieces.Piece.class, "BTP");
   }

   public static class Piece extends StructurePiece {
      public Piece() {
         super();
      }

      public Piece(BlockPos var1) {
         super(0);
         this.field_74887_e = new MutableBoundingBox(var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p(), var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p());
      }

      protected void func_143012_a(NBTTagCompound var1) {
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         int var5 = var1.func_201676_a(Heightmap.Type.OCEAN_FLOOR_WG, this.field_74887_e.field_78897_a, this.field_74887_e.field_78896_c);
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos(this.field_74887_e.field_78897_a, var5, this.field_74887_e.field_78896_c);

         while(var6.func_177956_o() > 0) {
            IBlockState var7 = var1.func_180495_p(var6);
            IBlockState var8 = var1.func_180495_p(var6.func_177977_b());
            if (var8 == Blocks.field_150322_A.func_176223_P() || var8 == Blocks.field_150348_b.func_176223_P() || var8 == Blocks.field_196656_g.func_176223_P() || var8 == Blocks.field_196650_c.func_176223_P() || var8 == Blocks.field_196654_e.func_176223_P()) {
               IBlockState var9 = !var7.func_196958_f() && !this.func_204295_a(var7) ? var7 : Blocks.field_150354_m.func_176223_P();
               EnumFacing[] var10 = EnumFacing.values();
               int var11 = var10.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  EnumFacing var13 = var10[var12];
                  BlockPos var14 = var6.func_177972_a(var13);
                  IBlockState var15 = var1.func_180495_p(var14);
                  if (var15.func_196958_f() || this.func_204295_a(var15)) {
                     BlockPos var16 = var14.func_177977_b();
                     IBlockState var17 = var1.func_180495_p(var16);
                     if ((var17.func_196958_f() || this.func_204295_a(var17)) && var13 != EnumFacing.UP) {
                        var1.func_180501_a(var14, var8, 3);
                     } else {
                        var1.func_180501_a(var14, var9, 3);
                     }
                  }
               }

               return this.func_191080_a(var1, var3, var2, new BlockPos(this.field_74887_e.field_78897_a, var6.func_177956_o(), this.field_74887_e.field_78896_c), LootTableList.field_204312_r, (IBlockState)null);
            }

            var6.func_196234_d(0, -1, 0);
         }

         return false;
      }

      private boolean func_204295_a(IBlockState var1) {
         return var1 == Blocks.field_150355_j.func_176223_P() || var1 == Blocks.field_150353_l.func_176223_P();
      }
   }
}
