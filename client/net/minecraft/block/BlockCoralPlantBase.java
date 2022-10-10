package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockCoralPlantBase extends Block implements IBucketPickupHandler, ILiquidContainer {
   public static final BooleanProperty field_212560_b;
   private static final VoxelShape field_212559_a;

   protected BlockCoralPlantBase(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_212560_b, true));
   }

   protected void func_212558_a(IBlockState var1, IWorld var2, BlockPos var3) {
      if (!func_212557_b_(var1, var2, var3)) {
         var2.func_205220_G_().func_205360_a(var3, this, 60 + var2.func_201674_k().nextInt(40));
      }

   }

   protected static boolean func_212557_b_(IBlockState var0, IBlockReader var1, BlockPos var2) {
      if ((Boolean)var0.func_177229_b(field_212560_b)) {
         return true;
      } else {
         EnumFacing[] var3 = EnumFacing.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EnumFacing var6 = var3[var5];
            if (var1.func_204610_c(var2.func_177972_a(var6)).func_206884_a(FluidTags.field_206959_a)) {
               return true;
            }
         }

         return false;
      }
   }

   protected boolean func_149700_E() {
      return true;
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 0;
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IFluidState var2 = var1.func_195991_k().func_204610_c(var1.func_195995_a());
      return (IBlockState)this.func_176223_P().func_206870_a(field_212560_b, var2.func_206884_a(FluidTags.field_206959_a) && var2.func_206882_g() == 8);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_212559_a;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.func_177229_b(field_212560_b)) {
         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
      }

      return var2 == EnumFacing.DOWN && !this.func_196260_a(var1, var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      return var2.func_180495_p(var3.func_177977_b()).func_185896_q();
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_212560_b);
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_212560_b) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(var1);
   }

   public Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_212560_b)) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_212560_b, false), 3);
         return Fluids.field_204546_a;
      } else {
         return Fluids.field_204541_a;
      }
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return !(Boolean)var3.func_177229_b(field_212560_b) && var4 == Fluids.field_204546_a;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      if (!(Boolean)var3.func_177229_b(field_212560_b) && var4.func_206886_c() == Fluids.field_204546_a) {
         if (!var1.func_201670_d()) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_212560_b, true), 3);
            var1.func_205219_F_().func_205360_a(var2, var4.func_206886_c(), var4.func_206886_c().func_205569_a(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   static {
      field_212560_b = BlockStateProperties.field_208198_y;
      field_212559_a = Block.func_208617_a(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
   }
}
