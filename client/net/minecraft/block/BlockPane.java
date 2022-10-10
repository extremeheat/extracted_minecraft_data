package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockPane extends BlockFourWay {
   protected BlockPane(Block.Properties var1) {
      super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196409_a, false)).func_206870_a(field_196411_b, false)).func_206870_a(field_196413_c, false)).func_206870_a(field_196414_y, false)).func_206870_a(field_204514_u, false));
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      IFluidState var4 = var1.func_195991_k().func_204610_c(var1.func_195995_a());
      BlockPos var5 = var3.func_177978_c();
      BlockPos var6 = var3.func_177968_d();
      BlockPos var7 = var3.func_177976_e();
      BlockPos var8 = var3.func_177974_f();
      IBlockState var9 = var2.func_180495_p(var5);
      IBlockState var10 = var2.func_180495_p(var6);
      IBlockState var11 = var2.func_180495_p(var7);
      IBlockState var12 = var2.func_180495_p(var8);
      return (IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_196409_a, this.func_196417_a(var9, var9.func_193401_d(var2, var5, EnumFacing.SOUTH)))).func_206870_a(field_196413_c, this.func_196417_a(var10, var10.func_193401_d(var2, var6, EnumFacing.NORTH)))).func_206870_a(field_196414_y, this.func_196417_a(var11, var11.func_193401_d(var2, var7, EnumFacing.EAST)))).func_206870_a(field_196411_b, this.func_196417_a(var12, var12.func_193401_d(var2, var8, EnumFacing.WEST)))).func_206870_a(field_204514_u, var4.func_206886_c() == Fluids.field_204546_a);
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.func_177229_b(field_204514_u)) {
         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
      }

      return var2.func_176740_k().func_176722_c() ? (IBlockState)var1.func_206870_a((IProperty)field_196415_z.get(var2), this.func_196417_a(var3, var3.func_193401_d(var4, var6, var2.func_176734_d()))) : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_200122_a(IBlockState var1, IBlockState var2, EnumFacing var3) {
      if (var2.func_177230_c() == this) {
         if (!var3.func_176740_k().func_176722_c()) {
            return true;
         }

         if ((Boolean)var1.func_177229_b((IProperty)field_196415_z.get(var3)) && (Boolean)var2.func_177229_b((IProperty)field_196415_z.get(var3.func_176734_d()))) {
            return true;
         }
      }

      return super.func_200122_a(var1, var2, var3);
   }

   public final boolean func_196417_a(IBlockState var1, BlockFaceShape var2) {
      Block var3 = var1.func_177230_c();
      return !func_196418_h(var3) && var2 == BlockFaceShape.SOLID || var2 == BlockFaceShape.MIDDLE_POLE_THIN;
   }

   public static boolean func_196418_h(Block var0) {
      return var0 instanceof BlockShulkerBox || var0 instanceof BlockLeaves || var0 == Blocks.field_150461_bJ || var0 == Blocks.field_150383_bp || var0 == Blocks.field_150426_aN || var0 == Blocks.field_150432_aD || var0 == Blocks.field_180398_cJ || var0 == Blocks.field_150331_J || var0 == Blocks.field_150320_F || var0 == Blocks.field_150332_K || var0 == Blocks.field_150440_ba || var0 == Blocks.field_150423_aK || var0 == Blocks.field_196625_cS || var0 == Blocks.field_196628_cT || var0 == Blocks.field_180401_cv;
   }

   protected boolean func_149700_E() {
      return true;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196409_a, field_196411_b, field_196414_y, field_196413_c, field_204514_u);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return var4 != EnumFacing.UP && var4 != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THIN : BlockFaceShape.CENTER_SMALL;
   }
}
