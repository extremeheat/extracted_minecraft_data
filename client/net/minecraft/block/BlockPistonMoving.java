package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer {
   public static final DirectionProperty field_196344_a;
   public static final EnumProperty<PistonType> field_196345_b;

   public BlockPistonMoving(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196344_a, EnumFacing.NORTH)).func_206870_a(field_196345_b, PistonType.DEFAULT));
   }

   @Nullable
   public TileEntity func_196283_a_(IBlockReader var1) {
      return null;
   }

   public static TileEntity func_196343_a(IBlockState var0, EnumFacing var1, boolean var2, boolean var3) {
      return new TileEntityPiston(var0, var1, var2, var3);
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         TileEntity var6 = var2.func_175625_s(var3);
         if (var6 instanceof TileEntityPiston) {
            ((TileEntityPiston)var6).func_145866_f();
         } else {
            super.func_196243_a(var1, var2, var3, var4, var5);
         }

      }
   }

   public void func_176206_d(IWorld var1, BlockPos var2, IBlockState var3) {
      BlockPos var4 = var2.func_177972_a(((EnumFacing)var3.func_177229_b(field_196344_a)).func_176734_d());
      IBlockState var5 = var1.func_180495_p(var4);
      if (var5.func_177230_c() instanceof BlockPistonBase && (Boolean)var5.func_177229_b(BlockPistonBase.field_176320_b)) {
         var1.func_175698_g(var4);
      }

   }

   public boolean func_200124_e(IBlockState var1) {
      return false;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (!var2.field_72995_K && var2.func_175625_s(var3) == null) {
         var2.func_175698_g(var3);
         return true;
      } else {
         return false;
      }
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_190931_a;
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      if (!var2.field_72995_K) {
         TileEntityPiston var6 = this.func_196342_a(var2, var3);
         if (var6 != null) {
            var6.func_200230_i().func_196949_c(var2, var3, 0);
         }
      }
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return VoxelShapes.func_197880_a();
   }

   public VoxelShape func_196268_f(IBlockState var1, IBlockReader var2, BlockPos var3) {
      TileEntityPiston var4 = this.func_196342_a(var2, var3);
      return var4 != null ? var4.func_195508_a(var2, var3) : VoxelShapes.func_197880_a();
   }

   @Nullable
   private TileEntityPiston func_196342_a(IBlockReader var1, BlockPos var2) {
      TileEntity var3 = var1.func_175625_s(var2);
      return var3 instanceof TileEntityPiston ? (TileEntityPiston)var3 : null;
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return ItemStack.field_190927_a;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_196344_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_196344_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_196344_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196344_a, field_196345_b);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_196344_a = BlockPistonExtension.field_176387_N;
      field_196345_b = BlockPistonExtension.field_176325_b;
   }
}
