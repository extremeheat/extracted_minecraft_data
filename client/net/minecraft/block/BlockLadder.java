package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockLadder extends Block implements IBucketPickupHandler, ILiquidContainer {
   public static final DirectionProperty field_176382_a;
   public static final BooleanProperty field_204612_b;
   protected static final VoxelShape field_185687_b;
   protected static final VoxelShape field_185688_c;
   protected static final VoxelShape field_185689_d;
   protected static final VoxelShape field_185690_e;

   protected BlockLadder(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176382_a, EnumFacing.NORTH)).func_206870_a(field_204612_b, false));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      switch((EnumFacing)var1.func_177229_b(field_176382_a)) {
      case NORTH:
         return field_185690_e;
      case SOUTH:
         return field_185689_d;
      case WEST:
         return field_185688_c;
      case EAST:
      default:
         return field_185687_b;
      }
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   private boolean func_196471_a(IBlockReader var1, BlockPos var2, EnumFacing var3) {
      IBlockState var4 = var1.func_180495_p(var2);
      boolean var5 = func_193382_c(var4.func_177230_c());
      return !var5 && var4.func_193401_d(var1, var2, var3) == BlockFaceShape.SOLID && !var4.func_185897_m();
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      EnumFacing var4 = (EnumFacing)var1.func_177229_b(field_176382_a);
      return this.func_196471_a(var2, var3.func_177972_a(var4.func_176734_d()), var4);
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (var2.func_176734_d() == var1.func_177229_b(field_176382_a) && !var1.func_196955_c(var4, var5)) {
         return Blocks.field_150350_a.func_176223_P();
      } else {
         if ((Boolean)var1.func_177229_b(field_204612_b)) {
            var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
         }

         return super.func_196271_a(var1, var2, var3, var4, var5, var6);
      }
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2;
      if (!var1.func_196012_c()) {
         var2 = var1.func_195991_k().func_180495_p(var1.func_195995_a().func_177972_a(var1.func_196000_l().func_176734_d()));
         if (var2.func_177230_c() == this && var2.func_177229_b(field_176382_a) == var1.func_196000_l()) {
            return null;
         }
      }

      var2 = this.func_176223_P();
      World var3 = var1.func_195991_k();
      BlockPos var4 = var1.func_195995_a();
      IFluidState var5 = var1.func_195991_k().func_204610_c(var1.func_195995_a());
      EnumFacing[] var6 = var1.func_196009_e();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         EnumFacing var9 = var6[var8];
         if (var9.func_176740_k().func_176722_c()) {
            var2 = (IBlockState)var2.func_206870_a(field_176382_a, var9.func_176734_d());
            if (var2.func_196955_c(var3, var4)) {
               return (IBlockState)var2.func_206870_a(field_204612_b, var5.func_206886_c() == Fluids.field_204546_a);
            }
         }
      }

      return null;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176382_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176382_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_176382_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176382_a, field_204612_b);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_204612_b)) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204612_b, false), 3);
         return Fluids.field_204546_a;
      } else {
         return Fluids.field_204541_a;
      }
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_204612_b) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(var1);
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return !(Boolean)var3.func_177229_b(field_204612_b) && var4 == Fluids.field_204546_a;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      if (!(Boolean)var3.func_177229_b(field_204612_b) && var4.func_206886_c() == Fluids.field_204546_a) {
         if (!var1.func_201670_d()) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204612_b, true), 3);
            var1.func_205219_F_().func_205360_a(var2, var4.func_206886_c(), var4.func_206886_c().func_205569_a(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   static {
      field_176382_a = BlockHorizontal.field_185512_D;
      field_204612_b = BlockStateProperties.field_208198_y;
      field_185687_b = Block.func_208617_a(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
      field_185688_c = Block.func_208617_a(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      field_185689_d = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
      field_185690_e = Block.func_208617_a(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
   }
}
