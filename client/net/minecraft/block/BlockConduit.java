package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityConduit;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockConduit extends BlockContainer implements IBucketPickupHandler, ILiquidContainer {
   public static final BooleanProperty field_212563_a;
   protected static final VoxelShape field_207796_a;

   public BlockConduit(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_212563_a, true));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_212563_a);
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityConduit();
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_212563_a) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(var1);
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.func_177229_b(field_212563_a)) {
         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
      }

      return super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_207796_a;
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, @Nullable EntityLivingBase var4, ItemStack var5) {
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityBeacon) {
            ((TileEntityBeacon)var6).func_200227_a(var5.func_200301_q());
         }
      }

   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IFluidState var2 = var1.func_195991_k().func_204610_c(var1.func_195995_a());
      return (IBlockState)this.func_176223_P().func_206870_a(field_212563_a, var2.func_206884_a(FluidTags.field_206959_a) && var2.func_206882_g() == 8);
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   public Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_212563_a)) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_212563_a, false), 3);
         return Fluids.field_204546_a;
      } else {
         return Fluids.field_204541_a;
      }
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return !(Boolean)var3.func_177229_b(field_212563_a) && var4 == Fluids.field_204546_a;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      if (!(Boolean)var3.func_177229_b(field_212563_a) && var4.func_206886_c() == Fluids.field_204546_a) {
         if (!var1.func_201670_d()) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_212563_a, true), 3);
            var1.func_205219_F_().func_205360_a(var2, var4.func_206886_c(), var4.func_206886_c().func_205569_a(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   static {
      field_212563_a = BlockStateProperties.field_208198_y;
      field_207796_a = Block.func_208617_a(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);
   }
}
