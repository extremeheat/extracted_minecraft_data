package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Particles;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockEnderChest extends BlockContainer implements IBucketPickupHandler, ILiquidContainer {
   public static final DirectionProperty field_176437_a;
   public static final BooleanProperty field_204615_b;
   protected static final VoxelShape field_196324_b;

   protected BlockEnderChest(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176437_a, EnumFacing.NORTH)).func_206870_a(field_204615_b, false));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196324_b;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_190946_v(IBlockState var1) {
      return true;
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Blocks.field_150343_Z;
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 8;
   }

   protected boolean func_149700_E() {
      return true;
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IFluidState var2 = var1.func_195991_k().func_204610_c(var1.func_195995_a());
      return (IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_176437_a, var1.func_195992_f().func_176734_d())).func_206870_a(field_204615_b, var2.func_206886_c() == Fluids.field_204546_a);
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      InventoryEnderChest var10 = var4.func_71005_bN();
      TileEntity var11 = var2.func_175625_s(var3);
      if (var10 != null && var11 instanceof TileEntityEnderChest) {
         if (var2.func_180495_p(var3.func_177984_a()).func_185915_l()) {
            return true;
         } else if (var2.field_72995_K) {
            return true;
         } else {
            var10.func_146031_a((TileEntityEnderChest)var11);
            var4.func_71007_a(var10);
            var4.func_195066_a(StatList.field_188090_X);
            return true;
         }
      } else {
         return true;
      }
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityEnderChest();
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      for(int var5 = 0; var5 < 3; ++var5) {
         int var6 = var4.nextInt(2) * 2 - 1;
         int var7 = var4.nextInt(2) * 2 - 1;
         double var8 = (double)var3.func_177958_n() + 0.5D + 0.25D * (double)var6;
         double var10 = (double)((float)var3.func_177956_o() + var4.nextFloat());
         double var12 = (double)var3.func_177952_p() + 0.5D + 0.25D * (double)var7;
         double var14 = (double)(var4.nextFloat() * (float)var6);
         double var16 = ((double)var4.nextFloat() - 0.5D) * 0.125D;
         double var18 = (double)(var4.nextFloat() * (float)var7);
         var2.func_195594_a(Particles.field_197599_J, var8, var10, var12, var14, var16, var18);
      }

   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176437_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176437_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_176437_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176437_a, field_204615_b);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_204615_b)) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204615_b, false), 3);
         return Fluids.field_204546_a;
      } else {
         return Fluids.field_204541_a;
      }
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_204615_b) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(var1);
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return !(Boolean)var3.func_177229_b(field_204615_b) && var4 == Fluids.field_204546_a;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      if (!(Boolean)var3.func_177229_b(field_204615_b) && var4.func_206886_c() == Fluids.field_204546_a) {
         if (!var1.func_201670_d()) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204615_b, true), 3);
            var1.func_205219_F_().func_205360_a(var2, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.func_177229_b(field_204615_b)) {
         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
      }

      return super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176437_a = BlockHorizontal.field_185512_D;
      field_204615_b = BlockStateProperties.field_208198_y;
      field_196324_b = Block.func_208617_a(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
   }
}
