package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class BlockSign extends BlockContainer implements IBucketPickupHandler, ILiquidContainer {
   public static final BooleanProperty field_204613_a;
   protected static final VoxelShape field_196340_a;

   protected BlockSign(Block.Properties var1) {
      super(var1);
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.func_177229_b(field_204613_a)) {
         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
      }

      return super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196340_a;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_190946_v(IBlockState var1) {
      return true;
   }

   public boolean func_181623_g() {
      return true;
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntitySign();
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (var2.field_72995_K) {
         return true;
      } else {
         TileEntity var10 = var2.func_175625_s(var3);
         return var10 instanceof TileEntitySign && ((TileEntitySign)var10).func_174882_b(var4);
      }
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_204613_a)) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204613_a, false), 3);
         return Fluids.field_204546_a;
      } else {
         return Fluids.field_204541_a;
      }
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_204613_a) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(var1);
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return !(Boolean)var3.func_177229_b(field_204613_a) && var4 == Fluids.field_204546_a;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      if (!(Boolean)var3.func_177229_b(field_204613_a) && var4.func_206886_c() == Fluids.field_204546_a) {
         if (!var1.func_201670_d()) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204613_a, true), 3);
            var1.func_205219_F_().func_205360_a(var2, var4.func_206886_c(), var4.func_206886_c().func_205569_a(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   static {
      field_204613_a = BlockStateProperties.field_208198_y;
      field_196340_a = Block.func_208617_a(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
   }
}
